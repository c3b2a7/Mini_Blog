package me.lolicom.blog.lang;

import me.lolicom.blog.util.HashUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;

/**
 * @author lolicom
 */
public class Constant {
    public static class security {
        public static final HashUtils.Algorithm ALGORITHM_NAME = HashUtils.Algorithm.SHA256;
        public static final int ITERATIONS = 3;
        public static final boolean TO_HEX = true;
    }
}
