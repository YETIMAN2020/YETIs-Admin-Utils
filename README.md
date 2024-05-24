# YETIsUtils

## Features

- **Admin Mode**: Toggle between player and admin inventories using `/adminmode`.
- **Player Warnings**: Issue warnings to players with a specified reason using `/warn <player> <reason>`.
- **Warning Logs**: Check the number of warnings a player has received using `/warnings <player>`.
- **Warning GUI**: View and interact with player warnings and detailed warning logs via a GUI using `/warninggui`.

## Installation

1. Download the latest release of YETIsUtils.
2. Place the downloaded JAR file into your server's `plugins` directory.
3. Restart your server to generate the configuration files and activate the plugin.

## Commands

- `/adminmode`
  - **Description**: Toggle between player and admin inventories.
  - **Permission**: `yetisutils.adminmode`
- `/warn <player> <reason>`
  - **Description**: Issue a warning to a player with a specified reason.
  - **Permission**: `yetisutils.warn`
- `/warnings <player>`
  - **Description**: Check the number of warnings a player has received.
  - **Permission**: `yetisutils.warnings`
- `/warninggui`
  - **Description**: Open the warnings GUI to view and interact with player warnings.
  - **Permission**: `yetisutils.warninggui`

## Permissions

- `yetisutils.adminmode`
  - **Description**: Allows the player to toggle between player and admin inventories.
  - **Default**: OP
- `yetisutils.warn`
  - **Description**: Allows the player to issue warnings to others.
  - **Default**: OP
- `yetisutils.warnings`
  - **Description**: Allows the player to check the number of warnings another player has received.
  - **Default**: OP
- `yetisutils.warninggui`
  - **Description**: Allows the player to open the warnings GUI.
  - **Default**: OP

## Usage

### Toggling Admin Mode

Use the `/adminmode` command to switch between your player inventory and a separate admin inventory. This allows admins to manage items without cluttering their personal inventory.

### Issuing Warnings

To issue a warning to a player, use the `/warn <player> <reason>` command. This will log the warning along with the specified reason and timestamp.

### Viewing Warnings

Use the `/warnings <player>` command to check how many warnings a player has received.

### Warning GUI

Open the warning GUI with the `/warninggui` command. In this GUI:
- Click on a player's head to view detailed warning logs.
- Each warning log includes the warning number, reason, and timestamp.
- A "Back" button in the bottom left corner of the reasons GUI allows you to return to the player warnings view.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request with your improvements and bug fixes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For support or inquiries, please open an issue on the GitHub repository.
