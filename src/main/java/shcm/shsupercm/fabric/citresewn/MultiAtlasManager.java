package shcm.shsupercm.fabric.citresewn;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import shcm.shsupercm.fabric.citresewn.cit.CIT; // Pas aan als nodig

import java.util.List;

public class MultiAtlasManager {
    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new MultiAtlasReloader());
    }

    // Voorbeeld: Haal sprite uit juiste atlas (gebruik in rendering mixin)
    public static Sprite getSprite(Identifier id) {
        for (SpriteAtlasTexture atlas : CITResewn.CIT_ATLASES.values()) {
            Sprite sprite = atlas.getSprite(id);
            if (sprite != null) return sprite;
        }
        return null; // Of fallback naar default
    }

    // Voeg toe: Filter CITs per atlas (gebruik allCITs van CITRegistry of zoek de list)
    public static List<CIT> getCitsForAtlas(Identifier atlasId) {
        // Placeholder: Vervang door echte list, bijv. CITRegistry.allCITs.stream().filter(cit -> cit.assignedAtlas.equals(atlasId)).toList();
        return List.of(); // Pas aan!
    }
}

class MultiAtlasReloader implements SimpleSynchronousResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return Identifier.of("citresewn", "multi_atlas_reloader");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Hier: Assign atlases na parsing (assume CITs al geladen door mod's reloader)
        // Als reload order issue, maak dit reloader afhankelijk (via Fabric's dependencies) of integreer in mod's parser

        for (CIT cit : MultiAtlasManager.getCitsForAtlas(null)) { // Of haal allCITs
            int atlasIndex = Math.abs(cit.hashCode()) % CITResewn.MAX_ATLASES;
            cit.assignedAtlas = CITResewn.CIT_ATLAS_IDS.get(atlasIndex);
            CITResewn.info("Assigned CIT " + cit + " to atlas " + cit.assignedAtlas);
        }

        // Stitch atlases (MC doet dit auto na registration)
        for (Identifier atlasId : CITResewn.CIT_ATLAS_IDS) {
            SpriteAtlasTexture atlas = CITResewn.CIT_ATLASES.get(atlasId);
            // atlas.stitch(manager, ...); // Niet nodig, MC handelt via callbacks
        }

        CITResewn.info("Reloaded multi-atlases for CITs.");
    }
}