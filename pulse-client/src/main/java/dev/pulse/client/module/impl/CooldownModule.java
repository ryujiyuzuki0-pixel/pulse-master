package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Shows item cooldowns (pearls, gapples, chorus, wind charges) as small draining bars. */
public final class CooldownModule extends Module {
    private static final ItemStack[] TRACKED = {
            new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.GOLDEN_APPLE),
            new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), new ItemStack(Items.CHORUS_FRUIT),
            new ItemStack(Items.WIND_CHARGE)};
    private static final int ROW = 19;

    public CooldownModule() {
        super("cooldowns", "Cooldowns", "Active item cooldowns.", Category.COMBAT,
                true, 0.008f, 0.40f, true);
    }

    /** Remaining fraction (0..1) per tracked item; -1 means not on cooldown. */
    private List<float[]> active(Minecraft mc) {
        List<float[]> out = new ArrayList<>();
        for (int i = 0; i < TRACKED.length; i++) {
            float p = 0f;
            if (HudRenderer.editing()) p = i == 0 ? 0.65f : i == 1 ? 0.3f : 0f;
            else if (mc.player != null) p = mc.player.getCooldowns().getCooldownPercent(TRACKED[i], 0f);
            if (p > 0f) out.add(new float[]{i, p});
        }
        return out;
    }

    @Override public int width(Minecraft mc) { return 84; }

    @Override
    public int height(Minecraft mc) {
        int n = active(mc).size();
        return n == 0 ? 0 : n * ROW + 3;
    }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        int row = 0;
        for (float[] a : active(mc)) {
            int y = 3 + row++ * ROW;
            g.renderItem(TRACKED[(int) a[0]], 3, y);
            int bx = 24, bw = w - bx - 6;
            g.fill(bx, y + 6, bx + bw, y + 10, Theme.alpha(Theme.LINE, 0.9f));
            g.fill(bx, y + 6, bx + Math.round(bw * a[1]), y + 10, Theme.ACCENT);
        }
    }
}
