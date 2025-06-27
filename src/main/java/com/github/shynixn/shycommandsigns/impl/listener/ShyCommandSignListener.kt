package com.github.shynixn.shycommandsigns.impl.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.event.ShyCommandSignsDestroyEvent
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class ShyCommandSignListener(
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

        if (signService.isInCooldown(player)) {
            return
        }

        signService.addCooldown(player)
        val signLocation = Location(
            signState.world, signState.block.x.toDouble(), signState.block.y.toDouble(), signState.block.z.toDouble()
        )

        if (signService.isRequestingSign(player)) {
            plugin.launch {
                signService.addCommandSignLocation(player, signLocation)
            }
            return
        }

        val sign = signService.getSignByLocation(signLocation) ?: return

        if (event.action == Action.LEFT_CLICK_BLOCK) {
            sign.executeCommand(signLocation, player, sign.leftClickCommands)
        }

        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            sign.executeCommand(signLocation, player, sign.rightClickCommands)
        }

        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK) {
            sign.executeCommand(signLocation, player, sign.clickCommands)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        signService.clearData(event.player)
    }

    @EventHandler
    fun onSignDestroy(event: ShyCommandSignsDestroyEvent) {
        plugin.launch {
            signService.removeCommandSignLocation(event.signLocation.location.toLocation())
        }
    }
}
