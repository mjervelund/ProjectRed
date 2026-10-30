package mrtjp.projectred.expansion.mixin;

import mrtjp.projectred.expansion.MovementManager;
import mrtjp.projectred.expansion.client.MovingBlockRenderManager;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Leaves moving blocks out of the chunk mesh; {@link MovingBlockRenderManager} draws them at their animated offset.
 */
@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {

    @Redirect(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V"
            )
    )
    private void compile_WrapTesselateBlock(ModelBlockRenderer renderer, BlockQuadOutput output, float x, float y, float z,
                                            BlockAndTintGetter level, BlockPos pos, BlockState state, BlockStateModel model, long seed) {
        MovementManager manager = MovementManager.getClientInstanceNullable();
        if (manager != null && !manager.hasNoMovingStructures() && MovingBlockRenderManager.isMoving(manager, pos)) {
            return;
        }
        renderer.tesselateBlock(output, x, y, z, level, pos, state, model, seed);
    }
}
