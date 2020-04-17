package com.strixmc.souls;

import com.strixmc.souls.utilities.Member;
import com.strixmc.souls.utilities.MembersManager;
import com.strixmc.souls.utilities.PIN;
import com.strixmc.souls.utilities.Utils;
import fr.xephi.authme.events.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class AuthMeListeners implements Listener {

    private Security plugin;

    public AuthMeListeners(Security plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        Player p = event.getPlayer();
        if (p.isOp() || p.hasPermission("soul.staff")) {
            MembersManager manager = plugin.getManager();
            PIN pin = plugin.getPin();
            boolean registered = pin.containsMember(p.getName());
            Member member = new Member(p, registered, pin.getPIN(p.getName()));
            manager.addMember(member);
            if (pin.containsMember(p.getName())) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("Notify")));
            } else {
                p.sendMessage(Utils.c(plugin.getConfig().getString("Setup")));
            }
        }
    }

    @EventHandler
    public void onLogout(LogoutEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            manager.removeMember(p.getName());
        }
    }

    @EventHandler
    public void onUnregister(UnregisterByAdminEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        PIN pin = plugin.getPin();
        if (manager.containsMember(p.getName())) {
            pin.unregisterMember(p.getName());
            manager.removeMember(p.getName());
        }
    }

    @EventHandler
    public void onUnregister(UnregisterByPlayerEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        PIN pin = plugin.getPin();
        if (manager.containsMember(p.getName())) {
            pin.unregisterMember(p.getName());
            manager.removeMember(p.getName());
        }
    }

    @EventHandler
    public void onRegister(RegisterEvent event) {
        Player p = event.getPlayer();
        if (p.isOp() || p.hasPermission("soul.staff")) {
            MembersManager manager = plugin.getManager();
            PIN pin = plugin.getPin();
            boolean registered = pin.containsMember(p.getName());
            Member member = new Member(p, registered, pin.getPIN(p.getName()));
            manager.addMember(member);
            if (pin.containsMember(p.getName())) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("Notify")));
            } else {
                p.sendMessage(Utils.c(plugin.getConfig().getString("Setup")));
            }
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        if ("AuthMe".equals(event.getPlugin().getName())) {
            plugin.getAuthMeHook().removeAuthMeHook();
        }
    }

    @EventHandler
    public void onEnable(PluginEnableEvent event) {
        if ("AuthMe".equals(event.getPlugin().getName())) {
            plugin.getAuthMeHook().initializeHook();
        }
    }

}
