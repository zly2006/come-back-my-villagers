package com.github.zly2006.cbmv.fabric.mixin.command;

import com.github.zly2006.cbmv.fabric.ComeBackMyVillagers;
import com.github.zly2006.cbmv.fabric.Settings;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import static net.minecraft.server.command.CommandManager.literal;

@Mixin(CommandManager.class)
public abstract class MixinCM {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/command/TitleCommand;register(Lcom/mojang/brigadier/CommandDispatcher;Lnet/minecraft/command/CommandRegistryAccess;)V"
            )
    )
    private void register(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci) throws FileNotFoundException {
        dispatcher.register(literal("come").then(literal("back")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("cure").executes(toggle("villagerOldCure")))
                .then(literal("witchRaiderDrop").executes(toggle("oldWitchDropIfRaider")))
                .then(literal("raid").executes(toggle("oldRaid")))
                .executes(context -> {
                    context.getSource().sendMessage(Text.literal(ComeBackMyVillagers.settings.toString()));
                    return 1;
                })));
    }

    @Unique
    private Command<ServerCommandSource> toggle(String name) {
        return context -> {
            try {
                Field field = Settings.class.getField(name);
                boolean value = !((Boolean) field.get(ComeBackMyVillagers.settings));
                field.set(ComeBackMyVillagers.settings, value);
                ComeBackMyVillagers.saveSettings();
                context.getSource().sendFeedback(() -> Text.literal(name + " is set to: " + value), true);
                return 1;
            } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
                e.printStackTrace();
                throw new SimpleCommandExceptionType(Text.literal(e.getMessage())).create();
            }
        };
    }
}
