package com.cobolt.cli;

import com.cobolt.core.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Create, list, or delete tags
 */
@Command(name = "tag", description = "Create, list, or delete tags")
public class TagCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Tag name to create", defaultValue = "")
    private String tagName;

    @picocli.CommandLine.Option(names = { "-m", "--message" }, description = "Tag message (for annotated tags)")
    private String message;

    @picocli.CommandLine.Option(names = { "-d", "--delete" }, description = "Delete tag")
    private boolean delete;

    @picocli.CommandLine.Option(names = { "-l", "--list" }, description = "List tags")
    private boolean list;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);

            if (delete) {
                return deleteTag(repo, tagName);
            } else if (list || tagName.isEmpty()) {
                return listTags(repo);
            } else {
                return createTag(repo, tagName);
            }
        } catch (Exception e) {
            OutputFormatter.error("Failed to manage tags: " + e.getMessage());
            return 1;
        }
    }

    private int createTag(Repository repo, String name) throws Exception {
        String headCommitId = repo.resolveRef("HEAD");
        if (headCommitId == null) {
            OutputFormatter.error("Cannot create tag - no commits yet");
            return 1;
        }

        repo.createTag(name, headCommitId);
        OutputFormatter.success("Created tag " + OutputFormatter.hash(name) + " at " + headCommitId.substring(0, 7));

        return 0;
    }

    private int deleteTag(Repository repo, String name) throws Exception {
        if (name.isEmpty()) {
            OutputFormatter.error("Please specify a tag name to delete");
            return 1;
        }

        repo.deleteTag(name);
        OutputFormatter.success("Deleted tag " + name);

        return 0;
    }

    private int listTags(Repository repo) throws Exception {
        var tags = repo.listTags();

        if (tags.isEmpty()) {
            OutputFormatter.info("No tags yet");
            return 0;
        }

        OutputFormatter.blank();
        OutputFormatter.header("Tags:");
        OutputFormatter.blank();

        for (String tag : tags) {
            System.out.println("  " + OutputFormatter.hash(tag));
        }
        OutputFormatter.blank();

        return 0;
    }
}
