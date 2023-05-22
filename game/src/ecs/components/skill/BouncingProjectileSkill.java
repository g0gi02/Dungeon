package ecs.components.skill;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.damage.Damage;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;
import tools.Point;

public abstract class BouncingProjectileSkill extends DamageProjectileSkill{

    private String pathToTexturesOfProjectile;
    private float projectileSpeed;

    private float projectileRange;
    private Damage projectileDamage;
    private Point projectileHitboxSize;

    private ITargetSelection selectionFunction;
    private int maxBounceCount;
    Entity projectile;
    Entity entity; // entity that casted the skill

    public BouncingProjectileSkill(
        String pathToTexturesOfProjectile,
        float projectileSpeed,
        Damage projectileDamage,
        Point projectileHitboxSize,
        ITargetSelection selectionFunction,
        float projectileRange,
        int maxBounceCount) {
        super(pathToTexturesOfProjectile,
                projectileSpeed,
                projectileDamage,
                projectileHitboxSize,
                selectionFunction,
                projectileRange);
        this.pathToTexturesOfProjectile = pathToTexturesOfProjectile;
        this.projectileDamage = projectileDamage;
        this.projectileSpeed = projectileSpeed;
        this.projectileRange = projectileRange;
        this.projectileHitboxSize = projectileHitboxSize;
        this.selectionFunction = selectionFunction;
        this.maxBounceCount = maxBounceCount;
    }

    @Override
    public void execute(Entity entity) {
        this.entity = entity;
        projectile = new Entity();
        PositionComponent epc =
                (PositionComponent)
                        entity.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));
        new PositionComponent(projectile, epc.getPosition());

        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);

        new BouncingComponent(projectile, maxBounceCount, this);

        Point aimedOn = selectionFunction.selectTargetPoint();
        Point targetPoint =
                SkillTools.calculateLastPositionInRange(
                        epc.getPosition(), aimedOn, projectileRange);
        Point velocity =
                SkillTools.calculateVelocity(epc.getPosition(), targetPoint, projectileSpeed);
        VelocityComponent vc =
                new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
        new ProjectileComponent(projectile, epc.getPosition(), targetPoint);
        ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc -> {
                                            ((HealthComponent) hc).receiveHit(projectileDamage);
                                            // execute(entity);
                                            SkillTools.causeKnockBack(a, b);
                                            Game.removeEntity(projectile);
                                        });
                    }
                };

        new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }

    public void bounce(float newXVelocity, float newYVelocity, int bounceCount) {
        Point startPoint = getPositionOfEntity(projectile);
        // New endPoint 
        Point endPoint = new Point(startPoint.x + newXVelocity, startPoint.y + newYVelocity);

        projectile = new Entity();
        new PositionComponent(projectile, startPoint);
        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);

        new BouncingComponent(projectile, maxBounceCount, this, bounceCount);


        Point aimedOn = endPoint;
        Point targetPoint =
                SkillTools.calculateLastPositionInRange(
                        startPoint, aimedOn, projectileRange);
        Point velocity = SkillTools.calculateVelocity(startPoint, targetPoint, projectileSpeed);
        VelocityComponent vc =
                new VelocityComponent(projectile, velocity.x, velocity.y, animation, animation);
        new ProjectileComponent(projectile, startPoint, targetPoint);
        ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc -> {
                                            ((HealthComponent) hc).receiveHit(projectileDamage);
                                            // execute(entity);
                                            SkillTools.causeKnockBack(a, b);
                                            Game.removeEntity(projectile);
                                        });
                    }
                };

        new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }

    private Point getPositionOfEntity(Entity entity) {
        PositionComponent epc =
                (PositionComponent)
                        entity.getComponent(PositionComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("PositionComponent"));
        return epc.getPosition();
    }

    private Point getStartPositionOfProjectile(Entity entity) {
        ProjectileComponent epc =
                (ProjectileComponent)
                        entity.getComponent(ProjectileComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("ProjectileComponent"));
        return epc.getStartPosition();
    }
}
