package com.github.zly2006.cbmv.fabric.mixin.raid;

import net.minecraft.data.server.loottable.vanilla.VanillaEntityLootTableGenerator;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(VanillaEntityLootTableGenerator.class)
public class MixinRaiderLoot {
    @Redirect(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/loot/LootPool$Builder;with(Lnet/minecraft/loot/entry/LootPoolEntry$Builder;)Lnet/minecraft/loot/LootPool$Builder;"
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/entity/EntityType;PILLAGER:Lnet/minecraft/entity/EntityType;"
                    ),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/entity/EntityType;PLAYER:Lnet/minecraft/entity/EntityType;"
                    )
            ),
            require = 1,
            allow = 1
    )
    private LootPool.Builder modify(LootPool.Builder instance, LootPoolEntry.Builder<?> entry) {
        return instance;
    }
}
