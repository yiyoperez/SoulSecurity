package com.strixmc.souls.utilities;

import org.bukkit.ChatColor;

public class Utils {

    public static String c(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
}
