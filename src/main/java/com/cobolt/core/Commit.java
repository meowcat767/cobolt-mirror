package com.cobolt.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Commit object representing a snapshot of the repository
 */
public class Commit extends CoboltObject {
    private static final long serialVersionUID = 1L;

    private String treeId;
    private final List<String> parentIds;
    private String author;
    private String committer;
    private long timestamp;
    private String message;

    public Commit() {
        this.parentIds = new ArrayList<>();
        this.timestamp = Instant.now().getEpochSecond();
    }

    @Override
    public String getType() {
        return "commit";
    }

    @Override
    public byte[] getContent() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StringBuilder sb = new StringBuilder();

            sb.append("tree ").append(treeId).append("\n");

            for (String parentId : parentIds) {
                sb.append("parent ").append(parentId).append("\n");
            }

            sb.append("author ").append(author).append(" ").append(timestamp).append("\n");
            sb.append("committer ").append(committer).append(" ").append(timestamp).append("\n");
            sb.append("\n");
            sb.append(message);

            baos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize commit", e);
        }
    }

    // Getters and setters
    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public List<String> getParentIds() {
        return new ArrayList<>(parentIds);
    }

    public void addParent(String parentId) {
        this.parentIds.add(parentId);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMergeCommit() {
        return parentIds.size() > 1;
    }
}
