# YETIsUtils


## Features

- **Admin Mode Inventory Switch**: Toggle between your regular player inventory and a separate admin inventory.
- **Player Warnings**: Issue warnings to players, store and view warnings, and clear specific warnings.
- **Player Reports**: Players can report issues, which are stored and viewable by admins in a GUI.
- **Configuration Reload**: Reload the plugin's configuration without restarting the server.
- **Auto IP Ban**: Automatically ban a player's IP after a configurable number of warnings.
- **Permissions**: Configurable permissions for commands to ensure only authorized users can perform certain actions.

## Commands

- `/adminmode` - Toggle between player inventory and admin inventory.
- `/warn <player> <reason>` - Issue a warning to a player.
- `/warnings <player>` - View the number of warnings a player has.
- `/warnmenu` - Open a GUI to view all player warnings.
- `/warnclear <player> <warning number>` - Clear a specific warning from a player.
- `/mywarnings` - View your own warnings (if enabled in config).
- `/report <issue>` - Report an issue to the admins.
- `/reportmenu` - Open a GUI to view all player reports.
- `/yetisutils reload` - Reload the plugin's configuration.

## Permissions

- `yetisutils.adminmode` - Permission to toggle admin mode.
- `yetisutils.warn` - Permission to issue warnings.
- `yetisutils.warnings` - Permission to view player warnings.
- `yetisutils.warnmenu` - Permission to open the warnings GUI.
- `yetisutils.warnclear` - Permission to clear player warnings.
- `yetisutils.mywarnings` - Permission for players to view their own warnings.
- `yetisutils.report` - Permission to report issues.
- `yetisutils.reportmenu` - Permission to open the reports GUI.
- `yetisutils.reload` - Permission to reload the plugin configuration.

## Configuration

The plugin's configuration file (`config.yml`) includes the following options:

```yaml
# Configuration file for YETIsUtils

# Enable or disable players being able to view their own warnings
enablePlayerWarningsView: true

# Set the number of warnings before automatically banning a player's IP (0 for unlimited warnings)
autoIPBanThreshold: 3

# Enable or disable debug messages in the console
debug: false
