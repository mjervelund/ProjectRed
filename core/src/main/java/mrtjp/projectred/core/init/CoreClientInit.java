package mrtjp.projectred.core.init;

import mrtjp.projectred.core.client.HaloRenderer;
import mrtjp.projectred.core.gui.screen.inventory.ElectrotineGeneratorScreen;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import mrtjp.projectred.redui.ViewportRenderNode;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import static mrtjp.projectred.core.ProjectRedCore.MOD_ID;
import static mrtjp.projectred.core.init.CoreMenus.ELECTROTINE_GENERATOR_MENU;

@SuppressWarnings("NotNullFieldNotInitialized")
public class CoreClientInit {

    public static void init(ModContainer container, IEventBus modEventBus) {
        // Enable in-game config screen
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(CoreClientInit::clientSetup);
        modEventBus.addListener(CoreClientInit::onRegisterScreens);
        modEventBus.addListener(CoreClientInit::onRegisterPictureInPictureRenderers);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        // Init Halo Renderer
        HaloRenderer.init();

        // Register Halo renderer
        NeoForge.EVENT_BUS.addListener(HaloRenderer::onSubmitCustomGeometry);
    }

    private static void onRegisterScreens(RegisterMenuScreensEvent event) {
        // Register screens
        event.register(ELECTROTINE_GENERATOR_MENU.get(), ElectrotineGeneratorScreen::new);
    }

    private static void onRegisterPictureInPictureRenderers(RegisterPictureInPictureRenderersEvent event) {
        event.register(ViewportRenderNode.ViewportRenderState.class, ViewportRenderNode.Renderer::new);
    }
}
