package mrtjp.projectred.core.client;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.api.part.MultiPart;
import codechicken.multipart.api.part.render.StandardPartParticleHandler;
import codechicken.multipart.util.PartRayTraceResult;
import mrtjp.projectred.core.part.IconHitEffectsPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Particle handler for {@link IconHitEffectsPart}s (client only).
 */
public final class IconPartParticleHandler implements StandardPartParticleHandler<MultiPart> {

    public static final IconPartParticleHandler INSTANCE = new IconPartParticleHandler();

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
}
