package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.setting.BoolSetting;
import dev.pulse.client.module.setting.ChoiceSetting;
import dev.pulse.client.module.setting.SliderSetting;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Replaces the vanilla crosshair with a configurable one. */
public final class CrosshairModule extends Module {
    private final ChoiceSetting style = add(new ChoiceSetting("Style", 0, "Cross", "Dot", "Circle", "Cross + dot"));
    private final ChoiceSetting color = add(new ChoiceSetting("Color", 0,
            "White", "Accent", "Red", "Green", "Cyan", "Pink", "Yellow"));
    private final SliderSetting size = add(new SliderSetting("Size", 2, 20, 1, 5, ""));
    private final SliderSetting gap = add(new SliderSetting("Gap", 0, 10, 1, 2, ""));
    private final SliderSetting thick = add(new SliderSetting("Thickness", 1, 4, 1, 1, ""));
    private final BoolSetting outline = add(new BoolSetting("Outline", true));
    private final BoolSetting rainbow = add(new BoolSetting("Rainbow", false));

    public CrosshairModule() {
        super("crosshair", "Custom Crosshair", "Replace the vanilla crosshair with your own style.",
                Category.COSMETICS, false, 0f, 0f, false);
    }

    private int pick() {
        if (rainbow.value) return Theme.hsb((System.currentTimeMillis() / 12L) % 360, 0.8f, 1f);
        return switch (color.index) {
            case 1 -> Theme.ACCENT;
            case 2 -> 0xFFF87171;
            case 3 -> 0xFF4ADE80;
            case 4 -> 0xFF22D3EE;
            case 5 -> 0xFFF472B6;
            case 6 -> 0xFFFBBF24;
            default -> 0xFFFFFFFF;
        };
    }

    private void box(GuiGraphics g, int x1, int y1, int x2, int y2, int c) {
        if (outline.value) g.fill(x1 - 1, y1 - 1, x2 + 1, y2 + 1, 0xFF000000);
        g.fill(x1, y1, x2, y2, c);
    }

    public void render(GuiGraphics g, Minecraft mc) {
        if (mc.screen != null || !mc.options.getCameraType().isFirstPerson()) return;
        int cx = mc.getWindow().getGuiScaledWidth() / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;
        int c = pick();
        int s = (int) size.value, gp = (int) gap.value, t = (int) thick.value;
        int lo = t / 2, hi = t - lo;
        boolean cross = style.index == 0 || style.index == 3;
        boolean dot = style.index == 1 || style.index == 3;

        if (cross) {
            box(g, cx - gp - s, cy - lo, cx - gp, cy + hi, c);
            box(g, cx + gp, cy - lo, cx + gp + s, cy + hi, c);
            box(g, cx - lo, cy - gp - s, cx + hi, cy - gp, c);
            box(g, cx - lo, cy + gp, cx + hi, cy + gp + s, c);
        }
        if (dot) box(g, cx - lo - 1, cy - lo - 1, cx + hi + 1, cy + hi + 1, c);
        if (style.index == 2) {
            int r = s + gp;
            for (int a = 0; a < 360; a += 4) {
                double rad = Math.toRadians(a);
                int x = cx + (int) Math.round(Math.cos(rad) * r);
                int y = cy + (int) Math.round(Math.sin(rad) * r);
                if (outline.value) g.fill(x - 1, y - 1, x + t + 1, y + t + 1, 0xFF000000);
            }
            for (int a = 0; a < 360; a += 4) {
                double rad = Math.toRadians(a);
                int x = cx + (int) Math.round(Math.cos(rad) * r);
                int y = cy + (int) Math.round(Math.sin(rad) * r);
                g.fill(x, y, x + t, y + t, c);
            }
        }
    }
}
