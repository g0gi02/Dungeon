package ecs.items;

import ecs.entities.Entity;
import tools.Point;

import java.io.Serializable;

public interface IOnDrop extends Serializable {
    void onDrop(Entity user, ItemData which, Point position);
}
