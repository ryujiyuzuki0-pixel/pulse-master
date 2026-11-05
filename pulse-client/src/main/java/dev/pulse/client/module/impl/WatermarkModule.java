package dev.pulse.client.module.impl;

import dev.pulse.client.PulseClient;
import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.ui.Theme;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Small "PULSE <version>" tag. */
public final class WatermarkModule extends Module {
    private final String text;

    public WatermarkModule() {
        super("watermark", "Watermark", "Shows the client name and version.", Category.HUD,
                true, 0.5f, 0.008f, true);
        String v = FabricLoader.getInstance().getModContainer(PulseClient.ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("");
        int plus = v.indexOf('+');
        text = "PULSE " + (plus > 0 ? v.substring(0, plus) : v);
    }

    @Override public int width(Minecraft mc) { return 14 + Theme.width(text); }

    @Override public int height(Minecraft mc) { return 17; }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        Theme.shadow(g, text, 7, 5, Theme.ACCENT);
    }
}
