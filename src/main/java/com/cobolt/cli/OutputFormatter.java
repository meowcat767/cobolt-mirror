package com.cobolt.cli;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Rich terminal formatting utilities for beautiful CLI output
 */
public class OutputFormatter {

    public static boolean colorEnabled = true;

    static {
        AnsiConsole.systemInstall();
    }

    /**
     * Enable or disable colors
     */
    public static void setColorEnabled(boolean enabled) {
        colorEnabled = enabled;
    }

    /**
     * Print success message
     */
    public static void success(String message) {
        if (colorEnabled) {
            System.out.println(ansi().fgBrightGreen().a("✓ ").a(message).reset());
        } else {
            System.out.println("✓ " + message);
        }
    }

    /**
     * Print error message
     */
    public static void error(String message) {
        if (colorEnabled) {
            System.err.println(ansi().fgBrightRed().a("✗ ").a(message).reset());
        } else {
            System.err.println("✗ " + message);
        }
    }

    /**
     * Print warning message
     */
    public static void warning(String message) {
        if (colorEnabled) {
            System.out.println(ansi().fgBrightYellow().a("⚠ ").a(message).reset());
        } else {
            System.out.println("⚠ " + message);
        }
    }

    /**
     * Print info message
     */
    public static void info(String message) {
        if (colorEnabled) {
            System.out.println(ansi().fgBrightCyan().a("ℹ ").a(message).reset());
        } else {
            System.out.println("ℹ " + message);
        }
    }

    /**
     * Print section header
     */
    public static void header(String text) {
        if (colorEnabled) {
            System.out.println(ansi().bold().fgBright(Ansi.Color.WHITE).a(text).reset());
        } else {
            System.out.println(text);
        }
    }

    /**
     * Print with specific color
     */
    public static void colored(String text, Ansi.Color color) {
        if (colorEnabled) {
            System.out.println(ansi().fg(color).a(text).reset());
        } else {
            System.out.println(text);
        }
    }

    /**
     * Format added line (green with +)
     */
    public static String added(String line) {
        if (colorEnabled) {
            return ansi().fgGreen().a("+ ").a(line).reset().toString();
        }
        return "+ " + line;
    }

    /**
     * Format removed line (red with -)
     */
    public static String removed(String line) {
        if (colorEnabled) {
            return ansi().fgRed().a("- ").a(line).reset().toString();
        }
        return "- " + line;
    }

    /**
     * Format modified line (yellow with ~)
     */
    public static String modified(String line) {
        if (colorEnabled) {
            return ansi().fgYellow().a("~ ").a(line).reset().toString();
        }
        return "~ " + line;
    }

    /**
     * Format path/file name
     */
    public static String path(String pathStr) {
        if (colorEnabled) {
            return ansi().fgCyan().a(pathStr).reset().toString();
        }
        return pathStr;
    }

    /**
     * Format commit hash
     */
    public static String hash(String hashStr) {
        if (colorEnabled) {
            return ansi().fgYellow().a(hashStr).reset().toString();
        }
        return hashStr;
    }

    /**
     * Format branch name
     */
    public static String branch(String branchName) {
        if (colorEnabled) {
            return ansi().fgGreen().a(branchName).reset().toString();
        }
        return branchName;
    }

    /**
     * Print horizontal separator
     */
    public static void separator() {
        System.out.println("─".repeat(60));
    }

    /**
     * Print blank line
     */
    public static void blank() {
        System.out.println();
    }

    /**
     * Print table row
     */
    public static void tableRow(String... columns) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            sb.append(String.format("%-25s", columns[i]));
            if (i < columns.length - 1) {
                sb.append(" │ ");
            }
        }
        System.out.println(sb.toString());
    }

    /**
     * Print progress indicator
     */
    public static void progress(String message) {
        if (colorEnabled) {
            System.out.print(ansi().fgBrightBlue().a("⟳ ").a(message).a("...").reset());
        } else {
            System.out.print("⟳ " + message + "...");
        }
    }

    /**
     * Clear progress line
     */
    public static void clearProgress() {
        System.out.print("\r" + " ".repeat(80) + "\r");
    }

    /**
     * Print bold text
     */
    public static void bold(String text) {
        if (colorEnabled) {
            System.out.println(ansi().bold().a(text).reset());
        } else {
            System.out.println(text);
        }
    }

    /**
     * Format bold text as string
     */
    public static String boldStr(String text) {
        if (colorEnabled) {
            return ansi().bold().a(text).reset().toString();
        }
        return text;
    }

    /**
     * Dim text (for less important info)
     */
    public static String dim(String text) {
        if (colorEnabled) {
            return ansi().fgBrightBlack().a(text).reset().toString();
        }
        return text;
    }
}
