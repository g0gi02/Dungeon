package ecs.entities;

import configuration.ItemConfig;
import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.items.*;
import graphic.Animation;
import starter.Game;
import tools.Point;
import ecs.damage.Damage;
import ecs.damage.DamageType;
import java.util.Set;

import java.util.List;
import java.util.logging.Logger;

public class BombItem extends Item implements IOnUse, IOnCollect, IOnDrop {
    private Logger bombItemLogger = Logger.getLogger("BombItem");
    private ItemComponent itemComponent;
    private int damageAmmount = 10;
    private float range = 5;

    public BombItem() {
        super();
        setupItemComponent();
        setupAnimationComponent();
        setupPositionComponent();
        setupHitBoxComponent();
        bombItemLogger.info("BombItem created");
    }

    public BombItem(ItemData itemData, Point point) {
        super();
        new ItemComponent(this, itemData);
        new PositionComponent(this, point);
        setupHitBoxComponent();
        setupAnimationComponent();
        bombItemLogger.info("BombItem created");
    }

    @Override
    protected void setupAnimationComponent() {
        Animation idle = AnimationBuilder.buildAnimation(ItemConfig.BOMB_ITEM_TEXTURE.get());
        new AnimationComponent(this, idle);
    }

    @Override
    protected void setupPositionComponent() {
        new PositionComponent(this);
    }

    @Override
    protected void setupHitBoxComponent() {
        new HitboxComponent(
                this,
                (you, other, direction) -> onCollect(this, other),
                (you, other, direction) -> {
                });
    }

    @Override
    protected void setupItemComponent() {
        ItemData itemData = new ItemData(
                ItemConfig.BOMB_ITEM_TYPE.get(),
                new Animation(List.of(ItemConfig.BOMB_ITEM_TEXTURE.get()), 1),
                new Animation(List.of(ItemConfig.BOMB_ITEM_TEXTURE.get()), 1),
                ItemConfig.BOMB_ITEM_NAME.get(),
                ItemConfig.BOMB_ITEM_DESCRIPTION.get());

        itemData.setOnCollect(this::onCollect);
        itemData.setOnUse(this::onUse);
        itemData.setOnDrop(this::onDrop);

        this.itemComponent = new ItemComponent(this, itemData);
    }

    /**
     * This methode is used to collect the item
     * @param WorldItemEntity is the item, that will be collected
     * @param whoCollides that collects the item
     */
    @Override
    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {
        bombItemLogger.info(WorldItemEntity.toString() + " collected by " + whoCollides.toString());
        if (!Game.getHero().isPresent())
            return;
        if (!whoCollides.equals(Game.getHero().get()))
            return;
        if (!whoCollides.getComponent(InventoryComponent.class).isPresent())
            return;
        InventoryComponent ic = (InventoryComponent) whoCollides.getComponent(InventoryComponent.class).get();
        if (ic.addItem(
                WorldItemEntity.getComponent(ItemComponent.class)
                        .map(ItemComponent.class::cast)
                        .get()
                        .getItemData()))
            Game.removeEntity(WorldItemEntity);
    }

    /**
     * Uses the item and removes
     * the item from the
     * inventory.
     *
     * @param e Entity that uses the item
     * @param item Item that is used
     */
    @Override
    public void onUse(Entity e, ItemData item) {
        bombItemLogger.info(e.toString() + " used " + item.getItemName());
        if (!e.getComponent(InventoryComponent.class).isPresent())
            return;
        InventoryComponent ic = (InventoryComponent) e.getComponent(InventoryComponent.class).get();
        List<ItemData> itemData = ic.getItems();
        for (ItemData id : itemData) {
            if (!id.getItemType().equals(ItemType.Passive)) {
                if (!id.equals(item))
                    continue;
                ic.removeItem(item);
                explode(e);
                break;
            }
        }
    }

    /**
     * This methode is used to drop an item.
     * @param user the entity, that drops the item
     * @param which item that is dropped
     * @param position where the item will be dropped
     */
    @Override
    public void onDrop(Entity user, ItemData which, Point position) {
        bombItemLogger.info(user.toString() + " dropped " + which.getItemName() + " at " + position.toString());
        Game.addEntity(new BombItem(which, position));
        if (!user.getComponent(InventoryComponent.class).isPresent())
            return;
        user.getComponent(InventoryComponent.class)
                .map(InventoryComponent.class::cast)
                .get()
                .removeItem(which);
    }

    /**
     * This Methode is executing the ability of the item
     * @param entity
     */
    private void explode(Entity entity) {
        bombItemLogger.info(entity.toString() + " used " + itemComponent.getItemData().getItemName());
        Set<Entity> entities = Game.getEntities();

        for (Entity e : entities) {
            if (!e.getComponent(PositionComponent.class).isPresent())
                continue;
            // PositionComponent of the entity that uses the item and the entity that is hit
            PositionComponent pcu = (PositionComponent) entity.getComponent(PositionComponent.class).get();
            PositionComponent pce = (PositionComponent) e.getComponent(PositionComponent.class).get();
            if (Point.calculateDistance(pcu.getPosition(), pce.getPosition()) < range) {
                if (!e.getComponent(HealthComponent.class).isPresent())
                    continue;
                HealthComponent hc = (HealthComponent) e.getComponent(HealthComponent.class).get();
                Damage damage = new Damage(damageAmmount, DamageType.PHYSICAL, entity);
                hc.receiveHit(damage);
            }
        }
    }
    
}
