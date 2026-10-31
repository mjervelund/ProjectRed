package mrtjp.projectred.illumination.init;

import codechicken.microblock.init.CBMicroblockModContent;
import codechicken.multipart.api.RegisterMultipartRenderersEvent;
import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import mrtjp.projectred.core.client.IconPartParticleHandler;
import mrtjp.projectred.illumination.MultipartLightProperties;
import mrtjp.projectred.illumination.client.IllumarLampItemRenderer;
import mrtjp.projectred.illumination.client.IllumarSmartLampItemRenderer;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import static mrtjp.projectred.illumination.ProjectRedIllumination.MOD_ID;
import mrtjp.projectred.illumination.BlockLightType;
import mrtjp.projectred.illumination.MultipartLightType;
import mrtjp.projectred.illumination.client.*;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Objects;

public class IlluminationClientInit {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(IlluminationClientInit::clientSetup);
        modEventBus.addListener(IlluminationClientInit::onRegisterMultipartRenderers);
        modEventBus.addListener(IlluminationClientInit::onRegisterSpecialModelRenderers);

        // Register Sprites
        for (MultipartLightType type : MultipartLightType.values()) {
            modEventBus.addListener(type.getProperties()::onTextureStitchEvent);
        }
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        // Bind Tile entity renderers
        for (BlockLightType lampType : BlockLightType.values()) {
            for (int color = 0; color < 16; color++) {
                //TODO this only works because Illumar lamp is the only BlockLightType
                BlockEntityRenderers.register(SneakyUtils.unsafeCast(lampType.getTileEntityType(color, false)), c ->  IllumarLampBlockEntityRenderer.INSTANCE);
                BlockEntityRenderers.register(SneakyUtils.unsafeCast(lampType.getTileEntityType(color, true)), c -> IllumarLampBlockEntityRenderer.INSTANCE);
            }
        }

        BlockEntityRenderers.register(SneakyUtils.unsafeCast(IlluminationBlocks.ILLUMAR_SMART_LAMP_BLOCK_ENTITY.get()), c -> IllumarSmartLampBlockEntityRenderer.INSTANCE);
    }

    private static void onRegisterMultipartRenderers(RegisterMultipartRenderersEvent event) {
        for (MultipartLightType type : MultipartLightType.values()) {
            for (int colour = 0; colour < 16; colour++) {
                event.registerDynamicPartRenderer(type.getPartType(colour, false), () -> MultipartLightPartRenderer.INSTANCE);
                event.registerDynamicPartRenderer(type.getPartType(colour, true), () -> MultipartLightPartRenderer.INSTANCE);
                event.registerParticleHandler(type.getPartType(colour, false), IconPartParticleHandler.INSTANCE);
                event.registerParticleHandler(type.getPartType(colour, true), IconPartParticleHandler.INSTANCE);
            }
        }

        // Lamp microblock halos (CB Multipart registers only static renderers for microblocks)
        event.registerDynamicPartRenderer(CBMicroblockModContent.FACE_MICROBLOCK_PART.get(), IllumarLampMicroblockHaloRenderer::new);
        event.registerDynamicPartRenderer(CBMicroblockModContent.HOLLOW_MICROBLOCK_PART.get(), IllumarLampMicroblockHaloRenderer::new);
        event.registerDynamicPartRenderer(CBMicroblockModContent.CORNER_MICROBLOCK_PART.get(), IllumarLampMicroblockHaloRenderer::new);
        event.registerDynamicPartRenderer(CBMicroblockModContent.EDGE_MICROBLOCK_PART.get(), IllumarLampMicroblockHaloRenderer::new);
        event.registerDynamicPartRenderer(CBMicroblockModContent.POST_MICROBLOCK_PART.get(), IllumarLampMicroblockHaloRenderer::new);
    }

    public static final ProjectRedItemRenderer.Unbaked LAMP_ITEM_RENDERER = new ProjectRedItemRenderer.Unbaked(IllumarLampItemRenderer::new);
    public static final ProjectRedItemRenderer.Unbaked LIGHT_ITEM_RENDERER = new ProjectRedItemRenderer.Unbaked(MultipartLightProperties.DefaultMultipartLightItemRenderer::new);
    public static final ProjectRedItemRenderer.Unbaked SMART_LAMP_ITEM_RENDERER = new ProjectRedItemRenderer.Unbaked(IllumarSmartLampItemRenderer::new);

    private static void onRegisterSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "lamp"), LAMP_ITEM_RENDERER.type());
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "smart_lamp"), SMART_LAMP_ITEM_RENDERER.type());
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "light"), LIGHT_ITEM_RENDERER.type());
    }
}
