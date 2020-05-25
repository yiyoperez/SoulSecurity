package com.strixmc.souls.utilities.menusystem;

import com.strixmc.souls.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    protected PlayerMenuUtility playerMenuUtility;

    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract String getname();

    public abstract int rows();

    public abstract void handleMenu(InventoryClickEvent event);

    public abstract void setItems();

    public void open() {
        inventory = Bukkit.createInventory(this, rows() * 9, Utils.c(getname()));

        this.setItems();

        playerMenuUtility.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}