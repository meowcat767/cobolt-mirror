package com.cobolt.cli;

import com.cobolt.core.Commit;
import com.cobolt.core.CoboltObject;
import com.cobolt.core.Repository;
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
 * Show commit logs
 */
@Command(name = "log", description = "Show commit logs")
public class LogCommand implements Callable<Integer> {

    @Option(names = { "-n", "--max-count" }, description = "Limit number of commits to show")
    private Integer maxCount;

    @Option(names = { "--oneline" }, description = "Compact one-line format")
    private boolean oneline;

    @Option(names = { "--graph" }, description = "Show commit graph")
    private boolean graph;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            String headCommitId = repo.resolveRef("HEAD");
            if (headCommitId == null) {
                OutputFormatter.info("No commits yet");
                return 0;
            }

            List<Commit> commits = getCommitHistory(repo, headCommitId, maxCount);

            if (commits.isEmpty()) {
                OutputFormatter.info("No commits yet");
                return 0;
            }

            OutputFormatter.blank();

            for (int i = 0; i < commits.size(); i++) {
                Commit commit = commits.get(i);

                if (oneline) {
                    printOneLineCommit(commit);
                } else {
                    printDetailedCommit(commit, graph);
                    if (i < commits.size() - 1) {
                        OutputFormatter.blank();
                    }
                }
            }

            OutputFormatter.blank();

            return 0;
        } catch (Exception e) {
            OutputFormatter.error("Failed to show log: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private void printOneLineCommit(Commit commit) {
        String firstLine = commit.getMessage().split("\n")[0];
        System.out.println(OutputFormatter.hash(commit.getShortId()) + " " + firstLine);
    }

    private void printDetailedCommit(Commit commit, boolean showGraph) {
        String prefix = showGraph ? "│ " : "";

        if (showGraph) {
            System.out.println("●");
        }

        System.out.println(prefix + OutputFormatter.boldStr("commit " + commit.getId()));

        if (commit.isMergeCommit()) {
            String parents = String.join(" ", commit.getParentIds());
            String shortParents = parents.length() > 14 ? parents.substring(0, 14) : parents;
            System.out.println(prefix + "Merge: " + shortParents);
        }

        System.out.println(prefix + "Author: " + commit.getAuthor());
        System.out.println(prefix + "Date:   " + formatTimestamp(commit.getTimestamp()));
        OutputFormatter.blank();

        // Indent commit message
        for (String line : commit.getMessage().split("\n")) {
            System.out.println(prefix + "    " + line);
        }
    }

    private List<Commit> getCommitHistory(Repository repo, String startCommitId, Integer limit) throws Exception {
        List<Commit> commits = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(startCommitId);

        while (!queue.isEmpty() && (limit == null || commits.size() < limit)) {
            String commitId = queue.poll();

            if (visited.contains(commitId)) {
                continue;
            }
            visited.add(commitId);

            CoboltObject obj = repo.readObject(commitId);
            if (!(obj instanceof Commit)) {
                continue;
            }

            Commit commit = (Commit) obj;
            commits.add(commit);

            // Add parents to queue
            for (String parentId : commit.getParentIds()) {
                if (!visited.contains(parentId)) {
                    queue.add(parentId);
                }
            }
        }

        return commits;
    }

    private String formatTimestamp(long timestamp) {
        return DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochSecond(timestamp));
    }
}
