package me.voidxwalker.autoreset.mixin.hotkey;

import me.voidxwalker.autoreset.Atum;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.session.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    public abstract void disconnectWithSavingScreen();

    @Shadow
    @Final
    private LevelStorage levelStorage;

    @Unique
    private LevelStorage.Session levelStorageSession;

    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isLoading()Z", shift = At.Shift.AFTER))
    private void resetPreview(LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld, CallbackInfo ci) {
        if (Atum.isResetScheduled() && FabricLoader.getInstance().isModLoaded("worldpreview")) {
            this.clickButton(this.currentScreen, "menu.returnToMenu");
        }

        levelStorageSession = session;
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;render(Z)V", shift = At.Shift.AFTER))
    private void executeReset(CallbackInfo ci) {
        while (Atum.shouldReset()) {
            var shouldDeleteWorld = Atum.deleteWorld && this.isInSingleplayer();

            if (this.world != null) {
                Screen gameMenuScreen = new GameMenuScreen(true);
                gameMenuScreen.init((MinecraftClient) (Object) this, 0, 0);
                if (!this.clickButton(gameMenuScreen, "fast_reset.menu.quitWorld", "menu.quitWorld", "menu.returnToMenu", "menu.disconnect") || this.world != null) {
                    if (this.world != null) {
                        this.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
                        this.disconnectWithSavingScreen();
                    }
                }
            }

            if (shouldDeleteWorld) {
                var levelName = levelStorageSession.getDirectoryName();
                Atum.log(Level.INFO, "Deleting level: " + levelName);

                try (LevelStorage.Session session = this.levelStorage.createSessionWithoutSymlinkCheck(levelName)) {
                    session.deleteSessionLock();
                } catch (IOException iOException) {
                    Atum.log(Level.ERROR, "Failed to delete world: " + levelName);
                }
            }

            Atum.createNewWorld();
        }
    }

    @Shadow
    public abstract boolean isInSingleplayer();

    @Unique
    private boolean clickButton(Screen screen, String... translationKeys) {
        for (String translationKey : translationKeys) {
            for (Element element : screen.children()) {
                if (!(element instanceof ButtonWidget button)) {
                    continue;
                }
                if (I18n.translate(translationKey).equals(button.getMessage().getString())) {
                    button.onPress(null);
                    return true;
                }
            }
        }
        return false;
    }
}
