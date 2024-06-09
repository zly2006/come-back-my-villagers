package com.github.zly2006.cbmv.fabric.mixin.raid;

import com.github.zly2006.cbmv.fabric.ComeBackMyVillagers;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Raid.class)
public class MixinRaid {
    @Redirect(
            method = "start",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Lnet/minecraft/entity/effect/StatusEffectInstance;"
            )
    )
    private StatusEffectInstance onStart(ServerPlayerEntity instance, RegistryEntry<StatusEffect> registryEntry) {
        if (ComeBackMyVillagers.settings.oldRaid) {
            return instance.getStatusEffect(StatusEffects.BAD_OMEN);
        } else {
            return instance.getStatusEffect(registryEntry);
        }
    }
}
