package com.cobolt.merge;

import java.util.List;

/**
 * Represents a conflict in a file during a merge.
 */
public class Conflict {
    private final String filePath;
    private final Type type;
    private final List<String> ours;
    private final List<String> theirs;
    private final List<String> ancestor;

    public enum Type {
        CONTENT,
        MODE,
        DELETE_EDIT, // One side deleted, other edited
        ADD_ADD // Both added differently
    }

    public Conflict(String filePath, Type type, List<String> ours, List<String> theirs, List<String> ancestor) {
        this.filePath = filePath;
        this.type = type;
        this.ours = ours;
        this.theirs = theirs;
        this.ancestor = ancestor;
    }

    public String getFilePath() {
        return filePath;
    }

    public Type getType() {
        return type;
    }

    public List<String> getOurs() {
        return ours;
    }

    public List<String> getTheirs() {
        return theirs;
    }

    public List<String> getAncestor() {
        return ancestor;
    }
}
