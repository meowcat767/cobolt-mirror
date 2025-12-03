package com.cobolt.git;

import com.cobolt.cli.OutputFormatter;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

import java.io.Console;

/**
 * Handles interactive authentication for Git operations with credential storage
 */
public class InteractiveCredentialsProvider extends CredentialsProvider {

    private final CredentialStore credentialStore;
    private String lastUsername;
    private String lastPassword;

    public InteractiveCredentialsProvider(Repository repository) {
        this.credentialStore = new CredentialStore(repository);
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public boolean supports(CredentialItem... items) {
        return true;
    }

    @Override
    public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
        // First, try to get stored credentials
        CredentialStore.StoredCredential stored = credentialStore.getCredentials(uri);

        if (stored != null) {
            // Use stored credentials
            for (CredentialItem item : items) {
                if (item instanceof CredentialItem.Username) {
                    ((CredentialItem.Username) item).setValue(stored.getUsername());
                } else if (item instanceof CredentialItem.Password) {
                    ((CredentialItem.Password) item).setValue(stored.getPassword().toCharArray());
                }
            }
            return true;
        }

        // No stored credentials, prompt user
        Console console = System.console();

        // Fallback if no console available (e.g. redirected input)
        if (console == null) {
            OutputFormatter.error("No interactive console available for authentication");
            return false;
        }

        OutputFormatter.info("Authentication required for " + uri);

        for (CredentialItem item : items) {
            if (item instanceof CredentialItem.Username) {
                String username = console.readLine("Username: ");
                ((CredentialItem.Username) item).setValue(username);
                lastUsername = username;
            } else if (item instanceof CredentialItem.Password) {
                char[] password = console.readPassword("Password: ");
                ((CredentialItem.Password) item).setValue(password);
                lastPassword = new String(password);
            } else if (item instanceof CredentialItem.StringType) {
                String prompt = item.getPromptText();
                String value = console.readLine(prompt + ": ");
                ((CredentialItem.StringType) item).setValue(value);
            } else if (item instanceof CredentialItem.YesNoType) {
                String prompt = item.getPromptText();
                String value = console.readLine(prompt + " [y/n]: ");
                ((CredentialItem.YesNoType) item).setValue(value != null && value.toLowerCase().startsWith("y"));
            } else if (item instanceof CredentialItem.InformationalMessage) {
                OutputFormatter.info(item.getPromptText());
            } else {
                throw new UnsupportedCredentialItem(uri, item.getClass().getName() + " is not supported");
            }
        }

        // Ask if user wants to save credentials
        if (lastUsername != null && lastPassword != null) {
            String save = console.readLine("Save credentials for this repository? [y/n]: ");
            if (save != null && save.toLowerCase().startsWith("y")) {
                try {
                    credentialStore.saveCredentials(uri, lastUsername, lastPassword);
                    OutputFormatter.success("Credentials saved");
                } catch (Exception e) {
                    OutputFormatter.warning("Failed to save credentials: " + e.getMessage());
                }
            }
        }

        return true;
    }

    /**
     * Reset stored credentials (called after failed authentication)
     */
    @Override
    public void reset(URIish uri) {
        try {
            if (credentialStore.hasCredentials(uri)) {
                OutputFormatter.warning("Authentication failed with stored credentials");
                credentialStore.clearCredentials(uri);
                OutputFormatter.info("Stored credentials cleared, please re-enter");
            }
        } catch (Exception e) {
            OutputFormatter.warning("Failed to clear credentials: " + e.getMessage());
        }
    }
}
