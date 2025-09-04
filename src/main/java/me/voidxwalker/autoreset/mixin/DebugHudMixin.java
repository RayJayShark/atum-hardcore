package me.voidxwalker.autoreset.mixin;

import me.voidxwalker.autoreset.Atum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
    // We could add an actual debug category, but that makes the section disableable, and involves a bunch more work to prevent that.
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V", ordinal = 1))
    private List<String> addDebugInfo(List<String> lines) {
        if (!(Atum.isRunning && MinecraftClient.getInstance().debugHudEntryList.isF3Enabled())) return lines;

        if (!lines.isEmpty() && !lines.getLast().isEmpty()) lines.add("");
        lines.add("Resetting " + (Atum.seed == null || Atum.seed.isEmpty() ? "a random seed" : "the seed: \"" + Atum.seed + "\"") + ", " + (Atum.difficulty != -1 ? "" + Difficulty.byId(Atum.difficulty).getName().charAt(0) : "hc"));
        if (Atum.generatorType != 0) {
            String s = Atum.getGeneratorTypeString(Atum.generatorType);
            if (s != null) {
                lines.add(Text.literal("GenType: ").append(s).getString());
            }
            if (Atum.deleteWorld) {
                returnValue.add("DeleteWorld");
            }
        }
        if (!Atum.structures) {
            lines.add("NoStructures");
        }
        if (Atum.bonusChest) {
            lines.add("BonusChest");
        }
        return lines;
    }
}
