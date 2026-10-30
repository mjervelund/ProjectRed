package mrtjp.projectred.integration.part;

import mrtjp.projectred.integration.GateType;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Timer and sequencer gates loaded from hand-written or corrupted NBT (setblock, structures, tampered chunks).
 */
public class TimerGateLoadTest {

    private static CompoundTag tag(String pmaxKey, int pmax) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(pmaxKey, pmax);
        return tag;
    }

    @Test
    public void clampHelpers() {
        assertEquals(ComplexGatePart.MIN_TIMER_TICKS, ComplexGatePart.clampSequencerPointerMax(0));
        assertEquals(ComplexGatePart.MIN_TIMER_TICKS, ComplexGatePart.clampSequencerPointerMax(-5));
        assertEquals(40, ComplexGatePart.clampSequencerPointerMax(40));
        // timer stores interval - 2
        assertEquals(ComplexGatePart.MIN_TIMER_TICKS - 2, ComplexGatePart.clampTimerPointerMax(0));
        assertEquals(ComplexGatePart.MIN_TIMER_TICKS - 2, ComplexGatePart.clampTimerPointerMax(Integer.MIN_VALUE));
        assertEquals(18, ComplexGatePart.clampTimerPointerMax(18));
    }

    @Test
    public void sequencerZeroMaxIsClampedOnLoad() {
        ComplexGatePart.Sequencer seq = new ComplexGatePart.Sequencer(GateType.SEQUENCER);
        seq.load(tag("pmax", 0), null);
        assertEquals(ComplexGatePart.MIN_TIMER_TICKS, seq.pointerMax());
    }

    @Test
    public void sequencerMissingMaxUsesDefault() {
        ComplexGatePart.Sequencer seq = new ComplexGatePart.Sequencer(GateType.SEQUENCER);
        seq.load(new CompoundTag(), null);
        assertEquals(40, seq.pointerMax());
    }

    @Test
    public void sequencerValidMaxIsKept() {
        ComplexGatePart.Sequencer seq = new ComplexGatePart.Sequencer(GateType.SEQUENCER);
        seq.load(tag("pmax", 100), null);
        assertEquals(100, seq.pointerMax());
    }

    @Test
    public void timerZeroAndNegativeMaxAreClampedOnLoad() {
        ComplexGatePart.Timer timer = new ComplexGatePart.Timer(GateType.TIMER);
        timer.load(tag("pmax", 0), null);
        assertEquals(ComplexGatePart.MIN_TIMER_TICKS - 2, timer.pointerMax());
        timer.load(tag("pmax", -100), null);
        assertEquals(ComplexGatePart.MIN_TIMER_TICKS - 2, timer.pointerMax());
        timer.load(new CompoundTag(), null);
        assertEquals(38, timer.pointerMax());
    }

    @Test
    public void timerWithoutElapsedSavesBeforeJoiningALevel() {
        // /setblock loads the part and saves it before it has a level; without "pelapsed" the
        // pointer counts as started, and save used to call level() -> NPE/assertion
        ComplexGatePart.Timer timer = new ComplexGatePart.Timer(GateType.TIMER);
        timer.load(tag("pmax", 20), null);
        CompoundTag out = new CompoundTag();
        assertDoesNotThrow(() -> timer.save(out, null));
        assertEquals(0L, out.getLongOr("pelapsed", -2L));
        assertEquals(20, out.getIntOr("pmax", -1));
    }

    @Test
    public void timerElapsedSurvivesSaveBeforeJoiningALevel() {
        ComplexGatePart.Timer timer = new ComplexGatePart.Timer(GateType.TIMER);
        CompoundTag in = tag("pmax", 20);
        in.putLong("pelapsed", 7L);
        timer.load(in, null);
        CompoundTag out = new CompoundTag();
        timer.save(out, null);
        assertEquals(7L, out.getLongOr("pelapsed", -2L));

        CompoundTag stopped = tag("pmax", 20);
        stopped.putLong("pelapsed", -1L);
        timer.load(stopped, null);
        CompoundTag out2 = new CompoundTag();
        timer.save(out2, null);
        assertEquals(-1L, out2.getLongOr("pelapsed", -2L));
    }
}
