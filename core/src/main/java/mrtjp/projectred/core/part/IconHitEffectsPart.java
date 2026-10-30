package mrtjp.projectred.core.part;

import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.api.part.MultiPart;
import codechicken.multipart.util.PartRayTraceResult;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.api.distmarker.Dist;


/**
 * Parts whose breaking particles use their own icons. CB Multipart 3.6 removed its interface of this name in favour
 * of per-type particle handlers; register {@link #HANDLER} for each part type implementing this.
 */
public interface IconHitEffectsPart extends MultiPart {

    Cuboid6 getBounds();

    TextureAtlasSprite getBreakingIcon(PartRayTraceResult hit);

    TextureAtlasSprite getBrokenIcon(int side);
}
