package com.github.shynixn.shycommandsigns.impl.service

import com.github.shynixn.mcutils.common.language.sendPluginMessage
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.shycommandsigns.contract.ShyCommandSign
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignFactory
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignLocation
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignTag
import org.bukkit.Location
import org.bukkit.entity.Player

class ShyCommandSignServiceImpl(
    private val repository: CacheRepository<ShyCommandSignMeta>,
    private val signFactory: ShyCommandSignFactory,
    private val language: ShyCommandSignsLanguage
) : ShyCommandSignService {
    private val commandSigns = HashMap<String, HashMap<Int, HashMap<Int, HashMap<Int, ShyCommandSign>>>>()
    private val rightClickCache = HashMap<Player, Pair<String, Pair<String, String>>>()
    private val coolDown = HashSet<Player>()

    /**
     * Reloads all shyCommandSigns and configuration.
     */
    override suspend fun reload() {
        close()
        val signMetas = repository.getAll()

        for (meta in signMetas) {
            val sign = signFactory.createCommandSign(meta)

            for (locationData in meta.locations) {
                val location = locationData.location
                if (!commandSigns.containsKey(location.world)) {
                    commandSigns[location.world!!] = HashMap()
                }

                if (!commandSigns[location.world]!!.containsKey(location.blockX)) {
                    commandSigns[location.world!!]!![location.blockX] = HashMap()
                }

                if (!commandSigns[location.world]!![location.blockX]!!.containsKey(location.blockY)) {
                    commandSigns[location.world!!]!![location.blockX]!![location.blockY] = HashMap()
                }

                if (!commandSigns[location.world]!![location.blockX]!![location.blockY]!!.containsKey(location.blockZ)) {
                    commandSigns[location.world!!]!![location.blockX]!![location.blockY]!![location.blockZ] = sign
                }
            }
        }
    }

    /**
     * Clears all allocated data from this player.
     */
    override fun clearData(player: Player) {
        coolDown.remove(player)
        rightClickCache.remove(player)
    }

    /**
     * Adds a new sign request.
     */
    override fun addSignRequest(player: Player, signMetaName: String, keyValuePair: Pair<String, String>) {
        rightClickCache[player] = Pair(signMetaName, keyValuePair)
    }

    /**
     *  Checks if the player is requesting a sign.
     */
    override fun isRequestingSign(player: Player): Boolean {
        return rightClickCache.containsKey(player)
    }

    /**
     * Gets the sign by location.
     */
    override fun getSignByLocation(location: Location): ShyCommandSign? {
        val xSet = commandSigns[location.world!!.name] ?: return null
        val ySet = xSet[location.blockX] ?: return null
        val zSet = ySet[location.blockY] ?: return null
        return zSet[location.blockZ]
    }

    /**
     * Adds a new location to one of the command signs.
     */
    override suspend fun addCommandSignLocation(player: Player, location: Location) {
        val signRequest = rightClickCache[player] ?: return
        rightClickCache.remove(player)
        val signMetas = repository.getAll()
        val selectedSignMeta = signMetas.firstOrNull { e -> e.name == signRequest.first }
        if (selectedSignMeta == null) {
            return
        }

        // Check if location is present anywhere and remove it.
        for (signMeta in signMetas) {
            for (existingSignLocation in signMeta.locations.toTypedArray()) {
                val existingLocation = existingSignLocation.location

                if (existingLocation.world == location.world!!.name && existingLocation.x == location.x && existingLocation.y == location.y && existingLocation.z == location.z) {
                    signMeta.locations.remove(existingSignLocation)
                    repository.save(signMeta)
                }
            }
        }

        val tags = ArrayList<ShyCommandSignTag>()

        if (signRequest.second.first.isNotBlank()) {
            tags.add(ShyCommandSignTag().also {
                it.key = signRequest.second.first
                it.value = signRequest.second.second
            })
        }

        selectedSignMeta.locations.add(ShyCommandSignLocation().also {
            it.location = location.toVector3d()
            it.tags = tags
        })
        repository.save(selectedSignMeta)
        reload()
        player.sendPluginMessage(language.shyCommandSignsRightClickOnSignSuccess, signRequest.first)
    }

    /**
     * Removes the location from any command signs.
     */
    override suspend fun removeCommandSignLocation(location: Location) {
        val signMetas = repository.getAll()
        for (signMeta in signMetas) {
            for (existingSignLocation in signMeta.locations.toTypedArray()) {
                val existingLocation = existingSignLocation.location

                if (existingLocation.world == location.world!!.name && existingLocation.x == location.x && existingLocation.y == location.y && existingLocation.z == location.z) {
                    signMeta.locations.remove(existingSignLocation)
                    repository.save(signMeta)
                }
            }
        }
        reload()
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     *
     */
    override fun close() {
        repository.clearCache()
        rightClickCache.clear()
        coolDown.clear()
        for (world in commandSigns.keys.toTypedArray()) {
            for (x in commandSigns[world]!!.keys.toTypedArray()) {
                for (y in commandSigns[world]!![x]!!.keys.toTypedArray()) {
                    for (sign in commandSigns[world]!![x]!![y]!!.values.toTypedArray()) {
                        sign.close()
                    }
                }
            }
        }
        commandSigns.clear()
    }
}
