package com.github.zly2006.cbmv.fabric.mixin.datapack;

import com.github.zly2006.cbmv.fabric.ComeBackMyVillagers;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaResourcePackProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(VanillaResourcePackProvider.class)
public abstract class MixinVanillaResourcePackProvider {
    @Shadow protected abstract void forEachProfile(Consumer<ResourcePackProfile> consumer);

    @Shadow protected abstract void forEachProfile(@Nullable Path namespacedPath, BiConsumer<String, Function<String, ResourcePackProfile>> consumer);

    @Shadow @Final private ResourceType type;

    @Inject(
            method = "forEachProfile(Ljava/util/function/BiConsumer;)V",
            at = @At("RETURN")
    )
    private void andMe(BiConsumer<String, Function<String, ResourcePackProfile>> consumer, CallbackInfo ci) throws URISyntaxException {
        if (type == ResourceType.SERVER_DATA) {
            Path path = Path.of(ComeBackMyVillagers.class.getClassLoader().getResource("data/cbmv/datapacks").toURI());
            this.forEachProfile(path, consumer);
        }
    }
}
