package com.github.zly2006.cbmv.fabric.mixin.raid;

import com.github.zly2006.cbmv.fabric.Settings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RaiderEntity.class)
public abstract class MixInRaider extends PatrolEntity {
    @Shadow public abstract @Nullable Raid getRaid();

    protected MixInRaider(EntityType<? extends PatrolEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/PatrolEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"
            )
    )
    private void giveEffect(DamageSource damageSource, CallbackInfo ci) {
        if (Settings.oldRaid && getWorld() instanceof ServerWorld serverWorld) {
            if (this.isPatrolLeader()
                    && this.getRaid() == null
                    && serverWorld.getRaidAt(this.getBlockPos()) == null) {
                ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                PlayerEntity playerEntity = null;
                if (damageSource.getAttacker() instanceof PlayerEntity) {
                    playerEntity = (PlayerEntity) damageSource.getAttacker();
                } else if (damageSource.getAttacker() instanceof WolfEntity wolfEntity) {
                    LivingEntity livingEntity = wolfEntity.getOwner();
                    if (wolfEntity.isTamed() && livingEntity instanceof PlayerEntity) {
                        playerEntity = (PlayerEntity) livingEntity;
                    }
                }

                if (!itemStack.isEmpty()
                        && ItemStack.areEqual(itemStack, Raid.getOminousBanner(this.getRegistryManager().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN)))
                        && playerEntity != null) {
                    StatusEffectInstance statusEffectInstance = playerEntity.getStatusEffect(StatusEffects.BAD_OMEN);
                    int i = 1;
                    if (statusEffectInstance != null) {
                        i += statusEffectInstance.getAmplifier();
                        playerEntity.removeStatusEffectInternal(StatusEffects.BAD_OMEN);
                    } else {
                        --i;
                    }

                    i = MathHelper.clamp(i, 0, 4);
                    StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, i, false, false, true);
                    if (!this.getWorld().getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
                        playerEntity.addStatusEffect(statusEffectInstance2);
                    }
                }
            }
        }
    }
}
