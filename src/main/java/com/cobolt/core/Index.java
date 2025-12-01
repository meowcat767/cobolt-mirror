package com.cobolt.core;

import com.cobolt.objects.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

/**
 * Staging area implementation tracking files ready for commit
 */
public class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, IndexEntry> entries;

    public Index() {
        this.entries = new TreeMap<>();
    }

    /**
     * Add file to index
     */
    public void add(String path, String blobId, String mode) {
        entries.put(path, new IndexEntry(path, blobId, mode));
    }

    /**
     * Remove file from index
     */
    public void remove(String path) {
        entries.remove(path);
    }

    /**
     * Get entry by path
     */
    public IndexEntry getEntry(String path) {
        return entries.get(path);
    }

    /**
     * Get all entries
     */
    public Collection<IndexEntry> getEntries() {
        return entries.values();
    }

    /**
     * Check if path is staged
     */
    public boolean contains(String path) {
        return entries.containsKey(path);
    }

    /**
     * Clear all entries
     */
    public void clear() {
        entries.clear();
    }

    /**
     * Check if index is empty
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Save index to file
     */
    public void save(Path indexPath) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
        }
        FileUtils.writeBytes(indexPath, baos.toByteArray());
    }

    /**
     * Load index from file
     */
    public static Index load(Path indexPath) throws IOException {
        if (!FileUtils.exists(indexPath)) {
            return new Index();
        }

        try {
            byte[] data = FileUtils.readBytes(indexPath);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                return (Index) ois.readObject();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to load index", e);
        }
    }

    /**
     * Index entry representing a staged file
     */
    public static class IndexEntry implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String path;
        private final String blobId;
        private final String mode;

        public IndexEntry(String path, String blobId, String mode) {
            this.path = path;
            this.blobId = blobId;
            this.mode = mode;
        }

        public String getPath() {
            return path;
        }

        public String getBlobId() {
            return blobId;
        }

        public String getMode() {
            return mode;
        }
    }
}
