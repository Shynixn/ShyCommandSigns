package com.github.shynixn.shycommandsigns.contract

import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignLocation
import org.bukkit.Location
import org.bukkit.entity.Player

interface ShyCommandSign {
    /**
     * Name of the meta.
     */
    val name: String

    /**
     * The lines being displayed on the sign. These lines can contain placeholders.
     */
    var lines: Array<String>

    var clickCommands: List<CommandMeta>

    var leftClickCommands: List<CommandMeta>

    var rightClickCommands: List<CommandMeta>

    var locations: MutableList<ShyCommandSignLocation>

    /**
     * Is this sign disposed.
     */
    val isDisposed: Boolean

    fun executeCommand(location : Location, player : Player, commands: List<CommandMeta>)

    /**
     * Performs an immediate update to all placed signs of this command sign.
     */
    fun update()

    /**
     * Disposes this shycommandsign permanently.
     */
    fun close()
}
