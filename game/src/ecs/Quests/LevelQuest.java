package ecs.Quests;

import ecs.components.Component;
import ecs.components.HealthComponent;
import starter.Game;
import java.util.Optional;

public class LevelQuest extends Quest {
    private final int levelsNeeded = 5;

    public LevelQuest(String questname, String description) {
        super(questname,description);
        progressionMessage = " Es wurden null von " + levelsNeeded + "abgeschlossen";
    }

    @Override
    public void trackProgress(){
        progressionMessage = "Es wurden " + Game.getCurrentLevel() + " von " + levelsNeeded  + " abgeschlossen";
    }

    @Override
    public boolean isFinished(){
        if (Game.getCurrentLevel() >= levelsNeeded + 1){
            return true;
        }
        return false;
    }


    @Override
    public void giveReward() {
        Optional<Component> heroHealth = Game.getHero().get().getComponent(HealthComponent.class);
        HealthComponent currentHeroHealth = (HealthComponent) heroHealth.orElseThrow();
        currentHeroHealth.setCurrentHealthpoints(currentHeroHealth.getCurrentHealthpoints() + 10);
        System.out.println("LevelQuest reward " + Game.getCurrentLevel());
    }
}
