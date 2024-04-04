package dev.eposs.blockdecay;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class BlockDecay implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "block-decay";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isActive = false;
	public static final Map<BlockPos, Integer> blockDecayPositionMap = new ConcurrentHashMap<>();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		UseBlockCallback.EVENT.register(BlockDecay::doBlockDecay);
		ToggleCommand.init();
	}

	private static ActionResult doBlockDecay(@NotNull PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
		if (!isActive) return ActionResult.PASS;
		if (playerEntity.isSpectator()) return ActionResult.PASS;
		if (world.isClient()) return ActionResult.PASS;
		if (playerEntity.hasPermissionLevel(4)) return ActionResult.PASS;

		new Timer(MOD_ID + "-timer").schedule(new TimerTask() {
					@Override
					public void run() {
						LOGGER.warn("decay");

						BlockPos blockPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide(), 1);

                        for (Map.Entry<BlockPos, Integer> entry : blockDecayPositionMap.entrySet()) {
                            BlockPos position = entry.getKey();
                            Integer radius = entry.getValue();
                            if (position.isWithinDistance(blockPos, radius)) {
                                world.removeBlock(blockPos, false);
								return;
                            }
                        }
                    }
				}
				, Duration.of(10, ChronoUnit.SECONDS).toMillis());

		return ActionResult.PASS;
	}
}