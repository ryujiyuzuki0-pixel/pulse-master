package dev.pulse.client.ui;

import dev.pulse.client.PulseConfig;
import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.ModuleManager;
import dev.pulse.client.module.impl.CapeModule;
import dev.pulse.client.module.impl.SpotifyModule;
import dev.pulse.client.module.setting.BoolSetting;
import dev.pulse.client.module.setting.ChoiceSetting;
import dev.pulse.client.module.setting.Setting;
import dev.pulse.client.module.setting.SliderSetting;
import dev.pulse.client.spotify.SpotifyClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen extends Screen {
    private static final int SB = 118, HEADER = 46, ROW = 36, SROW = 24, GAP = 6, SPOTIFY_BLOCK = 58, CAPE_BLOCK = 112;

    private record Hit(int x, int y, int w, int h, boolean scrolled, Runnable action) {}

    private final Screen parent;
    private final List<Hit> hits = new ArrayList<>();
    private final String version;
    private Category category = Category.HUD;
    private Module expanded;
    private float scroll, targetScroll, maxScroll;
    private SliderSetting dragging;
    private int dragX, dragW;
    private int viewY, viewH;

    public MenuScreen(Screen parent) {
        super(Component.literal("Pulse"));
        this.parent = parent;
        this.version = FabricLoader.getInstance().getModContainer("pulse")
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("dev");
    }

    // ------------------------------------------------------------------ render

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        hits.clear();
        scroll += (targetScroll - scroll) * 0.3f;
        g.fill(0, 0, width, height, 0xAA06080F);

        int pw = Math.min(520, width - 16), ph = Math.min(330, height - 16);
        int px = (width - pw) / 2, py = (height - ph) / 2;
        Theme.roundRect(g, px - 1, py - 1, pw + 2, ph + 2, 7, 0x40FFFFFF);
        Theme.roundRect(g, px, py, pw, ph, 6, Theme.BG);

        drawHeader(g, px, py, pw);
        drawSidebar(g, px, py, ph, mx, my);
        drawContent(g, px, py, pw, ph, mx, my);
    }

    private void drawHeader(GuiGraphics g, int px, int py, int pw) {
        // animated pulse glyph
        double t = System.currentTimeMillis() / 420.0;
        for (int i = 0; i < 5; i++) {
            int bh = 6 + (int) Math.round((Math.sin(t + i * 0.9) * 0.5 + 0.5) * 14);
            int bx = px + 16 + i * 5;
            Theme.roundRect(g, bx, py + 23 - bh / 2, 3, bh, 1, Theme.lerp(Theme.ACCENT, Theme.ACCENT2, i / 4f));
        }
        Theme.scaledText(g, "PULSE", px + 46, py + 15, 1.6f, Theme.TEXT, false);
        String v = "v" + version;
        Theme.text(g, v, px + pw - 14 - Theme.width(v), py + 19, Theme.DIM);
        g.fill(px, py + HEADER, px + pw, py + HEADER + 1, Theme.LINE);
        Theme.hGradient(g, px + 8, py + HEADER, 90, 1, Theme.ACCENT, Theme.ACCENT2);
    }

    private void drawSidebar(GuiGraphics g, int px, int py, int ph, int mx, int my) {
        g.fill(px + SB, py + HEADER + 1, px + SB + 1, py + ph - 6, Theme.LINE);
        int y = py + HEADER + 12;
        // Rows are 32px apart when there is room; squeeze them (down to 22px) so the Edit HUD button never overlaps.
        int room = (ph - 62 - 8) - (HEADER + 12);
        int step = Math.max(22, Math.min(32, room / Category.values().length));
        int rh = step - 4;
        for (Category c : Category.values()) {
            boolean active = c == category;
            boolean hover = Theme.in(mx, my, px + 8, y, SB - 16, rh);
            if (active) {
                Theme.roundRect(g, px + 8, y, SB - 16, rh, 5, Theme.alpha(Theme.ACCENT, 0.22f));
                g.fill(px + 8, y + (rh - 14) / 2, px + 11, y + (rh - 14) / 2 + 14, Theme.ACCENT);
            } else if (hover) {
                Theme.roundRect(g, px + 8, y, SB - 16, rh, 5, 0x10FFFFFF);
            }
            Theme.text(g, c.label, px + 20, y + (rh - 8) / 2, active ? Theme.TEXT : Theme.DIM);
            int on = 0, total = 0;
            for (Module m : ModuleManager.byCategory(c)) {
                total++;
                if (m.enabled) on++;
            }
            String cnt = on + "/" + total;
            Theme.text(g, cnt, px + SB - 16 - Theme.width(cnt), y + (rh - 8) / 2, active ? Theme.ACCENT2 : 0xFF565C7E);
            final Category cc = c;
            hits.add(new Hit(px + 8, y, SB - 16, rh, false, () -> {
                category = cc;
                expanded = null;
                targetScroll = 0;
                scroll = 0;
            }));
            y += step;
        }
        int by = py + ph - 62;
        boolean h1 = Theme.in(mx, my, px + 10, by, SB - 20, 24);
        Theme.button(g, px + 10, by, SB - 20, 24, "Edit HUD", true, h1);
        hits.add(new Hit(px + 10, by, SB - 20, 24, false, () -> minecraft.setScreen(new HudEditorScreen(this))));
        Theme.centered(g, "RShift to close", px + SB / 2, py + ph - 28, 0xFF565C7E, false);
    }

    private int extraHeight(Module m) {
        int h = m.settings.size() * SROW;
        if (m instanceof SpotifyModule) h += SPOTIFY_BLOCK;
        if (m instanceof CapeModule) h += CAPE_BLOCK;
        return h == 0 ? 0 : h + 8;
    }

    private void drawContent(GuiGraphics g, int px, int py, int pw, int ph, int mx, int my) {
        int cx = px + SB + 14, cw = pw - SB - 28;
        Theme.scaledText(g, category.label, cx, py + HEADER + 11, 1.3f, Theme.TEXT, false);
        viewY = py + HEADER + 32;
        viewH = ph - HEADER - 32 - 10;

        List<Module> mods = ModuleManager.byCategory(category);
        int y = viewY - Math.round(scroll);
        int total = 0;
        g.enableScissor(cx - 2, viewY, cx + cw + 2, viewY + viewH);
        for (Module m : mods) {
            boolean open = m == expanded;
            int extra = open ? extraHeight(m) : 0;
            drawCard(g, m, cx, y, cw, open, extra, mx, my);
            y += ROW + extra + GAP;
            total += ROW + extra + GAP;
        }
        g.disableScissor();
        maxScroll = Math.max(0, total - viewH);
        targetScroll = Math.max(0, Math.min(maxScroll, targetScroll));
    }

    private boolean visible(int y, int h) {
        return y + h > viewY && y < viewY + viewH;
    }

    private void drawCard(GuiGraphics g, Module m, int x, int y, int w, boolean open, int extra, int mx, int my) {
        int h = ROW + extra;
        boolean inView = my >= viewY && my < viewY + viewH;
        boolean hover = inView && Theme.in(mx, my, x, y, w, ROW);
        Theme.roundRect(g, x, y, w, h, 5, hover || open ? Theme.PANEL_HI : Theme.PANEL);
        if (m.enabled) g.fill(x, y + 8, x + 2, y + ROW - 8, Theme.ACCENT);

        Theme.text(g, m.name, x + 12, y + 7, m.enabled ? Theme.TEXT : 0xFFB4B9D6);
        Theme.text(g, Theme.fit(m.description, w - 100), x + 12, y + 21, Theme.DIM);

        m.toggleAnim += ((m.enabled ? 1f : 0f) - m.toggleAnim) * 0.3f;
        Theme.toggle(g, x + w - 38, y + 11, m.toggleAnim);
        if (extraHeight(m) > 0) Theme.chevron(g, x + w - 54, y + ROW / 2, open, Theme.DIM);

        if (visible(y, ROW)) {
            if (extraHeight(m) > 0) {
                hits.add(new Hit(x, y, w - 44, ROW, true, () -> {
                    expanded = open ? null : m;
                }));
            }
            hits.add(new Hit(x + w - 44, y, 44, ROW, true, m::toggle));
        }

        if (!open) return;
        g.fill(x + 10, y + ROW, x + w - 10, y + ROW + 1, Theme.LINE);
        int sy = y + ROW + 5;
        for (Setting s : m.settings) {
            drawSetting(g, s, x, sy, w, mx, my);
            sy += SROW;
        }
        if (m instanceof SpotifyModule) drawSpotifyBlock(g, x, sy + 2, w, mx, my);
        if (m instanceof CapeModule cm) drawCapeBlock(g, cm, x, sy + 2, w);
    }

    private void drawSetting(GuiGraphics g, Setting s, int x, int y, int w, int mx, int my) {
        Theme.text(g, s.name, x + 14, y + 8, 0xFFC4C8E2);
        if (s instanceof BoolSetting b) {
            b.anim += ((b.value ? 1f : 0f) - b.anim) * 0.3f;
            int sx = x + w - 14 - 26;
            Theme.toggle(g, sx, y + 5, b.anim);
            if (visible(y, SROW)) hits.add(new Hit(sx - 4, y, 34, SROW, true, () -> b.value = !b.value));
        } else if (s instanceof ChoiceSetting c) {
            int bw = 96, bx = x + w - 14 - bw;
            boolean hover = my >= viewY && my < viewY + viewH && Theme.in(mx, my, bx, y + 3, bw, 18);
            Theme.button(g, bx, y + 3, bw, 18, c.get(), false, hover);
            if (visible(y, SROW)) hits.add(new Hit(bx, y + 3, bw, 18, true, c::cycle));
        } else if (s instanceof SliderSetting sl) {
            int tw = 110, tx = x + w - 14 - 46 - tw, ty = y + 11;
            Theme.roundRect(g, tx, ty, tw, 4, 2, 0xFF2A2F4A);
            int fill = (int) Math.round(tw * sl.normalized());
            if (fill > 0) Theme.hGradient(g, tx, ty, fill, 4, Theme.ACCENT, Theme.ACCENT2);
            Theme.roundRect(g, tx + fill - 4, ty - 3, 8, 10, 4, 0xFFFFFFFF);
            String val = sl.format();
            Theme.text(g, val, x + w - 14 - Theme.width(val), y + 8, Theme.ACCENT2);
            if (visible(y, SROW)) {
                hits.add(new Hit(tx - 5, y, tw + 10, SROW, true, () -> {
                    dragging = sl;
                    dragX = tx;
                    dragW = tw;
                }));
            }
        }
    }

    private void drawCapeBlock(GuiGraphics g, CapeModule cape, int x, int y, int w) {
        int[] face = cape.face();
        int s = 5, ox = x + w / 2 - 5 * s, oy = y + 18;
        Theme.roundRect(g, ox - 10, oy - 9, 10 * s + 20, 8, 3, 0xFF3A3F5C);
        double t = System.currentTimeMillis() / 600.0;
        for (int py = 0; py < 16; py++) {
            float f = py / 15f;
            for (int px = 0; px < 10; px++) {
                float sway = (float) (Math.sin(t * 1.6 + py * 0.45) * 9 * f * f + Math.sin(t * 0.9 + px * 0.3) * 2 * f);
                float lift = (float) (Math.cos(t * 1.3 + px * 0.5) * 1.5 * f);
                int x0 = ox + px * s + Math.round(sway), y0 = oy + py * s + Math.round(lift);
                g.fill(x0, y0, x0 + s + 1, y0 + s + 1, face[py * 10 + px]);
            }
        }
    }

    private void drawSpotifyBlock(GuiGraphics g, int x, int y, int w, int mx, int my) {
        Theme.text(g, Theme.fit(SpotifyClient.statusLine(), w - 28), x + 14, y + 4, Theme.DIM);
        int by = y + 20, bh = 20, bx = x + 14;
        boolean conn = SpotifyClient.connected();
        bx = btn(g, bx, by, conn ? 78 : 62, bh, conn ? "Disconnect" : "Connect", !conn, mx, my,
                conn ? SpotifyClient::logout : SpotifyClient::login);
        bx = btn(g, bx, by, 38, bh, "Prev", false, mx, my, () -> SpotifyClient.control("previous"));
        bx = btn(g, bx, by, 42, bh, "Pause", false, mx, my, () -> SpotifyClient.control("pause"));
        bx = btn(g, bx, by, 38, bh, "Play", false, mx, my, () -> SpotifyClient.control("play"));
        btn(g, bx, by, 38, bh, "Next", false, mx, my, () -> SpotifyClient.control("next"));
    }

    private int btn(GuiGraphics g, int x, int y, int w, int h, String label, boolean primary,
                    int mx, int my, Runnable r) {
        boolean hover = my >= viewY && my < viewY + viewH && Theme.in(mx, my, x, y, w, h);
        Theme.button(g, x, y, w, h, label, primary, hover);
        if (visible(y, h)) hits.add(new Hit(x, y, w, h, true, r));
        return x + w + 6;
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
        if (e.button() != 0) return super.mouseClicked(e, dbl);
        double mx = e.x(), my = e.y();
        for (int i = hits.size() - 1; i >= 0; i--) {
            Hit h = hits.get(i);
            if (!Theme.in(mx, my, h.x(), h.y(), h.w(), h.h())) continue;
            if (h.scrolled() && (my < viewY || my >= viewY + viewH)) continue;
            h.action().run();
            if (dragging != null) updateSlider(mx);
            return true;
        }
        return super.mouseClicked(e, dbl);
    }

    private void updateSlider(double mx) {
        if (dragging != null) dragging.setNormalized((mx - dragX) / dragW);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (dragging != null) {
            updateSlider(e.x());
            return true;
        }
        return super.mouseDragged(e, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        if (dragging != null) {
            dragging = null;
            PulseConfig.save();
            return true;
        }
        return super.mouseReleased(e);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        targetScroll = Math.max(0, Math.min(maxScroll, targetScroll - (float) sy * 26f));
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            onClose();
            return true;
        }
        return super.keyPressed(e);
    }

    @Override
    public void onClose() {
        PulseConfig.save();
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
