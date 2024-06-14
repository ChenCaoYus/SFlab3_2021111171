package org.example;

import java.util.*;
import java.io.*;

public class Main {
    private static final Set<Character> delimiters = new HashSet<>(Arrays.asList(',', '.', '!', '(', ')', '-', '+', '=', '[', ']', '{', '}', ':', ';', '"', '\'', '<', '>', '?'));
    private static final Set<String> mermaidKeywords = new HashSet<>(Arrays.asList("graph", "subgraph", "end", "style", "click", "direction", "fill", "stroke"));

    public static void main(String[] args) throws IOException {
        TextGraph tg = null;
        String str1, str2;
        int command;
        List<String> v;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            clearScreen();
            System.out.println("\t\t\tWelcome to the TextGraph System");
            System.out.println("1. Create a graph with a text file");
            System.out.println("2. Show the graph");
            System.out.println("3. Query the bridge words");
            System.out.println("4. Generate a new text");
            System.out.println("5. Calculate the shortest path");
            System.out.println("6. Random walk");
            System.out.println("7. Exit");
            System.out.print("\nPlease input the command: ");
            command = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            if (tg == null && command != 1 && command != 7) {
                // output with red color
                System.out.println("\033[31mCreate a graph first!\033[0m");
                try { Thread.sleep(1000); } catch (InterruptedException e) { }
                continue;
            }
            if (command == 7) {
                break;
            }
            switch (command) {
                case 1:
                    System.out.print("Input the file name: ");
                    str1 = scanner.nextLine();
                    tg = new TextGraph(str1);
                    break;
                case 2:
                    tg.showGraph();
                    break;
                case 3:
                    System.out.print("Input two words: ");
                    str1 = scanner.next();
                    str2 = scanner.next();
                    v = tg.queryBridgeWords(str1, str2);
                    if (v.get(0).contains("No")) {
                        System.out.println(v.get(0));
                    } else {
                        System.out.print("The bridge words from " + str1 + " to " + str2 + " are: ");
                        for (int i = 0; i < v.size(); i++) {
                            System.out.print(v.get(i));
                            if (i != v.size() - 1) {
                                System.out.print(", ");
                            }
                        }
                        System.out.println();
                    }
                    break;
                case 4:
                    System.out.print("Input a text: ");
                    str1 = scanner.nextLine();
                    System.out.println("The new text is: " + tg.generateNewText(str1));
                    break;
                case 5:
                    System.out.print("Input two words: ");
                    str1 = scanner.next();
                    str2 = scanner.next();
                    v = tg.calcShortestPath(str1, str2);
                    if (v.isEmpty()) {
                        System.out.println("\033[31mNo path from " + str1 + " to " + str2 + "\033[0m");
                    } else {
                        System.out.println("The shortest path from " + str1 + " to " + str2 + " is: ");
                        for (int i = 0; i < v.size(); i++) {
                            System.out.print(v.get(i));
                            if (i != v.size() - 1) {
                                System.out.print(" -> ");
                            }
                        }
                        System.out.println();
                    }
                    break;
                case 6:
                    System.out.println("The random walk is: ");
                    System.out.println(tg.randomWalk());
                    break;
                default:
                    System.out.println("Invalid command!");
                    break;
            }
            try { Thread.sleep(2000); } catch (InterruptedException e) { }
        }

        clearScreen();
        System.out.println("\t\tGood Bye!");
        try { Thread.sleep(1000); } catch (InterruptedException e) { }
    }

    private static void clearScreen() {
        // For Windows
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // For Linux and macOS
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
