package com.cobolt.merge;

import com.cobolt.core.*;
import java.io.IOException;
import java.util.*;

/**
 * Engine for performing 3-way merges.
 */
public class MergeEngine {

    private final Repository repository;

    public MergeEngine(Repository repository) {
        this.repository = repository;
    }

    /**
     * Perform a 3-way merge.
     */
    public MergeResult merge(Commit ours, Commit theirs, Commit ancestor) throws IOException {
        MergeResult result = new MergeResult(true);

        Map<String, Tree.TreeEntry> ourFiles = collectFiles(ours.getTreeId());
        Map<String, Tree.TreeEntry> theirFiles = collectFiles(theirs.getTreeId());
        Map<String, Tree.TreeEntry> ancestorFiles = collectFiles(ancestor.getTreeId());

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(ourFiles.keySet());
        allFiles.addAll(theirFiles.keySet());
        allFiles.addAll(ancestorFiles.keySet());

        for (String path : allFiles) {
            Tree.TreeEntry ourEntry = ourFiles.get(path);
            Tree.TreeEntry theirEntry = theirFiles.get(path);
            Tree.TreeEntry ancestorEntry = ancestorFiles.get(path);

            if (Objects.equals(ourEntry, theirEntry)) {
                // Both sides match (or both deleted), nothing to do (keep ours)
                continue;
            }

            if (Objects.equals(ourEntry, ancestorEntry)) {
                // We haven't changed it, they have (or deleted it)
                if (theirEntry != null) {
                    // They modified it, take theirs
                    String content = getBlobContent(theirEntry.getId());
                    result.addMergedFile(path, content);
                } else {
                    // They deleted it
                    result.addMergedFile(path, null); // null indicates deletion
                }
            } else if (Objects.equals(theirEntry, ancestorEntry)) {
                // They haven't changed it, we have (or deleted it)
                // Keep ours, nothing to do
            } else {
                // Both changed it
                if (ourEntry == null) {
                    // We deleted, they modified -> Conflict
                    result.addConflict(new Conflict(path, Conflict.Type.DELETE_EDIT,
                            null,
                            getLines(theirEntry.getId()),
                            getLines(ancestorEntry.getId())));
                    result.addMergedFile(path, null); // Tentatively delete? Or keep? Let's say conflict implies manual
                                                      // resolution.
                } else if (theirEntry == null) {
                    // They deleted, we modified -> Conflict
                    result.addConflict(new Conflict(path, Conflict.Type.DELETE_EDIT,
                            getLines(ourEntry.getId()),
                            null,
                            getLines(ancestorEntry.getId())));
                } else {
                    // Both modified
                    // Check if content is same
                    if (ourEntry.getId().equals(theirEntry.getId())) {
                        // Same content change, auto-merge (keep ours)
                    } else {
                        // Different content -> Content Conflict
                        // TODO: Try line-level merge here? For now, just mark as conflict.
                        result.addConflict(new Conflict(path, Conflict.Type.CONTENT,
                                getLines(ourEntry.getId()),
                                getLines(theirEntry.getId()),
                                getLines(ancestorEntry.getId())));
                    }
                }
            }
        }

        return result;
    }

    private Map<String, Tree.TreeEntry> collectFiles(String treeId) throws IOException {
        Map<String, Tree.TreeEntry> files = new HashMap<>();
        if (treeId == null)
            return files;

        CoboltObject obj = repository.readObject(treeId);
        if (!(obj instanceof Tree))
            return files;

        collectFilesRecursive((Tree) obj, "", files);
        return files;
    }

    private void collectFilesRecursive(Tree tree, String prefix, Map<String, Tree.TreeEntry> files) throws IOException {
        for (Tree.TreeEntry entry : tree.getEntries()) {
            String path = prefix + entry.getName();
            if (entry.isTree()) {
                CoboltObject obj = repository.readObject(entry.getId());
                if (obj instanceof Tree) {
                    collectFilesRecursive((Tree) obj, path + "/", files);
                }
            } else {
                files.put(path, entry);
            }
        }
    }

    private String getBlobContent(String blobId) throws IOException {
        CoboltObject obj = repository.readObject(blobId);
        if (obj instanceof Blob) {
            return ((Blob) obj).getContentAsString();
        }
        return "";
    }

    private List<String> getLines(String blobId) throws IOException {
        if (blobId == null)
            return null;
        String content = getBlobContent(blobId);
        return Arrays.asList(content.split("\\r?\\n"));
    }
}
