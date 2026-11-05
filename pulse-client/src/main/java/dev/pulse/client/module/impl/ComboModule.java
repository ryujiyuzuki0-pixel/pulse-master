package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;

public final class ComboModule extends LineModule {
    private int combo;
    private long lastHit;
    private boolean wasHurt;

    public ComboModule() {
        super("combo", "Combo", "Consecutive hits without taking damage.", Category.COMBAT, 0.008f, 0.2f);
    }

    public void hit() {
        combo++;
        lastHit = System.currentTimeMillis();
    }

    @Override
    public void tick(Minecraft mc) {
        if (mc.player == null) return;
        boolean hurt = mc.player.hurtTime > 0;
        if (hurt && !wasHurt) combo = 0;
        wasHurt = hurt;
        if (combo > 0 && System.currentTimeMillis() - lastHit > 3000) combo = 0;
    }

    @Override protected String label() { return "COMBO"; }

    @Override protected String value(Minecraft mc) { return String.valueOf(combo); }

    @Override protected int valueColor(Minecraft mc) { return combo >= 5 ? Theme.ACCENT2 : Theme.TEXT; }
}
