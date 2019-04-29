package net.casino.casino.defaultgames.slots.slot;

import net.casino.utils.AngleUtil;
import org.bukkit.entity.ArmorStand;

public enum SlotWheelPosition {
    SLOT_1(SlotWheelIcon.SEVEN, 360, 0, 45),
    SLOT_2(SlotWheelIcon.HORSE_SHOE, 45, 65, 90),
    SLOT_3(SlotWheelIcon.CHERRY, 90, 100, 135),
    SLOT_4(SlotWheelIcon.APPLE, 135, 150, 180),
    SLOT_5(SlotWheelIcon.STRAWBERRY, 180, 200, 225),
    SLOT_6(SlotWheelIcon.DIAMOND, 225, 245, 270),
    SLOT_7(SlotWheelIcon.CLOUD_ESCAPE_LOGO, 270, 295, 315),
    SLOT_8(SlotWheelIcon.BELL, 315, 340, 360); // bonus item

    private SlotWheelIcon slotWheelIcon;
    private double[] bounds;

    // 45 degree range
    SlotWheelPosition(SlotWheelIcon slotWheelIcon, double... bounds) {
        this.slotWheelIcon = slotWheelIcon;
        this.bounds = bounds;
    }

    public SlotWheelIcon getSlotWheelIcon() {
        return slotWheelIcon;
    }

    public double[] getBounds() {
        return bounds;
    }

    public static SlotWheelPosition getIcoPosition(ArmorStand stand) {

        double angle = AngleUtil.getDegreesFromRadians(stand.getHeadPose().getX());

        for (SlotWheelPosition position : values()) {
            if (position.getBounds()[1] == angle) {
                return position;
            } else if (((angle >= position.getBounds()[0]) && (angle <= position.getBounds()[2]))) {
                return position;
            }
        }

        return SLOT_1;
    }
}
