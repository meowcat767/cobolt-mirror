package com.cobolt.git;

import com.cobolt.cli.OutputFormatter;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

import java.io.Console;

/**
 * Handles interactive authentication for Git operations
 */
public class InteractiveCredentialsProvider extends CredentialsProvider {

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
        Console console = System.console();

        // Fallback if no console available (e.g. redirected input)
        if (console == null) {
            OutputFormatter.error("No interactive console available for authentication");
            return false;
        }

        OutputFormatter.info("Authentication required for " + uri);

        for (CredentialItem item : items) {
            if (item instanceof CredentialItem.Username) {
                String prompt = item.getPromptText();
                if (prompt == null || prompt.isEmpty()) {
                    prompt = "Username: ";
                }
                String username = console.readLine(prompt);
                ((CredentialItem.Username) item).setValue(username);
            } else if (item instanceof CredentialItem.Password) {
                String prompt = item.getPromptText();
                if (prompt == null || prompt.isEmpty()) {
                    prompt = "Password: ";
                }
                char[] password = console.readPassword(prompt);
                ((CredentialItem.Password) item).setValue(password);
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
        return true;
    }
}
