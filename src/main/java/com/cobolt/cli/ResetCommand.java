package com.cobolt.cli;

import com.cobolt.core.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Reset current HEAD to specified state (unstage files)
 */
@Command(name = "reset", description = "Unstage files or reset to a specific commit")
public class ResetCommand implements Callable<Integer> {

    @Parameters(description = "Files to unstage (or commit to reset to)", arity = "0..*")
    private List<String> files;

    @picocli.CommandLine.Option(names = { "--hard" }, description = "Reset working directory (destructive)")
    private boolean hard;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            if (files == null || files.isEmpty()) {
                // Reset all staged files
                int count = repo.getIndex().getEntries().size();
                repo.getIndex().clear();
                repo.saveIndex();

                OutputFormatter.success("Unstaged " + count + " file(s)");
            } else {
                // Reset specific files
                int count = 0;
                for (String file : files) {
                    if (repo.getIndex().contains(file)) {
                        repo.getIndex().remove(file);
                        count++;
                    }
                }
                repo.saveIndex();

                if (count > 0) {
                    OutputFormatter.success("Unstaged " + count + " file(s)");
                } else {
                    OutputFormatter.warning("No files were unstaged");
                }
            }

            return 0;
        } catch (Exception e) {
            OutputFormatter.error("Failed to reset: " + e.getMessage());
            return 1;
        }
    }
}
