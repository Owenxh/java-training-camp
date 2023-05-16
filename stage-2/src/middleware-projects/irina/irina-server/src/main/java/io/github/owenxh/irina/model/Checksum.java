package io.github.owenxh.irina.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Checksum
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Checksum {

    /**
     * The algorithm of the checksum.
     */
    private Algorithm algorithm;

    /**
     * The hash value that applying the {@link #algorithm} to the original content.
     */
    private String hash;
}
