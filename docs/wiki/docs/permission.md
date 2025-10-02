# Permissions System

ShyCommandSigns uses a simple but effective permission system to control access to various features.

## Permission Levels

### 👥 User Permissions
Basic permissions that can be granted to regular players. Currently, no user-level permissions are required for basic sign interaction.

### 🛡️ Admin Permissions
Reserved for server administrators and moderators. These permissions provide control over sign management and configuration.

## Permission List

| Permission Node | Level | Description | Example Usage |
|----------------|-------|-------------|---------------|
| `shycommandsigns.command` | Admin | Access to base command | Using `/shycommandsigns` |
| `shycommandsigns.reload` | Admin | Reload plugin configs | Updating configurations |
| `shycommandsigns.add` | Admin | Create new signs | Setting up sign locations |
| `shycommandsigns.server` | Admin | Server teleport command | Moving players between servers |
| `shycommandsigns.manipulateother` | Admin | Modify other players | Running commands as others |

💡 **Tip**: Use a permissions plugin like LuckPerms to manage these permissions effectively.
