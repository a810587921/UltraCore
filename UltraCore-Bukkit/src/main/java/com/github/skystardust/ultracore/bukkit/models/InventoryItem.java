package com.github.skystardust.ultracore.bukkit.models;

import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class InventoryItem {
    private Map<String, Object> itemstackData;

    public ItemStack toItemStack() {
        org.bukkit.Material type = org.bukkit.Material.getMaterial((String) itemstackData.get("type"));
        short damage = 0;
        int amount = 1;
        if (itemstackData.containsKey("damage")) {
            damage = ((Number) itemstackData.get("damage")).shortValue();
        }

        if (itemstackData.containsKey("amount")) {
            amount = ((Number) itemstackData.get("amount")).intValue();
        }

        ItemStack result = new ItemStack(type, amount, damage);
        Object raw;
        if (itemstackData.containsKey("enchantments")) {
            raw = itemstackData.get("enchantments");
            if (raw instanceof Map) {
                Map<?, ?> map = (Map) raw;
                Iterator var7 = map.entrySet().iterator();

                while (var7.hasNext()) {
                    Map.Entry<?, ?> entry = (Map.Entry) var7.next();
                    Enchantment enchantment = Enchantment.getByName(entry.getKey().toString());
                    if (enchantment != null && entry.getValue() instanceof Integer) {
                        result.addUnsafeEnchantment(enchantment, ((Integer) entry.getValue()).intValue());
                    }
                }
            }
        } else if (itemstackData.containsKey("meta")) {
            raw = itemstackData.get("meta");
            ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(type);
            LinkedTreeMap linkedTreeMap = (LinkedTreeMap) raw;
            if (((LinkedTreeMap) raw).containsKey("displayName")) {
                itemMeta.setDisplayName((String) linkedTreeMap.get("displayName"));
            }
            if (((LinkedTreeMap) raw).containsKey("lore")) {
                itemMeta.setLore(((List<String>) ((LinkedTreeMap) raw).get("lore")));
            }
            result.setItemMeta(itemMeta);
        }

        return result;
    }
}
