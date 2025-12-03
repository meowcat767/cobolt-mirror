package com.cobolt.cli;

import com.cobolt.git.CredentialStore;
import com.cobolt.git.GitAdapter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Manage stored Git credentials
 */
@Command(name = "credential", description = "Manage stored Git credentials", subcommands = {
        CredentialCommand.ListCommand.class,
        CredentialCommand.RemoveCommand.class,
        CredentialCommand.ClearCommand.class
})
public class CredentialCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        // Show usage if no subcommand
        System.out.println("Usage: cobolt credential <subcommand>");
        System.out.println();
        System.out.println("Subcommands:");
        System.out.println("  list          List stored credential URLs");
        System.out.println("  remove <url>  Remove credentials for a specific URL");
        System.out.println("  clear         Remove all stored credentials");
        return 0;
    }

    @Command(name = "list", description = "List stored credential URLs")
    static class ListCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            try {
                Path currentPath = Paths.get("").toAbsolutePath();

                if (!GitAdapter.isGitRepository(currentPath)) {
                    OutputFormatter.error("Not a git repository");
                    return 1;
                }

                GitAdapter adapter = new GitAdapter(currentPath);
                CredentialStore store = new CredentialStore(adapter.getRepository());

                List<String> urls = store.listCredentialUrls();

                if (urls.isEmpty()) {
                    OutputFormatter.info("No stored credentials");
                    return 0;
                }

                OutputFormatter.success("Stored credentials for:");
                for (String url : urls) {
                    System.out.println("  • " + url);
                }

                adapter.close();
                return 0;

            } catch (Exception e) {
                OutputFormatter.error("Failed to list credentials: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "remove", description = "Remove credentials for a specific URL")
    static class RemoveCommand implements Callable<Integer> {
        @Parameters(index = "0", description = "Remote URL")
        private String url;

        @Override
        public Integer call() {
            try {
                Path currentPath = Paths.get("").toAbsolutePath();

                if (!GitAdapter.isGitRepository(currentPath)) {
                    OutputFormatter.error("Not a git repository");
                    return 1;
                }

                GitAdapter adapter = new GitAdapter(currentPath);
                CredentialStore store = new CredentialStore(adapter.getRepository());

                org.eclipse.jgit.transport.URIish uri = new org.eclipse.jgit.transport.URIish(url);

                if (!store.hasCredentials(uri)) {
                    OutputFormatter.warning("No credentials stored for: " + url);
                    adapter.close();
                    return 1;
                }

                store.clearCredentials(uri);
                OutputFormatter.success("Removed credentials for: " + url);

                adapter.close();
                return 0;

            } catch (Exception e) {
                OutputFormatter.error("Failed to remove credentials: " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "clear", description = "Remove all stored credentials")
    static class ClearCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            try {
                Path currentPath = Paths.get("").toAbsolutePath();

                if (!GitAdapter.isGitRepository(currentPath)) {
                    OutputFormatter.error("Not a git repository");
                    return 1;
                }

                GitAdapter adapter = new GitAdapter(currentPath);
                CredentialStore store = new CredentialStore(adapter.getRepository());

                store.clearAllCredentials();
                OutputFormatter.success("All credentials cleared");

                adapter.close();
                return 0;

            } catch (Exception e) {
                OutputFormatter.error("Failed to clear credentials: " + e.getMessage());
                return 1;
            }
        }
    }
}
