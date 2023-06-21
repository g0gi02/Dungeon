package ecs.entities;

import java.util.List;
import java.util.logging.Logger;
import dslToGame.AnimationBuilder;

import ecs.components.ai.idle.IIdleAI;
import level.tools.LevelElement;
import tools.Point;
import starter.Game;
import ecs.components.ai.idle.*;
import ecs.components.ai.fight.*;
import ecs.components.ai.transition.*;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import ecs.components.ai.idle.RadiusWalk;
import ecs.components.HealthComponent;
import ecs.components.HitboxComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import ecs.components.AnimationComponent;
import ecs.components.ai.AIComponent;
import ecs.components.InteractionComponent;
import graphic.Animation;

public class MimicMonster extends Monster {
    private transient Logger mimicMonsterLogger;

    private static final float X_SPEED = 0.2f;
    private static final float Y_SPEED = 0.2f;
    private static final int MAX_HEALTH = 5;
    private static final float ATTACK_RANGE = 5f;
    private static final float TRANSITION_RANGE = 5f;
    private static final float defaultInteractionRadius = 2f;

    private static final String DEFAULT_CLOSED_ANIMATION_FRAMES = "monster/mimicMonster/neutral";

    private static final String PATH_TO_IDLE_LEFT = "monster/mimicMonster/idle";
    private static final String PATH_TO_IDLE_RIGHT = "monster/mimicMonster/idle";
    private static final String PATH_TO_RUN_LEFT = "monster/mimicMonster/idle";
    private static final String PATH_TO_RUN_RIGHT = "monster/mimicMonster/idle";

    private boolean isClosed = true;

    /**
     * Creates a new MimicMonster at the given position.
     * The imp will try to hurt the hero.
     * @param position
     */
    public MimicMonster(Point position) {
        super(
            X_SPEED,
            Y_SPEED,
            MAX_HEALTH
        );
        setupPositionComponent(position);
        setupAnimationComponent();
        setupInteractionComponent();
        setupHitboxComponent();
        setupLogger();
        mimicMonsterLogger.info("MimicMonster created");
    }

    
    /**
     * Creates a new MimicMonster at a random position on the current level.
     *
     * @return a new MimicMonster Monster
     */
    public static MimicMonster createNewMimicMonster() {
        return new MimicMonster(
            Game.currentLevel.getRandomTile(LevelElement.FLOOR).getCoordinate().toPoint());
    }

    /**
     * The action the MimicMonster performs when it hits another entity.
     * If the entity is a hero, it will take damage.
     * @param entity
     */
    private void action1(Entity entity) {
        if (isClosed) return;
        Damage damage = new Damage(1, DamageType.MAGIC, this);
        if (entity instanceof Hero ) {
            Game.getHero().stream()
            .flatMap(e -> e.getComponent(HealthComponent.class).stream())
            .map(HealthComponent.class::cast)
            .forEach(healthComponent -> {healthComponent.receiveHit(damage);});
        }
    }

    private void setupHealthComponent() {
        List<String> missingTexture = List.of("animation/missingTexture.png");
        Animation monsterHit = new Animation(missingTexture, 100, false);
        Animation monsterDeath = new Animation(missingTexture, 100, false);
        new HealthComponent(this, MAX_HEALTH, this::onDeath, monsterHit, monsterDeath);
    }

    private void onDeath(Entity entity) {
        Point position =  ((PositionComponent) entity.getComponent(PositionComponent.class).get()).getPosition();
        MimicMonsterChest.createNewMimicMonsterChest(position);
    }

    private void setupPositionComponent(Point position) {
        new PositionComponent(this, position);
    }

    private void setupAnimationComponent() {
        Animation closedAnimation = AnimationBuilder.buildAnimation(DEFAULT_CLOSED_ANIMATION_FRAMES);
        Animation idleRight = AnimationBuilder.buildAnimation(PATH_TO_IDLE_RIGHT);
        Animation idleLeft = AnimationBuilder.buildAnimation(PATH_TO_IDLE_LEFT);
        AnimationComponent animationComponent  = new AnimationComponent(this, idleLeft, idleRight);
        animationComponent.setCurrentAnimation(closedAnimation);
    }

    /**
     * Sets up the logger for the MimicMonster.
     */
    @Override
    public void setupLogger() {
        mimicMonsterLogger = Logger.getLogger("MimicMonster");
    }

    private void setupVelocityComponent() {
        Animation moveRight = AnimationBuilder.buildAnimation(PATH_TO_RUN_RIGHT);
        Animation moveLeft = AnimationBuilder.buildAnimation(PATH_TO_RUN_LEFT);
        new VelocityComponent(this, X_SPEED, Y_SPEED, moveLeft, moveRight);
    }

    private void setupHitboxComponent() {
        new HitboxComponent(
                this,
                (you, other, direction) -> action1(other),
                (you, other, direction) -> mimicMonsterLogger.info("monsterCollision")
        );
    }

    private void transition(Entity entity) {
        setupVelocityComponent();
        setupAIComponent();
        setupHealthComponent();
        isClosed = false;
        mimicMonsterLogger.info("MimicMonster transitioned");
    }

    private void setupInteractionComponent() {
        new InteractionComponent(this,
                    defaultInteractionRadius,
                    false, 
                    this::transition);
    }

    /**
     * Sets up the AI component of the MimicMonster.
     */
    public void setupAIComponent() {
        new AIComponent(this, setupFightStrategy(),
                        setupIdleStrategy(), setupTransitionStrategy());
       
    }

    private IFightAI setupFightStrategy() {
        return new CollideAI(ATTACK_RANGE);
    }

    private IIdleAI setupIdleStrategy() {
        return new RadiusWalk(3, 1);
    }

    private ITransition setupTransitionStrategy() {
        return new RangeTransition(TRANSITION_RANGE);
    }
}
