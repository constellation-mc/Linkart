package com.github.vini2003.linkart.accessor;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import java.util.UUID;

public interface AbstractMinecartEntityAccessor {

    AbstractMinecartEntity getPrevious();

    void setPrevious(AbstractMinecartEntity var1);

    AbstractMinecartEntity getNext();

    void setNext(AbstractMinecartEntity var1);

    UUID getPreviousUuid();

    void setPreviousUuid(UUID var1);

    UUID getNextUuid();

    void setNextUuid(UUID var1);

    double squaredDistanceTo(double x, double y, double z);
}
