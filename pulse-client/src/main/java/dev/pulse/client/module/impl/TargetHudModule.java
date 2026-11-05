package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

/** Shows the health of whoever you last hit. */
public final class TargetHudModule extends Module {
    private LivingEntity target;
    private long lastHit;

    public TargetHudModule() {
        super("target", "Target HUD", "Health bar of the last entity you hit.", Category.COMBAT,
                true, 0.5f, 0.62f, true);
    }

    public void set(LivingEntity e) {
        target = e;
        lastHit = System.currentTimeMillis();
    }

    private boolean active() {
        return HudRenderer.editing()
                || (target != null && target.isAlive() && System.currentTimeMillis() - lastHit < 4000);
    }

    @Override public int width(Minecraft mc) { return active() ? 150 : 0; }

    @Override public int height(Minecraft mc) { return active() ? 36 : 0; }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        boolean sample = target == null || !target.isAlive();
        String name = sample ? "Player" : target.getName().getString();
        float hp = sample ? 14f : target.getHealth();
        float max = sample ? 20f : Math.max(1f, target.getMaxHealth());
        float ratio = Math.max(0f, Math.min(1f, hp / max));

        Theme.shadow(g, Theme.fit(name, w - 60), 8, 6, Theme.TEXT);
        String num = String.format("%.1f", hp);
        Theme.shadow(g, num, w - 8 - Theme.width(num), 6, Theme.DIM);

        int bx = 8, bw = w - 16, by = 22;
        Theme.roundRect(g, bx, by, bw, 6, 3, 0xFF2A2F4A);
        int fill = Math.round(bw * ratio);
        if (fill > 0) {
            int c = Theme.lerp(Theme.BAD, Theme.GOOD, ratio);
            Theme.roundRect(g, bx, by, Math.max(4, fill), 6, 3, c);
        }
    }
}
