package ecs.items;

import ecs.entities.Entity;

import java.io.Serializable;

public interface IOnCollect extends Serializable {
    void onCollect(Entity WorldItemEntity, Entity whoCollides);
}
