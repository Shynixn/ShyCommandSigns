package com.github.shynixn.shycommandsigns.impl.listener

import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignSettings
import com.github.shynixn.shycommandsigns.event.ShyCommandSignsDestroyEvent
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class ShyCommandSignListener(
    private val settings: ShyCommandSignSettings,
    private val plugin: Plugin,
    private val signService: ShyCommandSignService
) : Listener {
    @EventHandler
    fun onSignClickEvent(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock ?: return
        val signState = clickedBlock.state

        if (signState !is Sign) {
            return
        }

        val player = event.player





        if (signService.rightClickPlayers.containsKey(player)) {
            val f = signService.rightClickPlayers.remove(player)!!
            val signMeta = SignMeta()
            signMeta.location = Vector3d(
                signState.world.name,
                signState.block.x.toDouble(),
                signState.block.y.toDouble(),
                signState.block.z.toDouble()
            )
            f.invoke(signMeta)
            return
        }

        signService.executeSign(signState.location, player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        signService.clearData(event.player)
    }

    @EventHandler
    fun onSignDestroy(event: ShyCommandSignsDestroyEvent) {
        signService.clearData(event.player)
    }
}
