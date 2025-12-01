package com.cobolt.core;

import java.nio.charset.StandardCharsets;

/**
 * Blob object representing file content
 */
public class Blob extends CoboltObject {
    private static final long serialVersionUID = 1L;

    private final byte[] data;

    public Blob(byte[] data) {
        this.data = data.clone();
    }

    public Blob(String content) {
        this.data = content.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getType() {
        return "blob";
    }

    @Override
    public byte[] getContent() {
        return data.clone();
    }

    /**
     * Get content as string
     */
    public String getContentAsString() {
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Get size of blob in bytes
     */
    public int getSize() {
        return data.length;
    }
}
