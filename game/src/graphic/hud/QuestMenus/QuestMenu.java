package graphic.hud.QuestMenus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import controller.ScreenController;
import graphic.hud.FontBuilder;
import graphic.hud.LabelStyleBuilder;
import graphic.hud.ScreenText;
import tools.Constants;
import tools.Point;

public class QuestMenu <T extends Actor> extends ScreenController<T> {

    public QuestMenu(){this (new SpriteBatch());}

    public boolean isMenuOpen = false;

    //specifies how the QuestMenu looks
    public QuestMenu(SpriteBatch batch) {
        super(batch);
        ScreenText screenText =
            new ScreenText(
                "   Press H to accept the Quest,\n               J to reject it. \n      To show active Quests \n                   press G",
                new Point(0, 0),
                3,
                new LabelStyleBuilder(FontBuilder.DEFAULT_FONT)
                    .setFontcolor(Color.RED)
                    .build());
        screenText.setFontScale(3);
        screenText.setPosition(
            (Constants.WINDOW_WIDTH) / 2.0f - screenText.getWidth(),
            (Constants.WINDOW_HEIGHT) / 3.0f + screenText.getHeight(),
            Align.center | Align.bottom);

        add((T) screenText);
        this.hideQuestMenu();
    }

    /**
     * makes the QuestMenu visible
     **/
    public void showQuestMenu(){
        this.forEach((Actor s) -> s.setVisible(true));
        System.out.println("open Quest Menu");
        isMenuOpen = true;
    }

    /**
     * makes the QuestMenu invisible
     **/
    public void hideQuestMenu(){
        this.forEach((Actor s) -> s.setVisible(false));
        isMenuOpen =false;
        System.out.println("Hide Quest Menu");
    }
}
