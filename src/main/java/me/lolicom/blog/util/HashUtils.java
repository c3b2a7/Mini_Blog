package me.lolicom.blog.util;

import me.lolicom.blog.lang.Constant;
import org.apache.shiro.crypto.hash.*;

/**
 * @author lolicom
 */
public class HashUtils {
    
    private static final Algorithm defaultAlgorithm = Constant.security.ALGORITHM_NAME;
    private static final int defaultHashIterations = Constant.security.ITERATIONS;
    private static final boolean defaultToHex = Constant.security.TO_HEX;
    
    public static String hash(Object source) {
        return hash(defaultAlgorithm, source);
    }
    
    public static String hash(Object source, boolean toHex) {
        return hash(defaultAlgorithm.getAlgorithm(), source, null, defaultHashIterations, toHex);
    }
    
    public static String hash(Object source, Object salt) {
        return hash(defaultAlgorithm.getAlgorithm(), source, salt, defaultHashIterations, defaultToHex);
    }
    
    public static String hash(Object source, Object salt, boolean toHex) {
        return hash(defaultAlgorithm.getAlgorithm(), source, salt, defaultHashIterations, toHex);
    }
    
    public static String hash(Algorithm algorithm, Object source) {
        return hash(algorithm.getAlgorithm(), source, null, defaultHashIterations, defaultToHex);
    }
    
    public static String hash(Algorithm algorithm, Object source, Object salt) {
        return hash(algorithm.getAlgorithm(), source, salt, defaultHashIterations, defaultToHex);
    }
    
    public static String hash(Algorithm algorithm, Object source, Object salt, int hashIterations) {
        return hash(algorithm.getAlgorithm(), source, salt, hashIterations, defaultToHex);
    }
    
    private static String hash(String algorithm, Object source, Object salt, int hashIterations, boolean toHex) {
        if (toHex)
            return new SimpleHash(algorithm, source, salt, hashIterations).toHex();
        return new SimpleHash(algorithm, source, salt, hashIterations).toBase64();
    }
    
    public enum Algorithm {
        
        MD2(Md2Hash.ALGORITHM_NAME),
        MD5(Md5Hash.ALGORITHM_NAME),
        SHA128(Sha1Hash.ALGORITHM_NAME),
        SHA256(Sha256Hash.ALGORITHM_NAME),
        SHA384(Sha384Hash.ALGORITHM_NAME),
        Sha512(Sha512Hash.ALGORITHM_NAME);
        
        private String algorithm;
        
        private Algorithm(String algorithm) {
            this.algorithm = algorithm;
        }
        
        public String getAlgorithm() {
            return algorithm;
        }
    }
    
}
