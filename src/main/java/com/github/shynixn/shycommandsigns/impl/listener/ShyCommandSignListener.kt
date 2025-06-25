package com.github.shynixn.shycommandsigns.impl.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
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
    private val signService: ShyCommandSignService,
    private val commandService: CommandService,
    private val placeHolderService: PlaceHolderService
) : Listener {
    @EventHandler
    fun onSignClickEvent(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock ?: return
        val signState = clickedBlock.state

        if (signState !is Sign) {
            return
        }

        val player = event.player
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
            commandService.executeCommands(
                listOf(player), sign.leftClickCommands
            ) { t, p -> placeHolderService.resolvePlaceHolder(t, p) }
        }

        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            commandService.executeCommands(
                listOf(player), sign.rightClickCommands
            ) { t, p -> placeHolderService.resolvePlaceHolder(t, p) }
        }

        if (event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_BLOCK) {
            commandService.executeCommands(
                listOf(player), sign.clickCommands
            ) { t, p -> placeHolderService.resolvePlaceHolder(t, p) }
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
