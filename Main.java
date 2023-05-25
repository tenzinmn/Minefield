// Importing the Scanner class to get input from the user.
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Minefield!");

        // Setting up variables that are going to be determined based on the level of difficulty the user selects.
        int rows = 0, columns = 0, mines = 0, flags = 0;
        String level = "";

        // Looping until the user selects a valid difficulty level (easy, medium, or hard).
        while (!level.equals("easy") && !level.equals("medium") && !level.equals("hard")) {
            // Asking the user to select a difficulty level.
            System.out.println("Please select a level (easy, medium, or hard): ");
            level = scanner.nextLine().toLowerCase();

            // Assigning values to the variables based on the user's selection.
            switch (level) {
                case "easy":
                    rows = 5;
                    columns = 5;
                    mines = 5;
                    flags = 5;
                    break;
                case "medium":
                    rows = 9;
                    columns = 9;
                    mines = 12;
                    flags = 12;
                    break;
                case "hard":
                    rows = 20;
                    columns = 20;
                    mines = 40;
                    flags = 40;
                    break;
                default:
                    System.out.println("Invalid level. Please try again.");
                    break;
            }
        }

        // Prompting the user to select debug mode and storing the value in a boolean variable.
        boolean debugMode = false;
        System.out.println("Would you like to play in debug mode? (y/n)");
        String debugInput = scanner.nextLine().toLowerCase();
        if (debugInput.equals("y")) {
            debugMode = true;
        }

        // Creating a Minefield object with the selected rows, columns, and flags.
        Minefield minefield = new Minefield(rows, columns, flags);

        // Placing the mines randomly in the minefield.
        minefield.createMines(-1, -1, mines);

        // Adding a newline before printing the minefield.
        System.out.println();
        minefield.printMinefield(debugMode);

        // Looping until the game is over.
        boolean firstIteration = true;
        while (!minefield.gameOver()) {
            int x, y;

            if (firstIteration) {
                // Prompting the user to enter starting coordinates.
                System.out.print("Enter starting coordinates: [x] [y] ");
                x = scanner.nextInt();
                y = scanner.nextInt();
                firstIteration = false;
            } else {
                // Prompting the user to enter coordinates and whether or not they want to place a flag.
                System.out.printf("\nEnter a coordinate and if you wish to place a flag (Remaining: %2d): [x] [y] [f (-1, else) ", flags);
                x = scanner.nextInt();
                y = scanner.nextInt();
            }

            // Prompting the user to select whether they want to guess or flag the selected square.
            System.out.print("Would you like to guess (g) or flag (f)? ");
            String guessInput = scanner.next().toLowerCase();
            boolean flag = guessInput.equals("f");

            // Guessing or flagging the selected square and updating the minefield.
            boolean mineFound = minefield.guess(x, y, flag);

            // Adding a newline before printing the minefield.
            System.out.println();
            minefield.printMinefield(debugMode);

            if (flag) {
                // Updating the number of remaining flags.
                flags--;
            }

            if (mineFound) {
                // If the player found a mine, the game is over.
                System.out.println("Oh no! You found a mine. Game over.");
                return;
            }
        }

        // If the game is over and the while loop exited, the player won.
        System.out.println("Congratulations! You won!");
    }
}
