package ecs.entities;

import configuration.ItemConfig;
import ecs.components.*;
import ecs.items.ItemData;
import ecs.items.ItemDataGenerator;
import graphic.Animation;
import level.tools.LevelElement;
import starter.Game;
import tools.Point;

import java.security.Key;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import ecs.entities.MasterworkChest;

public class MasterworkChest extends Entity {

    public static final float defaultInteractionRadius = 1f;

    public static final List<String> DEFAULT_CLOSED_ANIMATION_FRAMES =
        List.of("objects/masterworkChest/masterworkchest_full_open_anim_f0.png");
    public static final List<String> DEFAULT_OPENING_ANIMATION_FRAMES =
        List.of(
            "objects/masterworkChest/masterworkchest_full_open_anim_f0.png",
            "objects/masterworkChest/masterworkchest_full_open_anim_f1.png",
            "objects/masterworkChest/masterworkchest_full_open_anim_f2.png",
            "objects/masterworkChest/masterworkchest_empty_open_anim_f2.png");


    /**
     * Creates a new Chest which drops the given items on interaction
     *
     * @param itemData which the chest is supposed to drop
     * @param position the position where the chest is placed
     */
    public MasterworkChest(List<ItemData> itemData, Point position) {
        new PositionComponent(this, position);
        InventoryComponent ic = new InventoryComponent(this, itemData.size());
        itemData.forEach(ic::addItem);
        new InteractionComponent(this, defaultInteractionRadius, true, this::dropItems);
        AnimationComponent ac =
            new AnimationComponent(
                this,
                new Animation(DEFAULT_CLOSED_ANIMATION_FRAMES, 20, false),
                new Animation(DEFAULT_OPENING_ANIMATION_FRAMES, 20, false));
    }


    /**
     * small Generator which uses the Item#ITEM_REGISTER
     *
     * @return a configured Chest
     */
    public static MasterworkChest createNewMasterworkChest() {
        Random random = new Random();
        ItemDataGenerator itemDataGenerator = new ItemDataGenerator();

        List<ItemData> itemData =
            IntStream.range(0, random.nextInt(1, 3))
                .mapToObj(i -> itemDataGenerator.generateItemData())
                .toList();
        return new MasterworkChest(
            itemData,
            Game.currentLevel.getRandomTile(LevelElement.FLOOR).getCoordinate().toPoint());
    }

    private void dropItems(Entity entity) {
        Hero hero = (Hero) Game.getHero().get();
        System.out.println(hero.canOpenChest);
        if (hero.canOpenChest) {
            InventoryComponent inventoryComponent =
                entity.getComponent(InventoryComponent.class)
                    .map(InventoryComponent.class::cast)
                    .orElseThrow(
                        () ->
                            createMissingComponentException(
                                InventoryComponent.class.getName(), entity));
            PositionComponent positionComponent =
                entity.getComponent(PositionComponent.class)
                    .map(PositionComponent.class::cast)
                    .orElseThrow(
                        () ->
                            createMissingComponentException(
                                PositionComponent.class.getName(), entity));
            List<ItemData> itemData = inventoryComponent.getItems();
            double count = itemData.size();

            IntStream.range(0, itemData.size())
                .forEach(
                    index ->
                        itemData.get(index)
                            .triggerDrop(
                                entity,
                                calculateDropPosition(
                                    positionComponent, index / count)));
            entity.getComponent(AnimationComponent.class)
                .map(AnimationComponent.class::cast)
                .ifPresent(x -> x.setCurrentAnimation(x.getIdleRight()));
            Entity entity1 = new Entity();

            hero.canOpenChest = false;
            ItemData itemData2 = hero.getComponent(InventoryComponent.class)
                .map(InventoryComponent.class::cast)
                .get().getItems().stream().filter(itemData1 -> itemData1.getItemName()
                    .equals(ItemConfig.KEY_ITEM_NAME.get())).findFirst().get();

            hero.getComponent(InventoryComponent.class).map(InventoryComponent.class::cast)
                .get().removeItem(itemData2);

        }
    }


    /**
     * small Helper to determine the Position of the dropped item simple circle drop
     *
     * @param positionComponent The PositionComponent of the Chest
     * @param radian            of the current Item
     * @return a Point in a unit Vector around the Chest
     */
    private static Point calculateDropPosition(PositionComponent positionComponent, double radian) {
        return new Point(
            (float) Math.cos(radian * Math.PI) + positionComponent.getPosition().x,
            (float) Math.sin(radian * Math.PI) + positionComponent.getPosition().y);
    }

    /**
     * Helper to create a MissingComponentException with a bit more information
     *
     * @param Component the name of the Component which is missing
     * @param e         the Entity which did miss the Component
     * @return the newly created Exception
     */
    private static MissingComponentException createMissingComponentException(
        String Component, Entity e) {
        return new MissingComponentException(
            Component
                + " missing in "
                + Chest.class.getName()
                + " in Entity "
                + e.getClass().getName());
    }
}
