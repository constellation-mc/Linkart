package com.github.vini2003.linkart.configuration;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;

@Config(
   name = "linkart"
)
public class LinkartConfiguration implements ConfigData {
   @Tooltip
   public int pathfindingDistance = 8;
   @Tooltip
   public float velocityMultiplier = 1.0F;
   @Tooltip
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
