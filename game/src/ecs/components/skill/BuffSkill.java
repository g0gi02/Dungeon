package ecs.components.skill;

import ecs.entities.Entity;

import java.io.Serializable;

public abstract class BuffSkill implements ISkillFunction, Serializable {

    @Override
    public void execute(Entity entity) {}
}
