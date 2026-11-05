package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** A compact "LABEL ........ value" HUD line. */
public abstract class LineModule extends Module {
    protected LineModule(String id, String name, String desc, Category cat, float fx, float fy) {
        super(id, name, desc, cat, true, fx, fy, true);
    }

    protected abstract String label();

    protected abstract String value(Minecraft mc);

    protected int valueColor(Minecraft mc) {
        return Theme.TEXT;
    }

    protected int minWidth() {
        return 66;
    }

    @Override
    public int width(Minecraft mc) {
        return Math.max(minWidth(), 12 + Theme.width(label()) + 10 + Theme.width(value(mc)));
    }

    @Override
    public int height(Minecraft mc) {
        return 17;
    }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        String v = value(mc);
        Theme.shadow(g, label(), 6, 5, Theme.DIM);
        Theme.shadow(g, v, w - 6 - Theme.width(v), 5, valueColor(mc));
    }
}
