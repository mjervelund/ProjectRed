package mrtjp.projectred.lib;

/**
 * Pair of values. Replaces {@code net.minecraft.util.Tuple}, removed in 26.x.
 */
public record Tuple<A, B>(A getA, B getB) {
}
