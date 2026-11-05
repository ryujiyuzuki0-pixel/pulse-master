package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class ZoomModule extends Module {
    // Vanilla clamps FOV to 30..110, so 30 is the strongest zoom without mixins.
    private final SliderSetting fov = add(new SliderSetting("Zoom FOV", 30, 70, 1, 30, ""));
    private boolean zooming;
    private int original;

    public ZoomModule() {
        super("zoom", "Zoom", "Hold C to zoom in.", Category.UTILITY, false, 0f, 0f, false);
    }

    @Override
    public void tick(Minecraft mc) {
        if (mc.getWindow() == null || mc.player == null) return;
        boolean down = mc.screen == null
                && GLFW.glfwGetKey(mc.getWindow().handle(), GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS;
        if (down && !zooming) {
            original = mc.options.fov().get();
            zooming = true;
        }
        if (zooming) {
            if (down) {
                mc.options.fov().set((int) fov.value);
            } else {
                restore(mc);
            }
        }
    }

    private void restore(Minecraft mc) {
        if (zooming && mc.options != null) mc.options.fov().set(original);
        zooming = false;
    }

    @Override
    public void onDisable() {
        restore(Minecraft.getInstance());
    }
}
