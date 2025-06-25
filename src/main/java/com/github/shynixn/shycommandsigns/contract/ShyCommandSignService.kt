package com.github.shynixn.shycommandsigns.contract

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
     * Adds a new sign request.
     */
    fun addSignRequest(player: Player, signMetaName: String, keyValuePair: Pair<String, String>)

    /**
     *  Checks if the player is requesting a sign.
     */
    fun isRequestingSign(player: Player): Boolean

    /**
     * Gets the sign by location.
     */
    fun getSignByLocation(location: Location): ShyCommandSign?

    /**
     * Adds a new location to one of the command signs.
     */
    suspend fun addCommandSignLocation(player: Player, location: Location)

    /**
     * Removes the location from any command signs.
     */
    suspend fun removeCommandSignLocation(location: Location)
}
