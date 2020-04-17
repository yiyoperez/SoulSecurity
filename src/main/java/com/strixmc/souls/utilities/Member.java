package com.strixmc.souls.utilities;

import org.bukkit.entity.Player;

public class Member {

    private Player player;
    private long pin;
    private boolean verified = false;
    private boolean registered;

    public Member(Player player, boolean registered, long pin) {
        this.player = player;
        this.registered = registered;
        this.pin = pin;
    }

    public boolean isRegistered() {
        return registered;
    }

    public Member setRegistered(boolean registered) {
        this.registered = registered;
        return this;
    }

    public boolean isVerified() {
        return verified;
    }

    public Member setVerified(boolean verified) {
        this.verified = verified;
        return this;
    }

    public Player getPlayer() {
        return player;
    }

    public long getPin() {
        return pin;
    }

    public Member setPin(long pin) {
        this.pin = pin;
        return this;
    }

}
