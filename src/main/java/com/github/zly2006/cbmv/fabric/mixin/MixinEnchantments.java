package com.github.zly2006.cbmv.fabric.mixin;

import net.minecraft.data.server.tag.vanilla.VanillaEnchantmentTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaEnchantmentTagProvider.class)
public abstract class MixinEnchantments {
    @Inject(method = "configure", at = @At("RETURN"))
    private void configure(RegistryWrapper.WrapperLookup lookup, CallbackInfo ci) {
    }
}
