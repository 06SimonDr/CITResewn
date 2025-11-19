package shcm.shsupercm.fabric.citresewn;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlConst;
import shcm.shsupercm.fabric.citresewn.config.CITResewnConfig;
import shcm.shsupercm.fabric.citresewn.cit.CITRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main initializer for CIT Resewn. Contains various internal utilities(just logging for now).
 */
public class CITResewn implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("CITResewn");
    @Entrypoint(Entrypoint.CLIENT)
    public static final CITResewn INSTANCE = new CITResewn();

    // Nieuw: Support voor multiple atlases
    public static final int MAX_ATLASES = CITResewnConfig.INSTANCE.numAtlases; // Configurabel, default 4
    public static final List<Identifier> CIT_ATLAS_IDS = new ArrayList<>();
    public static final Map<Identifier, SpriteAtlasTexture> CIT_ATLASES = new HashMap<>();
    public static int MAX_TEXTURE_SIZE = 16384; // Default, detect later

    @Override
    public void onInitializeClient() {
        // Detect GPU max texture size
        MAX_TEXTURE_SIZE = GlStateManager._getInteger(GlConst.GL_MAX_TEXTURE_SIZE);
        info("Detected max texture size: " + MAX_TEXTURE_SIZE);

        // Registreer custom CIT atlases
        for (int i = 0; i < MAX_ATLASES; i++) {
            Identifier atlasId = Identifier.of("citresewn", "cit" + i);  // FIX: Gebruik .of() in plaats van new
            CIT_ATLAS_IDS.add(atlasId);
            SpriteAtlasTexture atlas = new SpriteAtlasTexture(atlasId);
            MinecraftClient.getInstance().getTextureManager().registerTexture(atlasId, atlas);
            CIT_ATLASES.put(atlasId, atlas);
            info("Registered CIT atlas: " + atlasId);
        }

        // Registreer CIT met multi-atlas support (veronderstelt aanpassing in CITRegistry)
        CITRegistry.registerAll(); // Pas CITRegistry aan om sprites te groeperen (zie tips)

        if (FabricLoader.getInstance().isModLoaded("fabric-command-api-v2"))
            CITResewnCommand.register();

        // Nieuw: Init multi-atlas manager
        MultiAtlasManager.init();

    }

    /**
     * Logs an info line in CIT Resewn's name.
     * @param message log message
     */
    public static void info(String message) {
        LOG.info("[citresewn] " + message);
    }

    /**
     * Logs a warning line in CIT Resewn's name if enabled in config.
     * @see CITResewnConfig#mute_warns
     * @param message warn message
     */
    public static void logWarnLoading(String message) {
        if (CITResewnConfig.INSTANCE.mute_warns)
            return;
        LOG.warn("[citresewn] " + message);
    }

    /**
     * Logs an error line in CIT Resewn's name if enabled in config.
     * @see CITResewnConfig#mute_errors
     * @param message error message
     */
    public static void logErrorLoading(String message) {
        if (CITResewnConfig.INSTANCE.mute_errors)
            return;
        LOG.error("[citresewn] " + message);
    }
}