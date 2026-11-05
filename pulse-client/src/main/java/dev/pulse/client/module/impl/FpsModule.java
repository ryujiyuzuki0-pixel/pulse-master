package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.ui.Theme;
import dev.pulse.client.util.FrameClock;
import net.minecraft.client.Minecraft;

public final class FpsModule extends LineModule {
    public FpsModule() {
        super("fps", "FPS", "Frames per second.", Category.HUD, 0.008f, 0.012f);
    }

    @Override protected String label() { return "FPS"; }

    @Override protected String value(Minecraft mc) { return String.valueOf(FrameClock.fps()); }

    @Override
    protected int valueColor(Minecraft mc) {
        int f = FrameClock.fps();
        return f >= 100 ? Theme.GOOD : f >= 45 ? Theme.TEXT : f >= 25 ? Theme.WARN : Theme.BAD;
    }
}
