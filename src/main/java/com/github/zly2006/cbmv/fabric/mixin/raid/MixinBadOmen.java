package com.github.zly2006.cbmv.fabric.mixin.raid;

import com.github.zly2006.cbmv.fabric.Settings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.entity.effect.BadOmenStatusEffect")
public class MixinBadOmen {
    @Inject(
            method = "applyUpdateEffect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getServerWorld()Lnet/minecraft/server/world/ServerWorld;"
            ),
            cancellable = true
    )
    private void triggerRaid(LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir, @Local ServerPlayerEntity player) {
        if (Settings.stackedRaidFarms) {
            BlockPos blockPos = player.getBlockPos();
            ServerWorld world = player.getServerWorld();
            if (world.getDifficulty() != Difficulty.PEACEFUL && world.isNearOccupiedPointOfInterest(blockPos)) {
                world.getRaidManager().startRaid(player, blockPos);
            }

            cir.setReturnValue(true);
        }
    }
}
