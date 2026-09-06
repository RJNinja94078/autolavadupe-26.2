package net.rjninja.dupe;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LavaDupeSettingsScreen extends Screen {
    private EditBox repeatBox;
    private EditBox dropDelayBox;
    private EditBox reconnectDelayBox;
    private CycleButton<Boolean> onlyShulkerBtn;
    private CycleButton<Boolean> autoReconnectBtn;
    private CycleButton<Boolean> alternateBtn;

    public LavaDupeSettingsScreen() {
        super(Component.literal("AutoLavaDupe Settings"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 30;

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Repeat Count"),
                                button -> {}
                        )
                        .pos(centerX - 130, y)
                        .size(100, 20)
                        .build()
        );

        repeatBox = new EditBox(
                this.font,
                centerX - 20,
                y,
                80,
                20,
                Component.literal("Repeat Count")
        );
        repeatBox.setValue(
                String.valueOf(LavaDupeController.REPEAT_COUNT)
        );
        this.addRenderableWidget(repeatBox);

        y += 30;

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Drop Delay"),
                                button -> {}
                        )
                        .pos(centerX - 130, y)
                        .size(100, 20)
                        .build()
        );

        dropDelayBox = new EditBox(
                this.font,
                centerX - 20,
                y,
                80,
                20,
                Component.literal("Drop Delay")
        );
        dropDelayBox.setValue(
                String.valueOf(LavaDupeController.DROP_DELAY_MS)
        );
        this.addRenderableWidget(dropDelayBox);

        y += 30;

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Reconnect Delay"),
                                button -> {}
                        )
                        .pos(centerX - 130, y)
                        .size(100, 20)
                        .build()
        );

        reconnectDelayBox = new EditBox(
                this.font,
                centerX - 20,
                y,
                80,
                20,
                Component.literal("Reconnect Delay")
        );
        reconnectDelayBox.setValue(
                String.valueOf(
                        LavaDupeController.RECONNECT_DELAY_MS
                )
        );
        this.addRenderableWidget(reconnectDelayBox);

        y += 40;

        onlyShulkerBtn = CycleButton
                .onOffBuilder(LavaDupeController.ONLY_SHULKER)
                .create(
                        centerX - 80,
                        y,
                        160,
                        20,
                        Component.literal("Only Shulker"),
                        (button, value) ->
                                LavaDupeController.ONLY_SHULKER = value
                );
        this.addRenderableWidget(onlyShulkerBtn);

        y += 30;

        autoReconnectBtn = CycleButton
                .onOffBuilder(LavaDupeController.AUTO_RECONNECT)
                .create(
                        centerX - 80,
                        y,
                        160,
                        20,
                        Component.literal("Auto Reconnect"),
                        (button, value) ->
                                LavaDupeController.AUTO_RECONNECT = value
                );
        this.addRenderableWidget(autoReconnectBtn);

        y += 30;

        alternateBtn = CycleButton
                .onOffBuilder(LavaDupeController.ALTERNATE)
                .create(
                        centerX - 80,
                        y,
                        160,
                        20,
                        Component.literal("Alternate"),
                        (button, value) ->
                                LavaDupeController.ALTERNATE = value
                );
        this.addRenderableWidget(alternateBtn);

        y += 50;

        this.addRenderableWidget(
                Button.builder(
                                Component.literal("Save & Done"),
                                button -> {
                                    saveSettings();
                                    this.onClose();
                                }
                        )
                        .pos(centerX - 80, y)
                        .size(160, 20)
                        .build()
        );
    }

    private void saveSettings() {
        try {
            int repeat = Integer.parseInt(
                    repeatBox.getValue().trim()
            );
            if (repeat > 0) {
                LavaDupeController.REPEAT_COUNT = repeat;
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            int drop = Integer.parseInt(
                    dropDelayBox.getValue().trim()
            );
            if (drop >= 0) {
                LavaDupeController.DROP_DELAY_MS = drop;
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            int reconnect = Integer.parseInt(
                    reconnectDelayBox.getValue().trim()
            );
            if (reconnect >= 0) {
                LavaDupeController.RECONNECT_DELAY_MS = reconnect;
            }
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public void onClose() {
        saveSettings();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}