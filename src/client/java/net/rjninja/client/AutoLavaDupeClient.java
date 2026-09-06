package net.rjninja.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.rjninja.dupe.LavaDupeController;
import net.rjninja.dupe.LavaDupeSettingsScreen;
import org.lwjgl.glfw.GLFW;

public class AutoLavaDupeClient implements ClientModInitializer {
	private static final LavaDupeController dupeController =
			new LavaDupeController();

	private static final KeyMapping.Category AUTO_LAVA_DUPE_CATEGORY =
			KeyMapping.Category.register(
					Identifier.fromNamespaceAndPath(
							"autolavadupe",
							"main"
					)
			);

	private static final KeyMapping toggleKey =
			KeyMappingHelper.registerKeyMapping(
					new KeyMapping(
							"key.autolavadupe.toggle",
							InputConstants.Type.KEYSYM,
							GLFW.GLFW_KEY_UNKNOWN,
							AUTO_LAVA_DUPE_CATEGORY
					)
			);

	private static final KeyMapping guiKey =
			KeyMappingHelper.registerKeyMapping(
					new KeyMapping(
							"key.autolavadupe.open_gui",
							InputConstants.Type.KEYSYM,
							GLFW.GLFW_KEY_G,
							AUTO_LAVA_DUPE_CATEGORY
					)
			);

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				toggleDupe();
			}
			while (guiKey.consumeClick()) {
				openSettingsGui();
			}
		});

		ClientCommandRegistrationCallback.EVENT.register(
				(dispatcher, buildContext) ->
						dispatcher.register(
								ClientCommands.literal("lavadupe")
										.then(
												ClientCommands.literal("start")
														.executes(ctx -> {
															if (!dupeController.isRunning()) {
																dupeController.start();
																Minecraft minecraft =
																		Minecraft.getInstance();
																if (minecraft.player != null) {
																	minecraft.player.sendSystemMessage(
																			Component.literal(
																					"§aAutoLavaDupe started."
																			)
																	);
																}
															}
															return 1;
														})
										)
										.then(
												ClientCommands.literal("stop")
														.executes(ctx -> {
															if (dupeController.isRunning()) {
																dupeController.stop();
																Minecraft minecraft =
																		Minecraft.getInstance();
																if (minecraft.player != null) {
																	minecraft.player.sendSystemMessage(
																			Component.literal(
																					"§cAutoLavaDupe stopped."
																			)
																	);
																}
															}
															return 1;
														})
										)
										.then(
												ClientCommands.literal("gui")
														.executes(ctx -> {
															openSettingsGui();
															return 1;
														})
										)
						)
		);
	}

	private void toggleDupe() {
		Minecraft minecraft = Minecraft.getInstance();
		if (dupeController.isRunning()) {
			dupeController.stop();
			if (minecraft.player != null) {
				minecraft.player.sendSystemMessage(
						Component.literal(
								"§cAutoLavaDupe stopped."
						)
				);
			}
		} else {
			dupeController.start();
			if (minecraft.player != null) {
				minecraft.player.sendSystemMessage(
						Component.literal(
								"§aAutoLavaDupe started."
						)
				);
			}
		}
	}

	private void openSettingsGui() {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.execute(() -> {
			minecraft.setScreenAndShow(
					new LavaDupeSettingsScreen()
			);
		});
	}
}