package com.github.zly2006.cbmv.fabric.mixin.raid;

import com.github.zly2006.cbmv.fabric.Settings;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RaiderEntity.class)
public class MixinRaider extends PatrolEntity {
    protected MixinRaider(EntityType<? extends PatrolEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/raid/RaiderEntity;isPatrolLeader()Z",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void giveOmen(DamageSource damageSource, CallbackInfo ci) {
        if (!Settings.stackedRaidFarms) return;
        Entity entity = damageSource.getAttacker();
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
        PlayerEntity playerEntity = null;
        if (entity instanceof PlayerEntity) {
            playerEntity = (PlayerEntity)entity;
        } else if (entity instanceof WolfEntity wolfEntity) {
            LivingEntity livingEntity = wolfEntity.getOwner();
            if (wolfEntity.isTamed() && livingEntity instanceof PlayerEntity) {
                playerEntity = (PlayerEntity)livingEntity;
            }
        }

        if (!itemStack.isEmpty() && ItemStack.areEqual(itemStack, Raid.getOminousBanner(this.getRegistryManager().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN))) && playerEntity != null) {
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

        super.onDeath(damageSource);
        ci.cancel();
    }
}
