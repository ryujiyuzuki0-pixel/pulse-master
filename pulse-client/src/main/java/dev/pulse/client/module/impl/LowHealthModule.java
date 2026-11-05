package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.setting.SliderSetting;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Pulsing red screen edges when your health is low. */
public final class LowHealthModule extends Module {
    private final SliderSetting below = add(new SliderSetting("Hearts below", 1, 10, 1, 4, ""));
    private final SliderSetting strength = add(new SliderSetting("Strength", 0.2, 1.0, 0.05, 0.6, ""));

    public LowHealthModule() {
        super("lowhealth", "Low Health Vignette", "Pulsing red edges when you're nearly dead.",
                Category.COSMETICS, false, 0f, 0f, false);
    }

    @Override
    public void drawOverlay(GuiGraphics g, Minecraft mc) {
        if (mc.player == null || mc.screen != null || mc.player.isCreative() || mc.player.isSpectator()) return;
        float hp = mc.player.getHealth();
        if (hp > below.value * 2 || hp <= 0) return;
        float urgency = 1f - hp / (float) (below.value * 2);
        float pulse = 0.65f + 0.35f * (float) Math.sin(System.currentTimeMillis() / 220.0);
        float a = (float) strength.value * pulse * (0.5f + 0.5f * urgency);
        int w = mc.getWindow().getGuiScaledWidth(), h = mc.getWindow().getGuiScaledHeight();
        int edge = Math.max(24, h / 5);
        int solid = Theme.alpha(0xFFE02020, a), none = 0x00E02020;
        g.fillGradient(0, 0, w, edge, solid, none);
        g.fillGradient(0, h - edge, w, h, none, solid);
        int side = edge;
        for (int i = 0; i < side; i += 2) {
            float t = 1f - i / (float) side;
            int c = Theme.alpha(0xFFE02020, a * t);
            g.fill(i, edge, i + 2, h - edge, c);
            g.fill(w - i - 2, edge, w - i, h - edge, c);
        }
    }
}
