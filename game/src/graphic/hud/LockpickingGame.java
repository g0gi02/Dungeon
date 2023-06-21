package graphic.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import configuration.KeyboardConfig;
import controller.ScreenController;
import ecs.components.InteractionComponent;
import ecs.entities.Chest;
import ecs.systems.ECS_System;
import starter.Game;
import tools.Constants;
import tools.Point;
import java.util.logging.Logger;
import ecs.entities.IChest;
import ecs.entities.MasterworkChest;
import ecs.entities.Entity;

/** A mini-game in which a progress bar is filled over time, requires a timed player input. */
public class LockpickingGame<T extends Actor> extends ScreenController<T> {
    private final Logger logger;
    private final ProgressBar progress;
    private final ScreenText markerLeft;
    private final ScreenText markerRight;
    private float desiredProgress;
    private float tolerance;
    private boolean isActive;
    private boolean isStarted;
    private static final String PATH_TO_BACKGROUND_TEXTURE ="minigames/lockpicking/background.png";
    private static final String PATH_TO_KNOB_TEXTURE ="minigames/lockpicking/knob.png";
    private MasterworkChest associatedChest;

    /** Creates a new LockpickingGame with a new Spritebatch */
    public LockpickingGame() {
        this(new SpriteBatch());
    }

    /**
     * Creates a new LockpickingGame with a given Spritebatch
     *
     * @param batch the Spritebatch
     */
    public LockpickingGame(SpriteBatch batch) {
        super(batch);
        // set up progress bar
        progress = new ProgressBar(
                0f,
                100f,
                0.5f,
                false,
                new ProgressBar.ProgressBarStyle(
                        new SpriteDrawable(new Sprite(new Texture(PATH_TO_BACKGROUND_TEXTURE))),
                        new SpriteDrawable(new Sprite(new Texture(PATH_TO_KNOB_TEXTURE)))
                ));
        progress.setBounds(Constants.WINDOW_WIDTH * 0.1f, Constants.WINDOW_HEIGHT * 0.45f,
                Constants.WINDOW_WIDTH * 0.8f, Constants.WINDOW_HEIGHT * 0.1f);
        add((T) progress);
        // set up left marker
        markerLeft = new ScreenText("*", new Point(0, 0), 3,
                new LabelStyleBuilder(FontBuilder.DEFAULT_FONT).setFontcolor(Color.RED).build());
        markerLeft.setFontScale(3);
        add((T) markerLeft);
        // set up right marker
        markerRight = new ScreenText("*", new Point(0, 0), 3,
                new LabelStyleBuilder(FontBuilder.DEFAULT_FONT).setFontcolor(Color.RED).build());
        markerRight.setFontScale(3);
        add((T) markerRight);

        endLockpickingGame();
        logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Starts the lock-picking mini-game with random values and pauses the game.
     *
     * @param chest chest which will be unlocked, can be null
     */
    public void startLockpickingGame(MasterworkChest chest) {
        // reset to starting values
        isActive = true;
        isStarted = false;
        progress.setValue(this.progress.getMinValue());
        associatedChest = chest;
        // determine a random tolerance between 2% and 10%
        tolerance = (float)(Math.random() * 8 + 2);
        // determine a random desired progress between 10% and 90%
        desiredProgress = (float)(Math.random() * 80 + 10);
        // determine a random step size (per frame) between 0.5% and 2%
        progress.setStepSize((float)(Math.random() * 1.5 + 0.5));
        // set the left markers position according to the desired progress and tolerance
        markerLeft.setPosition(Constants.WINDOW_WIDTH * 0.1f +
            ((desiredProgress-tolerance)/progress.getMaxValue()) * progress.getWidth(),
            Constants.WINDOW_HEIGHT* 0.5f);
        // set the right markers position according to the desired progress and tolerance
        markerRight.setPosition(Constants.WINDOW_WIDTH * 0.1f +
            ((desiredProgress+tolerance)/progress.getMaxValue()) * progress.getWidth(),
            Constants.WINDOW_HEIGHT* 0.5f);
        // pause the game while mini-game is running
        forEach((Actor s) -> s.setVisible(true));
        if (!Game.isPaused() && Game.systems != null) {
            Game.systems.forEach(ECS_System::toggleRun);
            Game.togglePaused();
        }
    }

    /** Ends the lock-picking mini-game and unpauses the game. */
    public void endLockpickingGame() {
        isActive = false;
        isStarted = false;
        associatedChest = null;
        forEach((Actor s) -> s.setVisible(false));
        if (Game.isPaused() && Game.systems != null) {
            Game.systems.forEach(ECS_System::toggleRun);
            Game.togglePaused();
        }
    }

    /**
     * Updates the mini-game and checks player input,
     * interacts with associated chest on completion if present.
     */
    @Override
    public void update() {
        super.update();
        if (isActive) {
            // progress the bar
            if (isStarted) progress.setValue(progress.getValue()+progress.getStepSize());
            // end when time is up
            if (progress.getValue() == progress.getMaxValue()) {
                endLockpickingGame();
                logger.info("lockpicking failed - didn't click");
            }
            // check progress when input occurs
            if (isStarted && Gdx.input.isButtonJustPressed(KeyboardConfig.MINI_GAME_INPUT.get())) {
                if (progress.getValue() + tolerance < desiredProgress) {
                    endLockpickingGame();
                    logger.info("lockpicking failed - clicked too soon");
                } else if (progress.getValue() - tolerance > desiredProgress) {
                    endLockpickingGame();
                    logger.info("lockpicking failed - clicked too late");
                } else {
                    logger.info("lockpicking successful");
                    if (associatedChest != null) {
                        associatedChest.dropItems((Entity) associatedChest);
                    }
                    endLockpickingGame();
                }
            }
            // click to start the mini-game
            if (!isStarted && Gdx.input.isButtonJustPressed(KeyboardConfig.MINI_GAME_INPUT.get()))
                isStarted = true;
        }
    }
}
