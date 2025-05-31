package com.github.shynixn.shycommandsigns.contract

import com.github.shynixn.shycommandsigns.entity.ShyCommandSignTag
import org.bukkit.Location
import org.bukkit.entity.Player

interface ShyCommandSignService : AutoCloseable {
    /**
     * Reloads all signs and configuration.
     */
    suspend fun reload()

    /**
     * Clears all allocated data from this player.
     */
    fun clearData(player: Player)

    /**
     * Adds a new location to one of the command signs.
     */
    suspend fun addCommandSignLocation(name: String, location: Location, tags : List<ShyCommandSignTag>)
}
