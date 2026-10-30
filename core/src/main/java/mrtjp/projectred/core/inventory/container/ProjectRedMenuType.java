package mrtjp.projectred.core.inventory.container;

import codechicken.lib.data.MCDataByteBuf;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Menu factory reading extra open data as {@link MCDataInput}. CodeChicken Lib's {@code ProjectRedMenuType}, which
 * Project Red used, was removed in CCL's 1.21.11 port; this is the same API.
 */
public interface ProjectRedMenuType<T extends AbstractContainerMenu> extends IContainerFactory<T> {

    static <T extends AbstractContainerMenu> MenuType<T> create(ProjectRedMenuType<T> factory) {
        return new MenuType<>(factory, FeatureFlags.VANILLA_SET);
    }

    static void openMenu(ServerPlayer player, MenuProvider provider) {
        openMenu(player, provider, null);
    }

    static void openMenu(ServerPlayer player, MenuProvider provider, @Nullable Consumer<MCDataOutput> packet) {
        if (packet != null) {
            player.openMenu(provider, e -> packet.accept(new MCDataByteBuf(e)));
        } else {
            player.openMenu(provider);
        }
    }

    T create(int windowId, Inventory inventory, @Nullable MCDataInput packet);

    @Override
    default T create(int windowId, Inventory inv, @Nullable RegistryFriendlyByteBuf data) {
        return create(windowId, inv, data != null ? new MCDataByteBuf(data) : null);
    }
}
