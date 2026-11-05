package dev.pulse.client.module.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.pulse.client.PulseClient;
import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.setting.SliderSetting;
import dev.pulse.client.ui.HudRenderer;
import dev.pulse.client.ui.Theme;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Named waypoints (managed with /pulse waypoint ...). The HUD lists the nearest ones in the
 * current dimension with an arrow showing which way to go. Saved to config/pulse-waypoints.json.
 */
public final class WaypointsModule extends Module {
    public static final class Waypoint {
        public String name;
        public double x, y, z;
        public String dim;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String[] ARROWS = {"\u2191", "\u2197", "\u2192", "\u2198", "\u2193", "\u2199", "\u2190", "\u2196"};
    private static final int ROW = 13;

    private final List<Waypoint> points = new ArrayList<>();
    private final SliderSetting rows = add(new SliderSetting("Rows", 1, 5, 1, 3, ""));

    public WaypointsModule() {
        super("waypoints", "Waypoints", "Nearest saved waypoints. Use /pulse waypoint add <name>.",
                Category.UTILITY, true, 0.992f, 0.08f, true);
        load();
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("pulse-waypoints.json");
    }

    private void load() {
        Path p = file();
        if (!Files.exists(p)) return;
        try (Reader r = Files.newBufferedReader(p)) {
            List<Waypoint> in = GSON.fromJson(r, new TypeToken<List<Waypoint>>() {}.getType());
            if (in != null) points.addAll(in);
        } catch (Exception e) {
            PulseClient.LOG.warn("Could not read pulse-waypoints.json", e);
        }
    }

    private void save() {
        try {
            Files.writeString(file(), GSON.toJson(points));
        } catch (Exception e) {
            PulseClient.LOG.warn("Could not save pulse-waypoints.json", e);
        }
    }

    private static String dimKey(Minecraft mc) {
        return mc.level == null ? "" : mc.level.dimension().toString();
    }

    public boolean add(Minecraft mc, String name) {
        if (mc.player == null || mc.level == null) return false;
        points.removeIf(w -> w.name.equalsIgnoreCase(name));
        Waypoint w = new Waypoint();
        w.name = name;
        w.x = mc.player.getX();
        w.y = mc.player.getY();
        w.z = mc.player.getZ();
        w.dim = dimKey(mc);
        points.add(w);
        save();
        return true;
    }

    public boolean remove(String name) {
        boolean r = points.removeIf(w -> w.name.equalsIgnoreCase(name));
        if (r) save();
        return r;
    }

    public void clear() {
        points.clear();
        save();
    }

    public List<Waypoint> all() {
        return points;
    }

    private List<Waypoint> nearest(Minecraft mc) {
        List<Waypoint> out = new ArrayList<>();
        if (HudRenderer.editing() && points.isEmpty()) {
            for (String n : new String[]{"Base", "Spawn"}) {
                Waypoint w = new Waypoint();
                w.name = n;
                w.dim = dimKey(mc);
                out.add(w);
            }
            return out;
        }
        if (mc.player == null) return out;
        String dim = dimKey(mc);
        for (Waypoint w : points) if (dim.equals(w.dim)) out.add(w);
        out.sort(Comparator.comparingDouble(w -> dist2(mc, w)));
        int max = (int) rows.value;
        return out.size() > max ? new ArrayList<>(out.subList(0, max)) : out;
    }

    private static double dist2(Minecraft mc, Waypoint w) {
        double dx = w.x - mc.player.getX(), dz = w.z - mc.player.getZ();
        return dx * dx + dz * dz;
    }

    @Override public int width(Minecraft mc) { return 150; }

    @Override
    public int height(Minecraft mc) {
        int n = nearest(mc).size();
        return n == 0 ? 0 : n * ROW + 6;
    }

    @Override
    public void draw(GuiGraphics g, Minecraft mc, int w, int h) {
        int row = 0;
        for (Waypoint wp : nearest(mc)) {
            int y = 4 + row++ * ROW;
            String arrow = "\u2191";
            String dist = "--";
            if (mc.player != null && (wp.x != 0 || wp.z != 0)) {
                double dx = wp.x - mc.player.getX(), dz = wp.z - mc.player.getZ();
                double target = Math.toDegrees(Math.atan2(-dx, dz));
                double rel = ((target - mc.player.getYRot()) % 360 + 360) % 360;
                arrow = ARROWS[(int) Math.round(rel / 45.0) % 8];
                dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz)) + "m";
            }
            Theme.shadow(g, arrow, 6, y, Theme.ACCENT);
            Theme.shadow(g, Theme.fit(wp.name, w - 70), 20, y, Theme.TEXT);
            Theme.shadow(g, dist, w - 6 - Theme.width(dist), y, Theme.DIM);
        }
    }
}
