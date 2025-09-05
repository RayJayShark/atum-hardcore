package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import me.voidxwalker.autoreset.screen.AutoResetOptionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier BUTTON_IMAGE = Identifier.of("textures/item/golden_boots.png");

    @Unique
    private ButtonWidget resetButton;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        this.resetButton = this.addDrawableChild(ButtonWidget.builder(Text.literal(""), buttonWidget -> {
            assert client != null;
            if (client.isShiftPressed()) {
                        client.setScreen(new AutoResetOptionScreen(this));
                    } else {
                        Atum.scheduleReset();
                    }
                }
        ).dimensions(this.width / 2 - 124, this.height / 4 + 48, 20, 20).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void goldBootsOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BUTTON_IMAGE, this.width / 2 - 124 + 2, this.height / 4 + 48 + 2, 0f, 0f, 16, 16, 16, 16);
        assert client != null;
        if (resetButton.isHovered() && client.isShiftPressed()) {
            context.drawCenteredTextWithShadow(textRenderer, getDifficultyText(), this.width / 2 - 124 + 11, this.height / 4 + 48 - 15, Colors.WHITE);
        }
    }

    @Unique
    Text getDifficultyText() {
        if (Atum.difficulty == -1) {
            return Text.translatable("selectWorld.gameMode.hardcore");
        }
        return Difficulty.byId(Atum.difficulty).getTranslatableName();
    }
}
