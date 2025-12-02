package com.cobolt.cli;

import com.cobolt.core.Index;
import com.cobolt.core.Repository;
import com.cobolt.objects.FileUtils;
import picocli.CommandLine.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Show working tree status
 */
@Command(name = "status", description = "Show the working tree status")
public class StatusCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository; have you run cobolt init?");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            // Show current branch
            OutputFormatter.blank();
            String currentBranch = repo.getCurrentBranch();
            if (currentBranch != null) {
                OutputFormatter.header("On branch " + OutputFormatter.branch(currentBranch));
            } else {
                OutputFormatter.header("HEAD detached");
            }
            OutputFormatter.blank();

            // Get staged files
            Set<String> stagedFiles = new HashSet<>();
            for (Index.IndexEntry entry : repo.getIndex().getEntries()) {
                stagedFiles.add(entry.getPath());
            }

            // Get all files in working directory
            List<Path> workingFiles = FileUtils.listFilesRecursively(repoRoot);
            Set<String> workingPaths = new HashSet<>();

            for (Path file : workingFiles) {
                if (!file.startsWith(repo.getCoboltDir())) {
                    String relativePath = repoRoot.relativize(file).toString();
                    workingPaths.add(relativePath);
                }
            }

            // Categorize files
            List<String> staged = new ArrayList<>();
            List<String> unstaged = new ArrayList<>();
            List<String> untracked = new ArrayList<>();

            for (String path : stagedFiles) {
                staged.add(path);
            }

            for (String path : workingPaths) {
                if (!stagedFiles.contains(path)) {
                    untracked.add(path);
                }
            }

            // Display sections
            if (!staged.isEmpty()) {
                OutputFormatter.header("Changes to be committed:");
                OutputFormatter.info("  (use 'cobolt reset <file>...' to unstage)");
                OutputFormatter.blank();

                Collections.sort(staged);
                for (String file : staged) {
                    System.out.println("  " + OutputFormatter.added(file));
                }
                OutputFormatter.blank();
            }

            if (!unstaged.isEmpty()) {
                OutputFormatter.header("Changes not staged for commit:");
                OutputFormatter.info("  (use 'cobolt add <file>...' to update what will be committed)");
                OutputFormatter.blank();

                Collections.sort(unstaged);
                for (String file : unstaged) {
                    System.out.println("  " + OutputFormatter.modified(file));
                }
                OutputFormatter.blank();
            }

            if (!untracked.isEmpty()) {
                OutputFormatter.header("Untracked files:");
                OutputFormatter.info("  (use 'cobolt add <file>...' to include in what will be committed)");
                OutputFormatter.blank();

                Collections.sort(untracked);
                for (String file : untracked) {
                    System.out.println("  " + OutputFormatter.dim(file));
                }
                OutputFormatter.blank();
            }

            if (staged.isEmpty() && unstaged.isEmpty() && untracked.isEmpty()) {
                OutputFormatter.info("Nothing to commit, working tree clean");
                OutputFormatter.blank();
            }

            return 0;
        } catch (Exception e) {
            OutputFormatter.error("Failed to get status: " + e.getMessage());
            return 1;
        }
    }
}
