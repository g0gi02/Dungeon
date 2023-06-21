package configuration;

import configuration.values.ConfigEnumValue;
import configuration.values.ConfigStringValue;
import ecs.items.ItemType;

/** The default ItemData values */
@ConfigMap(path = {"item"})
public class ItemConfig {
    /** The Description of the Default ItemData */
    public static final ConfigKey<String> DESCRIPTION =
            new ConfigKey<>(new String[] {"description"}, new ConfigStringValue("Default Item"));

    /** The Name of the Default ItemData */
    public static final ConfigKey<String> NAME =
            new ConfigKey<>(new String[] {"name"}, new ConfigStringValue("Defaultname"));

    /** The Type of the Default ItemData */
    public static final ConfigKey<ItemType> TYPE =
            new ConfigKey<>(new String[] {"type"}, new ConfigEnumValue<>(ItemType.Basic));

    /** The texturepath of the Default ItemData will be used for world and Inventory */
    public static final ConfigKey<String> TEXTURE =
            new ConfigKey<>(
                    new String[] {"texture"},
                    new ConfigStringValue("animation/missingTexture.png"));

    /** Healthpotion  */
    /** Healthpotion small */
    public static final ConfigKey<String> HEALTH_POTION_SMALL_DESCRIPTION =
            new ConfigKey<>(
                    new String[] {"healthPotionSmallDescription"},
                    new ConfigStringValue("A small Health Potion"));

    public static final ConfigKey<String> Health_POTION_SMALL_NAME =
            new ConfigKey<>(
                    new String[] {"healthPotionSmallName"},
                    new ConfigStringValue("Health Potion Small"));

    public static final ConfigKey<ItemType> HEALTH_POTION_SMALL_TYPE =
            new ConfigKey<>(
                    new String[] {"healthPotionSmallType"},
                    new ConfigEnumValue<>(ItemType.Active));

    public static final ConfigKey<String> HEALTH_POTION_SMALL_TEXTURE =
            new ConfigKey<>(
                    new String[] {"healthPotionSmallTexture"},
                    // new ConfigStringValue("objects/healthPotion/small/HealthPotionSmall.png"));
                    new ConfigStringValue("objects/healthPotion/small/potionssmall2.png"));

    /** Sword Item */
    public static final ConfigKey<String> SWORD_ITEM_DESCRIPTION =
            new ConfigKey<>(
                    new String[] {"swordDescription"},
                    new ConfigStringValue("A sword to fight with"));

    public static final ConfigKey<String> SWORD_ITEM_NAME =
            new ConfigKey<>(
                new String[] {"swordName"},
                new ConfigStringValue("Sword"));

    public static final ConfigKey<ItemType> SWORD_ITEM_TYPE =
            new ConfigKey<>(
                    new String[] {"swordType"},
                    new ConfigEnumValue<>(ItemType.Passive));

    public static final ConfigKey<String> SWORD_ITEM_TEXTURE =
            new ConfigKey<>(
                    new String[] {"swordTexture"},
                    new ConfigStringValue("skills/melee/SwordSlash_0.png"));


    /** Key Item */
    public static final ConfigKey<String> KEY_ITEM_DESCRIPTION =
        new ConfigKey<>(
            new String[] {"keyDescription"},
            new ConfigStringValue("A key to open the masterworkChest"));

    public static final ConfigKey<String> KEY_ITEM_NAME =
        new ConfigKey<>(
            new String[] {"keyName"},
            new ConfigStringValue("Key"));

    public static final ConfigKey<ItemType> KEY_ITEM_TYPE =
        new ConfigKey<>(
            new String[] {"keyType"},
            new ConfigEnumValue<>(ItemType.Passive));

    public static final ConfigKey<String> KEY_ITEM_TEXTURE =
        new ConfigKey<>(
            new String[] {"keyTexture"},
            new ConfigStringValue("objects/key/silver_key.png"));




    /** Bomb Item */
    public static final ConfigKey<String> BOMB_ITEM_DESCRIPTION =
            new ConfigKey<>(
                    new String[] {"bombDescription"},
                    new ConfigStringValue("A bomb to blow up stuff"));
    public static final ConfigKey<String> BOMB_ITEM_NAME =
            new ConfigKey<>(
                    new String[] {"bombName"},
                    new ConfigStringValue("Bomb"));
    public static final ConfigKey<ItemType> BOMB_ITEM_TYPE =
            new ConfigKey<>(
                    new String[] {"bombType"},
                    new ConfigEnumValue<>(ItemType.Active));
    public static final ConfigKey<String> BOMB_ITEM_TEXTURE =
            new ConfigKey<>(
                    new String[] {"bombTexture"},
                    new ConfigStringValue("objects/bomb/Bomb.png"));

    /** Backpack Item */
    public static final ConfigKey<String> BACKPACK_ITEM_DESCRIPTION =
            new ConfigKey<>(
                    new String[] {"backpackDescription"},
                    new ConfigStringValue("A bagpack to store stuff"));
    public static final ConfigKey<String> BACKPACK_ITEM_NAME =
            new ConfigKey<>(
                    new String[] {"backpackName"},
                    new ConfigStringValue("Backpack"));
    public static final ConfigKey<ItemType> BACKPACK_ITEM_TYPE =
            new ConfigKey<>(
                    new String[] {"backpackType"},
                    new ConfigEnumValue<>(ItemType.Passive));
    public static final ConfigKey<String> BACKPACK_ITEM_TEXTURE =
            new ConfigKey<>(
                    new String[] {"backpackTexture"},
                    new ConfigStringValue("objects/Backpack/Backpack.png"));


}
