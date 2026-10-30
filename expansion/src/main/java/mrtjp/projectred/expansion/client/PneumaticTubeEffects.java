package mrtjp.projectred.expansion.client;

import mrtjp.projectred.expansion.init.ExpansionSounds;
import mrtjp.projectred.expansion.graphs.ClientSideLinkCache;
import mrtjp.projectred.expansion.part.PneumaticTubePart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.List;

import static mrtjp.projectred.core.client.particle.ParticleAction.*;

/**
 * Smoke and sounds when pneumatic tube links change (client only).
 */
public final class PneumaticTubeEffects {

    private PneumaticTubeEffects() {
    }

    public static void onLinksRemoved(PneumaticTubePart part, List<ClientSideLinkCache.ClientLink> removed) {
        for (var link : removed) {
            spawn(part, link, 5, ExpansionSounds.DEPRESSURIZE.get());
        }
    }

    public static void onLinksAdded(PneumaticTubePart part, List<ClientSideLinkCache.ClientLink> added) {
        for (var link : added) {
            spawn(part, link, 10, ExpansionSounds.PRESSURIZE.get());
        }
    }

    private static void spawn(PneumaticTubePart part, ClientSideLinkCache.ClientLink link, double fadeOutTime, SoundEvent sound) {
        var points = link.getPointListFor(part.pos());
        var p = new PneumaticSmokeParticle((ClientLevel) part.level(), points);
        p.setAlpha(0.0F);
        p.addAction(
                sequence(
                        changeAlphaTo(0.7, 1),
                        changeAlphaTo(0.0, fadeOutTime),
                        remove()
                )
        );
        Minecraft.getInstance().particleEngine.add(p);
        part.level().playLocalSound(part.pos(), sound, SoundSource.BLOCKS,
                0.7F + part.level().getRandom().nextFloat() * 0.3F, //Volume
                1.0F,
                true);
    }
}
