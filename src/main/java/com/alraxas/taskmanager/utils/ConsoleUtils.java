package com.alraxas.taskmanager.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static void printLine(String line) {
        System.out.println(line);
    }

    public static void print(String message) {
        System.out.print(message);
    }

    public static void printLine() {
        System.out.println();
    }

    public static void printSeparator() {
        printLine("-".repeat(15));
    }

    public static void printTitle(String title) {
        printLine();
        printSeparator();
        printLine("   " + title.toUpperCase());
        printSeparator();
    }

    public static void printSubtitle(String subtitle) {
        printLine();
        printLine("-- " + subtitle + " --");
    }

    public static void printError(String message) {
        printLine("Error: " + message);
    }

    public static void printWarning(String message) {
        printLine("!!! " + message);
    }

    public static void printInfo(String message) {
        printLine("Info: " + message);
    }

    public static void printProcess(String message) {
        printLine("Progress: " + message);
    }

    public static String readString(String prompt) {
        print(prompt + ": ");
        String input = scanner.nextLine().trim();

        while (input.isEmpty()) {
            printError("Text field can not be empty!");
            print(prompt + ": ");
            input = scanner.nextLine().trim();
        }

        return input;
    }

    public static String readString(String prompt, String defaultValue) {
        print(prompt + " [" + defaultValue + "]: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Please, enter a number!");
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            printError(String.format("Number should be in range %d to %d!", min, max));
        }
    }

    public static long readLong(String prompt) {
        while (true) {
            try {
                print(prompt + ": ");
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                printError("Please, enter a number!");
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            String input = readString(prompt + " (yes/no)").toLowerCase();

            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n")) {
                return false;
            } else {
                printError("Please, enter 'yes' or 'no'!");
            }
        }
    }

    public static LocalDateTime readTime(String prompt) {
        while (true) {
            try {
                String timeString = readString(prompt + " (format: HH:mm)");
                return TimeUtils.parseTimeToday(timeString);
            } catch (IllegalArgumentException e) {
                printError(e.getMessage());
            }
        }
    }

    public static LocalDateTime readDateTime(String prompt) {
        while (true) {
            try {
                String dateTimeString = readString(prompt + " (format: dd.MM.yyyy HH:mm)");
                return TimeUtils.parseDateTime(dateTimeString);
            } catch (IllegalArgumentException e) {
                printError(e.getMessage());
            }
        }
    }

    public static String readTaskPriority(String prompt) {
        printSubtitle("CHOOSE PRIORITY:");
        printLine("1. Low");
        printLine("2. Medium");
        printLine("3. High");
        printLine("4. Urgent");

        int choice = readInt(prompt + " (1-4)", 1, 4);

        return switch (choice) {
            case 1 -> "LOW";
            case 2 -> "MEDIUM";
            case 3 -> "HIGH";
            case 4 -> "URGENT";
            default -> "MEDIUM";
        };
    }

    public static boolean readAlarmType(String prompt) {
        printSubtitle("ALARM TYPE");
        printLine("1. NOT repeated");
        printLine("2. Repeated");

        int choice = readInt(prompt + " (1-2)", 1, 2);
        return choice == 2;
    }

    public static int showMenu(String title, String[] options) {
        printTitle(title);

        for (int i = 0; i < options.length; i++) {
            printLine((i + 1) + ". " + options[i]);
        }

        printLine();
        return readInt("Choose option", 1, options.length);
    }

    public static int showMenuWithExit(String title, String exitOption, String[] options) {
        printTitle(title);

        for (int i = 0; i < options.length; i++) {
            printLine((i + 1) + ". " + options[i]);
        }
        printLine("0. " + exitOption);

        printLine();
        int choice = readInt("Choose option", 0, options.length);
        return choice == 0 ? -1 : choice;
    }

    public static boolean confirmAction(String message) {
        return readBoolean(message);
    }

    public static void waitForEnter() {
        printLine("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            printLine();
        }
    }

    public static void printList(String title, List<?> items) {
        if (items == null || items.isEmpty()) {
            printInfo("List '" + title + " is empty");
            return;
        }

        printTitle(title);
        for (int i = 0; i < items.size(); i++) {
            printLine((i + 1) + ". " + items.get(i).toString());
        }
        printLine();
    }

    public static <T> T selectFromList(String prompt, List<T> items) {
        if (items == null || items.isEmpty()) {
            printWarning("List is empty");
            return null;
        }

        printList(prompt, items);
        int choice = readInt("Choose item", 1, items.size());
        return items.get(choice - 1);
    }

    public static <T> List<T> selectMultipleFromList(String prompt, List<T> items) {
        if (items == null || items.isEmpty()) {
            printWarning("List is empty");
            return new ArrayList<>();
        }

        printList(prompt, items);
        printLine("Enter items (e.g.: 1,3,5)");
        printLine("Or enter 'all' to choose all items");

        while (true) {
            try {
                String input = readString("Chosen").trim();

                // Обработка выбора всех элементов
                if (input.equalsIgnoreCase("all")) {
                    return new ArrayList<>(items);
                }

                String[] indices = input.split(",");
                List<T> selected = new ArrayList<>();

                for (String indexStr : indices) {
                    int index = Integer.parseInt(indexStr.trim()) - 1;
                    if (index >= 0 && index < items.size()) {
                        T item = items.get(index);
                        if (!selected.contains(item)) {
                            selected.add(item);
                        }
                    }
                }

                if (selected.isEmpty()) {
                    printError("No item is selected!");
                    continue;
                }

                printLine("Selected items: " + selected.size());
                return selected;

            } catch (NumberFormatException e) {
                printError("Wrong enter format!");
            }
        }
    }

    public static void printFormattedText(String text, int lineLength) {
        if (text == null || text.isEmpty()) {
            return;
        }

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > lineLength) {
                printLine(line.toString());
                line = new StringBuilder();
            }
            if (!line.isEmpty()) {
                line.append(" ");
            }
            line.append(word);
        }

        if (!line.isEmpty()) {
            printLine(line.toString());
        }
    }

    public static void showProgress(int current, int total, String message) {
        int percent = (int) ((double) current / total * 100);
        int bars = percent / 2; // 50 символов = 100%

        String progressBar = "[" + "=".repeat(bars) + " ".repeat(50 - bars) + "]";
        print(String.format("\r%s %d%% %s", progressBar, percent, message));

        // Если достигнут конец, перейти на новую строку
        if (current == total) {
            printLine();
        }
    }

    public static void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
