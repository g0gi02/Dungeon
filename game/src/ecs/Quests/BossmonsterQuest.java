package ecs.Quests;

import ecs.components.Component;
import ecs.components.HealthComponent;
import ecs.entities.Hero;
import starter.Game;
import java.util.logging.Logger;
import java.util.Optional;

public class BossmonsterQuest extends Quest  {
    private Logger bossmonsterQuestLogger = Logger.getLogger("BossmonsterQuest");
    public int startHealth;
    public int endHealth;

    /**
     * Creates a new BossmonsterQuest
     * @param questname
     * @param description
     */
    public  BossmonsterQuest(String questname, String description){
        super(questname, description);
        progressionMessage = "Besiege das Bossmonster ohne Lebenspunkte zu verlieren";
    }

    /**
     * Tracks the progress of the quest
     */
    @Override
    public void trackProgress(){
        progressionMessage = "fight without fear";
    }


    /**
     * Checks if the quest is finished
     * Is finished if the hero has more or equal healthpoints after the bossmonsterfight than before
     * @return true if the quest is finished, false if not
     */
    @Override
    public boolean isFinished(){
        Optional<Component> health = Game.getHero().get().getComponent(HealthComponent.class);
        HealthComponent currentHeroHealth = (HealthComponent) health.orElseThrow();
        if (Game.getCurrentLevelCounter() % 10 == 0){
            startHealth = currentHeroHealth.getCurrentHealthpoints();
        }
        if (Game.getCurrentLevelCounter() % 10 == 1){
            endHealth = currentHeroHealth.getCurrentHealthpoints();
        }
        if ((endHealth >= startHealth)){
            return true;
        }
        return false;

    }

    /**
     * Reward for the BossmonsterQuest
     * Increases the maximal and current healthpoints of the hero by 10
     */
    @Override
    public void giveReward(){
        Optional<Component> heroHealth = Game.getHero().get().getComponent(HealthComponent.class);
        HealthComponent currentHeroHealth = (HealthComponent) heroHealth.orElseThrow();
        currentHeroHealth.setMaximalHealthpoints(currentHeroHealth.getMaximalHealthpoints() + 10);
        currentHeroHealth.setCurrentHealthpoints(currentHeroHealth.getCurrentHealthpoints() + 10);
        bossmonsterQuestLogger.info("BossmonsterQuest reward");
    }

}
