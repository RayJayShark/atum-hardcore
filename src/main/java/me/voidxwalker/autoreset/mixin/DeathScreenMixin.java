package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    protected DeathScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void moveAutoResetButton(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.reset"), (buttonWidget) -> {
            Atum.isRunning = false;
            Atum.scheduleReset();
        }).dimensions(this.width / 2 - 50, this.height / 4 + 130, 100, 20).build());
    }

    @Inject(method = "quitLevel", at = @At("HEAD"))
    private void stopResettingOnDeathQuit(CallbackInfo ci) {
        Atum.isRunning = false;
    }
}
