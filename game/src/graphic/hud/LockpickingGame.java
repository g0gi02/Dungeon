package graphic.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import controller.ScreenController;
import starter.Game;
import tools.Constants;

public class LockpickingGame<T extends Actor> extends ScreenController<T> {
    public boolean isActive;
    private final ProgressBar progress;

    /** Creates a new LockpickingGame with a new Spritebatch */
    public LockpickingGame() {
        this(new SpriteBatch());
    }

    /** Creates a new LockpickingGame with a given Spritebatch */
    public LockpickingGame(SpriteBatch batch) {
        super(batch);
        progress = new ProgressBar(
                0f,
                100f,
                1f,
                false,
                new ProgressBar.ProgressBarStyle(
                        new SpriteDrawable(new Sprite(new Texture("hud/white.png"))),
                        new SpriteDrawable(new Sprite(new Texture("hud/ui_heart_full.png")))
                ));
        progress.setBounds(0f, 0f, Constants.WINDOW_WIDTH, (float) Constants.WINDOW_HEIGHT/10);
        //progress.setPosition(0f,0f, Align.left);
        add((T) progress);
        endLockpickingGame();
    }

    /** starts the lock-picking game */
    public void startLockpickingGame() {
        isActive = true;
        progress.setValue(this.progress.getMinValue());
        forEach((Actor s) -> s.setVisible(true));
        if (!Game.isPaused()) Game.togglePause();
    }

    /** ends the lock-picking game */
    public void endLockpickingGame() {
        isActive = false;
        forEach((Actor s) -> s.setVisible(false));
        if (Game.isPaused()) Game.togglePause();
    }

    @Override
    public void update() {
        super.update();
        if (isActive) {
            progress.setValue(progress.getValue()+progress.getStepSize());
            if (progress.getValue() == progress.getMaxValue()) endLockpickingGame();
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                endLockpickingGame();
            }
        }
    }
}
