package com.yuki920.statsify.util;

public class BedwarsLevelUtil {

    private static final int PRESTIGE_XP = 487000;

    private static int getXpForLevel(int level) {
        if (level == 0) return 500;
        if (level == 1) return 1000;
        if (level == 2) return 2000;
        if (level == 3) return 3500;
        return 5000;
    }

    public static int getLevelFromExp(long exp) {
        int prestige = (int) (exp / PRESTIGE_XP);
        int level = prestige * 100;

        long remaining = exp % PRESTIGE_XP;

        int subLevel = 0;
        while (true) {
            int xpNeeded = getXpForLevel(subLevel);
            if (remaining < xpNeeded) break;
            remaining -= xpNeeded;
            subLevel++;
        }

        return level + subLevel;
    }
}
