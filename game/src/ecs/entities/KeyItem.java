package ecs.entities;

import configuration.ItemConfig;
import dslToGame.AnimationBuilder;
import ecs.components.*;
import ecs.items.IOnCollect;
import ecs.items.IOnDrop;
import ecs.items.IOnUse;
import ecs.items.ItemData;
import graphic.Animation;
import starter.Game;
import tools.Point;

import java.util.List;
import java.util.logging.Logger;

public class KeyItem extends Item implements IOnUse, IOnCollect, IOnDrop {

    private transient Logger KeyItemLogger;
    private ItemComponent itemComponent;

    /**
     * creates a new KeyItem
     */
    public KeyItem() {
        super();
        setupItemComponent();
        setupAnimationComponent();
        setupPositionComponent();
        setupHitBoxComponent();
        KeyItemLogger.info("KeyItem created");
    }

    /**
     * creates a new KeyItem with a given position and itemData
     * @param itemData
     * @param point
     */
    public KeyItem(ItemData itemData, Point point) {
        super();
        setupLogger();
        new ItemComponent(this, itemData);
        new PositionComponent(this, point);
        setupHitBoxComponent();
        setupAnimationComponent();
        KeyItemLogger.info("KeyItem created");
    }

    @Override
    protected void setupAnimationComponent() {
        Animation idle = AnimationBuilder.buildAnimation(ItemConfig.KEY_ITEM_TEXTURE.get());
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
            ItemConfig.KEY_ITEM_TYPE.get(),
            new Animation(List.of(ItemConfig.KEY_ITEM_TEXTURE.get()), 1),
            new Animation(List.of(ItemConfig.KEY_ITEM_TEXTURE.get()), 1),
            ItemConfig.KEY_ITEM_NAME.get(),
            ItemConfig.KEY_ITEM_DESCRIPTION.get());

        itemData.setOnCollect(this::onCollect);
        itemData.setOnUse(this::onUse);
        itemData.setOnDrop(this::onDrop);

        this.itemComponent = new ItemComponent(this, itemData);
    }


    /**
     * This methode is used to collect the item
     *
     * @param WorldItemEntity is the item, that will be collected
     * @param whoCollides     that collects the item
     */
    @Override
    public void onCollect(Entity WorldItemEntity, Entity whoCollides) {
        if (KeyItemLogger == null) setupLogger();
        KeyItemLogger.info(WorldItemEntity.toString() + " collected by " + whoCollides.toString());
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
        unlockChest(whoCollides);

    }



    /**
     * Uses the item and removes
     * the item from the
     * inventory.
     *
     * @param e    Entity that uses the item
     * @param item Item that is used
     */
    @Override
    public void onUse(Entity e, ItemData item) {
        if (KeyItemLogger == null) setupLogger();
        KeyItemLogger.info(e.toString() + " used " + item.getItemName());
    }


    /**
     * This methode is used to drop an item.
     *
     * @param user     the entity, that drops the item
     * @param which    item that is dropped
     * @param position where the item will be dropped
     */
    @Override
    public void onDrop(Entity user, ItemData which, Point position) {
        if (KeyItemLogger == null) setupLogger();
        KeyItemLogger.info(user.toString() + " dropped " + which.getItemName() + " at " + position.toString());
        Game.addEntity(new KeyItem(which, position));
        if (!user.getComponent(InventoryComponent.class).isPresent())
            return;
        user.getComponent(InventoryComponent.class)
            .map(InventoryComponent.class::cast)
            .get()
            .removeItem(which);
        if(user instanceof Hero)
        ((Hero) user).canOpenChest = false;
    }

    /**
     * This Methode is executing the ability of the item
     * @param entity
     */
    private void unlockChest(Entity entity) {
        if (KeyItemLogger == null) setupLogger();
        KeyItemLogger.info(entity.toString() + " can unlock the Masterworkchest now!");
        Hero hero = (Hero) entity;
        hero.canOpenChest= true;

    }

    /**
     * This methode is used to get the item data
     * @return the item data
     */
    public ItemData getItemData() {
        return itemComponent.getItemData();
    }

    /**
     * This methode is used to get the item config data
     * @return the item config data
     */
    public static ItemData getItemConfigData() {
        return new KeyItem(1).getItemData();
    }

    /**
     * This methode is used to get the item data
     * @return the item data
     */
    public KeyItem(int i) {
        setupItemComponent();
    }

    /**
     * This method sets up the logger
     */
    public void setupLogger() {
        KeyItemLogger = Logger.getLogger("SwordItem");
    }


}
