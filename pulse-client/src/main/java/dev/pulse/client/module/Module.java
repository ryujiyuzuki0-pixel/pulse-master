package dev.pulse.client.module;

import dev.pulse.client.module.setting.BoolSetting;
import dev.pulse.client.module.setting.Setting;
import dev.pulse.client.module.setting.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String id, name, description;
    public final Category category;
    public final boolean hud;
    public volatile boolean enabled;
    /** Position as a fraction (0..1) of the free space on screen, so layouts survive any resolution. */
    public float fx, fy;
    public final float defaultFx, defaultFy;
    public final List<Setting> settings = new ArrayList<>();
    public float toggleAnim;

    private SliderSetting scale;
    private BoolSetting background;

    protected Module(String id, String name, String description, Category category,
                     boolean hud, float fx, float fy, boolean backgroundDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.hud = hud;
        this.fx = this.defaultFx = fx;
        this.fy = this.defaultFy = fy;
        if (hud) {
            scale = add(new SliderSetting("Scale", 0.6, 2.0, 0.05, 1.0, "x"));
            background = add(new BoolSetting("Background", backgroundDefault));
        }
    }

    protected final <T extends Setting> T add(T s) {
        settings.add(s);
        return s;
    }

    public float scaleValue() {
        return scale == null ? 1f : (float) scale.value;
    }

    public boolean backgroundOn() {
        return background != null && background.value;
    }

    public void setEnabled(boolean e) {
        if (e == enabled) return;
        enabled = e;
        if (e) onEnable(); else onDisable();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void resetPosition() {
        fx = defaultFx;
        fy = defaultFy;
    }

    public void onEnable() {}

    public void onDisable() {}

    public void tick(Minecraft mc) {}

    /** Full-screen drawing that is not a movable HUD box (vignettes, etc). */
    public void drawOverlay(GuiGraphics g, Minecraft mc) {}

    /** Size of the HUD element in unscaled GUI pixels. Return 0 to hide. */
    public int width(Minecraft mc) { return 0; }

    public int height(Minecraft mc) { return 0; }

    /** Draw at (0,0) with size (w,h). The pose is already translated and scaled. */
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {}
}
