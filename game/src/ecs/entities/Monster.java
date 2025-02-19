package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.ai.fight.IFightAI;
import ecs.components.ai.idle.IIdleAI;
import ecs.components.ai.transition.ITransition;
import graphic.Animation;
import tools.Point;
import java.util.List;

public abstract class Monster extends Entity {

    private final float xSpeed;
    private final float ySpeed;
    private final int MAX_HEALTH;
    private final float attackRange;

    private final String pathToIdleLeft;
    private final String pathToIdleRight;
    private final String pathToRunLeft;
    private final String pathToRunRight;

    private IFightAI fightStrategy;
    private IIdleAI idleStrategy;
    private ITransition transitionStrategy;

    public Monster(
                   float xSpeed,
                   float ySpeed,
                   int MAX_HEALTH,
                   float attackRange,
                   String pathToIdleLeft,
                   String pathToIdleRight,
                   String pathToRunLeft,
                   String pathToRunRight,
                   Point position) {
        super();
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.MAX_HEALTH = MAX_HEALTH;
        this.attackRange = attackRange;

        this.pathToIdleLeft = pathToIdleLeft;
        this.pathToIdleRight = pathToIdleRight;
        this.pathToRunLeft = pathToRunLeft;
        this.pathToRunRight = pathToRunRight;

        new PositionComponent(this, position);
        setupVelocityComponent();
        setupAnimationComponent();
        // setupHitboxComponent();
        setupHealthComponent();
    }

    private void setupHealthComponent() {
        // Animation monsterHit = AnimationBuilder.buildAnimation(pathToHit);
        // Animation monsterDeath = AnimationBuilder.buildAnimation(pathToDeath);
        List<String> missingTexture = List.of("animation/missingTexture.png");
        Animation monsterHit = new Animation(missingTexture, 100, false);
        Animation monsterDeath = new Animation(missingTexture, 100, false);
        new HealthComponent(this, MAX_HEALTH, entity2 -> {}, monsterHit, monsterDeath);
    }

    private void setupVelocityComponent() {
        Animation moveRight = AnimationBuilder.buildAnimation(pathToRunRight);
        Animation moveLeft = AnimationBuilder.buildAnimation(pathToRunLeft);
        new VelocityComponent(this, xSpeed, ySpeed, moveLeft, moveRight);
    }

    private void setupAnimationComponent() {
        Animation idleRight = AnimationBuilder.buildAnimation(pathToIdleRight);
        Animation idleLeft = AnimationBuilder.buildAnimation(pathToIdleLeft);
        new AnimationComponent(this, idleLeft, idleRight);
    }

    private void attack(Entity entity) {}

    private void action(Entity entity) {
        System.out.println("monsterAction");
    }

    /**
     * Set up the AIComponent for the Monster
     */
    public void setupAIComponent() {}
}
