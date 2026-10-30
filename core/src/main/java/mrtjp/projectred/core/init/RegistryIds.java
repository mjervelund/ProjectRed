package mrtjp.projectred.core.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Since 1.21.2 blocks and items need their registry id in their {@code Properties} when they are constructed.
 * Project Red's blocks and items build their own properties, so registration goes through {@link #block}/{@link #item},
 * which make the id being registered available to {@link #blockProps()}/{@link #itemProps()} while the supplier runs.
 */
public final class RegistryIds {

    private static final ThreadLocal<@Nullable ResourceKey<Block>> BLOCK = new ThreadLocal<>();
    private static final ThreadLocal<@Nullable ResourceKey<Item>> ITEM = new ThreadLocal<>();

    private RegistryIds() {
    }

    /** For {@code DeferredRegister#register(String, Function)}: constructs the block with its id available. */
    public static <T extends Block> Function<Identifier, T> block(Supplier<T> factory) {
        return id -> {
            var prev = BLOCK.get();
            BLOCK.set(ResourceKey.create(Registries.BLOCK, id));
            try {
                return factory.get();
            } finally {
                BLOCK.set(prev);
            }
        };
    }

    /** For {@code DeferredRegister#register(String, Function)}: constructs the item with its id available. */
    public static <T extends Item> Function<Identifier, T> item(Supplier<T> factory) {
        return id -> {
            var prev = ITEM.get();
            ITEM.set(ResourceKey.create(Registries.ITEM, id));
            try {
                return factory.get();
            } finally {
                ITEM.set(prev);
            }
        };
    }

    public static ResourceKey<Block> currentBlock() {
        var key = BLOCK.get();
        if (key == null) throw new IllegalStateException("Block properties created outside RegistryIds.block(...)");
        return key;
    }

    public static ResourceKey<Item> currentItem() {
        var key = ITEM.get();
        if (key == null) throw new IllegalStateException("Item properties created outside RegistryIds.item(...)");
        return key;
    }

    /** {@code BlockBehaviour.Properties.of()} with the id of the block being registered. */
    public static BlockBehaviour.Properties blockProps() {
        return BlockBehaviour.Properties.of().setId(currentBlock());
    }

    /** {@code new Item.Properties()} with the id of the item being registered. */
    public static Item.Properties itemProps() {
        return new Item.Properties().setId(currentItem());
    }

    /** Item properties for a {@code BlockItem} (block translation key, like vanilla's block items). */
    public static Item.Properties blockItemProps() {
        return itemProps().useBlockDescriptionPrefix();
    }
}
