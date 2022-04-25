package minesweeper;

/**
 * @author tobukhova on 1/10/22
 * Minesweeper
 **/
public enum MINE {

    BOMB('*'),
    UNKNOWN('.'),
    EMPTY('/'),
    FAIL('X'),
    NUMBER(null); // if a number

    private Character symbol;

    MINE(Character symbol) {
        this.symbol = symbol;
    }

    public static MINE value(Character symbol) {
        if (symbol.equals(BOMB.getSymbol())) {
            return BOMB;
        } else if (symbol.equals(EMPTY.getSymbol())) {
            return EMPTY;
        } else if (symbol.equals(UNKNOWN.getSymbol())) {
            return UNKNOWN;
        } else if (symbol.equals(FAIL.getSymbol())) {
            return FAIL;
        } else {
            return NUMBER;
        }
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

}
