package com.popupmc.floorpaint.colors;

public enum Colors {
    BLACK,
    GRAY,
    LIGHT_BLUE,
    LIME,
    CYAN,
    PINK,
    MAGENTA,
    YELLOW,
    LIGHT_GRAY,
    BLUE,
    GREEN,
    BROWN,
    RED,
    PURPLE,
    ORANGE,
    WHITE;

    public static Colors brighter(Colors color) {
        switch (color) {
            case BLACK:
                return GRAY;

            case GRAY:
            case LIGHT_BLUE:
            case LIME:
            case CYAN:
            case PINK:
            case MAGENTA:
            case YELLOW:
                return LIGHT_GRAY;

            case LIGHT_GRAY:
                return WHITE;

            case BLUE:
                return LIGHT_BLUE;

            case GREEN:
                return LIME;

            case BROWN:
                return CYAN;

            case RED:
                return PINK;

            case PURPLE:
                return MAGENTA;

            case ORANGE:
                return YELLOW;
        }

        return WHITE;
    }

    public static Colors darker(Colors color) {
        switch (color) {
            case GRAY:
                return BLACK;

            case BLUE:
            case GREEN:
            case BROWN:
            case RED:
            case PURPLE:
            case ORANGE:
            case LIGHT_GRAY:
                return GRAY;

            case WHITE:
                return LIGHT_GRAY;

            case LIGHT_BLUE:
                return BLUE;

            case LIME:
                return GREEN;

            case CYAN:
                return BROWN;

            case PINK:
                return RED;

            case MAGENTA:
                return PURPLE;

            case YELLOW:
                return ORANGE;
        }

        return BLACK;
    }
}
