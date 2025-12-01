package com.cobolt.cli;

import com.cobolt.core.Blob;
import com.cobolt.core.Repository;
import com.cobolt.objects.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Add files to staging area
 */
@Command(name = "add", description = "Add file contents to the staging area")
public class AddCommand implements Callable<Integer> {

    @Parameters(description = "Files to add (supports '.' for all files)")
    private List<String> files;

    @Override
    public Integer call() {
        try {
            Path repoRoot = Repository.findRepositoryRoot(Paths.get("").toAbsolutePath());
            if (repoRoot == null) {
                OutputFormatter.error("Not a Cobolt repository");
                return 1;
            }

            Repository repo = new Repository(repoRoot);
            int filesAdded = 0;

            for (String filePattern : files) {
                if (filePattern.equals(".")) {
                    // Add all files in working directory
                    filesAdded += addDirectory(repo, repoRoot);
                } else {
                    Path filePath = repoRoot.resolve(filePattern);
                    if (!Files.exists(filePath)) {
                        OutputFormatter.warning("File not found: " + filePattern);
                        continue;
                    }

                    if (Files.isDirectory(filePath)) {
                        filesAdded += addDirectory(repo, filePath);
                    } else {
                        addFile(repo, repoRoot, filePath);
                        filesAdded++;
                    }
                }
            }

            repo.saveIndex();

            if (filesAdded > 0) {
                OutputFormatter.success("Added " + filesAdded + " file(s) to staging area");
            } else {
                OutputFormatter.info("No files to add");
            }

            return 0;
        } catch (Exception e) {
            OutputFormatter.error("Failed to add files: " + e.getMessage());
            return 1;
        }
    }

    private void addFile(Repository repo, Path repoRoot, Path filePath) throws Exception {
        // Skip .cobolt directory
        if (filePath.startsWith(repo.getCoboltDir())) {
            return;
        }

        String relativePath = repoRoot.relativize(filePath).toString();
        byte[] content = FileUtils.readBytes(filePath);

        Blob blob = new Blob(content);
        String blobId = repo.writeObject(blob);

        String mode = Files.isExecutable(filePath) ? "100755" : "100644";
        repo.getIndex().add(relativePath, blobId, mode);
    }

    private int addDirectory(Repository repo, Path dir) throws Exception {
        int count = 0;
        List<Path> files = FileUtils.listFilesRecursively(dir);

        for (Path file : files) {
            if (!file.startsWith(repo.getCoboltDir())) {
                addFile(repo, repo.getWorkingDir(), file);
                count++;
            }
        }

        return count;
    }
}
