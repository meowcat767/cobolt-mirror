package com.cobolt.objects;

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
    /**
     * Compute SHA-1 hash of byte array
     */
    public static String computeSHA1(byte[] data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(data);
            return bytesToHex(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    /**
     * Compute SHA-1 hash of string
     */
    public static String computeSHA1(String data) {
        return computeSHA1(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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
