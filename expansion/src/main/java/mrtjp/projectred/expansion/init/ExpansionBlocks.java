package mrtjp.projectred.expansion.init;

import mrtjp.projectred.core.init.RegistryIds;
import mrtjp.projectred.expansion.block.*;
import mrtjp.projectred.expansion.tile.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.function.Supplier;

import static mrtjp.projectred.expansion.ProjectRedExpansion.*;

@SuppressWarnings({ "DataFlowIssue", "NotNullFieldNotInitialized" })
public class ExpansionBlocks {

    public static final String ID_PROJECT_BENCH = "project_bench";
    public static final String ID_BATTERY_BOX = "battery_box";
    public static final String ID_AUTO_CRAFTER = "auto_crafter";
    public static final String ID_CHARGING_BENCH = "charging_bench";
    public static final String ID_FIRE_STARTER = "fire_starter";
    public static final String ID_FRAME = "frame";
    public static final String ID_FRAME_MOTOR = "frame_motor";
    public static final String ID_FRAME_ACTUATOR = "frame_actuator";
    public static final String ID_TRANSPOSER = "transposer";
    public static final String ID_BLOCK_BREAKER = "block_breaker";
    public static final String ID_DEPLOYER = "deployer";

    // Blocks
    public static Supplier<Block> FRAME_BLOCK;

    public static Supplier<Block> PROJECT_BENCH_BLOCK;
    public static Supplier<Block> BATTERY_BOX_BLOCK;
    public static Supplier<Block> CHARGING_BENCH_BLOCK;
    public static Supplier<Block> AUTO_CRAFTER_BLOCK;
    public static Supplier<Block> FIRE_STARTER_BLOCK;
    public static Supplier<Block> FRAME_MOTOR_BLOCK;
    public static Supplier<Block> FRAME_ACTUATOR_BLOCK;
    public static Supplier<Block> TRANSPOSER_BLOCK;
    public static Supplier<Block> BLOCK_BREAKER_BLOCK;
    public static Supplier<Block> DEPLOYER_BLOCK;

    // Tiles
    public static Supplier<BlockEntityType<ProjectBenchBlockEntity>> PROJECT_BENCH_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<ChargingBenchBlockEntity>> CHARGING_BENCH_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<AutoCrafterBlockEntity>> AUTO_CRAFTER_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<FireStarterBlockEntity>> FIRE_STARTER_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<FrameMotorBlockEntity>> FRAME_MOTOR_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<FrameActuatorBlockEntity>> FRAME_ACTUATOR_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<TransposerBlockEntity>> TRANSPOSER_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<BlockBreakerBlockEntity>> BLOCK_BREAKER_BLOCK_ENTITY;
    public static Supplier<BlockEntityType<DeployerBlockEntity>> DEPLOYER_BLOCK_ENTITY;


    public static void register() {

        /* Entity Blocks */

        // Blocks
        PROJECT_BENCH_BLOCK = BLOCKS.register(ID_PROJECT_BENCH, RegistryIds.block(ProjectBenchBlock::new));
        BATTERY_BOX_BLOCK = BLOCKS.register(ID_BATTERY_BOX, RegistryIds.block(BatteryBoxBlock::new));
        CHARGING_BENCH_BLOCK = BLOCKS.register(ID_CHARGING_BENCH, RegistryIds.block(ChargingBenchBlock::new));
        AUTO_CRAFTER_BLOCK = BLOCKS.register(ID_AUTO_CRAFTER, RegistryIds.block(AutoCrafterBlock::new));
        FIRE_STARTER_BLOCK = BLOCKS.register(ID_FIRE_STARTER, RegistryIds.block(FireStarterBlock::new));
        FRAME_MOTOR_BLOCK = BLOCKS.register(ID_FRAME_MOTOR, RegistryIds.block(FrameMotorBlock::new));
        FRAME_ACTUATOR_BLOCK = BLOCKS.register(ID_FRAME_ACTUATOR, RegistryIds.block(FrameActuatorBlock::new));
        TRANSPOSER_BLOCK = BLOCKS.register(ID_TRANSPOSER, RegistryIds.block(TransposerBlock::new));
        BLOCK_BREAKER_BLOCK = BLOCKS.register(ID_BLOCK_BREAKER, RegistryIds.block(BlockBreakerBlock::new));
        DEPLOYER_BLOCK = BLOCKS.register(ID_DEPLOYER, RegistryIds.block(DeployerBlock::new));

        // Block Items
        ITEMS.register(ID_PROJECT_BENCH, RegistryIds.item(() -> new BlockItem(PROJECT_BENCH_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_BATTERY_BOX, RegistryIds.item(() -> new BlockItem(BATTERY_BOX_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_AUTO_CRAFTER, RegistryIds.item(() -> new BlockItem(AUTO_CRAFTER_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_CHARGING_BENCH, RegistryIds.item(() -> new BlockItem(CHARGING_BENCH_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_FIRE_STARTER, RegistryIds.item(() -> new BlockItem(FIRE_STARTER_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_FRAME_MOTOR, RegistryIds.item(() -> new BlockItem(FRAME_MOTOR_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_FRAME_ACTUATOR, RegistryIds.item(() -> new BlockItem(FRAME_ACTUATOR_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_TRANSPOSER, RegistryIds.item(() -> new BlockItem(TRANSPOSER_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_BLOCK_BREAKER, RegistryIds.item(() -> new BlockItem(BLOCK_BREAKER_BLOCK.get(), RegistryIds.blockItemProps())));
        ITEMS.register(ID_DEPLOYER, RegistryIds.item(() -> new BlockItem(DEPLOYER_BLOCK.get(), RegistryIds.blockItemProps())));

        // Tiles
        PROJECT_BENCH_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_PROJECT_BENCH, () -> new BlockEntityType<>(ProjectBenchBlockEntity::new, PROJECT_BENCH_BLOCK.get()));
        BATTERY_BOX_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_BATTERY_BOX, () -> new BlockEntityType<>(BatteryBoxBlockEntity::new, BATTERY_BOX_BLOCK.get()));
        CHARGING_BENCH_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_CHARGING_BENCH, () -> new BlockEntityType<>(ChargingBenchBlockEntity::new, CHARGING_BENCH_BLOCK.get()));
        AUTO_CRAFTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_AUTO_CRAFTER, () -> new BlockEntityType<>(AutoCrafterBlockEntity::new, AUTO_CRAFTER_BLOCK.get()));
        FIRE_STARTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_FIRE_STARTER, () -> new BlockEntityType<>(FireStarterBlockEntity::new, FIRE_STARTER_BLOCK.get()));
        FRAME_MOTOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_FRAME_MOTOR, () -> new BlockEntityType<>(FrameMotorBlockEntity::new, FRAME_MOTOR_BLOCK.get()));
        FRAME_ACTUATOR_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_FRAME_ACTUATOR, () -> new BlockEntityType<>(FrameActuatorBlockEntity::new, FRAME_ACTUATOR_BLOCK.get()));
        TRANSPOSER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_TRANSPOSER, () -> new BlockEntityType<>(TransposerBlockEntity::new, TRANSPOSER_BLOCK.get()));
        BLOCK_BREAKER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_BLOCK_BREAKER, () -> new BlockEntityType<>(BlockBreakerBlockEntity::new, BLOCK_BREAKER_BLOCK.get()));
        DEPLOYER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(ID_DEPLOYER, () -> new BlockEntityType<>(DeployerBlockEntity::new, DEPLOYER_BLOCK.get()));

        /* Blocks */
        FRAME_BLOCK = BLOCKS.register(ID_FRAME, RegistryIds.block(FrameBlock::new));

        ITEMS.register(ID_FRAME, RegistryIds.item(() -> new BlockItem(FRAME_BLOCK.get(), RegistryIds.blockItemProps())));
    }

    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, PROJECT_BENCH_BLOCK_ENTITY.get(), (tile, ctx) -> tile.getHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, BATTERY_BOX_BLOCK_ENTITY.get(), BatteryBoxBlockEntity::getHandler);
        event.registerBlockEntity(Capabilities.Item.BLOCK, CHARGING_BENCH_BLOCK_ENTITY.get(), ChargingBenchBlockEntity::getHandler);
        event.registerBlockEntity(Capabilities.Item.BLOCK, AUTO_CRAFTER_BLOCK_ENTITY.get(), (tile, ctx) -> tile.getHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, DEPLOYER_BLOCK_ENTITY.get(), (tile, ctx) -> tile.getHandler());
    }
}