package mrtjp.projectred.expansion.init;

import mrtjp.projectred.expansion.block.BatteryBoxBlock;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import mrtjp.projectred.expansion.part.BaseTubePart;
import mrtjp.projectred.expansion.client.TubePartRenderer;
import mrtjp.projectred.expansion.client.TubePartItemRenderer;
import mrtjp.projectred.expansion.client.FrameMotorBlockModel;
import mrtjp.projectred.expansion.client.FrameBlockModel;
import mrtjp.projectred.expansion.client.ExpansionItemProperties;
import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import mrtjp.projectred.core.client.IconPartParticleHandler;
import codechicken.multipart.api.part.render.BlockStatePartBakedModelRenderer;
import codechicken.multipart.api.RegisterMultipartRenderersEvent;
import codechicken.multipart.api.MultipartType;
import mrtjp.projectred.expansion.GraphDebugManager;
import mrtjp.projectred.expansion.TubeType;
import mrtjp.projectred.expansion.client.*;
import mrtjp.projectred.expansion.gui.screen.inventory.*;
import mrtjp.projectred.expansion.item.BatteryBoxStorageComponent;
import mrtjp.projectred.expansion.item.RecipePlanComponent;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

import static mrtjp.projectred.expansion.ProjectRedExpansion.MOD_ID;
import static mrtjp.projectred.expansion.init.ExpansionBlocks.BATTERY_BOX_BLOCK;
import static mrtjp.projectred.expansion.init.ExpansionItems.RECIPE_PLAN_ITEM;
import static mrtjp.projectred.expansion.init.ExpansionMenus.*;
import static mrtjp.projectred.expansion.init.ExpansionParts.FRAME_PART;

public class ExpansionClientInit {

    public static final Identifier ITEM_MODEL_PROPERTY_CHARGE_LEVEL = Identifier.fromNamespaceAndPath(MOD_ID, "charge_level");
    public static final Identifier ITEM_MODEL_PROPERTY_WRITTEN_RECIPE_PLAN = Identifier.fromNamespaceAndPath(MOD_ID, "written");

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(ExpansionClientInit::clientSetup);
        modEventBus.addListener(ExpansionClientInit::onRegisterRangeSelectProperties);
        modEventBus.addListener(ExpansionClientInit::onRegisterConditionalProperties);
        modEventBus.addListener(ExpansionClientInit::onRegisterBlockStateModels);
        modEventBus.addListener(ExpansionClientInit::onRegisterMultipartRenderers);
        modEventBus.addListener(ExpansionClientInit::onRegisterSpecialModelRenderers);

        // Register menu screens
        modEventBus.addListener(ExpansionClientInit::onRegisterMenuScreensEvent);

        // MovementManager hooks
        NeoForge.EVENT_BUS.addListener(MovingBlockRenderManager::onSubmitCustomGeometry);
        NeoForge.EVENT_BUS.addListener(MovingBlockRenderManager::onExtractBlockOutline);
        NeoForge.EVENT_BUS.addListener(PneumaticSmokeParticle::onSubmitCustomGeometry);

        // GraphDebugManager hooks
        NeoForge.EVENT_BUS.addListener(GraphDebugManager::onSubmitCustomGeometry);
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent e) -> {
            if (e.getItemStack().is(BATTERY_BOX_BLOCK.get().asItem())) {
                BatteryBoxBlock.appendStoredPower(e.getItemStack(), e.getToolTip()::add);
            }
        });

        // Register sprites
        modEventBus.addListener(FrameModelRenderer::onTextureStitchEvent);
        modEventBus.addListener(FrameMotorBlockModel::onTextureStitchEvent);
        modEventBus.addListener(PneumaticSmokeParticle::onTextureStitchEvent);
        for (var type : TubeType.values()) {
            modEventBus.addListener(type::onTextureStitchEvent);
        }
    }

    private static void clientSetup(final FMLClientSetupEvent event) {

    }

    private static void onRegisterMenuScreensEvent(RegisterMenuScreensEvent event) {
        event.register(PROJECT_BENCH_MENU.get(), ProjectBenchScreen::new);
        event.register(BATTERY_BOX_MENU.get(), BatteryBoxScreen::new);
        event.register(AUTO_CRAFTER_MENU.get(), AutoCrafterScreen::new);
        event.register(CHARGING_BENCH_MENU.get(), ChargingBenchScreen::new);
        event.register(DEPLOYER_MENU.get(), DeployerScreen::new);
    }

    private static void onRegisterRangeSelectProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(ITEM_MODEL_PROPERTY_CHARGE_LEVEL, ExpansionItemProperties.ChargeLevel.CODEC);
    }

    private static void onRegisterConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(ITEM_MODEL_PROPERTY_WRITTEN_RECIPE_PLAN, ExpansionItemProperties.WrittenRecipePlan.CODEC);
    }

    private static void onRegisterBlockStateModels(RegisterBlockStateModels event) {
        event.registerModel(FrameBlockModel.Unbaked.TYPE, FrameBlockModel.Unbaked.CODEC);
        event.registerModel(FrameMotorBlockModel.Unbaked.TYPE, FrameMotorBlockModel.Unbaked.CODEC);
    }

    private static void onRegisterMultipartRenderers(RegisterMultipartRenderersEvent event) {
        event.registerStaticPartRenderer(FRAME_PART.get(), BlockStatePartBakedModelRenderer::new);
        event.registerParticleHandler(FRAME_PART.get(), IconPartParticleHandler.INSTANCE);
        for (TubeType type : TubeType.values()) {
            event.registerDynamicPartRenderer(SneakyUtils.<MultipartType<BaseTubePart>>unsafeCast(type.getPartType()), () -> TubePartRenderer.INSTANCE);
            event.registerParticleHandler(type.getPartType(), IconPartParticleHandler.INSTANCE);
        }
    }

    public static final ProjectRedItemRenderer.Unbaked TUBE_ITEM_RENDERER = new ProjectRedItemRenderer.Unbaked(TubePartItemRenderer::new);

    private static void onRegisterSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "tube"), TUBE_ITEM_RENDERER.type());
    }
}
