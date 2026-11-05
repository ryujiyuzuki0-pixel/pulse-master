package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.setting.BoolSetting;
import dev.pulse.client.module.setting.ChoiceSetting;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;

/** Recolors the whole client: menu, HUD cards, keystrokes and progress bars. */
public final class ThemeModule extends Module {
    private static final int[][] PRESETS = {
            {0xFF7C5CFF, 0xFF22D3EE}, // Midnight
            {0xFF3B82F6, 0xFF2DD4BF}, // Ocean
            {0xFFF97316, 0xFFFBBF24}, // Ember
            {0xFFEC4899, 0xFFF9A8D4}, // Rose
            {0xFF22C55E, 0xFFA3E635}, // Forest
            {0xFFE5E7EB, 0xFF9CA3AF}, // Mono
    };
    private final ChoiceSetting preset = add(new ChoiceSetting("Preset", 0,
            "Midnight", "Ocean", "Ember", "Rose", "Forest", "Mono"));
    private final BoolSetting rainbow = add(new BoolSetting("Rainbow accent", false));

    public ThemeModule() {
        super("theme", "Client Theme", "Pick the accent colors used across Pulse.",
                Category.COSMETICS, false, 0f, 0f, false);
    }

    @Override
    public void tick(Minecraft mc) {
        if (rainbow.value) {
            float hue = (System.currentTimeMillis() / 25L) % 360;
            Theme.setAccents(Theme.hsb(hue, 0.65f, 1f), Theme.hsb(hue + 50f, 0.65f, 1f));
        } else {
            int[] p = PRESETS[preset.index];
            Theme.setAccents(p[0], p[1]);
        }
    }

    @Override
    public void onDisable() {
        Theme.setAccents(PRESETS[0][0], PRESETS[0][1]);
    }
}
