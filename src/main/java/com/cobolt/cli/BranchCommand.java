package com.cobolt.cli;

import com.cobolt.core.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * List, create, or delete branches
 */
@Command(name = "branch", description = "List, create, or delete branches")
public class BranchCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Branch name to create", defaultValue = "")
    private String branchName;

    @Option(names = { "-d", "--delete" }, description = "Delete branch")
    private boolean delete;

    @Option(names = { "-a", "--all" }, description = "List all branches")
    private boolean all;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            if (delete) {
                return deleteBranch(repo, branchName);
            } else if (branchName.isEmpty()) {
                return listBranches(repo);
            } else {
                return createBranch(repo, branchName);
            }
        } catch (Exception e) {
            OutputFormatter.error("Failed to manage branches: " + e.getMessage());
            return 1;
        }
    }

    private int createBranch(Repository repo, String name) throws Exception {
        if (repo.getBranch(name) != null) {
            OutputFormatter.error("Branch '" + name + "' already exists");
            return 1;
        }

        String headCommitId = repo.resolveRef("HEAD");
        if (headCommitId == null) {
            OutputFormatter.error("Cannot create branch - no commits yet");
            return 1;
        }

        repo.createBranch(name, headCommitId);
        OutputFormatter.success("Created branch " + OutputFormatter.branch(name));

        return 0;
    }

    private int deleteBranch(Repository repo, String name) throws Exception {
        if (name.isEmpty()) {
            OutputFormatter.error("Please specify a branch name to delete");
            return 1;
        }

        String currentBranch = repo.getCurrentBranch();
        if (name.equals(currentBranch)) {
            OutputFormatter.error("Cannot delete the currently checked out branch");
            return 1;
        }

        if (repo.getBranch(name) == null) {
            OutputFormatter.error("Branch '" + name + "' does not exist");
            return 1;
        }

        repo.deleteBranch(name);
        OutputFormatter.success("Deleted branch " + name);

        return 0;
    }

    private int listBranches(Repository repo) throws Exception {
        List<String> branches = repo.listBranches();
        String currentBranch = repo.getCurrentBranch();

        if (branches.isEmpty()) {
            OutputFormatter.info("No branches yet");
            return 0;
        }

        OutputFormatter.blank();
        OutputFormatter.header("Branches:");
        OutputFormatter.blank();

        for (String branch : branches) {
            if (branch.equals(currentBranch)) {
                System.out.println("  * " + OutputFormatter.branch(branch) + " (current)");
            } else {
                System.out.println("    " + branch);
            }
        }
        OutputFormatter.blank();

        return 0;
    }
}
