package com.github.shynixn.shycommandsigns.impl

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.shycommandsigns.ShyCommandSignsPlugin
import com.github.shynixn.shycommandsigns.contract.ShyCommandSign
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignLocation
import com.github.shynixn.shycommandsigns.event.ShyCommandSignsDestroyEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.plugin.Plugin


class ShyCommandSignImpl(
    override val name: String,
    /**
     * How often refreshed.
     */
    private var refreshMilliSeconds: Long,

    /**
     * PlaceHolder service.
     */
    private var placeHolderService: PlaceHolderService,

    /**
     * The lines being displayed on the sign. These lines can contain placeholders.
     */
    override var lines: Array<String>,
    override var clickCommands: List<CommandMeta>,
    override var leftClickCommands: List<CommandMeta>,
    override var rightClickCommands: List<CommandMeta>,
    override var locations: MutableList<ShyCommandSignLocation>,
    /**
     * Plugin
     */
    private var plugin: Plugin,
) : ShyCommandSign {
    /**
     * Is this sign disposed.
     */
    override var isDisposed: Boolean = false

    init {
        plugin.launch {
            while (!isDisposed) {
                updateAsync()
                delay(refreshMilliSeconds)
            }
        }
    }

    /**
     * Performs an immediate update to all placed signs of this command sign.
     */
    override fun update() {
        checkDisposed()
        plugin.launch {
            updateAsync()
        }
    }

    /**
     * Disposes this shycommandsign permanently.
     */
    override fun close() {
        isDisposed = true
    }

    private suspend fun updateAsync() {
        if (isDisposed) {
            return
        }

        val jobs = ArrayList<Job>()

        for (signLocation in locations.toTypedArray()) {
            if (isDisposed) {
                return
            }

            val location = signLocation.location.toLocation()
            val job = plugin.launch(plugin.regionDispatcher(location)) {
                if (!isChunkLoadedAtLocation(signLocation.location)) {
                    return@launch
                }

                val finalLines = resolveLines(signLocation)
                val blockState = location.block.state

                // Check if block is still a sign.
                if (blockState !is Sign) {
                    Bukkit.getPluginManager().callEvent(ShyCommandSignsDestroyEvent(signLocation))
                    locations.remove(signLocation)
                    return@launch
                }

                for (i in finalLines.indices) {
                    blockState.setLine(i, finalLines[i])
                }
                blockState.update(true)
            }

            jobs.add(job)
        }

        jobs.joinAll()
    }

    private suspend fun resolveLines(location: ShyCommandSignLocation): Array<String> {
        return withContext(plugin.globalRegionDispatcher) {
            val newLines = arrayOf("", "", "", "")
            for (i in newLines.indices) {
                newLines[i] = placeHolderService.resolvePlaceHolder(
                    lines[i], null, mapOf(ShyCommandSignsPlugin.signLocationKey to location)
                )
            }
            newLines
        }
    }

    private fun isChunkLoadedAtLocation(location: Vector3d): Boolean {
        val world = try {
            Bukkit.getWorld(location.world!!)
        } catch (e: Exception) {
            return false
        }

        if (world == null) {
            return false
        }
        val chunkX = location.blockX shr 4
        val chunkZ = location.blockZ shr 4
        return world.isChunkLoaded(chunkX, chunkZ)
    }

    private fun checkDisposed() {
        if (isDisposed) {
            throw IllegalArgumentException("ShyCommandSign is already disposed!")
        }
    }
}
