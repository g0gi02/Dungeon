package ecs.entities;

import ecs.components.*;
import ecs.items.ItemData;
import ecs.items.ItemDataGenerator;
import graphic.Animation;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import level.tools.LevelElement;
import starter.Game;
import tools.Point;

public class MimicMonsterChest extends Entity {
    //TODO: Variablennamen anpassen
    public static final float defaultInteractionRadius = 5f;
    public static final List<String> DEFAULT_CLOSED_ANIMATION_FRAMES =
            List.of("objects/mimicchest/mimic_chest_full_open_anim_f0.png");
    public static final List<String> DEFAULT_OPENING_ANIMATION_FRAMES =
            List.of(
                    "objects/mimicchest/mimic_chest_full_open_anim_f0.png",
                    "objects/mimicchest/mimic_chest_full_open_anim_f1.png",
                    "objects/mimicchest/mimic_chest_full_open_anim_f2.png");

    /**
     * Create an Instance of MimicMonsterChest at a random position
     *
     * @return a configured MimicMonsterChest
     */
    public static MimicMonsterChest createNewMimicMonsterChest() {
        Random random = new Random();
        ItemDataGenerator itemDataGenerator = new ItemDataGenerator();

        List<ItemData> itemData =
                IntStream.range(0, random.nextInt(1, 3))
                        .mapToObj(i -> itemDataGenerator.generateItemData(ItemDataGenerator.ItemPool.SPECIAL))
                        .toList();
        return new MimicMonsterChest(
                itemData,
                Game.currentLevel.getRandomTile(LevelElement.FLOOR).getCoordinate().toPoint());
    }

    /**
     * Create an Instance of MimicMonsterChest at the given position
     * With random items
     * @param position
     * @return
     */
    public static MimicMonsterChest createNewMimicMonsterChest(Point position) {
        Random random = new Random();
        ItemDataGenerator itemDataGenerator = new ItemDataGenerator();

        List<ItemData> itemData =
                IntStream.range(0, random.nextInt(1, 3))
                        .mapToObj(i -> itemDataGenerator.generateItemData(ItemDataGenerator.ItemPool.SPECIAL))
                        .toList();
        return new MimicMonsterChest(itemData, position);
    }

    /**
     * Creates a new MimicMonsterChest which drops the given items on interaction
     *
     * @param itemData which the chest is supposed to drop
     * @param position the position where the chest is placed
     */
    public MimicMonsterChest(List<ItemData> itemData, Point position) {
        new PositionComponent(this, position);
        InventoryComponent ic = new InventoryComponent(this, itemData.size());
        itemData.forEach(ic::addItem);
        new InteractionComponent(this, defaultInteractionRadius, false, this::dropItems);
        AnimationComponent ac =
                new AnimationComponent(
                        this,
                        new Animation(DEFAULT_CLOSED_ANIMATION_FRAMES, 2, false),
                        new Animation(DEFAULT_OPENING_ANIMATION_FRAMES, 2, false));
    }

    private void dropItems(Entity entity) {
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

        entity.getComponent(AnimationComponent.class)
                .map(AnimationComponent.class::cast)
                .ifPresent(x -> x.setCurrentAnimation(x.getIdleRight()));

        IntStream.range(0, itemData.size())
                .forEach(
                        index ->
                                itemData.get(index)
                                        .triggerDrop(
                                                entity,
                                                calculateDropPosition(
                                                        positionComponent, index / count)));
    }

    /**
     * small Helper to determine the Position of the dropped item simple circle drop
     *
     * @param positionComponent The PositionComponent of the MimicMonsterChest
     * @param radian of the current Item
     * @return a Point in a unit Vector around the MimicMonsterChest
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
     * @param e the Entity which did miss the Component
     * @return the newly created Exception
     */
    private static MissingComponentException createMissingComponentException(
            String Component, Entity e) {
        return new MissingComponentException(
                Component
                        + " missing in "
                        + MimicMonsterChest.class.getName()
                        + " in Entity "
                        + e.getClass().getName());
    }
}

