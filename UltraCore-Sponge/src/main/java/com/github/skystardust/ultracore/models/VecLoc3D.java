package com.github.skystardust.ultracore.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Data
@AllArgsConstructor
@Builder
public class VecLoc3D {
    private String world;
    private double x;
    private double y;
    private double z;

    public static VecLoc3D valueOf(Location<World> location) {
        return new VecLoc3D(location.getExtent().getName(), location.getX(), location.getY(), location.getZ());
    }

    public Location toBukkitLocation() {
        try {
            World world = Sponge.getServer().getWorld(this.world).orElseThrow(IllegalAccessException::new);
            return new Location<>(world, x, y, z);
        } catch (IllegalAccessException e) {
            return null;
        }

    }
}
