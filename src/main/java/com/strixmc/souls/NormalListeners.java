package com.strixmc.souls;

import com.strixmc.souls.utilities.Member;
import com.strixmc.souls.utilities.MembersManager;
import com.strixmc.souls.utilities.PIN;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NormalListeners implements Listener {

    private Security plugin;

    public NormalListeners(Security plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.isOp() || p.hasPermission("soulsecurity.staff")) {
            MembersManager manager = plugin.getManager();
            PIN pin = plugin.getPin();
            boolean registered = pin.containsMember(p.getName());
            Member member = new Member(p, registered, pin.getPIN(p.getName()));
            manager.addMember(member);
        }
    }
}
