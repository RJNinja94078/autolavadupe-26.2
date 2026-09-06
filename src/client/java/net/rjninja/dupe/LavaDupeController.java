package net.rjninja.dupe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.ArrayList;
import java.util.List;

public class LavaDupeController {

    public static volatile int REPEAT_COUNT = 100;
    public static volatile int DROP_DELAY_MS = 500;
    public static volatile int RECONNECT_DELAY_MS = 1500;
    public static volatile boolean ONLY_SHULKER = false;
    public static volatile boolean AUTO_RECONNECT = true;
    public static volatile boolean ALTERNATE = false;

    private volatile boolean isRunning = false;
    private volatile boolean stopRequested = false;
    private volatile boolean reconnecting = false;

    private Thread dupeThread;

    private ServerData serverData = null;
    private String serverIp = null;

    private int alternateIndex = 0;

    public synchronized void start() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            return;
        }

        if (isRunning) {
            return;
        }

        serverData = mc.getCurrentServer();

        if (serverData != null) {
            serverIp = serverData.ip;
        } else {
            System.out.println("[LavaDupe] Not on a server, cannot start.");
            return;
        }

        alternateIndex = 0;

        isRunning = true;
        stopRequested = false;
        reconnecting = false;

        dupeThread = new Thread(
                this::dupeLoop,
                "LavaDupe-Loop"
        );

        dupeThread.setDaemon(true);
        dupeThread.start();
    }

    public synchronized void stop() {
        stopRequested = true;
        isRunning = false;

        if (dupeThread != null) {
            dupeThread.interrupt();
            dupeThread = null;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void dupeLoop() {

        int remaining = REPEAT_COUNT;

        while (remaining > 0 && !stopRequested && isRunning) {

            Minecraft mc = Minecraft.getInstance();

            if (!isFullyConnected(mc)) {

                if (AUTO_RECONNECT && serverIp != null && !reconnecting) {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                        break;
                    }

                    reconnecting = true;

                    reconnectToServer(mc);

                    boolean connected = waitForConnection(
                            mc,
                            15000
                    );

                    reconnecting = false;

                    if (!connected) {
                        break;
                    }

                    try {
                        Thread.sleep(RECONNECT_DELAY_MS);
                    } catch (InterruptedException ignored) {
                        break;
                    }
                } else {
                    break;
                }
            }

            if (!stopRequested && isFullyConnected(mc)) {
                executeDupe(mc);
            }

            remaining--;

            if (remaining > 0 && !stopRequested) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        }

        isRunning = false;
        reconnecting = false;

        Minecraft.getInstance().execute(() -> {
        });
    }

    private boolean isFullyConnected(Minecraft mc) {

        if (mc.player == null ||
                mc.getConnection() == null ||
                mc.level == null) {
            return false;
        }

        if (mc.player.tickCount < 50) {
            return false;
        }

        return mc.player.getHealth() > 0;
    }

    private void executeDupe(Minecraft mc) {

        if (mc.player == null ||
                mc.getConnection() == null) {
            return;
        }

        if (ALTERNATE) {

            List<Integer> shulkerSlots =
                    findShulkerSlots(mc);

            if (shulkerSlots.isEmpty()) {
                System.out.println(
                        "[LavaDupe] No shulkers found."
                );
                return;
            }

            if (alternateIndex >= shulkerSlots.size()) {
                alternateIndex = 0;
            }

            int targetSlot =
                    shulkerSlots.get(alternateIndex);

            alternateIndex++;

            int containerSlot =
                    inventorySlotToContainerSlot(targetSlot);

            if (containerSlot == -1) {
                return;
            }

            mc.execute(() -> {

                if (mc.player == null ||
                        mc.gameMode == null) {
                    return;
                }

                mc.gameMode.handleContainerInput(
                        mc.player.containerMenu.containerId,
                        containerSlot,
                        0,
                        ContainerInput.THROW,
                        mc.player
                );
            });

        } else {

            ItemStack held =
                    mc.player.getMainHandItem();

            if (ONLY_SHULKER &&
                    !isShulker(held)) {
                return;
            }

            mc.execute(() -> {

                if (mc.player != null) {
                    mc.player.drop(false);
                }
            });
        }

        try {
            Thread.sleep(DROP_DELAY_MS);
        } catch (InterruptedException ignored) {
            return;
        }

        if (mc.getConnection() != null &&
                mc.getConnection().getConnection() != null) {

            mc.getConnection()
                    .getConnection()
                    .disconnect(
                            Component.literal(
                                    "AutoLavaDupe executed!"
                            )
                    );
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
    }

    private List<Integer> findShulkerSlots(Minecraft mc) {

        List<Integer> slots = new ArrayList<>();

        if (mc.player == null) {
            return slots;
        }

        for (int i = 0; i < 36; i++) {

            ItemStack stack =
                    mc.player.getInventory().getItem(i);

            if (isShulker(stack)) {
                slots.add(i);
            }
        }

        if (isShulker(mc.player.getOffhandItem())) {
            slots.add(40);
        }

        return slots;
    }

    private int inventorySlotToContainerSlot(
            int inventorySlot) {

        if (inventorySlot >= 0 &&
                inventorySlot <= 8) {
            return 36 + inventorySlot;
        }

        if (inventorySlot >= 9 &&
                inventorySlot <= 35) {
            return inventorySlot;
        }

        if (inventorySlot >= 36 &&
                inventorySlot <= 39) {
            return 5 + (inventorySlot - 36);
        }

        if (inventorySlot == 40) {
            return 45;
        }

        return -1;
    }

    private void reconnectToServer(Minecraft mc) {

        if (serverIp == null ||
                serverData == null) {
            return;
        }

        try {

            ServerAddress address =
                    ServerAddress.parseString(serverIp);

            mc.execute(() ->
                    ConnectScreen.startConnecting(
                            mc.gui.screen(),
                            mc,
                            address,
                            serverData,
                            false,
                            null
                    )
            );

        } catch (Exception e) {

            System.err.println(
                    "[LavaDupe] Reconnect failed: "
                            + e.getMessage()
            );
        }
    }

    private boolean waitForConnection(
            Minecraft mc,
            long timeoutMs) {

        long start =
                System.currentTimeMillis();

        while (
                System.currentTimeMillis() - start < timeoutMs
                        && !stopRequested
        ) {

            if (isFullyConnected(mc)) {
                return true;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
                return false;
            }
        }

        return false;
    }

    private boolean isShulker(ItemStack stack) {

        return !stack.isEmpty()
                && stack.getItem() instanceof BlockItem
                && ((BlockItem) stack.getItem()).getBlock()
                instanceof ShulkerBoxBlock;
    }
}