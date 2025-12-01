package com.cobolt.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Main CLI entry point for Cobolt
 */
@Command(name = "cobolt", mixinStandardHelpOptions = true, version = "Cobolt 1.0.0", description = "A modern version control system with improved merge handling and CLI", subcommands = {
        InitCommand.class,
        AddCommand.class,
        CommitCommand.class,
        StatusCommand.class,
        LogCommand.class,
        DiffCommand.class,
        BranchCommand.class,
        CheckoutCommand.class,
        MergeCommand.class,
        TagCommand.class,
        ResetCommand.class,
        ShowCommand.class
})
public class CoboltCLI implements Runnable {

    @Option(names = { "-C", "--directory" }, description = "Run as if cobolt was started in <path>")
    private String directory;

    @Option(names = { "--no-color" }, description = "Disable colored output")
    private boolean noColor;

    @Option(names = { "-v", "--verbose" }, description = "Verbose output")
    private boolean verbose;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CoboltCLI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // Show banner
        Banner.print();
        // Show help if no subcommand
        CommandLine.usage(this, System.out);
    }
}
