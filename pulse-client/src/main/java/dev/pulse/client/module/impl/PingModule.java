package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

public final class PingModule extends LineModule {
    public PingModule() {
        super("ping", "Ping", "Latency to the server.", Category.HUD, 0.008f, 0.088f);
    }

    private int ping(Minecraft mc) {
        if (mc.player == null || mc.getConnection() == null) return 0;
        PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        return info == null ? 0 : info.getLatency();
    }

    @Override protected String label() { return "PING"; }

    @Override protected String value(Minecraft mc) { return ping(mc) + " ms"; }

    @Override
    protected int valueColor(Minecraft mc) {
        int p = ping(mc);
        return p < 80 ? Theme.GOOD : p < 160 ? Theme.WARN : Theme.BAD;
    }
}
