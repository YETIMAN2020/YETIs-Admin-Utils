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
- `/mywarns` - View your own warnings (if enabled in config).
- `/report <issue>` - Report an issue to the admins.
- `/reportmenu` - Open a GUI to view all player reports.
- `/yetisutils reload` - Reload the plugin's configuration.

## Permissions

- `yetisutils.adminmode` - Permission to toggle admin mode.
- `yetisutils.warn` - Permission to issue warnings.
- `yetisutils.warnings` - Permission to view player warnings.
- `yetisutils.warnmenu` - Permission to open the warnings GUI.
- `yetisutils.warnclear` - Permission to clear player warnings.
- `yetisutils.mywarns` - Permission for players to view their own warnings.
- `yetisutils.report` - Permission to report issues.
- `yetisutils.reportmenu` - Permission to open the reports GUI.
- `yetisutils.reload` - Permission to reload the plugin configuration.

## Configurable Settings (`config.yml`)

```yaml
log-time-and-date: true  # Enable or disable the logging of when a warn was given
enablePlayerWarningsView: true  # Enable or disable player being able to view their own warns
debug: false # For debugging, spams console when used. Recommended false
maxWarningsBeforeBan: 0  # Set to 0 for unlimited warnings, any other number for the warning limit before ban-IP a player
enableReportFeature: true # This will enable or disable the in-game report feature so players will or won't be able to use /report
