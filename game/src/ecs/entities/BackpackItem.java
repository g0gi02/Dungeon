package ecs.entities;

import configuration.ItemConfig;
import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.items.*;
import graphic.Animation;
import starter.Game;
import tools.Point;

import java.util.List;
import java.util.logging.Logger;

public class BackpackItem extends Item implements IOnUse, IOnCollect, IOnDrop {
    private transient Logger backpackItemLogger;
    private ItemComponent itemComponent;
    private int size = 3;

    /**
     * creates a new BackpackItem
     */
    public BackpackItem() {
        super();
        setupLogger();
        setupItemComponent();
        setupAnimationComponent();
        setupPositionComponent();
        setupHitBoxComponent();
        backpackItemLogger.info("BackpackItem created");
    }

    /**
     * creates a new BackpackItem with a given position and itemData
     * @param itemData
     * @param point
     */
    public BackpackItem(ItemData itemData, Point point) {
        super();
        setupLogger();
        new ItemComponent(this, itemData);
        new PositionComponent(this, point);
        setupHitBoxComponent();
        setupAnimationComponent();
        backpackItemLogger.info("BackpackItem created");
    }

    @Override
    protected void setupAnimationComponent() {
        Animation idle = AnimationBuilder.buildAnimation(ItemConfig.BACKPACK_ITEM_TEXTURE.get());
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

    /**
     * Sets up the ItemComponent
     * and configures the ItemData
     */
    @Override
    protected void setupItemComponent() {
        ItemData itemData = new ItemData(
                ItemConfig.BACKPACK_ITEM_TYPE.get(),
                new Animation(List.of(ItemConfig.BACKPACK_ITEM_TEXTURE.get()), 1),
                new Animation(List.of(ItemConfig.BACKPACK_ITEM_TEXTURE.get()), 1),
                ItemConfig.BACKPACK_ITEM_NAME.get(),
                ItemConfig.BACKPACK_ITEM_DESCRIPTION.get());

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
        if (backpackItemLogger == null) setupLogger();
        backpackItemLogger.info(WorldItemEntity.toString() + " collected by " + whoCollides.toString());
        if (!Game.getHero().isPresent())
            return;
        if (!whoCollides.equals(Game.getHero().get()))
            return;
        if (!whoCollides.getComponent(InventoryComponent.class).isPresent())
            return;
        InventoryComponent ic = (InventoryComponent) whoCollides.getComponent(InventoryComponent.class).get();
        if (ic.getMaxSize() >= 10) {
            backpackItemLogger.info("Inventory reached max size");
            return;
        }
        if (ic.addItem(
                WorldItemEntity.getComponent(ItemComponent.class)
                        .map(ItemComponent.class::cast)
                        .get()
                        .getItemData())) {
            Game.removeEntity(WorldItemEntity);
            increaseInventorySize(whoCollides);
        }
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
        if (backpackItemLogger == null) setupLogger();
        backpackItemLogger.info(e.toString() + " used " + item.getItemName());
    }

    /**
     * This methode is used to drop an item.
     * @param user the entity, that drops the item
     * @param which item that is dropped
     * @param position where the item will be dropped
     */
    @Override
    public void onDrop(Entity user, ItemData which, Point position) {
        if (backpackItemLogger == null) setupLogger();
        backpackItemLogger.info(user.toString() + " dropped " + which.getItemName() + " at " + position.toString());
        Game.addEntity(new BackpackItem(which, position));
        if (!user.getComponent(InventoryComponent.class).isPresent())
            return;
        user.getComponent(InventoryComponent.class)
                .map(InventoryComponent.class::cast)
                .get()
                .removeItem(which);
        reduceInventorySize(user);
    }

    /**
     * This Methode is executing the ability of the item
     * the backpack increases the inventory size by 3
     * @param entity
     */
    private void increaseInventorySize(Entity entity) {
        if (!entity.getComponent(InventoryComponent.class).isPresent())
            return;
        InventoryComponent ic = (InventoryComponent) entity.getComponent(InventoryComponent.class).get();
        ic.setMaxSize(ic.getMaxSize() + size);
    }

    /**
     * This Methode reduces the inventory size by the size of the backpack
     * @param entity
     */
    private void reduceInventorySize(Entity entity) {
        System.out.println("reduceInventorySize");
        if (!entity.getComponent(InventoryComponent.class).isPresent())
            return;
        InventoryComponent ic = (InventoryComponent) entity.getComponent(InventoryComponent.class).get();
        ic.setMaxSize(ic.getMaxSize() - size);
    }

    @Override
    public void setupLogger() {
        backpackItemLogger = Logger.getLogger("BackpackItem");
    }
}
