# Commands

The following commands are available in ShyCommandSigns. You can access them by typing:

```
/shycommandsigns help 1
```

### /shycommandsigns add

```
/shycommandsigns add <sign> [tagkey] [tagvalue]
```

Adds a new sign request for the given player. After a player has right-clicked on a placed sign, this sign will be added to the list of locations.
Tags can be used for sign placeholders per location. 
e.g. a tag key of ``game`` and tag value ``cool game`` can be used in the ``lines`` and ``commands`` properties via the placeholder ``$game$``.
e.g. a tag key of ``mynumber`` and tag value ``5`` can be used in the ``lines`` and ``commands`` properties via the placeholder ``$mynumber$``.

Note: Signs can be removed by simply destroying the sign block.

### /shycommandsigns reload

```
/shycommandsigns reload
```

Reloads all configurations and signs.
