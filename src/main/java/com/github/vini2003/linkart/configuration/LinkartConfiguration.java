package com.github.vini2003.linkart.configuration;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.Tooltip;

@Config(
   name = "linkart"
)
public class LinkartConfiguration implements ConfigData {
   @Tooltip
   public boolean enableLinker = true;
   @Tooltip
   public boolean enableChain = true;
   @Tooltip
   public int pathfindingDistance = 8;
   @Tooltip
   public float velocityMultiplier = 0.5F;
   @Tooltip
   public int collisionDepth = 16;

   public boolean isLinkerEnabled() {
      return this.enableLinker;
   }

   public boolean isChainEnabled() {
      return this.enableChain;
   }

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
