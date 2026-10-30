package mrtjp.projectred.transmission.init;

import codechicken.multipart.api.MultipartType;
import codechicken.multipart.api.RegisterMultipartRenderersEvent;
import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import mrtjp.projectred.core.client.IconPartParticleHandler;
import mrtjp.projectred.transmission.part.BaseCenterWirePart;
import mrtjp.projectred.transmission.part.BaseFaceWirePart;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import static mrtjp.projectred.transmission.ProjectRedTransmission.MOD_ID;
import mrtjp.projectred.transmission.WireType;
import mrtjp.projectred.transmission.client.CenterWirePartRenderer;
import mrtjp.projectred.transmission.client.FaceWirePartRenderer;
import mrtjp.projectred.transmission.client.FramedWirePartItemRenderer;
import mrtjp.projectred.transmission.client.WirePartItemRenderer;
import net.covers1624.quack.util.SneakyUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class TransmissionClientInit {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(TransmissionClientInit::clientSetup);
        modEventBus.addListener(TransmissionClientInit::onRegisterMultipartRenderers);
        modEventBus.addListener(TransmissionClientInit::onRegisterSpecialModelRenderers);

        // Register sprites
        for (WireType type : WireType.values()) {
            modEventBus.addListener(type::onTextureStitchEvent);
        }
    }

    private static void clientSetup(final FMLClientSetupEvent event) {

        // TODO 26.3: cover-on-wire highlight (FramedWireHighlightRenderer) needs CB Multipart's new highlight API
    }

    private static void onRegisterMultipartRenderers(RegisterMultipartRenderersEvent event) {
        for (WireType type : WireType.values()) {
            event.registerParticleHandler(type.getPartType(), IconPartParticleHandler.INSTANCE);
            if (type.isCenterPart()) {
                event.registerDynamicPartRenderer(SneakyUtils.<MultipartType<BaseCenterWirePart>>unsafeCast(type.getPartType()), () -> CenterWirePartRenderer.INSTANCE);
            } else {
                event.registerDynamicPartRenderer(SneakyUtils.<MultipartType<BaseFaceWirePart>>unsafeCast(type.getPartType()), () -> FaceWirePartRenderer.INSTANCE);
            }
        }
    }

    public static final ProjectRedItemRenderer.Unbaked WIRE_ITEM_RENDERER = new ProjectRedItemRenderer.Unbaked(WirePartItemRenderer::new);
    public static final ProjectRedItemRenderer.Unbaked FRAMED_WIRE_ITEM_RENDERER = new ProjectRedItemRenderer.Unbaked(FramedWirePartItemRenderer::new);

    private static void onRegisterSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "wire"), WIRE_ITEM_RENDERER.type());
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "framed_wire"), FRAMED_WIRE_ITEM_RENDERER.type());
    }
}
