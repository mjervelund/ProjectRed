package mrtjp.projectred.integration.init;

import mrtjp.projectred.core.client.StaticPartModel;
import codechicken.multipart.api.RegisterMultipartRenderersEvent;
import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import mrtjp.projectred.core.client.IconPartParticleHandler;
import mrtjp.projectred.integration.client.GatePartItemRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import static mrtjp.projectred.integration.ProjectRedIntegration.MOD_ID;
import mrtjp.projectred.integration.GateType;
import mrtjp.projectred.integration.client.GateModelRenderer;
import mrtjp.projectred.integration.client.GatePartRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class IntegrationClientInit {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(IntegrationClientInit::clientSetup);
        modEventBus.addListener(IntegrationClientInit::onRegisterMultipartRenderers);
        modEventBus.addListener(IntegrationClientInit::onRegisterSpecialModelRenderers);
        modEventBus.addListener(IntegrationClientInit::onRegisterReloadListeners);

        // Register sprites
        modEventBus.addListener(GateModelRenderer::onTextureStitchEvent);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
    }

    private static void onRegisterMultipartRenderers(RegisterMultipartRenderersEvent event) {
        for (GateType type : GateType.values()) {
            if (type.isExternalGate()) continue;
            event.registerDynamicPartRenderer(type.getPartType(), () -> GatePartRenderer.INSTANCE);
            event.registerStaticPartRenderer(type.getPartType(), () -> StaticPartModel.Renderer.INSTANCE);
            event.registerParticleHandler(type.getPartType(), IconPartParticleHandler.INSTANCE);
        }
    }

    public static final ProjectRedItemRenderer.Unbaked GATE_ITEM_RENDERER = new ProjectRedItemRenderer.Unbaked(GatePartItemRenderer::new);

    private static void onRegisterSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "gate"), GATE_ITEM_RENDERER.type());
    }

    private static void onRegisterReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "gate_models"), (ResourceManagerReloadListener) GateModelRenderer::onResourceManagerReload);
    }
}
