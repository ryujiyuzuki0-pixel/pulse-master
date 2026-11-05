package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.setting.BoolSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;

public final class CoordsModule extends LineModule {
    private final BoolSetting direction = add(new BoolSetting("Show direction", true));

    public CoordsModule() {
        super("coords", "Coordinates", "Your XYZ position and facing.", Category.HUD, 0.008f, 0.126f);
    }

    @Override protected String label() { return "XYZ"; }

    @Override
    protected String value(Minecraft mc) {
        if (mc.player == null) return "0 0 0";
        String s = (int) Math.floor(mc.player.getX()) + "  " + (int) Math.floor(mc.player.getY())
                + "  " + (int) Math.floor(mc.player.getZ());
        if (direction.value) {
            Direction d = mc.player.getDirection();
            s += "  " + Character.toUpperCase(d.getName().charAt(0));
        }
        return s;
    }

    @Override protected int minWidth() { return 120; }
}
