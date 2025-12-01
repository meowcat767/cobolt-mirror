package com.cobolt.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Tree object representing directory structure
 */
public class Tree extends CoboltObject {
    private static final long serialVersionUID = 1L;

    private final Map<String, TreeEntry> entries;

    public Tree() {
        this.entries = new TreeMap<>(); // Sorted by name
    }

    @Override
    public String getType() {
        return "tree";
    }

    @Override
    public byte[] getContent() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (TreeEntry entry : entries.values()) {
                String line = entry.mode + " " + entry.name + " " + entry.id + "\n";
                baos.write(line.getBytes(StandardCharsets.UTF_8));
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize tree", e);
        }
    }

    /**
     * Add entry to tree
     */
    public void addEntry(String name, String id, String mode) {
        entries.put(name, new TreeEntry(name, id, mode));
    }

    /**
     * Get entry by name
     */
    public TreeEntry getEntry(String name) {
        return entries.get(name);
    }

    /**
     * Get all entries
     */
    public Collection<TreeEntry> getEntries() {
        return entries.values();
    }

    /**
     * Remove entry
     */
    public void removeEntry(String name) {
        entries.remove(name);
    }

    /**
     * Check if tree contains entry
     */
    public boolean hasEntry(String name) {
        return entries.containsKey(name);
    }

    /**
     * Tree entry representing a file or subdirectory
     */
    public static class TreeEntry implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final String id;
        private final String mode;

        public TreeEntry(String name, String id, String mode) {
            this.name = name;
            this.id = id;
            this.mode = mode;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getMode() {
            return mode;
        }

        public boolean isBlob() {
            return mode.equals("100644") || mode.equals("100755");
        }

        public boolean isTree() {
            return mode.equals("040000");
        }

        public boolean isExecutable() {
            return mode.equals("100755");
        }
    }
}
