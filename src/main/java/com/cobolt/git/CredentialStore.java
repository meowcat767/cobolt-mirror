package com.cobolt.git;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.IOException;
import java.util.*;

/**
 * Manages credential storage in Git config file
 */
public class CredentialStore {

    private static final String CREDENTIAL_SECTION = "credential";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private final Repository repository;

    public CredentialStore(Repository repository) {
        this.repository = repository;
    }

    /**
     * Save credentials for a specific URI
     */
    public void saveCredentials(URIish uri, String username, String password) throws IOException {
        StoredConfig config = repository.getConfig();
        String subsection = getSubsectionName(uri);

        config.setString(CREDENTIAL_SECTION, subsection, USERNAME_KEY, username);
        config.setString(CREDENTIAL_SECTION, subsection, PASSWORD_KEY, password);

        config.save();
    }

    /**
     * Get stored credentials for a URI
     * Returns null if no credentials are stored
     */
    public StoredCredential getCredentials(URIish uri) {
        StoredConfig config = repository.getConfig();
        String subsection = getSubsectionName(uri);

        String username = config.getString(CREDENTIAL_SECTION, subsection, USERNAME_KEY);
        String password = config.getString(CREDENTIAL_SECTION, subsection, PASSWORD_KEY);

        if (username != null && password != null) {
            return new StoredCredential(username, password);
        }

        return null;
    }

    /**
     * Check if credentials exist for a URI
     */
    public boolean hasCredentials(URIish uri) {
        return getCredentials(uri) != null;
    }

    /**
     * Remove credentials for a specific URI
     */
    public void clearCredentials(URIish uri) throws IOException {
        StoredConfig config = repository.getConfig();
        String subsection = getSubsectionName(uri);

        config.unsetSection(CREDENTIAL_SECTION, subsection);
        config.save();
    }

    /**
     * List all stored credential URIs
     */
    public List<String> listCredentialUrls() {
        StoredConfig config = repository.getConfig();
        Set<String> subsections = config.getSubsections(CREDENTIAL_SECTION);

        return new ArrayList<>(subsections);
    }

    /**
     * Clear all stored credentials
     */
    public void clearAllCredentials() throws IOException {
        StoredConfig config = repository.getConfig();
        Set<String> subsections = config.getSubsections(CREDENTIAL_SECTION);

        for (String subsection : subsections) {
            config.unsetSection(CREDENTIAL_SECTION, subsection);
        }

        config.save();
    }

    /**
     * Get subsection name from URI
     * Uses the full URL as the subsection name
     */
    private String getSubsectionName(URIish uri) {
        // Remove user info from URI to use as key
        String url = uri.toString();

        // Remove any existing credentials from the URL
        if (uri.getUser() != null) {
            url = url.replace(uri.getUser() + "@", "");
        }

        return url;
    }

    /**
     * Stored credential data
     */
    public static class StoredCredential {
        private final String username;
        private final String password;

        public StoredCredential(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
