package com.inferris;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class DurationTracker {
    private static final String FILE_NAME = "event_durations.txt";
    private static Mode currentMode = Mode.PROGRAM;

    public static void main(String[] args) {
        currentMode = Mode.PROGRAM;

        boolean running = true;
        while (running) {
            running = onChat();
        }
    }

    public static boolean onChat() {
        Scanner scanner = new Scanner(System.in);

        switch (currentMode) {
            case PROGRAM -> {
                System.out.println("Enter event duration in milliseconds (or 'menu' for menu, 'exit' to quit):");
                addDuration(scanner.nextLine());
            }
            case MAIN_MENU -> {
                System.out.print("Enter a command ('program', 'list', 'exit'): ");
                handleMenuCommand(scanner.nextLine());
            }
            case EXIT -> {
                sendMessage("Exiting program.");
                return false;
            }
        }
        return true;
    }

    private static void handleMenuCommand(String command) {
        switch (command.toLowerCase()) {
            case "program" -> setMode(Mode.PROGRAM);
            case "list" -> {
                displayDurations();
            }
            case "exit" -> setMode(Mode.EXIT);
            default -> System.out.println("Invalid command. Please try again.");
        }
    }

    private static void setMode(Mode mode) {
        currentMode = mode;
        if(mode == Mode.MAIN_MENU){
            sendMessage("- Main menu -\n\n* Program - return to the program\n* List - list all recorded durations\n* Exit - exit program");
        }
    }

    private static void addDuration(String argument) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            if (argument.equalsIgnoreCase("menu")) {
                setMode(Mode.MAIN_MENU);
            } else if (argument.equalsIgnoreCase("exit")) {
                setMode(Mode.EXIT);
            } else {
                int duration = Integer.parseInt(argument);
                ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                String timestamp = now.format(DateTimeFormatter.ofPattern("HH:mm:ss 'UTC'"));
                printWriter.println(String.format("[%s] " + "Duration recorded: " + duration + "ms", timestamp));
                sendMessage("Duration recorded: " + duration + "ms");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid duration in milliseconds.");
        } catch (IOException e) {
            System.out.println("Error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    private static void displayDurations() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            if (lines.isEmpty()) {
                System.out.println("No recorded durations.");
            } else {
                System.out.println("Recorded durations:");
                for (String line : lines) {
                    System.out.println(line + "ms");
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    private static void sendMessage(String message) {
        System.out.println(message);
    }

    private enum Mode {
        PROGRAM, MAIN_MENU, EXIT
    }
}
