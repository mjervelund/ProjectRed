package mrtjp.projectred.exploration.init;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

import static mrtjp.projectred.core.init.CoreTags.*;
import static mrtjp.projectred.exploration.ProjectRedExploration.MOD_ID;

/**
 * Armor materials. Since 1.21.2 these are plain records (no registry); the textures are equipment assets
 * ({@code assets/projectred_exploration/equipment/<id>.json}).
 */
public class ExplorationArmorMaterials {

    public static final String ID_RUBY_ARMOR_MATERIAL = "ruby";
    public static final String ID_SAPPHIRE_ARMOR_MATERIAL = "sapphire";
    public static final String ID_PERIDOT_ARMOR_MATERIAL = "peridot";

    // Durability per slot was base durability * 16 (same multiplier as vanilla diamond is 33)
    private static final int DURABILITY = 16;

    private static final Map<ArmorType, Integer> DEFENSE = Map.of(
            ArmorType.BOOTS, 3,
            ArmorType.LEGGINGS, 6,
            ArmorType.CHESTPLATE, 8,
            ArmorType.HELMET, 3,
            ArmorType.BODY, 11);

    public static final ArmorMaterial RUBY_ARMOR_MATERIAL = create(ID_RUBY_ARMOR_MATERIAL, 10, RUBY_GEM_TAG);
    public static final ArmorMaterial SAPPHIRE_ARMOR_MATERIAL = create(ID_SAPPHIRE_ARMOR_MATERIAL, 10, SAPPHIRE_GEM_TAG);
    public static final ArmorMaterial PERIDOT_ARMOR_MATERIAL = create(ID_PERIDOT_ARMOR_MATERIAL, 14, PERIDOT_GEM_TAG);

    private static ArmorMaterial create(String id, int enchantmentValue, TagKey<Item> repairIngredient) {
        ResourceKey<EquipmentAsset> asset = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(MOD_ID, id));
        return new ArmorMaterial(DURABILITY, DEFENSE, enchantmentValue, SoundEvents.ARMOR_EQUIP_DIAMOND, 1.25F, 0.0F, repairIngredient, asset);
    }
}
