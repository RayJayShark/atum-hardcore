package me.voidxwalker.autoreset.mixin;

import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(TranslationStorage.class)
public abstract class TranslationStorageMixin {

    @ModifyArg(
            method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resource/language/TranslationStorage;",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;copyOf(Ljava/util/Map;)Ljava/util/Map;")
    )
    private static Map<String, String> addQuitToTitleTranslation2(Map<String, String> map) {
        map.putIfAbsent("menu.autoresetTitle", "Autoreset Options");
        map.putIfAbsent("menu.enterSeed", "Enter a Seed");
        map.putIfAbsent("menu.stop_resets", "Stop Resets & Quit");
        map.putIfAbsent("menu.enterSeed", "Seed (Leave empty for a random Seed)");
        map.putIfAbsent("key.category.atum.keys", "Atum");

        // Added to hardcore version
        map.putIfAbsent("menu.reset", "Reset World");
        map.putIfAbsent("menu.enterWorldName", "World Name (Leave empty for a random name)");
        map.putIfAbsent("menu.deleteWorldHover", "Deletes world on reset");
        map.putIfAbsent("menu.enterWorldName", "Enter a World Name");

        if (map.containsKey("key.atum.reset")) map.putIfAbsent("Create New World", map.get("Create New World"));
        return map;
    }
}
