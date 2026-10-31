package mrtjp.projectred.expansion.client;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Cuboid6;
import com.mojang.blaze3d.vertex.PoseStack;
import mrtjp.projectred.core.client.ProjectRedItemRenderer;
import mrtjp.projectred.core.client.RecordingBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Item renderer for frames and frame motors. Their block models are dynamic ({@link FrameBlockModel},
 * {@link FrameMotorBlockModel}), which a plain item model can't reference, so the items draw the same CCL geometry
 * here (1.21.1 used CCL's {@code codechickenlib:class} model loader for both).
 */
public class FrameItemRenderer extends ProjectRedItemRenderer {

    private static final CCModel MOTOR_MODEL = CCModel.quadModel(24).generateBlock(0, Cuboid6.full, 0).computeNormals().shrinkUVs(0.0005);

    private final boolean motor;

    public FrameItemRenderer(boolean motor) {
        this.motor = motor;
    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack mStack, RecordingBufferSource getter, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        getter.bind(ccrs, RenderTypes.cutoutMovingBlock(), mStack);

        if (motor) {
            MOTOR_MODEL.render(ccrs, FrameMotorBlockModel.getItemIconT());
        } else {
            FrameModelRenderer.renderStatic(ccrs, 0);
        }
    }
}
