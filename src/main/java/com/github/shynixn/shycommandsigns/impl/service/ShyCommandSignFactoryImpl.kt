package com.github.shynixn.shycommandsigns.impl.service

import com.github.shynixn.mccoroutine.folia.ticks
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSign
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignFactory
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta
import com.github.shynixn.shycommandsigns.impl.ShyCommandSignImpl
import org.bukkit.plugin.Plugin

class ShyCommandSignFactoryImpl(
    private val plugin: Plugin, private val placeHolderService: PlaceHolderService,
    private val commandService: CommandService
) : ShyCommandSignFactory {
    /**
     * Creates a new command sign from the given metadata.
     */
    override fun createCommandSign(meta: ShyCommandSignMeta): ShyCommandSign {
        val refreshMilliSeconds = meta.refreshTicks.ticks
        val lines = arrayOf("", "", "", "")
        if (meta.lines.isNotEmpty()) {
            lines[0] = meta.lines[0]
        }
        if (meta.lines.size > 1) {
            lines[1] = meta.lines[1]
        }
        if (meta.lines.size > 2) {
            lines[2] = meta.lines[2]
        }
        if (meta.lines.size > 3) {
            lines[3] = meta.lines[3]
        }
        val commandSign = ShyCommandSignImpl(
            meta.name,
            refreshMilliSeconds,
            placeHolderService,
            lines,
            ArrayList(meta.clickCommands),
            ArrayList(meta.leftClickCommands),
            ArrayList(meta.rightClickCommands),
            ArrayList(meta.locations),
            commandService,
            plugin
        )
        return commandSign
    }
}
