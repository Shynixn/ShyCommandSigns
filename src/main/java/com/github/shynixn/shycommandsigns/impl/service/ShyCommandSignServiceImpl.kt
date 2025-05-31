package com.github.shynixn.shycommandsigns.impl.service

import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.shycommandsigns.contract.ShyCommandSign
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignFactory
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignLocation
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignTag
import org.bukkit.Location
import org.bukkit.entity.Player

class ShyCommandSignServiceImpl(
    private val repository: CacheRepository<ShyCommandSignMeta>,
    private val signFactory: ShyCommandSignFactory,
) : ShyCommandSignService {
    private val commandSigns = ArrayList<ShyCommandSign>()
    private val rightClickCache = HashSet<Player>()
    private val coolDown = HashSet<Player>()

    /**
     * Reloads all shyCOmmandSigns and configuration.
     */
    override suspend fun reload() {
        close()
        val signMetas = repository.getAll()

        for (meta in signMetas) {
            signFactory.createCommandSign(meta)
        }
    }

    /**
     * Clears all allocated data from this player.
     */
    override fun clearData(player: Player) {
        // We do not remove cool down here because we want to wait the entire cooldown.
        rightClickCache.remove(player)
    }

    /**
     * Adds a new location to one of the command signs.
     */
    override suspend fun addCommandSignLocation(name: String, location: Location, tags: List<ShyCommandSignTag>) {
        val signMetas = repository.getAll()
        val signMeta = signMetas.firstOrNull { e -> e.name == name }

        if (signMeta == null) {
            return
        }

        signMeta.locations.add(ShyCommandSignLocation().also {
            it.location = location.toVector3d()
            it.tags = tags
        })
        repository.save(signMeta)
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
        for (commandSign in commandSigns) {
            commandSign.close()
        }
        commandSigns.clear()
    }
}
