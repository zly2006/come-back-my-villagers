package com.github.zly2006.cbmv.fabric.mixin.raid;

import com.github.zly2006.cbmv.fabric.ComeBackMyVillagers;
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
            at = @At("HEAD"),
            cancellable = true
    )
    private void apply(LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        if (ComeBackMyVillagers.settings.oldRaid && entity instanceof ServerPlayerEntity player && !player.isSpectator()) {
            BlockPos blockPos = player.getBlockPos();
            ServerWorld world = player.getServerWorld();
            if (world.getDifficulty() != Difficulty.PEACEFUL && world.isNearOccupiedPointOfInterest(blockPos)) {
                cir.setReturnValue(world.getRaidManager().startRaid(player, blockPos) == null);
            } else {
                cir.setReturnValue(true);
            }
        }
    }
}
