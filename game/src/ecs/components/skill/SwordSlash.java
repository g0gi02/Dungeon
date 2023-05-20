package ecs.components.skill;

import ecs.damage.Damage;
import ecs.damage.DamageType;
import tools.Point;

public class SwordSlash extends DamageProjectileSkill {
    public SwordSlash(ITargetSelection targetSelection, float knockback) {
        super(
            "skills/melee/",
            1f,
            new Damage(1, DamageType.PHYSICAL, null),
            new Point(10, 10),
            targetSelection,
            0.5f,
            knockback);
    }
}
