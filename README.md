# StaffOnSteroids

A Spigot plugin for a strong and feature-packed staff system.

Made for Spigot/Paper 1.20.4+

## Features

- **Staff Chat**: Private communication channel for staff members
- **Player Punishments**: Ban, kick, and mute players with IP-based variants
- **Vanish System**: Hide staff members from regular players
- **Freeze System**: Prevent player movement with automatic ban on disconnect
- **MySQL Database**: Persistent data storage with connection pooling
- **Configurable Messages**: Fully customizable messages and formats
- **Permission-based Access**: Granular permission control for different staff levels

## Commands

### Staff Commands (staffonsteroids.staff)
- `/staffchat [message]` - Toggle staff chat or send staff message (alias: `/sc`)
- `/ban <player> [reason]` - Ban a player
- `/ipban <player> [reason]` - IP ban a player
- `/mute <player> [reason]` - Mute/unmute a player
- `/ipmute <player> [reason]` - IP mute/unmute a player
- `/kick <player> [reason]` - Kick a player
- `/ipkick <player> [reason]` - Kick all players with the same IP

### Admin Commands (staffonsteroids.admin)
- `/vanish [player]` - Toggle vanish mode (alias: `/v`)
- `/freeze <player>` - Freeze/unfreeze a player

## Permissions

- `staffonsteroids.staff` - Access to punishment commands only
- `staffonsteroids.admin` - Access to all features (includes staff permissions)

## Installation

1. Download the plugin JAR file from [releases](https://github.com/rogueliver/staffonsteroids/releases) (or build it yourself)
2. Place it in your server's `plugins` folder
3. Restart your server
4. Customize the plugin's configuration in `config.yml`

## Configuration
The plugin creates a [`config.yml`](src/main/resources/config.yml) file with the following options:

### # Database Setup

```yaml
database:
  host: localhost
  port: 3306
  database: staffonsteroids
  username: root
  password: password
  pool-size: 10
  connection-timeout: 30000
  idle-timeout: 600000
  max-lifetime: 1800000
```

### # Plugin Settings

```yaml
settings:
  save-interval: 300
  auto-save: true
  debug: false

staff-chat:
  format: "&8[&cStaff&8] &f{player}: {message}"

vanish:
  hide-from-tab: true
  silent-join-quit: true

freeze:
  ban-on-quit: true
  movement-message-interval: 5
```

## Building
Requirements:

- Java 17+
- Maven

```bash
mvn clean package
```

## License ![License](https://img.shields.io/github/license/rogueliver/staffonsteroids)

This project is under the MIT License. See the [LICENSE](LICENSE) file for details.

## Author

Made by rogueliver (RL)

* GitHub: https://github.com/rogueliver
* Discord: https://discord.com/users/1354013258884972610