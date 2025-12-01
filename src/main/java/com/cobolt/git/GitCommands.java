package com.cobolt.git;

import com.cobolt.cli.OutputFormatter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.nio.file.Path;
import java.util.Set;

/**
 * Git operations wrapper with Cobolt-style output
 */
public class GitCommands {

    private final GitAdapter adapter;

    public GitCommands(Path workingDir) throws Exception {
        this.adapter = new GitAdapter(workingDir);
    }

    /**
     * Add files to Git index
     */
    public void add(String... patterns) throws Exception {
        Git git = adapter.getGit();

        for (String pattern : patterns) {
            if (pattern.equals(".")) {
                git.add().addFilepattern(".").call();
            } else {
                git.add().addFilepattern(pattern).call();
            }
        }
    }

    /**
     * Commit changes
     */
    public String commit(String message, String author) throws Exception {
        Git git = adapter.getGit();

        RevCommit commit = git.commit()
                .setMessage(message)
                .setAuthor(new PersonIdent(author, ""))
                .call();

        return commit.getName();
    }

    /**
     * Get repository status
     */
    public GitStatus getStatus() throws Exception {
        Git git = adapter.getGit();
        Status status = git.status().call();

        return new GitStatus(
                status.getAdded(),
                status.getChanged(),
                status.getModified(),
                status.getUntracked(),
                status.getRemoved());
    }

    /**
     * Push to remote
     */
    public void push(String remote, String branch) throws Exception {
        Git git = adapter.getGit();
        git.push()
                .setRemote(remote)
                .add(branch)
                .call();
    }

    /**
     * Pull from remote
     */
    public void pull() throws Exception {
        Git git = adapter.getGit();
        git.pull().call();
    }

    /**
     * Create branch
     */
    public void createBranch(String name) throws Exception {
        Git git = adapter.getGit();
        git.branchCreate()
                .setName(name)
                .call();
    }

    /**
     * Checkout branch
     */
    public void checkout(String name) throws Exception {
        Git git = adapter.getGit();
        git.checkout()
                .setName(name)
                .call();
    }

    /**
     * Get current branch name
     */
    public String getCurrentBranch() throws Exception {
        return adapter.getRepository().getBranch();
    }

    /**
     * Close adapter
     */
    public void close() {
        adapter.close();
    }

    /**
     * Git status wrapper
     */
    public static class GitStatus {
        public final Set<String> added;
        public final Set<String> changed;
        public final Set<String> modified;
        public final Set<String> untracked;
        public final Set<String> removed;

        public GitStatus(Set<String> added, Set<String> changed, Set<String> modified,
                Set<String> untracked, Set<String> removed) {
            this.added = added;
            this.changed = changed;
            this.modified = modified;
            this.untracked = untracked;
            this.removed = removed;
        }
    }
}
