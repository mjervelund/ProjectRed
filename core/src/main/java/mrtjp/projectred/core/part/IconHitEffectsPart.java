package mrtjp.projectred.core.part;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.api.part.MultiPart;
import codechicken.multipart.api.part.render.StandardPartParticleHandler;
import codechicken.multipart.util.PartRayTraceResult;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Parts whose breaking particles use their own icons. CB Multipart 3.6 removed its interface of this name in favour
 * of per-type particle handlers; register {@link #HANDLER} for each part type implementing this.
 */
public interface IconHitEffectsPart extends MultiPart {

    Cuboid6 getBounds();

    @OnlyIn (Dist.CLIENT)
    TextureAtlasSprite getBreakingIcon(PartRayTraceResult hit);

    @OnlyIn (Dist.CLIENT)
    TextureAtlasSprite getBrokenIcon(int side);

    StandardPartParticleHandler<MultiPart> HANDLER = new StandardPartParticleHandler<>() {
        @Override
        public Cuboid6 getBounds(MultiPart part) {
            return ((IconHitEffectsPart) part).getBounds();
        }

        @Override
        public @Nullable TextureAtlasSprite getParticleTexture(MultiPart part, PartRayTraceResult hit) {
            return ((IconHitEffectsPart) part).getBreakingIcon(hit);
        }

        @Override
        public List<TextureAtlasSprite> getBrokenTextures(MultiPart part) {
            List<TextureAtlasSprite> icons = new ArrayList<>(6);
            for (int s = 0; s < 6; s++) {
                icons.add(((IconHitEffectsPart) part).getBrokenIcon(s));
            }
            return icons;
        }
    };
}
