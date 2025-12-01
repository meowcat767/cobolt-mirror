package com.cobolt.cli;

import com.cobolt.git.GitAdapter;
import com.cobolt.git.GitCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Push to remote repository (Git integration)
 */
@Command(name = "push", description = "Update remote refs along with associated objects")
public class PushCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Remote name (default: origin)", defaultValue = "origin")
    private String remote;

    @Parameters(index = "1", description = "Branch to push (default: current branch)", defaultValue = "")
    private String branch;

    @Override
    public Integer call() {
        try {
            Path currentDir = Paths.get("").toAbsolutePath();

            // Check if in a Git repository
            if (!GitAdapter.isGitRepository(currentDir)) {
                OutputFormatter.error("Not a Git repository");
                OutputFormatter.info("Push command requires a Git repository");
                return 1;
            }

            GitCommands git = new GitCommands(currentDir);

            // Get current branch if not specified
            if (branch.isEmpty()) {
                branch = git.getCurrentBranch();
            }

            Banner.printCompact();
            OutputFormatter.blank();
            OutputFormatter.progress("Pushing to " + remote + "/" + branch);

            git.push(remote, branch);
            git.close();

            OutputFormatter.clearProgress();
            OutputFormatter.success("Successfully pushed to " + remote + "/" + branch);

            return 0;
        } catch (Exception e) {
            OutputFormatter.clearProgress();
            OutputFormatter.error("Failed to push: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}
