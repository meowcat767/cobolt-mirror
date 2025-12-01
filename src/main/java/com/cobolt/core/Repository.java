package com.cobolt.core;

import com.cobolt.objects.FileUtils;
import com.cobolt.objects.SerializationUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central repository management class
 */
public class Repository {

    private final Path workingDir;
    private final Path coboltDir;
    private final Path objectsDir;
    private final Path refsDir;
    private final Path indexPath;
    private final Path configPath;

    private Index index;

    /**
     * Open existing repository
     */
    public Repository(Path workingDir) throws IOException {
        this.workingDir = workingDir.toAbsolutePath();
        this.coboltDir = workingDir.resolve(".cobolt");
        this.objectsDir = coboltDir.resolve("objects");
        this.refsDir = coboltDir.resolve("refs");
        this.indexPath = coboltDir.resolve("index");
        this.configPath = coboltDir.resolve("config");

        if (!Files.exists(coboltDir)) {
            throw new IOException("Not a Cobolt repository: " + workingDir);
        }

        this.index = Index.load(indexPath);
    }

    /**
     * Initialize new repository
     */
    public static Repository init(Path workingDir) throws IOException {
        Path coboltDir = workingDir.resolve(".cobolt");

        if (Files.exists(coboltDir)) {
            throw new IOException("Repository already exists at: " + workingDir);
        }

        // Create directory structure
        FileUtils.createDirectories(coboltDir.resolve("objects"));
        FileUtils.createDirectories(coboltDir.resolve("refs/heads"));
        FileUtils.createDirectories(coboltDir.resolve("refs/tags"));

        // Create initial HEAD pointing to main branch
        Reference head = new Reference("HEAD", Reference.Type.HEAD, "refs/heads/main", true);
        head.save(coboltDir.resolve("refs"));

        // Create empty config
        FileUtils.writeString(coboltDir.resolve("config"), "");

        return new Repository(workingDir);
    }

    /**
     * Check if directory contains a Cobolt repository
     */
    public static boolean isRepository(Path dir) {
        return Files.exists(dir.resolve(".cobolt"));
    }

    /**
     * Find repository root starting from current directory
     */
    public static Path findRepositoryRoot(Path startDir) {
        Path current = startDir.toAbsolutePath();
        while (current != null) {
            if (isRepository(current)) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    // Object storage methods

    /**
     * Write object to object database
     */
    public String writeObject(CoboltObject obj) throws IOException {
        obj.computeId();
        String id = obj.getId();

        Path objectPath = getObjectPath(id);
        if (Files.exists(objectPath)) {
            return id; // Object already exists
        }

        byte[] data = SerializationUtils.serialize(obj);
        FileUtils.writeBytes(objectPath, data);

        return id;
    }

    /**
     * Read object from object database
     */
    public CoboltObject readObject(String id) throws IOException {
        Path objectPath = getObjectPath(id);
        if (!Files.exists(objectPath)) {
            throw new IOException("Object not found: " + id);
        }

        try {
            byte[] data = FileUtils.readBytes(objectPath);
            return SerializationUtils.deserialize(data);
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to deserialize object: " + id, e);
        }
    }

    /**
     * Check if object exists
     */
    public boolean hasObject(String id) {
        return Files.exists(getObjectPath(id));
    }

    private Path getObjectPath(String id) {
        String dirName = id.substring(0, 2);
        String fileName = id.substring(2);
        return objectsDir.resolve(dirName).resolve(fileName);
    }

    // Index methods

    /**
     * Get the staging area
     */
    public Index getIndex() {
        return index;
    }

    /**
     * Save index to disk
     */
    public void saveIndex() throws IOException {
        index.save(indexPath);
    }

    // Reference methods

    /**
     * Get HEAD reference
     */
    public Reference getHead() throws IOException {
        return Reference.load("HEAD", Reference.Type.HEAD, refsDir);
    }

    /**
     * Update HEAD reference
     */
    public void setHead(String target, boolean symbolic) throws IOException {
        Reference head = new Reference("HEAD", Reference.Type.HEAD, target, symbolic);
        head.save(refsDir);
    }

    /**
     * Get branch reference
     */
    public Reference getBranch(String name) throws IOException {
        return Reference.load(name, Reference.Type.BRANCH, refsDir);
    }

    /**
     * Create or update branch
     */
    public void createBranch(String name, String commitId) throws IOException {
        Reference branch = new Reference(name, Reference.Type.BRANCH, commitId);
        branch.save(refsDir);
    }

    /**
     * Delete branch
     */
    public void deleteBranch(String name) throws IOException {
        Reference branch = getBranch(name);
        if (branch != null) {
            branch.delete(refsDir);
        }
    }

    /**
     * List all branches
     */
    public List<String> listBranches() throws IOException {
        Path headsDir = refsDir.resolve("heads");
        if (!Files.exists(headsDir)) {
            return new ArrayList<>();
        }

        return FileUtils.listFilesRecursively(headsDir).stream()
                .map(p -> headsDir.relativize(p).toString())
                .collect(Collectors.toList());
    }

    /**
     * Create or update tag
     */
    public void createTag(String name, String commitId) throws IOException {
        Reference tag = new Reference(name, Reference.Type.TAG, commitId);
        tag.save(refsDir);
    }

    /**
     * Get tag reference
     */
    public Reference getTag(String name) throws IOException {
        return Reference.load(name, Reference.Type.TAG, refsDir);
    }

    /**
     * Delete tag
     */
    public void deleteTag(String name) throws IOException {
        Reference tag = getTag(name);
        if (tag != null) {
            tag.delete(refsDir);
        }
    }

    /**
     * List all tags
     */
    public List<String> listTags() throws IOException {
        Path tagsDir = refsDir.resolve("tags");
        if (!Files.exists(tagsDir)) {
            return new ArrayList<>();
        }

        return FileUtils.listFilesRecursively(tagsDir).stream()
                .map(p -> tagsDir.relativize(p).toString())
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get current branch name (null if detached HEAD)
     */
    public String getCurrentBranch() throws IOException {
        Reference head = getHead();
        if (head != null && head.isSymbolic()) {
            String target = head.getTarget();
            if (target.startsWith("refs/heads/")) {
                return target.substring("refs/heads/".length());
            }
        }
        return null;
    }

    /**
     * Resolve reference to commit ID
     */
    public String resolveRef(String refName) throws IOException {
        // Try as commit ID first
        if (refName.matches("[0-9a-f]{40}")) {
            return refName;
        }

        // Try as branch
        Reference ref = getBranch(refName);
        if (ref != null) {
            return ref.getTarget();
        }

        // Try HEAD
        if (refName.equals("HEAD")) {
            Reference head = getHead();
            if (head != null) {
                if (head.isSymbolic()) {
                    String branchName = head.getTarget().replace("refs/heads/", "");
                    ref = getBranch(branchName);
                    return ref != null ? ref.getTarget() : null;
                }
                return head.getTarget();
            }
        }

        return null;
    }

    // Getters

    public Path getWorkingDir() {
        return workingDir;
    }

    public Path getCoboltDir() {
        return coboltDir;
    }

    public Path getObjectsDir() {
        return objectsDir;
    }

    public Path getRefsDir() {
        return refsDir;
    }
}
