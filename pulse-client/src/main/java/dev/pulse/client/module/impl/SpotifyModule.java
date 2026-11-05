package dev.pulse.client.module.impl;

import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.spotify.SpotifyClient;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class SpotifyModule extends Module {
    public SpotifyModule() {
        super("spotify", "Now Playing", "Shows your current Spotify track with a progress bar.",
                Category.MUSIC, true, 0.5f, 0.97f, true);
    }

    private boolean full() {
        return SpotifyClient.hasTrack() || HudRenderer.editing();
    }

    @Override public int width(Minecraft mc) { return 200; }

    @Override public int height(Minecraft mc) { return full() ? 50 : 30; }

    private static String time(long ms) {
        long s = ms / 1000;
        return (s / 60) + ":" + String.format("%02d", s % 60);
    }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        if (!full()) {
            Theme.shadow(g, "Spotify", 8, 6, Theme.TEXT);
            String line = SpotifyClient.connected() ? "Nothing playing" : "Not connected - /pulse spotify login";
            Theme.shadow(g, Theme.fit(line, w - 16), 8, 17, Theme.DIM);
            return;
        }
        boolean sample = !SpotifyClient.hasTrack();
        String title = sample ? "Song Title" : SpotifyClient.title();
        String artist = sample ? "Artist Name" : SpotifyClient.artist();
        long dur = sample ? 200_000 : SpotifyClient.duration();
        long prog = sample ? 72_000 : SpotifyClient.progress();
        boolean playing = sample || SpotifyClient.playing();

        // equalizer tile
        Theme.roundRect(g, 6, 6, 38, 38, 5, 0xFF1A1F3A);
        double t = System.currentTimeMillis() / 180.0;
        for (int i = 0; i < 4; i++) {
            int bh = playing ? 8 + (int) Math.round((Math.sin(t + i * 1.3) * 0.5 + 0.5) * 20) : 6;
            int bx = 11 + i * 7;
            int top = 38 - bh + 6;
            Theme.roundRect(g, bx, top, 4, bh, 2, Theme.lerp(Theme.ACCENT, Theme.ACCENT2, i / 3f));
        }

        Theme.shadow(g, Theme.fit(title, w - 58), 52, 8, Theme.TEXT);
        Theme.shadow(g, Theme.fit(artist, w - 58), 52, 19, Theme.DIM);

        int bx = 52, bw = w - 52 - 8, by = 33;
        Theme.roundRect(g, bx, by, bw, 3, 1, 0xFF2A2F4A);
        int fill = dur <= 0 ? 0 : (int) Math.round(bw * (prog / (double) dur));
        if (fill > 0) Theme.hGradient(g, bx, by, fill, 3, Theme.ACCENT, Theme.ACCENT2);
        Theme.scaledText(g, time(prog), bx, 39, 0.75f, Theme.DIM, false);
        String d = time(dur);
        Theme.scaledText(g, d, bx + bw - (int) (Theme.width(d) * 0.75f), 39, 0.75f, Theme.DIM, false);
    }
}
