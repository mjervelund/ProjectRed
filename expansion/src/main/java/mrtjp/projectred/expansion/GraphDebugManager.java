package mrtjp.projectred.expansion;

import java.util.Set;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import com.mojang.brigadier.arguments.BoolArgumentType;
import mrtjp.projectred.expansion.graphs.ClientSideLinkCache;
import mrtjp.projectred.expansion.graphs.GraphContainer;
import mrtjp.projectred.expansion.part.PneumaticTubePart;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.function.Function;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GraphDebugManager {

    private static final HashMap<ResourceKey<Level>, GraphDebugManager> CLIENT_INSTANCE = new HashMap<>();

    private static boolean enableRendering = false;

    private final ResourceKey<Level> dimension;
    private final HashSet<GraphContainer> containers = new HashSet<>();

    public GraphDebugManager(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public static GraphDebugManager getInstance(Level level) {
        return CLIENT_INSTANCE.computeIfAbsent(level.dimension(), GraphDebugManager::new);
    }

    private PacketCustom createPacket(int key) {
        return new PacketCustom(ExpansionNetwork.NET_CHANNEL, ExpansionNetwork.LINK_DEBUG_RENDERER_FROM_SERVER,
                Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).registryAccess())
                .writeByte(key);
    }

    public void read(MCDataInput input, Level level) {
        int key = input.readUByte();
        switch (key) {
            // Nothing yet!
            default -> throw new RuntimeException("Unknown key: " + key);
        }
    }

    public void onWorldJoin(GraphContainer container) {
        containers.add(container);
    }

    public void onWorldSeparate(GraphContainer container) {
        containers.remove(container);
    }
    //endregion

    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(literal("project_red")
                .then(literal("tube_graph_debug")
                        .then(argument("enable", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    enableRendering = BoolArgumentType.getBool(ctx, "enable");
                                    ctx.getSource().sendSuccess(() -> Component.literal("Tube graph debugging " + (enableRendering ? "enabled" : "disabled")), false);
                                    return 0;
                                })
                        )
                )
        );
    }

    //region Level events
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            getInstance(level).containers.clear();
        }
    }

    public static void onLevelTick(LevelTickEvent.Pre event) {

    }
    //endregion

    //region Client-side rendering

    // Rendering lives in client.GraphDebugRenderer (dedicated servers no longer strip client-only members)
    public static boolean isRenderingEnabled() {
        return enableRendering;
    }

    public Set<GraphContainer> getContainers() {
        return containers;
    }

    //endregion
}
