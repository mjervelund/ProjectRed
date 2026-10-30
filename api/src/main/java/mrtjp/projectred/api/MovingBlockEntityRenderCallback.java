package mrtjp.projectred.api;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

/**
 * Callbacks for when moving block entities are rendered. While the offset for the block entity itself
 * is auto-applied to the PoseStack, this gives you the ability to apply the same offset to external
 * rendering utilities that don't directly use the PoseStack given to {@link BlockEntityRenderer}.
 * <p>
 * Register via {@link IExpansionAPI#registerBlockEntityRenderCallback(MovingBlockEntityRenderCallback)}
 */
public interface MovingBlockEntityRenderCallback {

    /**
     * Called before a moving block entity is rendered with the specified offsets
     */
    void onMovingPreRender(double offsetX, double offsetY, double offsetZ);

    /**
     * Called after block entity is rendered
     */
    void onMovingPostRender();
}
