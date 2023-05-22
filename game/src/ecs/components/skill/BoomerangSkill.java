package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import tools.Point;

public class BoomerangSkill extends ReturningProjectileSkill {
    public BoomerangSkill(ITargetSelection targetSelection) {
        super(
                "skills/boomerang/boomerang_Down/",
                0.3f,
                new Damage(1, DamageType.PHYSICAL, null),
                new Point(1, 1),
                targetSelection,
                5f);
    }
}
