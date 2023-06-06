package ecs.entities;

import ecs.items.IOnCollect;
import ecs.items.IOnDrop;
import ecs.items.IOnUse;
import ecs.items.ItemData;
import tools.Point;

/**
 * Abstract class for all Items
 * Items are entities in the ECS_System which can be collected by the player and used by him
 *
 * <p>Items are entities which can be collected by the player and used by him
 */
public abstract class Item extends Entity implements IOnCollect, IOnUse, IOnDrop {

  public Item() {
    super();
  }

  protected abstract void setupAnimationComponent();

  protected abstract void setupPositionComponent();

  protected abstract void setupHitBoxComponent();

  protected abstract void setupItemComponent();

  public abstract void onCollect(Entity WorldItemEntity, Entity whoCollides);

  public abstract void onUse(Entity e, ItemData item);

  public abstract void onDrop(Entity user, ItemData which, Point position);
}
