package com.github.vini2003.linkart.configuration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(
        name = "linkart"
)
public class LinkartConfiguration implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public int pathfindingDistance = 8;
    @ConfigEntry.Gui.Tooltip
    public float velocityMultiplier = 1.0F;
    @ConfigEntry.Gui.Tooltip
    public int collisionDepth = 8;

    public int getPathfindingDistance() {
        return this.pathfindingDistance;
    }

    public float getVelocityMultiplier() {
        return this.velocityMultiplier;
    }

    public int getCollisionDepth() {
        return this.collisionDepth;
    }
}
