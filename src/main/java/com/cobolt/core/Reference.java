package com.cobolt.core;

import com.cobolt.objects.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Branch and tag reference management
 */
public class Reference {

    public enum Type {
        BRANCH,
        TAG,
        HEAD
    }

    private final String name;
    private final Type type;
    private String target; // commit ID or ref name
    private boolean symbolic; // true if points to another ref

    public Reference(String name, Type type, String target) {
        this(name, type, target, false);
    }

    public Reference(String name, Type type, String target, boolean symbolic) {
        this.name = name;
        this.type = type;
        this.target = target;
        this.symbolic = symbolic;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isSymbolic() {
        return symbolic;
    }

    public void setSymbolic(boolean symbolic) {
        this.symbolic = symbolic;
    }

    /**
     * Save reference to file
     */
    public void save(Path refsDir) throws IOException {
        Path refPath = getRefPath(refsDir);
        String content = symbolic ? "ref: " + target : target;
        FileUtils.writeString(refPath, content);
    }

    /**
     * Load reference from file
     */
    public static Reference load(String name, Type type, Path refsDir) throws IOException {
        Path refPath = getRefPath(refsDir, name, type);
        if (!FileUtils.exists(refPath)) {
            return null;
        }

        String content = FileUtils.readString(refPath).trim();
        boolean symbolic = content.startsWith("ref: ");
        String target = symbolic ? content.substring(5) : content;

        return new Reference(name, type, target, symbolic);
    }

    /**
     * Delete reference
     */
    public void delete(Path refsDir) throws IOException {
        Path refPath = getRefPath(refsDir);
        if (FileUtils.exists(refPath)) {
            FileUtils.deleteRecursively(refPath);
        }
    }

    private Path getRefPath(Path refsDir) {
        return getRefPath(refsDir, name, type);
    }

    private static Path getRefPath(Path refsDir, String name, Type type) {
        switch (type) {
            case BRANCH:
                return refsDir.resolve("heads").resolve(name);
            case TAG:
                return refsDir.resolve("tags").resolve(name);
            case HEAD:
                return refsDir.getParent().resolve("HEAD");
            default:
                throw new IllegalArgumentException("Unknown ref type: " + type);
        }
    }

    @Override
    public String toString() {
        return name + " -> " + target + (symbolic ? " (symbolic)" : "");
    }
}
