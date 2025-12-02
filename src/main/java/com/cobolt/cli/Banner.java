package com.cobolt.cli;

import org.fusesource.jansi.Ansi;

import java.time.LocalDate;
import java.time.Month;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * ASCII art banner for Cobolt
 */
public class Banner {

    private static final String LOGO = "   ____      _           _ _   \n" +
            "  / ___|___ | |__   ___ | | |_ \n" +
            " | |   / _ \\| '_ \\ / _ \\| | __|\n" +
            " | |__| (_) | |_) | (_) | | |_ \n" +
            "  \\____\\___/|_.__/ \\___/|_|\\__|\n";

    private static final String TAGLINE = "  A Modern Version Control System";

    /**
     * Print the Cobolt banner in blue
     */
    public static void print() {
        if (OutputFormatter.colorEnabled) {
            System.out.println(ansi().fgBright(Ansi.Color.CYAN).a(LOGO).reset());
            System.out.println(ansi().fgBright(Ansi.Color.BLUE).a(TAGLINE).reset());
        } else {
            System.out.println(LOGO);
            System.out.println(TAGLINE);
        }

        // Add Christmas message if it's December 25th
        if (isChristmas()) {
            String christmasMessage = "  🎄 Merry Christmas from the Cobolt team! 🎁";
            if (OutputFormatter.colorEnabled) {
                System.out.println(ansi().fgBright(Ansi.Color.GREEN).a(christmasMessage).reset());
            } else {
                System.out.println(christmasMessage);
            }
        }

        System.out.println();
    }

    /**
     * Print compact version (just name)
     */
    public static void printCompact() {
        if (OutputFormatter.colorEnabled) {
            String cobolt = ansi().fgBright(Ansi.Color.CYAN).bold().a("COBOLT").reset().toString();
            String vcs = ansi().fgBright(Ansi.Color.BLUE).a(" VCS").reset().toString();
            System.out.println(cobolt + vcs);
        } else {
            System.out.println("COBOLT VCS");
        }
    }

    /**
     * Check if today is Christmas (December 25th)
     */
    private static boolean isChristmas() {
        LocalDate today = LocalDate.now();
        return today.getMonth() == Month.DECEMBER && today.getDayOfMonth() == 25;
    }
}
