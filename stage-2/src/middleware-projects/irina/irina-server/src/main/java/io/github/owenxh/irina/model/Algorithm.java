package io.github.owenxh.irina.model;

import com.google.common.hash.Hashing;

/**
 * The hash algorithm
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public enum Algorithm {

    SHA256 {
        @Override
        public String hash(byte[] content) {
            return Hashing.sha256()
                    .hashBytes(content)
                    .toString();
        }
    };

    public abstract String hash(byte[] content);

}
