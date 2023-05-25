import java.util.Random;
import java.util.Scanner;
import java.awt.Color;

public class Minefield {
    /**
    Global Section: Define some ANSI color codes for use in printing the minefield
    */
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_GREY_BG = "\u001b[0m";

    // Instance variables
    private int rows;
    private int columns;
    private int mines;

    private int flags;
    private Cell[][] minefield;

    /**
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */


    public Minefield(int rows, int columns, int flags) {
        this.rows = rows;
        this.columns = columns;
        this.mines = flags;
        minefield = new Cell[rows][columns];

        // Initialize each cell in the minefield to be unrevealed and empty
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                minefield[i][j] = new Cell(false, "");
            }
        }
    }


    /**
     * evaluateField
     *
     * @function When a mine is found in the field, calculate the surrounding 9x9 tiles values. If a mine is found, increase the count for the square.
     */
    public void evaluateField() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (!minefield[i][j].getStatus().equals("M")) {
                    int adjacentMines = 0;

                    // Count the number of adjacent mines to this cell
                    for (int rowDelta = -1; rowDelta <= 1; rowDelta++) {
                        for (int colDelta = -1; colDelta <= 1; colDelta++) {
                            int newRow = i + rowDelta;
                            int newCol = j + colDelta;

                            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < columns &&
                                    minefield[newRow][newCol].getStatus().equals("M")) {
                                adjacentMines++;
                            }
                        }
                    }

                    // Update the status of this cell based on the number of adjacent mines
                    if (adjacentMines > 0) {
                        minefield[i][j].setStatus(Integer.toString(adjacentMines));
                    } else {
                        minefield[i][j].setStatus(" ");
                    }
                }
            }
        }
    }

    /**
     * createMines
     *
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        Random random = new Random();
        int placedMines = 0;

        while (placedMines < mines) {
            int row = random.nextInt(rows);
            int col = random.nextInt(columns);
            Cell cell = minefield[row][col];

            // Avoid placing a mine on the starting square, on a revealed cell, or on an already mined cell
            if ((row != x || col != y) && !cell.getRevealed() && !cell.getStatus().equals("M")) {
                cell.setStatus("M");
                placedMines++;
            }
        }

        // Update the minefield with the adjacent mine counts
        evaluateField();
    }



    /**
     * guess
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
        // Check if the guess is in-bounds
        if (x < 0 || x >= rows || y < 0 || y >= columns) {
            System.out.println("Invalid coordinates. Please try again.");
            return false;
        }

        Cell cell = minefield[x][y];

        // If the user wants to place a flag
        if (flag) {
            if (cell.getStatus().equals("F")) {
                cell.setStatus(" ");
            } else {
                cell.setStatus("F");
            }
            return false;
        }

        // If the cell is already revealed, do nothing
        if (cell.getRevealed()) {
            return false;
        }

        // Reveal the cell
        cell.setRevealed(true);

        // If the user hits a cell with a '0' status
        if (cell.getStatus().equals("0")) {
            revealZeroes(x, y);
            return false;
        }

        // If the user hits a mine
        if (cell.getStatus().equals("M")) {
            return true;
        }

        return false;
    }

    /**
     * gameOver
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        // Initialize the count of revealed cells to 0
        int revealedCells = 0;

        // Loop through each cell in the minefield
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = minefield[i][j];
                // If the cell has been revealed, increment the count of revealed cells
                if (cell.getRevealed()) {
                    revealedCells++;
                }
            }
        }
        // Calculate the total number of non-mine cells in the minefield
        int nonMineCells = rows * columns - mines;
        // If the number of revealed cells is equal to the number of non-mine cells, the game is over
        return revealedCells == nonMineCells;
    }


    /**
     * revealField
     *
     * This method should follow the psuedocode given.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        int[][] stack = new int[rows * columns][2];
        int top = 0;

        stack[top++] = new int[]{x, y};

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (top > 0) {
            int[] current = stack[--top]; // Simulate popping the top element
            int currentX = current[0];
            int currentY = current[1];

            minefield[currentX][currentY].setRevealed(true);

            if (minefield[currentX][currentY].getStatus().equals("0")) {
                for (int i = 0; i < 4; i++) {
                    int newX = currentX + dx[i];
                    int newY = currentY + dy[i];

                    if (newX >= 0 && newX < rows && newY >= 0 && newY < columns
                            && !minefield[newX][newY].getRevealed()) {
                        stack[top++] = new int[]{newX, newY}; // Simulate pushing an element onto the stack
                    }
                }
            }
        }
    }




    /**
     * revealMines
     *
     * This method should follow the psuedocode given.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealMines(int x, int y) {
        int[][] queue = new int[rows * columns][2];
        int head = 0;
        int tail = 0;

        // Start by adding the user's guess to the queue
        queue[tail++] = new int[]{x, y};

        while (head != tail) {
            int[] curr = queue[head++];
            int currX = curr[0];
            int currY = curr[1];

            // If the current cell is a mine, reveal it and stop searching
            if (minefield[currX][currY].getStatus().equals("M")) {
                minefield[currX][currY].setRevealed(true);
                break;
            }

            // If the current cell is not a mine, reveal it
            minefield[currX][currY].setRevealed(true);

            // Check all adjacent cells for mines and add them to the queue if they haven't been revealed yet
            for (int i = currX - 1; i <= currX + 1; i++) {
                for (int j = currY - 1; j <= currY + 1; j++) {
                    if (i >= 0 && i < rows && j >= 0 && j < columns && !minefield[i][j].getRevealed()) {
                        queue[tail++] = new int[]{i, j};
                    }
                }
            }
        }
    }



    /**
     * revealStart
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     */
    public void revealStart(int x, int y) {
        // Reveal the cell that the user guessed
        Cell cell = minefield[x][y];
        cell.setRevealed(true);

        // If the cell has a status of "0", reveal all surrounding zeroes
        if (cell.getStatus().equals("0")) {
            revealZeroes(x, y);
        }
    }

    /**
     * printMinefield
     *
     * @function This method should print the entire minefield or just the revealed cells, depending on the value of the `debugMode` parameter.
     * @param debugMode A boolean indicating whether to print the entire minefield or just the revealed cells.
     */
    public void printMinefield(boolean debugMode) {
        System.out.print("    ");
        for (int col = 0; col < columns; col++) {
            System.out.printf("%2d ", col);
        }
        System.out.println();

        for (int row = 0; row < rows; row++) {
            System.out.printf("%2d  ", row);
            for (int col = 0; col < columns; col++) {
                Cell cell = minefield[row][col];
                String status = cell.getStatus();

                // If the cell is revealed or debugMode is true, show the cell
                if (cell.getRevealed() || debugMode) {
                    switch (status) {
                        // Different colors are used for cells with different values to make it easier to read
                        case " ":
                            System.out.print(ANSI_YELLOW + " 0" + ANSI_GREY_BG + " ");
                            break;
                        case "1":
                            System.out.print(ANSI_BLUE + " " + status + ANSI_GREY_BG + " ");
                            break;
                        case "2":
                            System.out.print(ANSI_GREEN + " " + status + ANSI_GREY_BG + " ");
                            break;
                        case "3":
                            System.out.print(ANSI_RED + " " + status + ANSI_GREY_BG + " ");
                            break;
                        case "4":
                            System.out.print(ANSI_BLUE_BRIGHT + " " + status + ANSI_GREY_BG + " ");
                            break;
                        case "5":
                            System.out.print(ANSI_RED_BRIGHT + " " + status + ANSI_GREY_BG + " ");
                            break;
                        case "6":
                            System.out.print(ANSI_YELLOW + " " + status + ANSI_GREY_BG + " ");
                            break;
                        case "7":
                            System.out.print(ANSI_RED + " " + status + ANSI_GREY_BG + " ");
                            break;
                        case "8":
                            System.out.print(ANSI_GREY_BG + " " + status + " ");
                            break;
                        case "M":
                            System.out.print(ANSI_RED_BRIGHT + " " + status + ANSI_GREY_BG + " ");
                            break;
                        case "F":
                            System.out.print(ANSI_YELLOW + " " + status + ANSI_GREY_BG + " ");
                            break;
                    }
                } else {
                    // If the cell is not revealed and debugMode is false, show a blank cell
                    System.out.print(" - ");
                }
            }
            System.out.println();
        }
    }






    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // If the cell is revealed or flagged, append its status to the StringBuilder
                if (minefield[i][j].getRevealed() || minefield[i][j].getStatus().equals("F")) {
                    sb.append(minefield[i][j].getStatus());
                } else {
                    // Otherwise, append an underscore to represent an unrevealed, unflagged cell
                    sb.append("_");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


}
