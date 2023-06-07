package ecs.Quests;

import ecs.components.Component;
import ecs.components.HealthComponent;
import starter.Game;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Logger;


public class LevelQuest extends Quest {
    private Logger levelQuestLogger = Logger.getLogger("LevelQuest");
    private final int levelsNeeded = 5;

    /**
     * Creates a new LevelQuest
     * @param questname
     * @param description
     */
    public LevelQuest(String questname, String description) {
        super(questname,description);
        progressionMessage = " Es wurden null von " + levelsNeeded + "abgeschlossen";
    }

    /**
     * Tracks the progress of the quest
     */
    @Override
    public void trackProgress(){
        progressionMessage = "Es wurden " + Game.getCurrentLevelCounter() + " von " + levelsNeeded  + " abgeschlossen";
    }

    /**
     * Checks if the quest is finished
     * Is finished once the hero has completed 5 levels
     * @return true if the quest is finished, false if not
     */
    @Override
    public boolean isFinished(){
        if (Game.getCurrentLevelCounter() >= levelsNeeded + 1){
            Game.setHasOngoingQuest(false);
            return true;
        }
        return false;
    }

    /**
     * Reward for the LevelQuest
     * Increases the current healthpoints of the hero by 10
     */
    @Override
    public void giveReward() {
        Optional<Component> heroHealth = Game.getHero().get().getComponent(HealthComponent.class);
        HealthComponent currentHeroHealth = (HealthComponent) heroHealth.orElseThrow();
        currentHeroHealth.setCurrentHealthpoints(currentHeroHealth.getCurrentHealthpoints() + 10);
        levelQuestLogger.info("LevelQuest reward, heal 10 HP!");
    }
}
