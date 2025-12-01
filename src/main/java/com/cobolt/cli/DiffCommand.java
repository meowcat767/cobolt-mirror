package com.cobolt.cli;

import com.cobolt.core.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Show changes between commits, working tree, etc.
 */
@Command(name = "diff", description = "Show changes between commits, commit and working tree, etc")
public class DiffCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Commit to diff against (default: staged vs unstaged)", defaultValue = "")
    private String commit;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            // For now, just show a placeholder
            OutputFormatter.info("Diff functionality coming soon!");
            OutputFormatter.info("This will show file changes with syntax highlighting");

            return 0;
        } catch (Exception e) {
            OutputFormatter.error("Failed to show diff: " + e.getMessage());
            return 1;
        }
    }
}
