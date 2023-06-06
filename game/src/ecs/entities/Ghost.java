package ecs.entities;

import ecs.components.*;
import ecs.components.ai.AIComponent;
import ecs.components.ai.fight.CollideAI;
import ecs.components.ai.fight.IFightAI;
import ecs.components.ai.idle.IIdleAI;
import ecs.components.ai.idle.RadiusWalk;
import ecs.components.ai.idle.StaticRadiusWalk;
import ecs.components.ai.transition.ITransition;
import ecs.components.ai.transition.RangeTransition;
import ecs.components.ai.transition.friendlyTransition;
import level.tools.LevelElement;
import starter.Game;

import tools.Point;

import java.util.Set;

import ecs.components.VelocityComponent;

public class Ghost extends NPC {

    public boolean ghostWantsToFollow = true;
    private static final float X_SPEED = 0.2f;
    private static final float Y_SPEED = 0.2f;
    private static final float TRANSITION_RANGE = 50f;

    private static final float FOLLOW_RANGE = 50f;
    private static final float MOVE_RADIUS = 10f;

    private static final String PATH_TO_IDLE_LEFT = "Ghost/idleLeft";
    private static final String PATH_TO_IDLE_RIGHT = "Ghost/idleRight";
    private static final String PATH_TO_RUN_LEFT = "Ghost/runLeft";
    private static final String PATH_TO_RUN_RIGHT = "Ghost/runRight";

    public Ghost(Point position) {
        super(
            X_SPEED,
            Y_SPEED,
            PATH_TO_IDLE_LEFT,
            PATH_TO_IDLE_RIGHT,
            PATH_TO_RUN_LEFT,
            PATH_TO_RUN_RIGHT,
            position
        );
        setupHitboxComponent();
        setupAIComponent();
    }

    // creates new Ghost
    public static Ghost createNewGhost() {
        return new Ghost(
            Game.currentLevel.getRandomTile(LevelElement.FLOOR).getCoordinate().toPoint());
    }

    /**
     * checks if the Hero enters ghosts private zone
     */
    public void setupHitboxComponent() {
        new HitboxComponent(
            this,
            (you, other, direction) -> {
                if (other instanceof Hero) {
                    this.setUpPassiveAITransition();
                    ghostWantsToFollow = false;
                    System.out.println("no more followers!");
                }
            },
            (you, other, direction) -> System.out.println("leave Collision")
        );
    }

    @Override
    public void setupAIComponent() {
        new AIComponent(this, setupFollowStrategy(), setupIdleAIStrategy(), setupTransitionStrategy());
    }

    /**
     *  ghost will chase the Hero with some distance
     */
    private IFightAI setupFollowStrategy() {
        Hero hero = (Hero) Game.getHero().get();
        Ghost ghost = this;
        CollideAI collideAI = new CollideAI(FOLLOW_RANGE);
        return new IFightAI() {
            @Override
            public void fight(Entity entity) {
                PositionComponent heroPos = (PositionComponent) hero.getComponent(PositionComponent.class).get();
                PositionComponent ghostPos = (PositionComponent) ghost.getComponent(PositionComponent.class).get();
                float distance = Point.calculateDistance(heroPos.getPosition(), ghostPos.getPosition());
                if (distance <= 2f) {
                    System.out.println("That's to close for comfort!");
                } else {
                    collideAI.fight(entity);
                }
            }
        };
    }

    private IIdleAI setupIdleAIStrategy() {
        return new RadiusWalk(MOVE_RADIUS, 1);
    }

    private ITransition setupTransitionStrategy() {
        return new RangeTransition(TRANSITION_RANGE);
    }

    /**
     * make the ghost freak out when it gets touched by the Hero
     */
    private void setUpPassiveAITransition() {
        VelocityComponent speed = (VelocityComponent) this.getComponent(VelocityComponent.class).get();
        speed.setYVelocity(0.5f);
        speed.setXVelocity(0.5f);
        new AIComponent(this, new CollideAI(2f), new StaticRadiusWalk(30f, 1), new friendlyTransition());
    }

    /**
     *  this will make the ghost disappear,
     */
    public void makeGhostInvis() {
        Set<Entity> allEntities = Game.getEntities();
        for (Entity allEntity : allEntities) {
            if (allEntity instanceof Ghost) Game.removeEntity(allEntity);
        }
    }
}
