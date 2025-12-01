package com.cobolt.cli;

import com.cobolt.core.*;
import com.cobolt.objects.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Switch branches or restore working tree files
 */
@Command(name = "checkout", description = "Switch branches or restore working tree files")
public class CheckoutCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Branch name or commit to checkout")
    private String target;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            // Check if target is a branch
            Reference branch = repo.getBranch(target);
            if (branch != null) {
                return checkoutBranch(repo, target);
            }

            // Try as commit ID
            String commitId = repo.resolveRef(target);
            if (commitId != null && repo.hasObject(commitId)) {
                return checkoutCommit(repo, commitId);
            }

            OutputFormatter.error("Branch or commit '" + target + "' not found");
            return 1;

        } catch (Exception e) {
            OutputFormatter.error("Failed to checkout: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private int checkoutBranch(Repository repo, String branchName) throws Exception {
        Reference branch = repo.getBranch(branchName);
        String commitId = branch.getTarget();

        // Update working directory
        restoreWorkingTree(repo, commitId);

        // Update HEAD to point to branch
        repo.setHead("refs/heads/" + branchName, true);

        OutputFormatter.success("Switched to branch " + OutputFormatter.branch(branchName));

        return 0;
    }

    private int checkoutCommit(Repository repo, String commitId) throws Exception {
        // Update working directory
        restoreWorkingTree(repo, commitId);

        // Update HEAD to commit (detached)
        repo.setHead(commitId, false);

        OutputFormatter.warning("You are in 'detached HEAD' state");
        OutputFormatter.info("HEAD is now at " + OutputFormatter.hash(commitId.substring(0, 7)));

        return 0;
    }

    private void restoreWorkingTree(Repository repo, String commitId) throws Exception {
        CoboltObject obj = repo.readObject(commitId);
        if (!(obj instanceof Commit)) {
            throw new Exception("Not a commit: " + commitId);
        }

        Commit commit = (Commit) obj;
        Tree tree = (Tree) repo.readObject(commit.getTreeId());

        // Clear current working directory (except .cobolt)
        clearWorkingDirectory(repo);

        // Restore files from tree
        restoreTree(repo, tree, repo.getWorkingDir());

        // Clear staging area
        repo.getIndex().clear();
        repo.saveIndex();
    }

    private void clearWorkingDirectory(Repository repo) throws Exception {
        List<Path> files = FileUtils.listFilesRecursively(repo.getWorkingDir());
        for (Path file : files) {
            if (!file.startsWith(repo.getCoboltDir())) {
                FileUtils.deleteRecursively(file);
            }
        }
    }

    private void restoreTree(Repository repo, Tree tree, Path currentDir) throws Exception {
        for (Tree.TreeEntry entry : tree.getEntries()) {
            Path entryPath = currentDir.resolve(entry.getName());

            if (entry.isBlob()) {
                Blob blob = (Blob) repo.readObject(entry.getId());
                FileUtils.writeBytes(entryPath, blob.getContent());
            } else if (entry.isTree()) {
                FileUtils.createDirectories(entryPath);
                Tree subtree = (Tree) repo.readObject(entry.getId());
                restoreTree(repo, subtree, entryPath);
            }
        }
    }
}
