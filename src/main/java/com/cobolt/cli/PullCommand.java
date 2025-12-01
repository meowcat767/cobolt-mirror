package com.cobolt.cli;

import com.cobolt.git.GitAdapter;
import com.cobolt.git.GitCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Pull from remote repository (Git integration)
 */
@Command(name = "pull", description = "Fetch from and integrate with another repository")
public class PullCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Remote name (default: origin)", defaultValue = "origin")
    private String remote;

    @Override
    public Integer call() {
        try {
            Path currentDir = Paths.get("").toAbsolutePath();

            // Check if in a Git repository
            if (!GitAdapter.isGitRepository(currentDir)) {
                OutputFormatter.error("Not a Git repository");
                OutputFormatter.info("Pull command requires a Git repository");
                return 1;
            }

            Banner.printCompact();
            OutputFormatter.blank();
            OutputFormatter.progress("Pulling from " + remote);

            GitCommands git = new GitCommands(currentDir);
            git.pull();
            git.close();

            OutputFormatter.clearProgress();
            OutputFormatter.success("Successfully pulled from " + remote);

            return 0;
        } catch (Exception e) {
            OutputFormatter.clearProgress();
            OutputFormatter.error("Failed to pull: " + e.getMessage());
            return 1;
        }
    }
}
