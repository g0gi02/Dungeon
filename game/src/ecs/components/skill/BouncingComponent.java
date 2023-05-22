package ecs.components.skill;

import ecs.components.Component;
import ecs.components.VelocityComponent;
import ecs.entities.Entity;
import level.elements.tile.Tile;
import ecs.components.MissingComponentException;



public class BouncingComponent extends Component {
    private int maxBounceCount;
    private Entity entity;
    private BouncingProjectileSkill skill;
    private int currentBounceCount;

    BouncingComponent(Entity entity, int maxBounceCount, BouncingProjectileSkill skill) {
        super(entity);
        this.entity = entity;
        this.maxBounceCount = maxBounceCount;
        this.skill = skill;
        this.currentBounceCount = 0;
    }

    BouncingComponent(Entity entity, int maxBounceCount, BouncingProjectileSkill skill, int currentBounceCount) {
        super(entity);
        this.entity = entity;
        this.maxBounceCount = maxBounceCount;
        this.skill = skill;
        this.currentBounceCount = currentBounceCount;
    }

    /**
     * Bounce the entity by changing its direction
     * 
     * @param directions the directions to bounce to
     */
    public void bounce(Tile.Direction[] directions) {
        if (++currentBounceCount >= maxBounceCount) {
            return;
        }
        
        VelocityComponent vc = (VelocityComponent)
        entity.getComponent(VelocityComponent.class)
        .orElseThrow(
            () -> new MissingComponentException("VelocityComponent"));
        
        float newXVelocity = vc.getCurrentXVelocity();
        float newYVelocity = vc.getCurrentYVelocity();
        
        for (Tile.Direction direction : directions) {
            System.out.println(direction);
            System.out.println(vc.getCurrentXVelocity());
            switch (direction) {
                case N:
                    newYVelocity = -vc.getCurrentYVelocity();
                    break;
                case E:
                    newXVelocity = -vc.getCurrentXVelocity();
                    break;
                case S:
                    newYVelocity = -vc.getCurrentYVelocity();
                    break;
                case W:
                    newXVelocity = -vc.getCurrentXVelocity();
                    break;
            }
            System.out.println(vc.getCurrentXVelocity());
        }

        // New Targetpoint for the projectile
        skill.bounce(newXVelocity, newYVelocity, currentBounceCount);

        
    }

    public void incrementBounceCount() {
        currentBounceCount++;
    }

    public int getCurrentBounceCount() {
        return currentBounceCount;
    }

    public void setCurrentBounceCount(int currentBounceCount) {
        this.currentBounceCount = currentBounceCount;
    }

    public int getMaxBounceCount() {
        return maxBounceCount;
    }

    public void setMaxBounceCount(int maxBounceCount) {
        this.maxBounceCount = maxBounceCount;
    }
}
