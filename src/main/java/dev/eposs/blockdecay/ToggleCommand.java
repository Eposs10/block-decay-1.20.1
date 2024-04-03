package dev.eposs.blockdecay;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class ToggleCommand {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
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
            );
        });
    }

    private static int toggle(@NotNull CommandContext<ServerCommandSource> context) {
        BlockDecay.isActive = !BlockDecay.isActive;
        context.getSource().sendFeedback(() -> Text.literal("Toggeled block-decay to: " + BlockDecay.isActive), true);
        return 1;
    }

    private static int add(@NotNull CommandContext<ServerCommandSource> context) {
        BlockPos position = BlockPosArgumentType.getBlockPos(context, "position");
        int radius = IntegerArgumentType.getInteger(context, "radius");
        return 0;
    }

    private static int remove(@NotNull CommandContext<ServerCommandSource> context) {
        BlockPos position = BlockPosArgumentType.getBlockPos(context, "position");
        return 0;
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        return 0;
    }
}
