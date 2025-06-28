# How to create a new sign

In ShyCommandSigns you have to configure a ``sign file`` first, where you configure
the lines of a sign (including PlaceHolderApi compatible placeholders) and commands
which are executed on click.

After you have configured a ``sign file``, you can use the ``/shycommandsigns add <yourSignFileName>`` to request placing the sign.
The next step is to perform a right-click on an existing sign block to apply your ``sign file``. You can add multiple placed signs to a single ``sign file``.

A detailed description can be found below.

## Creating a basic sign

### Managing files

1. Open the ``/plugins/ShyCommandSigns/sign`` folder.
2. Copy the ``sample_sign.yml`` to a new file in the same folder and name it ``my_sign.yml``.
3. Open ``my_sign.yml`` and set the ``name`` to ``my_sign``
4. Set the ``lines`` to text, chat colors (including HTML color) or PlaceHolderApi compatible placeholders.
5. Set the commands, which should get executed on click. If you do not want commands to be executed on a certain type of click just set it to ``[]``. e.g. ``leftClickCommands: []``
6. Define how often this sign is refreshed. It is only refreshed, if the chunk, where the sign is placed, is currently loaded on your server.
7. Save the file.

### InGame

1. Execute the ``/shycommandsigns reload`` command
2. Execute the ``/shycommandsigns add my_sign`` command
3. Place a sign block anywhere
4. Right-click on this sign block. The sign will be added to the list of locations in your ``sign file``.
5. Repeat step 2-4 until you have placed all signs of the ``sign file`` on your server.
6. You can destroy a sign by simply destroying the sign block.

## Creating an advanced sign

ShyCommandSigns can be used to create MiniGame signs for all kinds of MiniGame plugins. They only have to provide a ``join`` command and optionally PlaceHolderApi placeholders.

Assume the following situation:

* You have got multiple arenas of a MiniGame plugin
* You want to design a join sign for all of those arenas 
* You do not want to create a new ``sign file`` per arena because they should all look the same

Here, the sign tagging feature of ShyCommandSigns can be used. 

### Managing files

* Open the ``/plugins/ShyCommandSigns/sign`` folder.
* Copy the ``sample_sign.yml`` to a new file in the same folder and name it ``blockball_join_sign.yml``.
* Open ``blockball_join_sign.yml`` and set the ``name`` to ``blockball_join_sign``
* Set the text of the ``lines`` and ``clickCommands``.

```yaml
lines:
- 'BlockBall'
- '&aJoin'
- '%blockball_game_players_game1%/%blockball_game_maxPlayers_game1%'
- ''
clickCommands: []
leftClickCommands: []
rightClickCommands:
- type: PER_PLAYER
  command: "/blockball join game1"
  cooldown: 0
  delay: 0
```

* You can see that those lines and commands directly reference the arena ``game1`` of BlockBall. 
* However, we can change it that the lines and commands reference a tag ``$arena$`` instead of ``game1``. This allows to use the same ``sign file`` for multiple arenas.
* A tag always starts with ``$`` and ends with ``$``.

```yaml
lines:
- 'BlockBall'
- '&aJoin'
- '%blockball_game_players_$arena$%/%blockball_game_maxPlayers_$arena$%'
- ''
clickCommands: []
leftClickCommands: []
rightClickCommands:
- type: PER_PLAYER
  command: "/blockball join $arena$"
  cooldown: 0
  delay: 0
```

### InGame

* Execute the ``/shycommandsigns reload`` command
* Execute the ``/shycommandsigns add blockball_join arena game1`` command. This creates a join sign for arena named ``game1``.
* Place a sign block anywhere and right-click on this sign block. The sign will be added to the list of locations in your ``sign file``.
* Execute the ``/shycommandsigns add blockball_join arena coolArena`` command. This creates a join sign for arena named ``coolArena``.
* Place a sign block anywhere and right-click on this sign block. The sign will be added to the list of locations in your ``sign file``.
