package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** The 27 main inventory slots as a 9x3 grid on the HUD. */
public final class InventoryHudModule extends Module {
    private static final int CELL = 18;

    public InventoryHudModule() {
        super("inventory_hud", "Inventory", "Your main inventory, always visible.", Category.HUD,
                true, 0.5f, 0.96f, true);
    }

    @Override public int width(Minecraft mc) { return 9 * CELL + 6; }

    @Override public int height(Minecraft mc) { return 3 * CELL + 6; }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        if (mc.player == null) return;
        var inv = mc.player.getInventory();
        for (int i = 0; i < 27; i++) {
            int x = 3 + (i % 9) * CELL;
            int y = 3 + (i / 9) * CELL;
            g.fill(x, y, x + CELL - 2, y + CELL - 2, Theme.alpha(Theme.PANEL_HI, 0.55f));
            ItemStack s = inv.getItem(9 + i);
            if (s.isEmpty() && HudRenderer.editing() && i < 4) {
                s = new ItemStack(i == 0 ? Items.DIAMOND_SWORD : i == 1 ? Items.ENDER_PEARL
                        : i == 2 ? Items.GOLDEN_APPLE : Items.TOTEM_OF_UNDYING);
            }
            if (s.isEmpty()) continue;
            g.renderItem(s, x + 1, y + 1);
            g.renderItemDecorations(Theme.font(), s, x + 1, y + 1);
        }
    }
}
