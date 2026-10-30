package mrtjp.projectred.integration;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustomChannel;
import codechicken.multipart.api.part.MultiPart;
import codechicken.multipart.block.BlockMultipart;
import codechicken.multipart.block.TileMultipart;
import mrtjp.projectred.integration.gui.screen.CounterScreen;
import mrtjp.projectred.integration.gui.screen.TimerScreen;
import mrtjp.projectred.integration.part.ComplexGatePart;
import mrtjp.projectred.integration.part.GatePart;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static mrtjp.projectred.integration.ProjectRedIntegration.LOGGER;
import static mrtjp.projectred.integration.ProjectRedIntegration.MOD_ID;

public class IntegrationNetwork {

    public static final Identifier NET_CHANNEL = Identifier.fromNamespaceAndPath(MOD_ID, "network");

    // Server to client messages
    public static final int OPEN_TIMER_GUI_FROM_SERVER = 1;
    public static final int OPEN_COUNTER_GUI_FROM_SERVER = 2;

    // Client to server messages
    public static final int INCR_TIMER_FROM_CLIENT = 3;
    public static final int INCR_COUNTER_FROM_CLIENT = 4;

    // Channel instance
    private static final PacketCustomChannel channel = new PacketCustomChannel(NET_CHANNEL)
            .versioned(ProjectRedIntegration.getContainer().getModInfo().getVersion().toString())
            .client(() -> ClientHandler::new)
            .server(() -> ServerHandler::new);

    public static void init(IEventBus modBus) {
        channel.init(modBus);
    }

    public static MCDataOutput writePartIndex(MCDataOutput out, MultiPart part) {
        out.writePos(part.pos()).writeByte(part.tile().getPartList().indexOf(part));
        return out;
    }

    public static MultiPart readPartIndex(Level world, MCDataInput in) {
        BlockPos pos = in.readPos();
        int index = in.readUByte();
        return Objects.requireNonNull(BlockMultipart.getTile(world, pos)).getPartList().get(index);
    }

    /**
     * Server side {@link #readPartIndex}: the part a client names, or null unless it is in a loaded chunk, within the
     * sender's reach, and the index is valid.
     */
    @Nullable
    private static MultiPart readPartIndexFromClient(ServerPlayer sender, MCDataInput in) {
        BlockPos pos = in.readPos();
        int index = in.readUByte();
        Level world = sender.level();
        if (!world.isLoaded(pos) || !sender.isWithinBlockInteractionRange(pos, 4.0)) return null;
        TileMultipart tile = BlockMultipart.getTile(world, pos);
        if (tile == null || index >= tile.getPartList().size()) return null;
        return tile.getPartList().get(index);
    }

    private static class ClientHandler implements ICustomPacketHandler.IClientPacketHandler {

        @Override
        public void handlePacket(PacketCustom packet, Minecraft mc) {
            switch (packet.getType()) {
                case OPEN_TIMER_GUI_FROM_SERVER:
                    handleOpenTimerGuiMessage(mc, packet);
                    break;
                case OPEN_COUNTER_GUI_FROM_SERVER:
                    handleOpenCounterGuiMessage(mc, packet);
                    break;
                default:
                    // unknown key
                    throw new RuntimeException("Invalid key received from server: " + packet.getType());
            }
        }

        private void handleOpenTimerGuiMessage(Minecraft mc, MCDataInput data) {
            MultiPart part = readPartIndex(Objects.requireNonNull(mc.level), data);
            if (part instanceof ComplexGatePart.ITimerGuiLogic) {
                mc.gui.setScreen(new TimerScreen((GatePart) part));
            }
        }

        private void handleOpenCounterGuiMessage(Minecraft mc, MCDataInput data) {
            MultiPart part = readPartIndex(Objects.requireNonNull(mc.level), data);
            if (part instanceof ComplexGatePart.ICounterGuiLogic) {
                mc.gui.setScreen(new CounterScreen((GatePart) part));
            }
        }
    }

    private static class ServerHandler implements ICustomPacketHandler.IServerPacketHandler {

        @Override
        public void handlePacket(PacketCustom packet, ServerPlayer sender) {
            switch (packet.getType()) {

                case INCR_TIMER_FROM_CLIENT:
                    handleIncrTimerMessage(sender, packet);
                    break;
                case INCR_COUNTER_FROM_CLIENT:
                    handleIncrCounterMessage(sender, packet);
                    break;
                default:
                    // unknown key
                    throw new RuntimeException("Invalid key received from client: " + packet.getType());
            }
        }

        private void handleIncrTimerMessage(ServerPlayer sender, PacketCustom packet) {
            MultiPart part = readPartIndexFromClient(sender, packet);
            if (part instanceof ComplexGatePart.ITimerGuiLogic gate) {
                gate.setTimerMax(gate.getTimerMax() + packet.readShort());
            }
        }

        private void handleIncrCounterMessage(ServerPlayer sender, PacketCustom packet) {
            MultiPart part = readPartIndexFromClient(sender, packet);
            if (part instanceof ComplexGatePart.ICounterGuiLogic gate) {
                int actionId = packet.readByte();
                switch (actionId) {
                    case 0:
                        gate.setCounterMax(gate.getCounterMax() + packet.readShort());
                        break;
                    case 1:
                        gate.setCounterIncr(gate.getCounterIncr() + packet.readShort());
                        break;
                    case 2:
                        gate.setCounterDecr(gate.getCounterDecr() + packet.readShort());
                        break;
                    default:
                        LOGGER.error("Unknown counter action id: " + actionId);
                }
            }
        }
    }
}
