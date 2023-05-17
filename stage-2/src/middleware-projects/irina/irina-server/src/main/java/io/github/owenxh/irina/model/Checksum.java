package io.github.owenxh.irina.model;

import lombok.*;

import java.util.function.Supplier;

/**
 * Checksum
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Checksum {

    /**
     * The algorithm of the checksum.
     */
    private Algorithm algorithm;

    /**
     * The hash value that applying the {@link #algorithm} to the original content.
     */
    private String hash;

    public static Checksum of(Algorithm algorithm, Supplier<byte[]> supplier) {
        return new Checksum(algorithm, algorithm.hash(supplier.get()));
    }
}
