# YETIsUtils

## Features

- **Admin Mode Inventory Switch**: Toggle between your regular player inventory and a separate admin inventory.
- **Player Warnings**: Issue warnings to players, store and view warnings, and clear specific warnings.
- **Player Reports**: Players can report issues, which are stored and viewable by admins in a GUI.
- **Configuration Reload**: Reload the plugin's configuration without restarting the server.
- **Auto IP Ban**: Automatically ban a player's IP after a configurable number of warnings.
- **Permissions**: Configurable permissions for commands to ensure only authorized users can perform certain actions.
- **Discord Integration**: Notifications about new reports and warnings are sent to specified Discord channels.

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
#YETIs Utils made by YETI
#reason for making it felt like it tbf
#plugin creation date 23-05-2024
#plugin version 3.5 - release date 02-06-24

log-time-and-date: true  #enable or disable the logging of when a warn was given
enablePlayerWarningsView: true  #enable or disable player being able to view their own warns
debug: false #for debugging does spam console when used recommended false (i dont think this even works anymore :/ )
maxWarningsBeforeBan: 0  # Set to 0 for unlimited warnings, any other number for the warning limit before ban-ip a player
enableReportFeature: true #this will enable or disable the ingame report feature so player will or wont be able to use /report


######################
# Discord Bot Settings
######################
# https://discord.com/developers/applications this is to create a new bot for your server then past its token in the discord-bot-token including the ""
# note you need the ID for the text channel using devmode on discord, once enabled right click the channel and a new copy id button will be available
# if you don't want to use the discord part of this plugin leave it how it is now

discord-bot-token: "YOUR_DISCORD_BOT_TOKEN"
discord-report-channel-id: "YOUR_DISCORD_REPORT_CHANNEL_ID"
discord-warning-channel-id: "YOUR_DISCORD_WARNING_CHANNEL_ID"
