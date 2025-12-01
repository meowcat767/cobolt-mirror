package com.cobolt.cli;

import com.cobolt.core.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Initialize a new Cobolt repository
 */
@Command(name = "init", description = "Initialize a new Cobolt repository")
public class InitCommand implements Callable<Integer> {

    @Parameters(index = "0", defaultValue = ".", description = "Directory to initialize repository in (default: current directory)")
    private String directory;

    @Override
    public Integer call() {
        try {
            Path path = Paths.get(directory).toAbsolutePath();

            if (Repository.isRepository(path)) {
                OutputFormatter.warning("Repository already exists at: " + path);
                return 1;
            }

            OutputFormatter.progress("Initializing repository");
            Repository.init(path);
            OutputFormatter.clearProgress();

            OutputFormatter.success("Initialized empty Cobolt repository in " +
                    OutputFormatter.path(path.resolve(".cobolt").toString()));

            return 0;
        } catch (Exception e) {
            OutputFormatter.error("Failed to initialize repository: " + e.getMessage());
            return 1;
        }
    }
}
