package com.strixmc.souls.utilities.menusystem;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerMenuUtilityManager {

    private HashMap<Player, PlayerMenuUtility> playerMenuUtilityHashMap = new HashMap<Player, PlayerMenuUtility>();

    public PlayerMenuUtility getPlayerMenuUtility(Player p) {

        PlayerMenuUtility playerMenuUtility;

        if (playerMenuUtilityHashMap.containsKey(p)) {
            return playerMenuUtilityHashMap.get(p);
        } else {

            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityHashMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        }
    }
}