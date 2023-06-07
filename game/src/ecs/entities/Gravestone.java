package ecs.entities;

import ecs.components.*;
import level.tools.LevelElement;
import starter.Game;
import tools.Point;
import java.lang.Math;
import java.util.Optional;

public class Gravestone extends NPC {

    private static final float X_SPEED = 0.0f;
    private static final float Y_SPEED = 0.0f;

    //make sure grave can only be activated once per level
    private boolean graveActive = true;

    private static final String PATH_TO_IDLE = "gravestone";

    private Ghost ghost;

    /**
     * basic gravestone constructor from NPC
     * @param position
     * @param ghost
     */
    public Gravestone(Point position, Ghost ghost) {
        super(
            X_SPEED,
            Y_SPEED,
            PATH_TO_IDLE,
            PATH_TO_IDLE,
            PATH_TO_IDLE,
            PATH_TO_IDLE,
            position);
        this.ghost = ghost;
        setupHitboxComponent();
    }

    /**
     * creates new gravestone
     */
    public static Gravestone createNewGravestone(Ghost ghost) {
        return new Gravestone(
            Game.currentLevel.getRandomTile(LevelElement.FLOOR).getCoordinate().toPoint(), ghost);
    }

    private void setupHitboxComponent() {
        new HitboxComponent(
            this,
            (you, other, direction) -> rewardHero(other),
            (you, other, direction) -> System.out.println("Collision leave tomb")
        );
    }

    /**
     * interaction when ghost and hero arrive at the gravestone
     *
     * @param hero
     */
    public void rewardHero(Entity hero) {
        if (hero instanceof Hero && graveActive && ghost.ghostWantsToFollow) {
            graveActive = false;
            graveInteraction();
            ghost.makeGhostInvis();
        }
    }

    /**
     * if the Hero gets a reward, he will gain 5 lives, if he  gets punished, he looses 5 instead
     */
    public void graveInteraction() {
        Optional<Component> heroHealth = Game.getHero().get().getComponent(HealthComponent.class);
        HealthComponent currentHeroHealth = (HealthComponent) heroHealth.orElseThrow();

        if (Math.random() > 0.5) {
            currentHeroHealth.setCurrentHealthpoints(currentHeroHealth.getCurrentHealthpoints() + 5);
            System.out.println("Reward!");

        } else {
            currentHeroHealth.setCurrentHealthpoints(currentHeroHealth.getCurrentHealthpoints() - 5);
            System.out.println("Punishment?!");
        }
    }
}
