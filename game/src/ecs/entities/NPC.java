package ecs.entities;

import dslToGame.AnimationBuilder;
import ecs.components.AnimationComponent;
import ecs.components.PositionComponent;
import ecs.components.VelocityComponent;
import graphic.Animation;
import tools.Point;

public abstract class NPC extends Entity {
    private final float xSpeed;
    private final float ySpeed;
    private final String pathToIdleLeft;
    private final String pathToIdleRight;
    private final String pathToRunLeft;
    private final String pathToRunRight;

    public NPC(
        float xSpeed,
        float ySpeed,
        String pathToIdleLeft,
        String pathToIdleRight,
        String pathToRunLeft,
        String pathToRunRight,
        Point position
    ) {

        super();
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.pathToIdleLeft = pathToIdleLeft;
        this.pathToIdleRight = pathToIdleRight;
        this.pathToRunLeft = pathToRunLeft;
        this.pathToRunRight = pathToRunRight;

        new PositionComponent(this, position);
        setupVelocityComponent();
        setupAnimationComponent();
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

    /**
     * Set up the AIComponent for the NPC
     */
    public void setupAIComponent() {}
}

