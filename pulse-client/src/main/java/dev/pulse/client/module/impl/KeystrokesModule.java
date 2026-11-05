package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.setting.BoolSetting;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.Theme;
import dev.pulse.client.util.CpsTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class KeystrokesModule extends Module {
    private static final int K = 22, GAP = 2;
    private final BoolSetting mouse = add(new BoolSetting("Show mouse buttons", true));
    private final BoolSetting space = add(new BoolSetting("Show spacebar", true));
    private final float[] anim = new float[7];

    public KeystrokesModule() {
        super("keystrokes", "Keystrokes", "WASD, jump and mouse buttons with live CPS.",
                Category.HUD, true, 0.008f, 0.78f, false);
    }

    @Override
    public int width(Minecraft mc) {
        return 3 * K + 2 * GAP;
    }

    @Override
    public int height(Minecraft mc) {
        int h = 2 * K + GAP;
        if (mouse.value) h += GAP + 26;
        if (space.value) h += GAP + 14;
        return h;
    }

    private boolean down(KeyMapping k, int i) {
        boolean d = k.isDown() || (HudRenderer.editing() && i == 0);
        anim[i] += ((d ? 1f : 0f) - anim[i]) * 0.35f;
        return d;
    }

    private void key(GuiGraphics g, int x, int y, int w, int h, int i, String label, String sub, KeyMapping k) {
        down(k, i);
        int base = 0xA00E1120;
        int bg = Theme.lerp(base, Theme.alpha(Theme.ACCENT, 0.85f), anim[i]);
        Theme.roundRect(g, x, y, w, h, 3, bg);
        if (sub == null) {
            Theme.centered(g, label, x + w / 2, y + (h - 8) / 2, Theme.lerp(Theme.TEXT, 0xFFFFFFFF, anim[i]), true);
        } else {
            Theme.centered(g, label, x + w / 2, y + 5, Theme.lerp(Theme.TEXT, 0xFFFFFFFF, anim[i]), true);
            Theme.centered(g, sub, x + w / 2, y + 15, Theme.lerp(Theme.DIM, 0xFFE0D9FF, anim[i]), false);
        }
    }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        var o = mc.options;
        key(g, K + GAP, 0, K, K, 0, "W", null, o.keyUp);
        key(g, 0, K + GAP, K, K, 1, "A", null, o.keyLeft);
        key(g, K + GAP, K + GAP, K, K, 2, "S", null, o.keyDown);
        key(g, 2 * (K + GAP), K + GAP, K, K, 3, "D", null, o.keyRight);
        int y = 2 * K + 2 * GAP;
        if (mouse.value) {
            int mw = (w - GAP) / 2;
            key(g, 0, y, mw, 26, 4, "LMB", String.valueOf(CpsTracker.left()), o.keyAttack);
            key(g, mw + GAP, y, w - mw - GAP, 26, 5, "RMB", String.valueOf(CpsTracker.right()), o.keyUse);
            y += 26 + GAP;
        }
        if (space.value) {
            down(o.keyJump, 6);
            int bg = Theme.lerp(0xA00E1120, Theme.alpha(Theme.ACCENT, 0.85f), anim[6]);
            Theme.roundRect(g, 0, y, w, 14, 3, bg);
            int cy = y + 7, lw = 18;
            g.fill((w - lw) / 2, cy, (w + lw) / 2, cy + 1, Theme.lerp(Theme.DIM, 0xFFFFFFFF, anim[6]));
        }
    }
}
