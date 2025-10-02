# Command Reference

Get started with ShyCommandSigns by using the help command:

```
/shycommandsigns help 1
```

## Core Commands

### 📝 Adding Signs
```
/shycommandsigns add <sign> [tagkey] [tagvalue]
```

Creates a new interactive sign using a predefined configuration.

**Parameters:**
- `<sign>`: The name of your sign configuration file
- `[tagkey]`: Optional tag name for dynamic content
- `[tagvalue]`: Optional value for the tag

**Example Usage:**
```
/shycommandsigns add shop                  # Basic sign
/shycommandsigns add shop price 100        # Sign with price tag
/shycommandsigns add game arena lobby      # Game sign with arena tag
```

**Using Tags:**
Tags allow you to create dynamic signs that can be reused with different values:
- Use `$tagname$` in your configuration to reference tags
- Example: Tag `game=lobby` can be used as `$game$` in sign text or commands

Note: Signs can be removed by simply destroying the sign block.

### /shycommandsigns server

```
/shycommandsigns server <server> [player]
```

Connects the player to a different BungeeCord or Velocity server.

* Server: Name of the BungeeCord or Velocity server to send the player to.
* Player: Optional player argument to execute the action for another player.

### /shycommandsigns reload

```
/shycommandsigns reload
```

Reloads all configurations and signs.
