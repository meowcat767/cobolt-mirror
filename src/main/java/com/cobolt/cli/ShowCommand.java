package com.cobolt.cli;

import com.cobolt.core.Commit;
import com.cobolt.core.CoboltObject;
import com.cobolt.core.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

/**
 * Show various types of objects (commits, trees, blobs)
 */
@Command(name = "show", description = "Show information about commits, trees, or blobs")
public class ShowCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Object to show (commit ID, tag, or 'HEAD')", defaultValue = "HEAD")
    private String object;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            String commitId = repo.resolveRef(object);
            if (commitId == null) {
                OutputFormatter.error("Object not found: " + object);
                return 1;
            }

            CoboltObject obj = repo.readObject(commitId);

            if (obj instanceof Commit) {
                showCommit((Commit) obj);
            } else {
                OutputFormatter.warning("Object type not yet supported for display");
            }

            return 0;
        } catch (Exception e) {
            OutputFormatter.error("Failed to show object: " + e.getMessage());
            return 1;
        }
    }

    private void showCommit(Commit commit) {
        OutputFormatter.blank();
        System.out.println(OutputFormatter.boldStr("commit " + commit.getId()));

        if (commit.isMergeCommit()) {
            System.out.println("Merge: " + String.join(" ", commit.getParentIds()));
        }

        System.out.println("Author: " + commit.getAuthor());
        System.out.println("Date:   " + formatTimestamp(commit.getTimestamp()));

        OutputFormatter.blank();

        for (String line : commit.getMessage().split("\n")) {
            System.out.println("    " + line);
        }

        OutputFormatter.blank();
    }

    private String formatTimestamp(long timestamp) {
        return DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(timestamp));
    }
}
