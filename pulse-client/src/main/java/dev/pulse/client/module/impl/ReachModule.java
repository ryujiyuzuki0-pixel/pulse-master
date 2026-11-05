package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class ReachModule extends LineModule {
    private double last = -1;

    public ReachModule() {
        super("reach", "Reach", "Distance of your last hit.", Category.COMBAT, 0.008f, 0.24f);
    }

    public void record(Player p, Entity e) {
        last = Math.sqrt(e.getBoundingBox().distanceToSqr(p.getEyePosition()));
    }

    @Override protected String label() { return "REACH"; }

    @Override protected String value(Minecraft mc) { return last < 0 ? "-" : String.format("%.2f", last); }
}
