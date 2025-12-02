package com.cobolt.cli;

import com.cobolt.git.GitAdapter;
import com.cobolt.git.GitCommands;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Manage remote repositories
 */
@Command(name = "remote", description = "Manage set of tracked repositories")
public class RemoteCommand implements Callable<Integer> {

    @Option(names = { "-a", "--add" }, description = "Add a new remote")
    private boolean add;

    @Option(names = { "-d", "--delete" }, description = "Remove a remote")
    private boolean delete;

    @Option(names = { "-v", "--verbose" }, description = "Show remote URLs")
    private boolean verbose;

    @Parameters(index = "0", description = "Remote name", defaultValue = "")
    private String remoteName;

    @Parameters(index = "1", description = "Remote URL (for add operation)", defaultValue = "")
    private String remoteUrl;

    @Override
    public Integer call() {
        try {
            // Find Git repository root
            Path currentPath = Paths.get("").toAbsolutePath();
            GitAdapter adapter = new GitAdapter(currentPath);
            GitCommands git = new GitCommands(currentPath);

            if (add) {
                return addRemote(git);
            } else if (delete) {
                return deleteRemote(git);
            } else {
                return listRemotes(git);
            }
        } catch (Exception e) {
            OutputFormatter.error("Failed to manage remotes: " + e.getMessage());
            return 1;
        }
    }

    private int addRemote(GitCommands git) throws Exception {
        if (remoteName.isEmpty() || remoteUrl.isEmpty()) {
            OutputFormatter.error("Please specify both remote name and URL");
            OutputFormatter.info("Usage: cobolt remote -a <name> <url>");
            return 1;
        }

        // Check if remote already exists
        Map<String, String> remotes = git.listRemotes();
        if (remotes.containsKey(remoteName)) {
            OutputFormatter.error("Remote '" + remoteName + "' already exists");
            return 1;
        }

        git.addRemote(remoteName, remoteUrl);
        OutputFormatter.success("Added remote " + OutputFormatter.boldStr(remoteName) + " → " + remoteUrl);

        return 0;
    }

    private int deleteRemote(GitCommands git) throws Exception {
        if (remoteName.isEmpty()) {
            OutputFormatter.error("Please specify a remote name to delete");
            OutputFormatter.info("Usage: cobolt remote -d <name>");
            return 1;
        }

        // Check if remote exists
        Map<String, String> remotes = git.listRemotes();
        if (!remotes.containsKey(remoteName)) {
            OutputFormatter.error("Remote '" + remoteName + "' does not exist");
            return 1;
        }

        git.removeRemote(remoteName);
        OutputFormatter.success("Removed remote " + remoteName);

        return 0;
    }

    private int listRemotes(GitCommands git) throws Exception {
        Map<String, String> remotes = git.listRemotes();

        if (remotes.isEmpty()) {
            OutputFormatter.info("No remotes configured");
            OutputFormatter.blank();
            OutputFormatter.info("Add a remote with: cobolt remote -a <name> <url>");
            return 0;
        }

        OutputFormatter.blank();
        OutputFormatter.header("Remotes:");
        OutputFormatter.blank();

        for (Map.Entry<String, String> entry : remotes.entrySet()) {
            if (verbose) {
                System.out.println("  " + OutputFormatter.boldStr(entry.getKey()) + " → " + entry.getValue());
            } else {
                System.out.println("  " + entry.getKey());
            }
        }
        OutputFormatter.blank();

        return 0;
    }
}
