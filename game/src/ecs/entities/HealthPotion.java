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

public class HealthPotion extends Item implements IOnUse, IOnCollect, IOnDrop {
    private transient Logger healthPotionLogger;
    private ItemComponent itemComponent;

    /**
     * creates a new HealthPotion
     */
    public HealthPotion() {
        super();
        setupLogger();
        setupItemComponent();
        setupAnimationComponent();
        setupPositionComponent();
        setupHitBoxComponent();
        healthPotionLogger.info("HealthPotion created");
    }

    /**
     * creates a new HealthPotion with a given position and itemData
     * @param itemData
     * @param point
     */
    public HealthPotion(ItemData itemData, Point point) {
        super();
        setupLogger();
        new ItemComponent(this, itemData);
        new PositionComponent(this, point);
        setupHitBoxComponent();
        setupAnimationComponent();
        healthPotionLogger.info("HealthPotion created");
    }

    @Override
    protected void setupAnimationComponent() {
        Animation idle = AnimationBuilder.buildAnimation(ItemConfig.HEALTH_POTION_SMALL_TEXTURE.get());
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
                ItemConfig.HEALTH_POTION_SMALL_TYPE.get(),
                new Animation(List.of(ItemConfig.HEALTH_POTION_SMALL_TEXTURE.get()), 1),
                new Animation(List.of(ItemConfig.HEALTH_POTION_SMALL_TEXTURE.get()), 1),
                ItemConfig.Health_POTION_SMALL_NAME.get(),
                ItemConfig.HEALTH_POTION_SMALL_DESCRIPTION.get());

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
        if (healthPotionLogger == null) setupLogger();
        healthPotionLogger.info(WorldItemEntity.toString() + " collected by " + whoCollides.toString());
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
        if (healthPotionLogger == null) setupLogger();
        healthPotionLogger.info(e.toString() + " used " + item.getItemName());
        if (!e.getComponent(InventoryComponent.class).isPresent())
            return;
        InventoryComponent ic = (InventoryComponent) e.getComponent(InventoryComponent.class).get();
        List<ItemData> itemData = ic.getItems();
        for (ItemData id : itemData) {
            if (!id.getItemType().equals(ItemType.Passive)) {
                if (!id.equals(item))
                    continue;
                ic.removeItem(item);
                heal(e);
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
        if (healthPotionLogger == null) setupLogger();
        healthPotionLogger.info(user.toString() + " dropped " + which.getItemName() + " at " + position.toString());
        Game.addEntity(new HealthPotion(which, position));
        if (!user.getComponent(InventoryComponent.class).isPresent())
            return;
        user.getComponent(InventoryComponent.class)
                .map(InventoryComponent.class::cast)
                .get()
                .removeItem(which);
    }

    /**
     * This Methode is executing the ability of the item
     * The potion heals the entity to full health
     * @param entity
     */
    private void heal(Entity entity) {
        if (healthPotionLogger == null) setupLogger();
        healthPotionLogger.info(entity.toString() + " healed by " + itemComponent.getItemData().getItemName());
        if (!entity.getComponent(HealthComponent.class).isPresent())
            return;
        HealthComponent hc = (HealthComponent) entity.getComponent(HealthComponent.class).get();
        hc.setCurrentHealthpoints(hc.getMaximalHealthpoints());
    }

    @Override
    public void setupLogger() {
        healthPotionLogger = Logger.getLogger("HealthPotion");
    }

    public ItemData getItemData() {
        return itemComponent.getItemData();
    }

    public static ItemData getItemConfigData() {
        return new HealthPotion(1).getItemData();
    }

    public HealthPotion(int i) {
        setupItemComponent();
    }
}
