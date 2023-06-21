package ecs.items;

import graphic.Animation;
import java.util.List;
import java.util.Random;
import configuration.ItemConfig;
import ecs.entities.BombItem;
import ecs.entities.HealthPotion;
import ecs.entities.SwordItem;
import ecs.entities.KeyItem;

/** Generator which creates a random ItemData based on the Templates prepared. */
public class ItemDataGenerator {
    private static final List<String> missingTexture = List.of("animation/missingTexture.png");
    public enum ItemPool {
        COMMON, SPECIAL;
    }

    private List<ItemData> templates =
            List.of(
                    BombItem.getItemConfigData(),
                    
                    HealthPotion.getItemConfigData(),
                    
                    SwordItem.getItemConfigData());
    private List<ItemData> specialTemplates =
            List.of(
                    SwordItem.getItemConfigData(),

                    KeyItem.getItemConfigData());

                            
    private Random rand = new Random();

    /**
     * @return a new randomItemData
     */
    public ItemData generateItemData() {
        return templates.get(rand.nextInt(templates.size()));
    }

    /**
     * @param itemPool
     * @return a new randomItemData from the given pool
     */
    public ItemData generateItemData(ItemPool itemPool) {
        switch (itemPool) {
            case COMMON:
                return templates.get(rand.nextInt(templates.size()));
            case SPECIAL:
                return specialTemplates.get(rand.nextInt(specialTemplates.size()));
        }
        return new ItemData();
    }
}
