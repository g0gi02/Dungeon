package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import tools.Point;

public class BouncingBallSkill extends BouncingProjectileSkill {
    public BouncingBallSkill(ITargetSelection targetSelection) {
        super(
                "skills/bouncingBall/bouncingBall_Down/",
                0.3f,
                new Damage(1, DamageType.PHYSICAL, null),
                new Point(1, 1),
                targetSelection,
                15f,
                5);
    }
}