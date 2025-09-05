package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    @Unique
    private ButtonWidget stopResetting;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;initWidgets()V"))
    public void addAutoResetButton(CallbackInfo ci) {
        stopResetting = this.addDrawableChild(ButtonWidget.builder(Atum.getTranslation("menu.stop_resets", "Reset World"), (buttonWidget) -> {
            Atum.isRunning = false;
            Atum.scheduleReset();
        }).size(100, 20).build());
    }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void moveAutoResetButton(CallbackInfo ci) {
        if (stopResetting != null) {
            stopResetting.setPosition(0, this.height - 20);
        }
    }
}
