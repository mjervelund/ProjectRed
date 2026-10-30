package mrtjp.projectred.core.block;

import mrtjp.projectred.core.init.RegistryIds;
import mrtjp.projectred.core.tile.IBlockEventBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class ProjectRedBlock extends Block implements EntityBlock {

    public static final IntegerProperty SIDE = IntegerProperty.create("side", 0, 5);
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
    public static final BooleanProperty WORKING = BooleanProperty.create("working");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    // Fresh per block: properties carry the block's registry id (see RegistryIds).
    public static BlockBehaviour.Properties woodenMachineProperties() {
        return RegistryIds.blockProps()
            .mapColor(MapColor.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .strength(2.5F)
            .sound(SoundType.WOOD);
    }

    public static BlockBehaviour.Properties stoneMachineProperties() {
        return RegistryIds.blockProps()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.STONE);
    }

    public ProjectRedBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected abstract BlockEntityType<?> getBlockEntityType();

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != getBlockEntityType()) return null;
        return (level1, pos, state1, tile) -> {
            if (tile instanceof IBlockEventBlockEntity t) {
                t.tick();
            }
        };
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, @Nullable Orientation orientation, boolean isMoving) {
        BlockEntity tile = world.getBlockEntity(pos);
        // 26.x no longer passes the neighbor's position. No handler uses it (same as CB Multipart).
        if (tile instanceof IBlockEventBlockEntity) ((IBlockEventBlockEntity) tile).onNeighborBlockChanged(BlockPos.ZERO);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IBlockEventBlockEntity) ((IBlockEventBlockEntity) tile).onNeighborTileChanged(neighbor);
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IBlockEventBlockEntity) return ((IBlockEventBlockEntity) tile).useItemOn(stack, player, hand, hit);
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof IBlockEventBlockEntity) return ((IBlockEventBlockEntity) tile).useWithoutItem(player, hit);
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity player, ItemStack stack) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IBlockEventBlockEntity) ((IBlockEventBlockEntity) tile).onBlockPlaced(player, stack);
    }
}
