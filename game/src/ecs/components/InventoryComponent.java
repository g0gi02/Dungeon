package ecs.components;

import ecs.entities.Entity;
import ecs.items.ItemData;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import logging.CustomLogLevel;
import ecs.components.ai.AITools;
import starter.Game;
import tools.Point;

/** Allows an Entity to carry Items */
public class InventoryComponent extends Component {

    private List<ItemData> inventory;
    private int maxSize;
    private transient Logger inventoryLogger;
    private boolean isOpen;

    /**
     * creates a new InventoryComponent
     *
     * @param entity the Entity where this Component should be added to
     * @param maxSize the maximal size of the inventory
     */
    public InventoryComponent(Entity entity, int maxSize) {
        super(entity);
        inventory = new ArrayList<>(maxSize);
        this.maxSize = maxSize;
        setupLogger();
    }

    /**
     * Adding an Element to the Inventory does not allow adding more items than the size of the
     * Inventory.
     *
     * @param itemData the item which should be added
     * @return true if the item was added, otherwise false
     */
    public boolean addItem(ItemData itemData) {
        if (inventory.size() >= maxSize) return false;
        inventoryLogger.log(
                CustomLogLevel.DEBUG,
                "Item '"
                        + this.getClass().getSimpleName()
                        + "' was added to the inventory of entity '"
                        + entity.getClass().getSimpleName()
                        + "'.");
        return inventory.add(itemData);
    }

    /**
     * removes the given Item from the inventory
     *
     * @param itemData the item which should be removed
     * @return true if the element was removed, otherwise false
     */
    public boolean removeItem(ItemData itemData) {
        inventoryLogger.log(
                CustomLogLevel.DEBUG,
                "Removing item '"
                        + this.getClass().getSimpleName()
                        + "' from inventory of entity '"
                        + entity.getClass().getSimpleName()
                        + "'.");
        return inventory.remove(itemData);
    }

    /**
     * Use the item at the given InventorySlot
     * @param slot
     * @return true if the item was used, otherwise false
     */
    public boolean useItem(int slot) {
        if(!isOpen) return false;
        isOpen = false;
        if (slot < 0 || slot >= inventory.size()) return false;
        inventory.get(slot).triggerUse(entity);
        return true;
    }

    /**
     * Drop the first item in the inventory
     * trigger the drop event of the item
     * @return true if the item was dropped, otherwise false
     */
    public void removeFirstItem() {
        if(inventory.size() <= 0 || !isOpen) return;
        isOpen = false;
        ItemData itemData = inventory.get(0);
        PositionComponent pc = (PositionComponent) entity.getComponent(PositionComponent.class).get();
        Point point = pc.getPosition();
        itemData.triggerDrop(entity, AITools.getRandomAccessibleTileCoordinateInRange(point, 1f).toPoint());
    }

    /**
     * Drop the last item in the inventory
     * trigger the drop event of the item
     * @return true if the item was dropped, otherwise false
     */
    public void removeLastItem() {
        if(inventory.size() <= 0 || !isOpen) return;
        isOpen = false;
        ItemData itemData = inventory.get(inventory.size()-1);
        PositionComponent pc = (PositionComponent) entity.getComponent(PositionComponent.class).get();
        Point point = pc.getPosition();
        itemData.triggerDrop(entity, AITools.getRandomAccessibleTileCoordinateInRange(point, 1f).toPoint());
    }

    /**
     * Set the inventory to open
     */
    public void setOpen() {
        isOpen = true;
        String inventoryString = "Inventory of entity '" + entity.getClass().getSimpleName() + "' size: "+ maxSize + ":\n";
        for (int i = 0; i < inventory.size(); i++) {
            ItemData itemData = inventory.get(i);
            inventoryString += i+1 + ": " + itemData.getItemName() + "\n";
        }
        inventoryLogger.info(inventoryString);
    }

    /**
     * @return the number of slots already filled with items
     */
    public int filledSlots() {
        return inventory.size();
    }

    /**
     * @return the number of slots still empty
     */
    public int emptySlots() {
        return maxSize - inventory.size();
    }

    /**
     * @return the size of the inventory
     */
    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        if (inventory.size() > maxSize) {
            // If the inventory is too big, drop the items that don't fit
            for(int i = maxSize; i < inventory.size(); i++)
                removeLastItem();
        }
    }

    /**
     * @return a copy of the inventory
     */
    public List<ItemData> getItems() {
        return new ArrayList<>(inventory);
    }

    /**
     * @param itemClassName the name of the item class
     * @return true if an item of the given type is in the inventory, otherwise false
     */
    public boolean hasItemOfType(String itemClassName) {
        for (ItemData itemData : inventory) {
            if (itemData.getItemName().equals(itemClassName)) return true;
        }
        return false;
    }

    /**
     * Set up the Logger for the InventoryComponent
     */
    @Override
    public void setupLogger() {
        inventoryLogger = Logger.getLogger(this.getClass().getName());
    }
}
