# YETIsUtils

**Version:** 1.1  
**API Version:** 1.20  

## Features

### Admin Mode Command (`/adminmode`)

- Allows administrators to toggle between their regular player inventory and a separate admin inventory. This ensures that their personal inventory remains unaffected while performing administrative tasks.

### Warning System

- **Warn Command (`/warn <player>`):**
  - Allows administrators to issue warnings to players.
  - Warnings are saved persistently and include the player's username and IP address.
  
- **Warnings Command (`/warnings <player>`):**
  - Allows administrators to check the total number of warnings a player has received.

### Persistent Storage

- Inventories and warnings are saved to files, ensuring data persistence across server restarts.
- Inventories are stored in `player_inventories.yml` and `admin_inventories.yml` within the `inventories` folder.
- Warnings are stored in `warnings.yml`.

## Permissions

- **yetisutils.warn:**
  - Allows the player to warn others.
  - Default: OP
- **yetisutils.warnings:**
  - Allows the player to check warnings.
  - Default: OP

## Commands

- **/adminmode:**
  - Usage: `/adminmode`
  - Description: Toggles between admin and player inventories.

- **/warn <player>:**
  - Usage: `/warn <player>`
  - Description: Issues a warning to the specified player.

- **/warnings <player>:**
  - Usage: `/warnings <player>`
  - Description: Checks the number of warnings the specified player has received.

## Installation

1. Download the latest version of YETIsUtils.
2. Place the plugin JAR file into your server's `plugins` directory.
3. Restart the server to load the plugin.
4. Configure the plugin permissions as needed.

## Configuration

none :/

