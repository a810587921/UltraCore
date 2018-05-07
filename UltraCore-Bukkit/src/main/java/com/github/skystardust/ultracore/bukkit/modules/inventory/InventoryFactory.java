package com.github.skystardust.ultracore.bukkit.modules.inventory;

import com.github.skystardust.ultracore.bukkit.UltraCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryFactory {
    private static Map<String, Listener> lockedInventory;

    static {
        lockedInventory = new HashMap<>();
    }

    public static void lockInventory(Inventory inventory) {
        lockInventory(inventory.getTitle());
    }

    public static void lockInventory(String inventory) {
        Listener listener = new Listener() {
            @EventHandler
            public void onInteractInventory(InventoryClickEvent inventoryClickEvent) {
                if (inventoryClickEvent.getClickedInventory().getName().equals(inventory)) {
                    inventoryClickEvent.setCancelled(true);
                }
            }
        };
        lockedInventory.put(inventory, listener);
        Bukkit.getPluginManager().registerEvents(listener, UltraCore.getUltraCore());
    }

    public static void lockInventoryByKey(String key) {
        Listener listener = new Listener() {
            @EventHandler
            public void onInteractInventory(InventoryClickEvent inventoryClickEvent) {
                if (inventoryClickEvent.getClickedInventory().getName().contains(key)) {
                    inventoryClickEvent.setCancelled(true);
                }
            }
        };
        lockedInventory.put(key, listener);
        Bukkit.getPluginManager().registerEvents(listener, UltraCore.getUltraCore());
    }

    public static void unlockInventory(String string) {
        new HashMap<>(lockedInventory).forEach((key, value) -> {
            if (key.equals(string)) {
                HandlerList.unregisterAll(value);
                lockedInventory.remove(key);
            }
        });
    }

    public static void unlockInventory(Inventory inventory) {
        unlockInventory(inventory.getTitle());
    }

    public static void fill(Inventory inventory, ItemStack itemStack) {
        while (inventory.firstEmpty() != -1) {
            inventory.setItem(inventory.firstEmpty(), itemStack);
        }
    }
}
