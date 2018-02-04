package com.github.skystardust.ultracore.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class VecLoc3D {
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
}
