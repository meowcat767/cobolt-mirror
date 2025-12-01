package com.cobolt.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Git integration layer using JGit
 */
public class GitAdapter {

    private final Path workingDir;
    private Git git;
    private Repository repo;

    public GitAdapter(Path workingDir) throws IOException {
        this.workingDir = workingDir;
        openRepository();
    }

    /**
     * Check if directory contains a Git repository
     */
    public static boolean isGitRepository(Path dir) {
        return Files.exists(dir.resolve(".git"));
    }

    /**
     * Open existing Git repository
     */
    private void openRepository() throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repo = builder.setGitDir(workingDir.resolve(".git").toFile())
                .readEnvironment()
                .findGitDir()
                .build();
        git = new Git(repo);
    }

    /**
     * Get JGit repository instance
     */
    public Repository getRepository() {
        return repo;
    }

    /**
     * Get JGit Git instance
     */
    public Git getGit() {
        return git;
    }

    /**
     * Close repository
     */
    public void close() {
        if (git != null) {
            git.close();
        }
        if (repo != null) {
            repo.close();
        }
    }

    /**
     * Get working directory
     */
    public Path getWorkingDir() {
        return workingDir;
    }
}
