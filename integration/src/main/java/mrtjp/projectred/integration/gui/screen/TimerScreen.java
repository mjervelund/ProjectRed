package mrtjp.projectred.integration.gui.screen;

import net.minecraft.client.renderer.RenderPipelines;
import codechicken.lib.packet.PacketCustom;
import mrtjp.projectred.integration.IntegrationNetwork;
import mrtjp.projectred.integration.part.ComplexGatePart;
import mrtjp.projectred.integration.part.GatePart;
import mrtjp.projectred.lib.Point;
import mrtjp.projectred.redui.ButtonNode;
import mrtjp.projectred.redui.RedUIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;

import static mrtjp.projectred.integration.ProjectRedIntegration.MOD_ID;

public class TimerScreen extends RedUIScreen {

    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/timer_gate.png");

    private final GatePart gate;
    private final ComplexGatePart.ITimerGuiLogic timerGate;

    public TimerScreen(GatePart gate) {
        super(256, 55, Component.literal(Objects.requireNonNull(gate.getType().getRegistryName()).toString()));
        this.gate = gate;
        this.timerGate = (ComplexGatePart.ITimerGuiLogic) gate;

        createButton(5, 25, 40, 20, "-10s", -200);
        createButton(46, 25, 40, 20, "-1s", -20);
        createButton(87, 25, 40, 20, "-50ms", -1);
        createButton(129, 25, 40, 20, "+50ms", 1);
        createButton(170, 25, 40, 20, "+1s", 20);
        createButton(211, 25, 40, 20, "+10s", 200);
    }

    private void createButton(int x, int y, int w, int h, String text, int delta) {
        ButtonNode b = new ButtonNode();
        b.setPosition(x, y);
        b.setSize(w, h);
        b.setButtonText(text);
        b.setClickFunction(() -> {
            PacketCustom packet = new PacketCustom(IntegrationNetwork.NET_CHANNEL, IntegrationNetwork.INCR_TIMER_FROM_CLIENT, Minecraft.getInstance().level.registryAccess());
            IntegrationNetwork.writePartIndex(packet, gate);
            packet.writeShort(delta);
            packet.sendToServer();
        });
        addChild(b);
    }

    @Override
    public void drawBack(GuiGraphicsExtractor graphics, Point mouse, float partialFrame) {
        super.drawBack(graphics, mouse, partialFrame);

        int x = getFrame().x();
        int y = getFrame().y();

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, (float) (0), (float) (0), getFrame().width(), getFrame().height(), 256, 256);

        String s = String.format("Timer interval: %.2fs", timerGate.getTimerMax() * 0.05);
        int sw = getFontRenderer().width(s);
        graphics.text(getFontRenderer(), s, x + (getFrame().width() - sw) / 2, y + 8, 0xFF404040, false);
    }

    @Override
    public void update() {
        if (!gate.hasTile()) {
            Objects.requireNonNull(getMinecraft().player).closeContainer();
        }
    }
}
