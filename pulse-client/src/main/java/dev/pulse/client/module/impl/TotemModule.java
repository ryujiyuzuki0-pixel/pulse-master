package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class TotemModule extends Module {
    private final ItemStack icon = new ItemStack(Items.TOTEM_OF_UNDYING);

    public TotemModule() {
        super("totems", "Totem Count", "Totems of Undying in your inventory.", Category.COMBAT,
                true, 0.008f, 0.164f, true);
    }

    private int count(Minecraft mc) {
        if (mc.player == null) return 0;
        int n = 0;
        var inv = mc.player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.is(Items.TOTEM_OF_UNDYING)) n += s.getCount();
        }
        return n;
    }

    @Override public int width(Minecraft mc) { return 52; }

    @Override public int height(Minecraft mc) { return 22; }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        g.renderItem(icon, 4, 3);
        int c = count(mc);
        Theme.shadow(g, "x" + c, 25, 7, c == 0 ? Theme.BAD : Theme.TEXT);
    }
}
