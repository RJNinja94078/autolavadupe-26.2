# AutoLavaDupe

A Fabric client-side mod that automates the lava dupe glitch in Minecraft.
**Made for 8b8t – for the Imperials.**

## Features
- Automatically drops items into lava and reconnects to duplicate them.
- Configurable **Repeat Count**, **Drop Delay**, and **Reconnect Delay**.
- Toggle options: `Only Shulker`, `Auto Reconnect`, `Alternate` mode.
- In‑game settings GUI (keybind: `G` by default).
- Keybind to start/stop the dupe (default unset – set in Controls).
- Commands: `/lavadupe start`, `/lavadupe stop`, `/lavadupe gui`.

## Installation
1. Install [Fabric Loader](https://fabricmc.net/use/) for your Minecraft version.
2. Place the mod `.jar` file into your `mods` folder.
3. Launch the game with the Fabric profile.

## Usage
- **Open Settings**: Press `G` (or your custom key) to open the settings screen.
- **Toggle Dupe**: Use the keybind (set in Controls → AutoLavaDupe) or run `/lavadupe start`/`stop`.
- **Settings explained**:
  - `Repeat Count` – number of dupe cycles to perform.
  - `Drop Delay` – milliseconds between dropping each item.
  - `Reconnect Delay` – milliseconds to wait before rejoining after disconnect.
  - `Only Shulker` – only duplicate shulker boxes.
  - `Auto Reconnect` – automatically reconnect after being kicked.
  - `Alternate` – multiple shulkers.

Settings are saved automatically when you close the GUI.

## Requirements
- Minecraft 26.2 (Fabric)
- [Fabric API] (https://modrinth.com/mod/fabric-api/version/0.159.0+26.2)