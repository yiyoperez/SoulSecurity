package com.strixmc.souls.utilities;


import com.strixmc.souls.Security;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class PIN {

    private HashMap<String, Long> members;

    public PIN(Security plugin) {
        this.members = new HashMap<>();
    }

    public HashMap<String, Long> getMembers() {
        return members;
    }

    public void registerMember(String member, Long pin) {
        if (!containsMember(member)) {
            members.put(member, pin);
        }
    }

    public void unregisterMember(String member) {
        if (containsMember(member)) {
            for (String m : members.keySet()) {
                if (m.equalsIgnoreCase(member)) {
                    members.remove(m);
                }
            }
        }
    }

    public long getPIN(String player) {
        if (containsMember(player)) {
            return members.get(player);
        }
        return -1;
    }

    public void setPIN(String player, long pin) {
        if (containsMember(player)) {
            members.put(getMember(player), pin);
        }
    }

    public String getMember(String player) {
        if (containsMember(player)) {
            for (String member : members.keySet()) {
                if (member.equalsIgnoreCase(player)) {
                    return member;
                }
            }
        }
        return null;
    }

    public boolean containsMember(String player) {
        for (String member : members.keySet()) {
            if (member.equalsIgnoreCase(player)) {
                return true;
            }
        }
        return false;
    }

    public void loadMembers(Security plugin) {
        if (plugin.getConfig().getConfigurationSection("Members").getKeys(false).isEmpty()) return;

        for (String member : plugin.getConfig().getConfigurationSection("Members").getKeys(false)) {
            members.put(member, plugin.getConfig().getLong("Members." + member));
            plugin.getConfig().set("Members." + member, null);
        }

        plugin.saveConfig();
        try {
            ConfigUpdater.update(plugin, "config.yml", new File(plugin.getDataFolder(), "config.yml"), Arrays.asList("Members"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.reloadConfig();

    }

    public void savePlayers(Security plugin) {
        if (members.isEmpty()) return;

        members.forEach((member, pin) -> plugin.getConfig().set("Members." + member, pin));

        plugin.saveConfig();
        try {
            ConfigUpdater.update(plugin, "config.yml", new File(plugin.getDataFolder(), "config.yml"), Arrays.asList("Members"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.reloadConfig();
    }

}
