package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.setting.BoolSetting;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ArmorModule extends Module {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final BoolSetting numbers = add(new BoolSetting("Show durability", true));
    private ItemStack[] sample;

    public ArmorModule() {
        super("armor", "Armor", "Equipped armor with remaining durability.", Category.COMBAT,
                true, 0.992f, 0.55f, false);
    }

    @Override
    public int width(Minecraft mc) {
        return numbers.value ? 16 + 6 + Theme.width("0000") + 6 : 22;
    }

    @Override
    public int height(Minecraft mc) {
        return 4 * 19 + 3;
    }

    private ItemStack stack(Minecraft mc, int i) {
        if (HudRenderer.editing()) {
            if (sample == null) {
                sample = new ItemStack[]{new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.DIAMOND_CHESTPLATE),
                        new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND_BOOTS)};
            }
            return sample[i];
        }
        return mc.player == null ? ItemStack.EMPTY : mc.player.getItemBySlot(SLOTS[i]);
    }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        for (int i = 0; i < 4; i++) {
            ItemStack s = stack(mc, i);
            int y = 3 + i * 19;
            if (s.isEmpty()) continue;
            g.renderItem(s, 3, y);
            if (numbers.value && s.isDamageableItem()) {
                int left = s.getMaxDamage() - s.getDamageValue();
                float ratio = left / (float) s.getMaxDamage();
                int c = ratio > 0.5f ? Theme.GOOD : ratio > 0.2f ? Theme.WARN : Theme.BAD;
                Theme.shadow(g, String.valueOf(left), 25, y + 4, c);
            }
        }
    }
}
