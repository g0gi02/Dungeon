package ecs.Quests;

import ecs.components.Component;
import ecs.components.HealthComponent;
import ecs.entities.Hero;
import starter.Game;

import java.util.Optional;

public class BossmonsterQuest extends Quest  {

    public int startHealth;
    public int endHealth;

    public  BossmonsterQuest(String questname, String description){
        super(questname, description);
        progressionMessage = "Besiege das Bossmonster ohne Lebenspunkte zu verlieren";
    }

    @Override
    public void trackProgress(){
        progressionMessage = "fight without fear";
    }


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

    @Override
    public void giveReward(){
        Optional<Component> heroHealth = Game.getHero().get().getComponent(HealthComponent.class);
        HealthComponent currentHeroHealth = (HealthComponent) heroHealth.orElseThrow();
        currentHeroHealth.setMaximalHealthpoints(currentHeroHealth.getMaximalHealthpoints() + 10);
        currentHeroHealth.setCurrentHealthpoints(currentHeroHealth.getCurrentHealthpoints() + 10);
        System.out.println("BossmonsterQuest reward ");
    }

}
