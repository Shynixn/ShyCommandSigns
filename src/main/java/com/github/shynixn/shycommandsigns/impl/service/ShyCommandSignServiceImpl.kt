package com.github.shynixn.shycommandsigns.impl.service

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.shycommandsigns.contract.ShyCommandSign
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignFactory
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignLocation
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignSettings
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignTag
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class ShyCommandSignServiceImpl(
    private val repository: CacheRepository<ShyCommandSignMeta>,
    private val signFactory: ShyCommandSignFactory,
    private val language: ShyCommandSignsLanguage,
    private val settings: ShyCommandSignSettings,
    private val chatMessageService: ChatMessageService,
    private val plugin: Plugin
) : ShyCommandSignService {
    private val commandSigns = HashMap<String, ShyCommandSign>()
    private val rightClickCache = HashMap<Player, Pair<String, Pair<String, String>>>()
    private val coolDown = HashMap<Player, Long>()

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
                val key = "${location.world}-${location.x}-${location.y}-${location.z}"
                commandSigns[key] = sign
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
     * Checks if the player is in cooldown.
     */
    override fun isInCooldown(player: Player): Boolean {
        val timestamp = coolDown[player] ?: return false
        val now = Date().time
        return now - timestamp < (this.settings.coolDownTicks * 50)
    }

    /**
     * Sets the player to cooldown.
     */
    override fun addCooldown(player: Player) {
        coolDown[player] = Date().time
    }

    /**
     * Gets the sign by location.
     */
    override fun getSignByLocation(location: Location): ShyCommandSign? {
        val key = "${location.world!!.name}-${location.x}-${location.y}-${location.z}"
        return commandSigns[key]
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

        plugin.launch(plugin.globalRegionDispatcher) {
            chatMessageService.sendLanguageMessage(
                player,
                language.shyCommandSignsRightClickOnSignSuccess,
                signRequest.first
            )
        }
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
        for (sign in commandSigns.values.toTypedArray()) {
            sign.close()
        }
        commandSigns.clear()
    }
}
