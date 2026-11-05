package dev.pulse.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.ModuleManager;
import dev.pulse.client.module.setting.Setting;
import dev.pulse.client.spotify.SpotifyClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PulseConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private PulseConfig() {}

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("pulse.json");
    }

    public static void load() {
        Path p = path();
        if (!Files.exists(p)) return;
        try (Reader r = Files.newBufferedReader(p)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (root.has("spotifyClientId")) SpotifyClient.clientId = root.get("spotifyClientId").getAsString();
            JsonObject mods = root.has("modules") ? root.getAsJsonObject("modules") : null;
            if (mods == null) return;
            for (Module m : ModuleManager.all()) {
                if (!mods.has(m.id)) continue;
                JsonObject o = mods.getAsJsonObject(m.id);
                if (o.has("enabled")) m.enabled = o.get("enabled").getAsBoolean();
                if (o.has("x")) m.fx = clamp(o.get("x").getAsFloat());
                if (o.has("y")) m.fy = clamp(o.get("y").getAsFloat());
                if (o.has("settings")) {
                    JsonObject so = o.getAsJsonObject("settings");
                    for (Setting s : m.settings) {
                        if (so.has(s.name)) {
                            try {
                                s.fromJson(so.get(s.name));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            PulseClient.LOG.warn("Could not read pulse.json, using defaults", e);
        }
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("spotifyClientId", SpotifyClient.clientId == null ? "" : SpotifyClient.clientId);
            JsonObject mods = new JsonObject();
            for (Module m : ModuleManager.all()) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.enabled);
                if (m.hud) {
                    o.addProperty("x", Math.round(m.fx * 10000f) / 10000f);
                    o.addProperty("y", Math.round(m.fy * 10000f) / 10000f);
                }
                JsonObject so = new JsonObject();
                for (Setting s : m.settings) so.add(s.name, s.toJson());
                o.add("settings", so);
                mods.add(m.id, o);
            }
            root.add("modules", mods);
            Files.writeString(path(), GSON.toJson(root));
        } catch (Exception e) {
            PulseClient.LOG.warn("Could not save pulse.json", e);
        }
    }

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}
