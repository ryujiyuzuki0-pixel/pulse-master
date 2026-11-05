package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PotionModule extends Module {
    private static final String[] ROMAN = {"", " II", " III", " IV", " V", " VI", " VII", " VIII", " IX", " X"};

    private record Entry(String name, String time, int color, boolean low) {}

    public PotionModule() {
        super("potions", "Potion Effects", "Active effects with a countdown.", Category.COMBAT,
                true, 0.992f, 0.02f, true);
    }

    private List<Entry> entries(Minecraft mc) {
        List<Entry> out = new ArrayList<>();
        if (HudRenderer.editing() && (mc.player == null || mc.player.getActiveEffects().isEmpty())) {
            out.add(new Entry("Speed II", "0:42", 0x7CAFC6, false));
            out.add(new Entry("Strength", "0:08", 0x932423, true));
            return out;
        }
        if (mc.player == null) return out;
        List<MobEffectInstance> list = new ArrayList<>(mc.player.getActiveEffects());
        list.sort(Comparator.comparingInt(MobEffectInstance::getDuration).reversed());
        for (MobEffectInstance e : list) {
            int amp = Math.max(0, Math.min(ROMAN.length - 1, e.getAmplifier()));
            String name = e.getEffect().value().getDisplayName().getString() + ROMAN[amp];
            String time;
            boolean low = false;
            if (e.isInfiniteDuration()) {
                time = "**";
            } else {
                int s = e.getDuration() / 20;
                time = (s / 60) + ":" + String.format("%02d", s % 60);
                low = s < 10;
            }
            out.add(new Entry(name, time, e.getEffect().value().getColor(), low));
        }
        return out;
    }

    @Override
    public int width(Minecraft mc) {
        return entries(mc).isEmpty() ? 0 : 128;
    }

    @Override
    public int height(Minecraft mc) {
        int n = entries(mc).size();
        return n == 0 ? 0 : 8 + n * 13;
    }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        int y = 5;
        for (Entry e : entries(mc)) {
            g.fill(6, y + 2, 9, y + 7, 0xFF000000 | e.color());
            Theme.shadow(g, Theme.fit(e.name(), w - 60), 13, y, Theme.TEXT);
            Theme.shadow(g, e.time(), w - 6 - Theme.width(e.time()), y, e.low() ? Theme.BAD : Theme.DIM);
            y += 13;
        }
    }
}
