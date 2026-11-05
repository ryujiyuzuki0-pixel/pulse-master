package dev.pulse.client.module.impl;

import dev.pulse.client.PulseClient;
import dev.pulse.client.module.Category;
import dev.pulse.client.module.Module;
import dev.pulse.client.module.setting.ChoiceSetting;
import net.minecraft.core.ClientAsset;
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.Optional;

/**
 * Cape selection. The textures ship in assets/pulse/textures/cape/ (64x32, standard cape layout).
 * The menu shows a live waving preview; AbstractClientPlayerMixin applies the chosen cape to your own player in game.
 */
public final class CapeModule extends Module {
    private final ChoiceSetting cape = add(new ChoiceSetting("Cape", 0, CapeArt.NAMES));
    private int patchIndex = -1;
    private PlayerSkin.Patch patch;

    public CapeModule() {
        super("cape", "Cape", "Choose a cape design. Preview waves live in the menu.",
                Category.COSMETICS, false, 0f, 0f, false);
    }

    public int[] face() {
        return CapeArt.FACES[cape.index];
    }

    /** Returns the given skin with the selected cape swapped in. Called by AbstractClientPlayerMixin. */
    public PlayerSkin applyTo(PlayerSkin skin) {
        if (patch == null || patchIndex != cape.index) {
            String name = CapeArt.FILES[cape.index];
            //? if >=1.21.11 {
            Identifier id = Identifier.fromNamespaceAndPath(PulseClient.ID, "cape/" + name);
            Identifier path = Identifier.fromNamespaceAndPath(PulseClient.ID, "textures/cape/" + name + ".png");
            //?} else {
            /*ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PulseClient.ID, "cape/" + name);
            ResourceLocation path = ResourceLocation.fromNamespaceAndPath(PulseClient.ID, "textures/cape/" + name + ".png");
            *///?}
            patch = new PlayerSkin.Patch(Optional.empty(),
                    Optional.of(new ClientAsset.ResourceTexture(id, path)), Optional.empty(), Optional.empty());
            patchIndex = cape.index;
        }
        return skin.with(patch);
    }
}
