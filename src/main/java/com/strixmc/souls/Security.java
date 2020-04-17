package com.strixmc.souls;

import com.strixmc.souls.commands.PinCommand;
import com.strixmc.souls.commands.SetPin;
import com.strixmc.souls.commands.SoulCommand;
import com.strixmc.souls.utilities.*;
import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Security extends JavaPlugin {

    private MembersManager manager;
    private PIN pin;
    private AuthMeHook authMeHook;

    public MembersManager getManager() {
        return manager;
    }

    public PIN getPin() {
        return pin;
    }

    public AuthMeHook getAuthMeHook() {
        return authMeHook;
    }

    @Override
    public void onEnable() {
        createConfig();
        pin = new PIN(this);
        pin.loadMembers(this);
        manager = new MembersManager();
        authMeHook = new AuthMeHook();

        securityScheduler();
        notifySheduler();

        getCommand("setpin").setExecutor(new SetPin(this));
        getCommand("pin").setExecutor(new PinCommand(this));
        getCommand("soul").setExecutor(new SoulCommand(this));

        if (getServer().getPluginManager().isPluginEnabled("AuthMe")) {
            getServer().getPluginManager().registerEvents(new AuthMeListeners(this), this);
            authMeHook.initializeHook();
            Bukkit.getLogger().info("AuthMe has been found. Using it!");
        } else {
            getServer().getPluginManager().registerEvents(new NormalListeners(this), this);
        }
        getServer().getPluginManager().registerEvents(new GlobalListeners(this), this);
    }

    @Override
    public void onDisable() {
        pin.savePlayers(this);
    }

    private void securityScheduler() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
/*
                Bukkit.broadcastMessage(Utils.c("&eHOOK &7" + authMeHook.isHookActive()
                        + "&f, &eAuth &7" + authMeHook.authMeApi.isAuthenticated(player) +
                        "&f, &eMember &7" + manager.containsMember(player.getName()) +
                        "&f, &ePin &7" + pin.containsMember(player.getName()) +
                        "&f, &eOP &7" + player.isOp()));
*/
                if (authMeHook.isHookActive()) {
                    if (authMeHook.authMeApi.isAuthenticated(player)) {
                        if (pin.containsMember(player.getName()) && !manager.containsMember(player.getName()) && player.hasPermission("soul.staff") || player.isOp() || !pin.containsMember(player.getName()) && !manager.containsMember(player.getName())) {
                            Member member = new Member(player.getPlayer(), pin.containsMember(player.getName()), pin.getPIN(player.getName()));
                            manager.addMember(member);
                        }
                    }
                } else {
                    if (pin.containsMember(player.getName()) && !manager.containsMember(player.getName()) && player.hasPermission("soul.staff") || player.isOp() || !pin.containsMember(player.getName()) && !manager.containsMember(player.getName())) {
                        Member member = new Member(player.getPlayer(), pin.containsMember(player.getName()), pin.getPIN(player.getName()));
                        manager.addMember(member);
                    }
                }
            }
        }, 0L, 20L);
    }

    private void notifySheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (manager.containsMember(player.getName())) {
                    Member member = manager.getMember(player.getName());
                    if (!member.isRegistered()) {
                        player.sendMessage(Utils.c(getConfig().getString("Setup")));
                        return;
                    } else {
                        if (!member.isVerified()) {
                            player.sendMessage(Utils.c(getConfig().getString("Notify")));
                        }
                    }
                }
            }
        }, 0L, 20L * getConfig().getInt("Notifications-Interval"));
    }

    private void createConfig() {
        Bukkit.getLogger().info("Loading config.yml");
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile, Arrays.asList("Members"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
    }

    public class AuthMeHook {
        private AuthMeApi authMeApi;

        public void initializeHook() {
            authMeApi = AuthMeApi.getInstance();
        }

        public boolean registerPlayer(String name, String password) {
            if (authMeApi != null) { // check that the API is loaded
                return authMeApi.registerPlayer(name, password);
            }
            return false;
        }

        public boolean isNameRegistered(String name) {
            return authMeApi != null && authMeApi.isRegistered(name);
        }

        public boolean isHookActive() {
            return authMeApi != null;
        }

        public void removeAuthMeHook() {
            authMeApi = null;
        }
    }
}
