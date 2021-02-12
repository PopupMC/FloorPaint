package com.popupmc.floorpaint.colors;

import java.util.Random;

public enum BaseColors {
    BLACK,
    BLUE,
    GREEN,
    BROWN,
    RED,
    PURPLE,
    ORANGE,
    NONE;

    public static Colors lightEquiv(BaseColors colors) {

        // Black => White
        if(colors == BLACK)
            return Colors.WHITE;

        // Special => White
        else if(colors == NONE)
            return Colors.WHITE;

        // Everything else => lighter form
        return Colors.brighter(Colors.valueOf(colors.toString()));
    }

    public static Colors darkEquiv(BaseColors colors) {

        // Special => Black
        if(colors == NONE)
            return Colors.BLACK;

        // Everything else => same color equiv
        return Colors.valueOf(colors.toString());
    }

    public static BaseColors random() {
        int colorInd = new Random().nextInt(7);
        return BaseColors.values()[colorInd];
    }
}
