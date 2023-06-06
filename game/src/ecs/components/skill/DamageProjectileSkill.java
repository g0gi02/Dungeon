package ecs.components.skill;

import javax.swing.text.Position;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.damage.Damage;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;
import tools.Point;

public abstract class DamageProjectileSkill implements ISkillFunction {

    private String pathToTexturesOfProjectile;
    private float projectileSpeed;

    private float projectileRange;
    private float knockback;
    private Damage projectileDamage;
    private Point projectileHitboxSize;

    private ITargetSelection selectionFunction;

    public DamageProjectileSkill(
            String pathToTexturesOfProjectile,
            float projectileSpeed,
            Damage projectileDamage,
            Point projectileHitboxSize,
            ITargetSelection selectionFunction,
            float projectileRange) {
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.projectileDamage = projectileDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.projectileHitboxSize = projectileHitboxSize;
        this.selectionFunction = selectionFunction;
        this.knockback = 0f;
    }

    /**
     * creates a damage-dealing projectile
     *
     * @param pathToTexturesOfProjectile where to get the textures from
     * @param projectileSpeed speed of the projectile
     * @param projectileDamage damage the projectile deals
     * @param projectileHitboxSize size of the projectile
     * @param selectionFunction how the target point is selected
     * @param projectileRange range of the projectile
     * @param knockback factor to the projectiles speed, by which the hit entity will be moved
     */
    public DamageProjectileSkill(
        String pathToTexturesOfProjectile,
        float projectileSpeed,
        Damage projectileDamage,
        Point projectileHitboxSize,
        ITargetSelection selectionFunction,
        float projectileRange,
        float knockback) {
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.projectileDamage = projectileDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.projectileHitboxSize = projectileHitboxSize;
        this.selectionFunction = selectionFunction;
        this.knockback = knockback;
    }

    @Override
    public void execute(Entity entity) {
        Entity projectile = new Entity();
        PositionComponent epc =
                (PositionComponent)
                        entity.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));
        new PositionComponent(projectile, epc.getPosition());

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);

        Point aimedOn = selectionFunction.selectTargetPoint();
        Point targetPoint =
                SkillTools.calculateLastPositionInRange(
                        epc.getPosition(), aimedOn, projectileRange);
        Point velocity =
                SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);
        VelocityComponent vc =
                new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
        new ProjectileComponent(projectile, epc.getPosition(), targetPoint);
        ICollide collide = setupCollision(entity, projectile, vc);

        new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }

    private ICollide setupCollision (Entity entity, Entity projectile, VelocityComponent vc) {
        return (a, b, from) -> {
            if (b != entity) {
                // if a value for knockback was set, overrides the velocity of the hit entity
                // with that of the skill, multiplied by the given knockback
                if (knockback != 0) {
                    b.getComponent(VelocityComponent.class)
                        .ifPresent(
                            v -> {
                                ((VelocityComponent) v).setCurrentXVelocity(
                                    vc.getXVelocity()*knockback);
                                ((VelocityComponent) v).setCurrentYVelocity(
                                    vc.getYVelocity()*knockback);
                            });
                }
                b.getComponent(HealthComponent.class)
                    .ifPresent(
                        hc -> {
                            ((HealthComponent) hc).receiveHit(projectileDamage);
                            Game.removeEntity(projectile);
                        });
            }
        };
    }
    
}
