package ecs.components.skill;

import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.components.collision.ICollide;
import ecs.damage.Damage;
import ecs.entities.Entity;
import graphic.Animation;
import starter.Game;
import tools.Point;

public class ReturningProjectileSkill extends DamageProjectileSkill {
    private String pathToTexturesOfProjectile;
    private float projectileSpeed;

    private float projectileRange;
    private Damage projectileDamage;
    private Point projectileHitboxSize;

    private ITargetSelection selectionFunction;

    public ReturningProjectileSkill(
            String pathToTexturesOfProjectile,
            float projectileSpeed,
            Damage projectileDamage,
            Point projectileHitboxSize,
            ITargetSelection selectionFunction,
            float projectileRange) {
        super(
                pathToTexturesOfProjectile,
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
        ICollide collide =
                (a, b, from) -> {
                    if (b != entity) {
                        b.getComponent(HealthComponent.class)
                        .ifPresent(
                            hc -> {
                                ((HealthComponent) hc).receiveHit(projectileDamage);
                                SkillTools.causeKnockBack(a, b);
                                createReturnProjectile(projectile, entity, b);
                                Game.removeEntity(projectile);
                                        });
                    }
                };

        new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }

    private void createReturnProjectile(Entity originProjectile, Entity entity, Entity hitEntity) {
        Point startPoint = getPositionOfEntity(originProjectile);
        Point endPoint = getStartPositionOfProjectile(originProjectile);

        Entity projectile = new Entity();
        new PositionComponent(projectile, startPoint);
        Animation animation = AnimationBuilder.buildAnimation(pathToTexturesOfProjectile);
        new AnimationComponent(projectile, animation);

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
                    if (b != entity && b != hitEntity) {
                        b.getComponent(HealthComponent.class)
                                .ifPresent(
                                        hc -> {
                                            ((HealthComponent) hc).receiveHit(projectileDamage);
                                            Game.removeEntity(projectile);
                                        });
                    }
                    if (b == entity) {
                        Game.removeEntity(projectile);
                        reduceCoolDown(entity);
                    }
                };
        new HitboxComponent(
                projectile, new Point(0.25f, 0.25f), projectileHitboxSize, collide, null);
    }

    private void reduceCoolDown(Entity entity) {
        SkillComponent sc =
                (SkillComponent)
                        entity.getComponent(SkillComponent.class)
                                .orElseThrow(
                                        () -> new MissingComponentException("SkillComponent"));
        sc.reduceCoolDown(BoomerangSkill.class, 0);

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

