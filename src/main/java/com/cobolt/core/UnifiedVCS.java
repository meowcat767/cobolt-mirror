package com.cobolt.core;

import com.cobolt.git.GitAdapter;
import com.cobolt.git.GitCommands;

import java.nio.file.Path;

/**
 * Unified VCS interface that auto-detects Git or Cobolt repositories
 */
public class UnifiedVCS {

    private final Path workingDir;
    private final boolean isGit;
    private Repository coboltRepo;
    private GitCommands gitCommands;

    private UnifiedVCS(Path workingDir, boolean isGit) {
        this.workingDir = workingDir;
        this.isGit = isGit;
    }

    /**
     * Auto-detect and open repository
     */
    public static UnifiedVCS open(Path workingDir) throws Exception {
        boolean isGit = GitAdapter.isGitRepository(workingDir);
        boolean isCobolt = Repository.isRepository(workingDir);

        // Prefer Git if both exist
        UnifiedVCS vcs = new UnifiedVCS(workingDir, isGit);

        if (isGit) {
            vcs.gitCommands = new GitCommands(workingDir);
        } else if (isCobolt) {
            vcs.coboltRepo = new Repository(workingDir);
        } else {
            throw new Exception("Not a Git or Cobolt repository");
        }

        return vcs;
    }

    /**
     * Check if using Git
     */
    public boolean isUsingGit() {
        return isGit;
    }

    /**
     * Get Cobolt repository (if using Cobolt)
     */
    public Repository getCoboltRepository() {
        return coboltRepo;
    }

    /**
     * Get Git commands (if using Git)
     */
    public GitCommands getGitCommands() {
        return gitCommands;
    }

    /**
     * Close resources
     */
    public void close() {
        if (gitCommands != null) {
            gitCommands.close();
        }
    }
}
