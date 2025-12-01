package com.cobolt.cli;

import com.cobolt.core.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Create a new commit
 */
@Command(name = "commit", description = "Record changes to the repository")
public class CommitCommand implements Callable<Integer> {

    @Option(names = { "-m", "--message" }, required = true, description = "Commit message")
    private String message;

    @Option(names = { "-a", "--all" }, description = "Automatically stage all modified files")
    private boolean all;

    @Option(names = { "--author" }, description = "Override commit author")
    private String author;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            if (repo.getIndex().isEmpty()) {
                OutputFormatter.warning("Nothing to commit (staging area is empty)");
                OutputFormatter.info("Use 'cobolt add <file>' to stage changes");
                return 1;
            }

            // Build tree from index
            Tree rootTree = buildTreeFromIndex(repo);
            String treeId = repo.writeObject(rootTree);

            // Create commit
            Commit commit = new Commit();
            commit.setTreeId(treeId);
            commit.setMessage(message);

            // Set author and committer
            String userName = author != null ? author : System.getProperty("user.name", "Unknown");
            commit.setAuthor(userName);
            commit.setCommitter(userName);

            // Set parent if HEAD exists
            String headCommitId = repo.resolveRef("HEAD");
            if (headCommitId != null) {
                commit.addParent(headCommitId);
            }

            String commitId = repo.writeObject(commit);

            // Update branch ref
            String currentBranch = repo.getCurrentBranch();
            if (currentBranch != null) {
                repo.createBranch(currentBranch, commitId);
            } else {
                // Detached HEAD
                repo.setHead(commitId, false);
            }

            // Clear staging area
            repo.getIndex().clear();
            repo.saveIndex();

            OutputFormatter.blank();
            OutputFormatter.success("Created commit " + OutputFormatter.hash(commit.getShortId()));

            if (currentBranch != null) {
                System.out.println("  Branch: " + OutputFormatter.branch(currentBranch));
            }

            System.out.println("  Author: " + commit.getAuthor());
            System.out.println("  Date:   " + formatTimestamp(commit.getTimestamp()));
            OutputFormatter.blank();
            System.out.println("  " + message);
            OutputFormatter.blank();

            return 0;
        } catch (Exception e) {
            OutputFormatter.error("Failed to create commit: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private Tree buildTreeFromIndex(Repository repo) throws Exception {
        Tree tree = new Tree();
        Map<String, Tree> subtrees = new HashMap<>();

        for (Index.IndexEntry entry : repo.getIndex().getEntries()) {
            String path = entry.getPath();
            String[] parts = path.split("/");

            if (parts.length == 1) {
                // File in root
                tree.addEntry(parts[0], entry.getBlobId(), entry.getMode());
            } else {
                // File in subdirectory
                String dirPath = String.join("/", Arrays.copyOf(parts, parts.length - 1));
                String fileName = parts[parts.length - 1];

                Tree subtree = subtrees.computeIfAbsent(dirPath, k -> new Tree());
                subtree.addEntry(fileName, entry.getBlobId(), entry.getMode());
            }
        }

        // Write subtrees and add to parent tree
        for (Map.Entry<String, Tree> subtreeEntry : subtrees.entrySet()) {
            String dirPath = subtreeEntry.getKey();
            Tree subtree = subtreeEntry.getValue();
            String subtreeId = repo.writeObject(subtree);

            String[] parts = dirPath.split("/");
            if (parts.length == 1) {
                tree.addEntry(parts[0], subtreeId, "040000");
            } else {
                // Nested directories - simplified for now
                tree.addEntry(dirPath, subtreeId, "040000");
            }
        }

        return tree;
    }

    private String formatTimestamp(long timestamp) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(timestamp));
    }
}
