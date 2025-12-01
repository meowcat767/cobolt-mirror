package com.cobolt.objects;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Utilities for SHA-1 hashing and object ID generation
 */
public class HashUtils {
    
    /**
     * Compute SHA-1 hash of byte array
     */
    public static String computeSHA1(byte[] data) {
        return DigestUtils.sha1Hex(data);
    }
    
    /**
     * Compute SHA-1 hash of string
     */
    public static String computeSHA1(String data) {
        return DigestUtils.sha1Hex(data);
    }
    
    /**
     * Compute object ID from type and content
     */
    public static String computeObjectId(String type, byte[] content) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String header = type + " " + content.length + "\0";
            baos.write(header.getBytes(StandardCharsets.UTF_8));
            baos.write(content);
            return computeSHA1(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to compute object ID", e);
        }
    }
    
    /**
     * Get short hash (first 7 characters)
     */
    public static String shortHash(String fullHash) {
        return fullHash != null && fullHash.length() >= 7 ? fullHash.substring(0, 7) : fullHash;
    }
}
