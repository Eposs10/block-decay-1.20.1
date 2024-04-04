package dev.eposs.blockdecay;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ToggleCommand {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(CommandManager.literal("blockdecay")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.literal("toggle")
                                .executes(ToggleCommand::toggle))
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                        .then(CommandManager.argument("radius", IntegerArgumentType.integer(1))
                                                .executes(ToggleCommand::add))))
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                        .executes(ToggleCommand::remove)))
                        .then(CommandManager.literal("list")
                                .executes(ToggleCommand::list))
                ));
    }

    private static int toggle(@NotNull CommandContext<ServerCommandSource> context) {
        BlockDecay.isActive = !BlockDecay.isActive;
        context.getSource().sendFeedback(() -> getText("Toggeled block-decay to: " + BlockDecay.isActive), true);
        return 1;
    }

    private static int add(@NotNull CommandContext<ServerCommandSource> context) {
        BlockPos position = BlockPosArgumentType.getBlockPos(context, "position");
        int radius = IntegerArgumentType.getInteger(context, "radius");

        if (BlockDecay.blockDecayPositionMap.containsKey(position)) {
            context.getSource().sendFeedback(() -> getText(
                    position.toShortString() + " is already on decay list with radius " + BlockDecay.blockDecayPositionMap.get(position)
            ), true);
        } else {
            BlockDecay.blockDecayPositionMap.put(position, radius);
            context.getSource().sendFeedback(() -> getText(
                    "Added " + position.toShortString() + " to decay list with radius " + radius
            ), true);
        }
        return 0;
    }

    private static int remove(@NotNull CommandContext<ServerCommandSource> context) {
        BlockPos position = BlockPosArgumentType.getBlockPos(context, "position");

        if (BlockDecay.blockDecayPositionMap.containsKey(position)) {
            Integer remove = BlockDecay.blockDecayPositionMap.remove(position);
            context.getSource().sendFeedback(() -> getText(
                    "Removed " + position.toShortString() + " from decay list with radius " + remove
            ), true);
        } else {
            context.getSource().sendFeedback(() -> getText(
                    position.toShortString() + " is not on decay list"
            ), true);
        }
        return 0;
    }

    private static int list(@NotNull CommandContext<ServerCommandSource> context) {
        StringBuilder stringBuilder = new StringBuilder();

        BlockDecay.blockDecayPositionMap.forEach((position, radius) ->
                stringBuilder.append("\n").append(position.toShortString())
                .append(" - radius: ").append(radius)
        );

        if (stringBuilder.isEmpty()) stringBuilder.append("empty");

        context.getSource().sendFeedback(() -> getText("Decay List:" + stringBuilder), true);
        return 0;
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull Text getText(String message) {
        return Text.literal("[" + BlockDecay.MOD_ID + "] " + message);
    }
}
