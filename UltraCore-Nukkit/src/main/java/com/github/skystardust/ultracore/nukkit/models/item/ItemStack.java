package com.github.skystardust.ultracore.nukkit.models.item;

import cn.nukkit.item.Item;
import com.google.gson.Gson;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemStack {
    private int id;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Item toItem() {
        return new Item(id);
    }
}
