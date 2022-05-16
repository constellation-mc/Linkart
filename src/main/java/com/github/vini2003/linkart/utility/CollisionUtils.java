package com.github.vini2003.linkart.utility;

import com.github.vini2003.linkart.accessor.AbstractMinecartEntityAccessor;
import com.github.vini2003.linkart.registry.LinkartConfigurations;
import net.minecraft.entity.Entity;

public class CollisionUtils {
    public static boolean shouldCollide(Entity source, Entity target) {
        if (source instanceof AbstractMinecartEntityAccessor) {
            AbstractMinecartEntityAccessor check = (AbstractMinecartEntityAccessor) source;
            int i = 0;

            do {
                if (check == target) {
                    return false;
                }

                check = (AbstractMinecartEntityAccessor) check.getNext();
                ++i;
            } while (check != null && i < LinkartConfigurations.INSTANCE.getConfig().getCollisionDepth());

            check = (AbstractMinecartEntityAccessor) source;
            i = 0;

            while (check != target) {
                check = (AbstractMinecartEntityAccessor) check.getPrevious();
                ++i;
                if (check == null || i >= LinkartConfigurations.INSTANCE.getConfig().getCollisionDepth()) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }
}
