package com.cobolt.merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of a merge operation.
 */
public class MergeResult {
    private final boolean success;
    private final List<Conflict> conflicts;
    private final Map<String, String> mergedFiles; // path -> content

    public MergeResult(boolean success) {
        this.success = success;
        this.conflicts = new ArrayList<>();
        this.mergedFiles = new HashMap<>();
    }

    public boolean isSuccess() {
        return success && conflicts.isEmpty();
    }

    public void addConflict(Conflict conflict) {
        conflicts.add(conflict);
    }

    public List<Conflict> getConflicts() {
        return conflicts;
    }

    public void addMergedFile(String path, String content) {
        mergedFiles.put(path, content);
    }

    public Map<String, String> getMergedFiles() {
        return mergedFiles;
    }
}
