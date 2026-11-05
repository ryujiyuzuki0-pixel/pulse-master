package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import net.minecraft.client.Minecraft;

public final class ToggleSprintModule extends Module {
    private boolean pressedByUs;

    public ToggleSprintModule() {
        super("sprint", "Toggle Sprint", "Sprint automatically while you move forward.",
                Category.UTILITY, false, 0f, 0f, false);
    }

    @Override
    public void tick(Minecraft mc) {
        if (mc.player == null) return;
        if (mc.screen == null && mc.options.keyUp.isDown()) {
            mc.options.keySprint.setDown(true);
            pressedByUs = true;
        } else if (pressedByUs) {
            mc.options.keySprint.setDown(false);
            pressedByUs = false;
        }
    }

    @Override
    public void onDisable() {
        Minecraft mc = Minecraft.getInstance();
        if (pressedByUs && mc.options != null) mc.options.keySprint.setDown(false);
        pressedByUs = false;
    }
}
