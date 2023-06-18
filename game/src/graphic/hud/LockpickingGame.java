package graphic.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import controller.ScreenController;
import ecs.systems.ECS_System;
import starter.Game;
import tools.Constants;
import tools.Point;
import java.util.logging.Logger;

// TODO formatting and documentation
public class LockpickingGame<T extends Actor> extends ScreenController<T> {
    private final Logger logger;
    private final ProgressBar progress;
    private final ScreenText markerLeft;
    private final ScreenText markerRight;
    private float desiredProgress;
    private float tolerance;
    private boolean isActive;
    private boolean isStarted;

    /** Creates a new LockpickingGame with a new Spritebatch */
    public LockpickingGame() {
        this(new SpriteBatch());
    }

    /** Creates a new LockpickingGame with a given Spritebatch */
    public LockpickingGame(SpriteBatch batch) {
        super(batch);
        // progress bar
        progress = new ProgressBar(
                0f,
                100f,
                0.5f,
                false,
                new ProgressBar.ProgressBarStyle(
                        new SpriteDrawable(new Sprite(new Texture("hud/white.png"))),
                        new SpriteDrawable(new Sprite(new Texture("hud/ui_heart_full.png")))
                ));
        progress.setBounds(Constants.WINDOW_WIDTH * 0.1f, Constants.WINDOW_HEIGHT * 0.4f, Constants.WINDOW_WIDTH * 0.8f, Constants.WINDOW_HEIGHT * 0.1f);
        add((T) progress);
        // left marker
        markerLeft = new ScreenText("*", new Point(0, 0), 3, new LabelStyleBuilder(FontBuilder.DEFAULT_FONT).setFontcolor(Color.RED).build());
        markerLeft.setFontScale(3);
        add((T) markerLeft);
        // right marker
        markerRight = new ScreenText("*", new Point(0, 0), 3, new LabelStyleBuilder(FontBuilder.DEFAULT_FONT).setFontcolor(Color.RED).build());
        markerRight.setFontScale(3);
        add((T) markerRight);

        endLockpickingGame();
        logger = Logger.getLogger(this.getClass().getName());
    }

    /** starts the lock-picking game */
    public void startLockpickingGame() {
        isActive = true;
        isStarted = false;
        progress.setValue(this.progress.getMinValue());
        // TODO randomise
        tolerance = 10f;
        desiredProgress = 50f;

        markerLeft.setPosition(Constants.WINDOW_WIDTH * 0.1f + ((desiredProgress-tolerance)/progress.getMaxValue()) * progress.getWidth(), Constants.WINDOW_HEIGHT* 0.5f);
        markerRight.setPosition(Constants.WINDOW_WIDTH * 0.1f + ((desiredProgress+tolerance)/progress.getMaxValue()) * progress.getWidth(), Constants.WINDOW_HEIGHT* 0.5f);

        forEach((Actor s) -> s.setVisible(true));
        if (!Game.isPaused() && Game.systems != null) {
            Game.systems.forEach(ECS_System::toggleRun);
        }
    }

    /** ends the lock-picking game */
    public void endLockpickingGame() {
        isActive = false;
        isStarted = false;
        forEach((Actor s) -> s.setVisible(false));
        if (Game.isPaused() && Game.systems != null) {
            Game.systems.forEach(ECS_System::toggleRun);
        }
    }

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
            if (isStarted && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                // success: current progress within desired interval
                if (progress.getValue() - tolerance < desiredProgress && progress.getValue() + tolerance > desiredProgress) {
                    endLockpickingGame();
                    logger.info("lockpicking successful");
                // fail: current progress not within desired interval
                } else {
                    endLockpickingGame();
                    logger.info("lockpicking failed - clicked at the wrong moment");
                }
            }
            // click to start the mini-game
            if (!isStarted && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) isStarted = true;
        }
    }
}
