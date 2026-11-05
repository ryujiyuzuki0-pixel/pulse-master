package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.setting.BoolSetting;
import dev.pulse.client.util.CpsTracker;
import net.minecraft.client.Minecraft;

public final class CpsModule extends LineModule {
    private final BoolSetting showRight = add(new BoolSetting("Show right click", true));

    public CpsModule() {
        super("cps", "CPS", "Clicks per second for both mouse buttons.", Category.COMBAT, 0.008f, 0.05f);
    }

    @Override protected String label() { return "CPS"; }

    @Override
    protected String value(Minecraft mc) {
        return showRight.value ? CpsTracker.left() + " | " + CpsTracker.right() : String.valueOf(CpsTracker.left());
    }
}
