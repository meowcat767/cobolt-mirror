package com.cobolt.cli;

import java.time.LocalDate;
import java.time.Month;

/**
 * Quick test to demonstrate the Christmas message
 */
public class ChristmasTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Banner Display ===\n");

        System.out.println("Current date: " + LocalDate.now());
        System.out.println("Is Christmas: " + isChristmas());
        System.out.println();

        System.out.println("Normal banner:");
        Banner.print();

        System.out.println("\n=== What it would look like on Christmas: ===");
        System.out.println("(Simulated - the actual banner checks the system date)");
        System.out.println();
        System.out.println("   ____      _           _ _   ");
        System.out.println("  / ___|___ | |__   ___ | | |_ ");
        System.out.println(" | |   / _ \\| '_ \\ / _ \\| | __|");
        System.out.println(" | |__| (_) | |_) | (_) | | |_ ");
        System.out.println("  \\____\\___/|_.__/ \\___/|_|\\__|");
        System.out.println();
        System.out.println("  A Modern Version Control System");
        System.out.println("  🎄 Merry Christmas from the Cobolt team! 🎁");
        System.out.println();
    }

    private static boolean isChristmas() {
        LocalDate today = LocalDate.now();
        return today.getMonth() == Month.DECEMBER && today.getDayOfMonth() == 25;
    }
}
