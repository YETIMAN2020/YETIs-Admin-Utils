# YETIsUtils


## Features
- **Admin Mode**: Toggle between player and admin inventories.
- **Player Warnings**: Issue warnings to players, view the number of warnings a player has, and clear specific warnings.
- **Warning GUI**: An in-game GUI for viewing player warnings.
- **Inventory Management**: Save and load player and admin inventories to and from files.
- **Player Warning Display**: Allows players to see their own warnings.

## Commands
- `/adminmode`: Toggles between player and admin inventories.
- `/warn <player> <reason>`: Issues a warning to a player with a specified reason.
- `/warnclear <player> <warning number>`: Clears a specific warning for a player.
- `/warnings <player>`: Displays the number of warnings a player has.
- `/warnmenu`: Opens the warning management GUI.
- `/mywarnings`: Allows players to see their own warnings.

## Permissions
- `yetisutils.adminmode`: Permission to use the `/adminmode` command.
- `yetisutils.warn`: Permission to issue warnings using the `/warn` command.
- `yetisutils.warnclear`: Permission to clear warnings using the `/warnclear` command.
- `yetisutils.warnings`: Permission to view the number of warnings a player has using the `/warnings` command.
- `yetisutils.warnmenu`: Permission to open the warning management GUI using the `/warnmenu` command.
- `yetisutils.mywarnings`: Permission for players to see their own warnings using the `/mywarnings` command.

## Configuration
- The plugin creates a `warnings.yml` file in the plugin's data folder to store warnings.
- Date and time format for warnings is `dd-MM-yy HH:mm`.

## Installation
1. Download the latest version of the YETIsUtils plugin.
2. Place the downloaded JAR file in your server's `plugins` directory.
3. Restart the server to generate the default configuration files.
4. Configure the plugin as needed by editing the `config.yml` and `warnings.yml` files in the `plugins/YETIsUtils` directory.

## Changelog
### Version 2.2
- Added the ability for players to see their own warnings with the `/mywarnings` command.
- Updated the date and time format for warnings to `dd-MM-yy HH:mm`.
- Fixed issues with warning retrieval and display.
- Improved compatibility with Minecraft 1.20.1.

### Version 2.1
- Added the ability to clear specific warnings with the `/warnclear` command.
- Improved the warning management GUI.

### Version 2.0
- Introduced the warning management GUI.
- Added the ability to issue warnings to offline players.
- Improved the inventory management system to persist inventories to files.

### Version 1.0
- Initial release with basic admin mode and warning functionalities.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contributing
Contributions are welcome! Please open an issue or submit a pull request on GitHub.

## Support
For support, please open an issue on GitHub or contact the plugin author.

## Acknowledgements
- Thanks to the Minecraft community for their continuous support and feedback.
