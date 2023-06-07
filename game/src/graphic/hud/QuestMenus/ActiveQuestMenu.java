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

public class ActiveQuestMenu<T extends Actor> extends ScreenController<T> {
    public ActiveQuestMenu() {
        this(new SpriteBatch());
    }

    public boolean isMenuOpen = false;
    private String activeQuestName = " no Quests yet";
    private String activeQuestDescription = " ";


    //specifies how the ActiveQuestMenu looks
    public ActiveQuestMenu(SpriteBatch batch) {
        super(batch);
        ScreenText screenText =
            new ScreenText(
                " active Quest : \n" + activeQuestName + "\n" + activeQuestDescription,
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
        this.hideActiveQuestMenu();
    }

    public void setScreenTextQuest(String name, String description) {
        activeQuestName = name;
        activeQuestDescription = description;
    }

    /**
     * makes the ActiveQuestMenu visible
     **/
    public void showActiveQuestMenu() {
        this.forEach((Actor s) -> s.setVisible(true));
        System.out.println("open ActiveQuest Menu");
        isMenuOpen = true;
    }

    /**
     * makes the ActiveQuestMenu invisible
     **/
    public void hideActiveQuestMenu() {
        this.forEach((Actor s) -> s.setVisible(false));
        isMenuOpen = false;
        System.out.println("Hide ActiveQuest Menu");
    }
}
