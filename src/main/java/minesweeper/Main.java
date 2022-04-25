package minesweeper;

import java.util.*;

import static minesweeper.MINE.*;

/**
 * @author tobukhova on 12/3/21
 * Minesweeper
 **/

public class Main {
    public static void main(String[] args) {

        //field size
        final int xSize = 9;
        final int ySize = 9;

        Character[][] baseField = createEmptyField(xSize, ySize);
        Character[][] userField = createEmptyField(xSize, ySize);

        //How many mines do you want on a field?
        final int numMines = getNumberOfMines(xSize * ySize);
        printField(baseField);

        play(userField, baseField, numMines);
    }

    private static int getNumberOfMines(int fieldSize) {
        System.out.println("How many mines do you want on the field? ");
        Scanner scanner = new Scanner(System.in);
        int numMines = scanner.nextInt();

        if ((numMines >= fieldSize) || (numMines <= 0)) {
            System.out.println(String
                    .format("Please choose another number of mines. (The number of mines should be a positive number less than %d.)", fieldSize));
            numMines = getNumberOfMines(fieldSize);
        }

        return numMines;
    }

    // user chose to free/open a cell [xNum][yNum]
    private static void freeCells(int xNum, int yNum, Character[][] baseField, Character[][] userField) {
        if (baseField[xNum - 1][yNum - 1] == FAIL.getSymbol()) {
            printField(showBombs(userField, baseField)); //Show the userField with all Bombs X
            System.out.println("You stepped on a mine and failed!");
            System.exit(0); // finish the game while user got a Bomb
        } else if (baseField[xNum - 1][yNum - 1] == EMPTY.getSymbol()) {
            userField[xNum - 1][yNum - 1] = EMPTY.getSymbol();
            final Integer[][] COORDINATES = {{1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, 0}, {1, -1}, {0, -1}, {-1, -1}};
            //to check the cells around the empty one
            for (Integer[] xy : COORDINATES) {
                if ((xNum - xy[0] - 1 >= 0) && (yNum - xy[1] - 1 >= 0) && (baseField.length > xNum - xy[0] - 1) && (baseField[0].length > yNum - xy[1] - 1)) {
                    MINE cell = MINE.value(baseField[xNum - xy[0] - 1][yNum - xy[1] - 1]);
                    switch (cell) {
                        case EMPTY -> {
                            if (MINE.value(userField[xNum - xy[0] - 1][yNum - xy[1] - 1]) != EMPTY) {
                                userField[xNum - xy[0] - 1][yNum - xy[1] - 1] = EMPTY.getSymbol();
                                freeCells(xNum - xy[0], yNum - xy[1], baseField, userField);
                                //recurring to open EMPTY ('/') cells around
                            }
                        }
                        case FAIL -> {
                            break;
                        }
                        case NUMBER -> userField[xNum - xy[0] - 1][yNum - xy[1] - 1] = baseField[xNum - xy[0] - 1][yNum - xy[1] - 1];
                    }
                }
            }
        } else {
            userField[xNum - 1][yNum - 1] = baseField[xNum - 1][yNum - 1];
        }
    }

    //to show bombs as 'X' on the user field if user failed
    private static Character[][] showBombs(Character[][] userField, Character[][] baseField) {
        for (int i = 0; i < baseField.length; i++) {
            for (int j = 0; j < baseField[0].length; j++) {
                if (baseField[i][j].equals(FAIL.getSymbol())) {
                    userField[i][j] = FAIL.getSymbol();
                }
            }
        }
        return userField;
    }

    //set mines on the field after 1st 'free' command
    private static void fillAField(int xNum, int yNum, Character[][] baseField, int numMines) {
        addRandomMine(baseField, numMines, xNum, yNum);
        showNumberOfMinesAround(baseField);
        addEmptyCells(baseField);
    }

    //just an empty field xSize*ySize with only '.' symbols
    private static Character[][] createEmptyField(int xSize, int ySize) {
        Character[][] field = new Character[xSize][ySize];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                field[i][j] = UNKNOWN.getSymbol();
            }
        }
        return field;
    }

    private static void play(Character[][] userField, Character[][] baseField, int numMines) {
        boolean endGame = false; //to finish a game if user won
        boolean firstFree = true; //to set mines after 1st 'free' command
        int userMines = 0; //to check win statement when user marked all mines
        do {
            System.out.println("Set/unset mine marks or claim a cell as free:");
            Scanner scanner = new Scanner(System.in);
            int y = scanner.nextInt();
            int x = scanner.nextInt();
            COMMAND command = COMMAND.valueOf(scanner.next().toUpperCase());

            switch (command) {
                case FREE -> {
                    if (firstFree) {
                        //first 'free' command
                        fillAField(x, y, baseField, numMines);
                        freeCells(x, y, baseField, userField);
                        firstFree = false;
                    } else {
                        freeCells(x, y, baseField, userField);
                        //Check the winning case - Opening all the safe cells so that only those with unexplored mines are left
                        endGame = compareCells(baseField, userField);
                    }
                }
                case MINE -> {
                    if (userField[x - 1][y - 1] == BOMB.getSymbol()) {
                        // if user sets 'mine' twice on the same cell, it will clear the cell
                        userField[x - 1][y - 1] = UNKNOWN.getSymbol();
                        userMines--;
                    } else {
                        userField[x - 1][y - 1] = BOMB.getSymbol();
                        userMines++;
                    }
                    if (userMines == numMines) {
                        //Check the winning case - Marking all the cells that have mines correctly
                        endGame = compareMines(baseField, userField);
                    }
                }
            }
            printField(userField);
        } while (!endGame);
        System.out.println("Congratulations! You found all the mines!");
    }

    //Check the winning case - Marking all the cells that have mines correctly
    private static boolean compareMines(Character[][] baseField, Character[][] userField) {
        for (int i = 0; i < baseField.length; i++) {
            for (int j = 0; j < baseField[0].length; j++) {
                if (MINE.value(baseField[i][j]) == FAIL) {
                    if (MINE.value(userField[i][j]) != BOMB) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Check the winning case - Opening all the safe cells so that only those with unexplored mines are left
    private static boolean compareCells(Character[][] baseField, Character[][] userField) {
        for (int i = 0; i < baseField.length; i++) {
            for (int j = 0; j < baseField[0].length; j++) {
                if (MINE.value(baseField[i][j]).equals(NUMBER)) {
                    if (baseField[i][j] != userField[i][j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //set numMines mines randomly after 1st 'free' command. [xNum][yNum] cell is excluded
    private static void addRandomMine(Character[][] field, int numMines, int xNum, int yNum) {
        Random random = new Random();
        for (int i = 1; i <= numMines; i++) {
            int ran1 = random.nextInt(field.length);
            int ran2 = random.nextInt(field[0].length);
            if ((field[ran1][ran2].equals(FAIL.getSymbol())) || ((ran1 == xNum - 1) && (ran2 == yNum - 1))) {
                i--;
            } else {
                field[ran1][ran2] = FAIL.getSymbol();
            }
        }
    }

    //fill the field with numbers after setting all the mines
    private static void showNumberOfMinesAround(Character[][] baseField) {
        for (int i = 0; i < baseField.length; i++) {
            for (int j = 0; j < baseField[0].length; j++) {
                if (baseField[i][j] == FAIL.getSymbol()) {
                    addAllNumbersAround(baseField, i, j);
                }
            }
        }
    }

    //check the cells around [i][j] to set the q-ty of nearest mines
    private static void addAllNumbersAround(Character[][] baseField, int i, int j) {
        final Integer[][] COORDINATES = {{1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        for (Integer[] xy : COORDINATES) {
            if ((i - xy[0] >= 0) && (j - xy[1] >= 0) && (baseField.length > i - xy[0]) && (baseField[0].length > j - xy[1])) {
                MINE cell = MINE.value(baseField[i - xy[0]][j - xy[1]]);
                switch (cell) {
                    case UNKNOWN -> baseField[i - xy[0]][j - xy[1]] = '1';
                    case FAIL, BOMB -> {
                        break;
                    }
                    case NUMBER -> baseField[i - xy[0]][j - xy[1]] = Character.forDigit(
                            Integer.parseInt(String.valueOf(baseField[i - xy[0]][j - xy[1]])) + 1, 10);
                }
            }
        }
    }

    //mark cells that are not BOMBS and NUMBERS as EMPTY('/') on base field
    private static void addEmptyCells(Character[][] baseField) {
        for (int i = 0; i < baseField.length; i++) {
            for (int j = 0; j < baseField[0].length; j++) {
                if (baseField[i][j] == UNKNOWN.getSymbol()) {
                    baseField[i][j] = EMPTY.getSymbol();
                }
            }
        }
    }

    //print the board with borders
    private static void printField(Character[][] field) {
        int xSize = field.length;
        int ySize = field[0].length;
        System.out.println("\n |123456789|\n-|---------|");
        for (int i = 0; i < xSize; i++) {
            System.out.print((i + 1) + "|");
            for (int j = 0; j < ySize; j++) {
                System.out.print(field[i][j]);
            }
            System.out.print("|\n");
        }
        System.out.println("-|---------|");
    }

}
