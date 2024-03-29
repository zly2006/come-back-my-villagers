package com.github.zly2006.cbmv.fabric.mixin.raid;

import com.github.zly2006.cbmv.fabric.Settings;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Raid.class, priority = 10)
public abstract class MixinRaid {
    @Shadow private int badOmenLevel;

    @Shadow public abstract int getMaxAcceptableBadOmenLevel();

    /**
     * @author zly2006
     * @reason bring back the villagers
     */
    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    public void start(ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (Settings.stackedRaidFarms) {
            if (player.hasStatusEffect(StatusEffects.BAD_OMEN)) {
                this.badOmenLevel += player.getStatusEffect(StatusEffects.BAD_OMEN).getAmplifier() + 1;
                this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxAcceptableBadOmenLevel());
            }

            player.removeStatusEffect(StatusEffects.BAD_OMEN);
            cir.setReturnValue(true);
        }
    }
}
