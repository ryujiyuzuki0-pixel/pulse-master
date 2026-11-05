# Pulse Client

An original Fabric PvP HUD client for **Minecraft 1.21.11** (Java 21). Press **Right Shift** in game.

## Build
```
./gradlew build
```
The jar is `build/libs/pulse-client-1.0.0.jar` (ignore the `-sources` jar).
No local setup? Push this folder to GitHub: the included workflow builds the jar and attaches it as an artifact.

## Install
Put the jar in `.minecraft/mods/` with Fabric Loader 0.19.2+ and Fabric API 0.141.3+1.21.11.

## Features
Keystrokes, CPS, FPS, Ping, Coordinates, Armor, Potion effects, Combo, Reach, Totem count,
Toggle Sprint, Zoom (hold C, vanilla FOV minimum is 30), Spotify now playing,
Target HUD, Clock.

### Cosmetics
Client Theme (6 presets or animated rainbow accent), Custom Crosshair (cross, dot, circle, cross + dot,
colors, size, gap, thickness, outline, rainbow), Low Health Vignette, Cape picker (Pulse, Japan, Heartbeat, Nightfall, Breaker) with a live waving preview
in the menu. All client-side only. The textures are in `assets/pulse/textures/cape/`; the in-game cape hook
is not wired yet (it needs a mixin checked against the real 1.21.11 sources).
Right Shift opens the menu. "Edit HUD" lets you drag elements. Settings save to `config/pulse.json`.

## Spotify
1. Create an app at https://developer.spotify.com/dashboard
2. Add the redirect URI `http://127.0.0.1:43019/callback`
3. In game: `/pulse spotify id <client id>` then `/pulse spotify login`
Play/pause/next need Spotify Premium. The refresh token is stored in `config/pulse-spotify.json`.

## Multiple Minecraft versions

One source tree (`src/`) builds every version listed in `settings.gradle`, using
[Stonecutter](https://stonecutter.kikugie.dev/). Per-version values live in
`versions/<mc>/gradle.properties`.

Build all versions: `./gradlew build` (jars land in `versions/<mc>/build/libs`).

Currently builds: 1.21.9, 1.21.10, 1.21.11. Versions before 1.21.9 need extra branches
(the `KeyEvent`/`MouseButtonEvent` input API doesn't exist there).

To add a version:
1. Create `versions/<mc>/gradle.properties` with `minecraft_version`, `minecraft_dep`,
   `fabric_version` and `java_version` (copy `versions/1.21.11` and edit).
2. Add the version to `versions '...'` in `settings.gradle`.
3. Where the game API differs, wrap the code in Stonecutter conditionals, e.g.
   `//? if >=1.21.9 { ... //?} else { ... }`.

## Extra modules

Inventory HUD, Cooldowns (pearls/gapples/chorus/wind charges), Item Counters, Waypoints and
Watermark. All are off by default; enable them in the menu (Right Shift).
Waypoints: `/pulse waypoint add <name>`, `remove <name>`, `list`, `clear`.

## FPS Boost

Performance tab -> **FPS Boost** (on by default). It lowers the vanilla video settings that cost the most frames and
puts yours back when you turn it off or close the game. Pick a preset (Balanced, Max FPS, Potato) or tune each row:
uncapped framerate, VSync, particles, clouds, entity shadows, entity distance, biome blend and a render distance cap.

- It only lowers values. A setting you already run lower is never raised.
- If you change a setting yourself in the vanilla menu while it is on, that setting is left alone when you turn it off.
- Your originals are kept in `config/pulse-fpsboost.json` while it is active, so a crash can't leave you on boosted values.
- It only changes video settings (no mixins). For faster chunk and entity rendering as well, run it next to Sodium and Lithium.

## Capes

Eight bundled capes (Pulse, Japan, Heartbeat, Nightfall, Breaker, Aurora, Ember, Frost). Pick one in the
Cosmetics tab; it shows on your own player in game (other players don't see it). A mixin
(`AbstractClientPlayerMixin`) swaps the cape into your skin. Textures are 64x32 PNGs in
`assets/pulse/textures/cape/`; add your own by dropping a PNG there and adding its name to `CapeArt`.
