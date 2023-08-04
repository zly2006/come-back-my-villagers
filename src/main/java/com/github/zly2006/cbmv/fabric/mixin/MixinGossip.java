package com.github.zly2006.cbmv.fabric.mixin;

import com.github.zly2006.cbmv.fabric.Settings;
import net.minecraft.village.VillageGossipType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillageGossipType.class)
public class MixinGossip {
    @Mutable
    @Shadow @Final public int maxValue;

    @Mutable
    @Shadow @Final public int shareDecrement;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(String string, int i, String key, int multiplier, int maxReputation, int decay, int shareDecrement, CallbackInfo ci) {
        if (Settings.villagerOldCure) {
            if (key.equals("major_positive")) {
                this.maxValue = 100;
                this.shareDecrement = 100;
            }
            if (key.equals("minor_positive")) {
                this.maxValue = 200;
            }
        }
    }
}
