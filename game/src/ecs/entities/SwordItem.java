// pc.getMeleeSkill().ifPresent(skill -> skill.execute(ksd.e));

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

public class SwordItem extends Item implements IOnUse, IOnCollect, IOnDrop {
    private transient Logger SwordItemLogger;
    private ItemComponent itemComponent;

    public SwordItem() {
        super();
        setupLogger();
        setupItemComponent();
        setupAnimationComponent();
        setupPositionComponent();
        setupHitBoxComponent();
        SwordItemLogger.info("SwordItem created");
    }

    public SwordItem(ItemData itemData, Point point) {
        super();
        setupLogger();
        new ItemComponent(this, itemData);
        new PositionComponent(this, point);
        setupHitBoxComponent();
        setupAnimationComponent();
        SwordItemLogger.info("SwordItem created");
    }

    @Override
    protected void setupAnimationComponent() {
        Animation idle = AnimationBuilder.buildAnimation(ItemConfig.SWORD_ITEM_TEXTURE.get());
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
                ItemConfig.SWORD_ITEM_TYPE.get(),
                new Animation(List.of(ItemConfig.SWORD_ITEM_TEXTURE.get()), 1),
                new Animation(List.of(ItemConfig.SWORD_ITEM_TEXTURE.get()), 1),
                ItemConfig.SWORD_ITEM_NAME.get(),
                ItemConfig.SWORD_ITEM_DESCRIPTION.get());

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
        if (SwordItemLogger == null) setupLogger();
        SwordItemLogger.info(WorldItemEntity.toString() + " collected by " + whoCollides.toString());
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
        unlockSkill(whoCollides);
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
        if (SwordItemLogger == null) setupLogger();
        SwordItemLogger.info(e.toString() + " used " + item.getItemName());
    }

    /**
     * This methode is used to drop an item.
     * @param user the entity, that drops the item
     * @param which item that is dropped
     * @param position where the item will be dropped
     */
    @Override
    public void onDrop(Entity user, ItemData which, Point position) {
        if (SwordItemLogger == null) setupLogger();
        SwordItemLogger.info(user.toString() + " dropped " + which.getItemName() + " at " + position.toString());
        Game.addEntity(new SwordItem(which, position));
        if (!user.getComponent(InventoryComponent.class).isPresent())
            return;
        user.getComponent(InventoryComponent.class)
                .map(InventoryComponent.class::cast)
                .get()
                .removeItem(which);
        if(user instanceof Hero)
        ((Hero) user).lockMeleeSkill();
    }

    /**
     * This Methode is executing the ability of the item
     * @param entity
     */
    private void unlockSkill(Entity entity) {
        if (SwordItemLogger == null) setupLogger();
        SwordItemLogger.info(entity.toString() + " unlocked the Swordskill");
        Hero hero = (Hero) entity;
        hero.unlockMeleeSkill();

    }

    @Override
    public void setupLogger() {
        SwordItemLogger = Logger.getLogger("SwordItem");
    }

    public ItemData getItemData() {
        return itemComponent.getItemData();
    }

    public static ItemData getItemConfigData() {
        return new SwordItem(1).getItemData();
    }

    public SwordItem(int i) {
        setupItemComponent();
    }
}
