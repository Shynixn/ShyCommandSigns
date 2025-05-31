package com.github.shynixn.shycommandsigns.entity

import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.repository.Element

class ShyCommandSignMeta : Element {
    @Comment("Unique Identifier of the element.")
    override var name: String = ""

    @Comment("The lines to display on the sign.")
    var lines: List<String> = listOf("", "", "", "")

    @Comment("How often this sign is updated in ticks.")
    var refreshTicks: Int = 20 * 3

    @Comment("How often this sign be clicked per player in ticks.")
    var cooldownTicks: Int = 20

    @Comment("The commands executed when clicking the sign. It does not matter if leftClick or rightClick.")
    var clickCommands: List<CommandMeta> = ArrayList()

    @Comment("The commands executed on leftClicking the sign.")
    var leftClickCommands: List<CommandMeta> = ArrayList()

    @Comment("The commands executed on rightClicking the sign.")
    var rightClickCommands: List<CommandMeta> = ArrayList()

    @Comment("All locations where a sign of this type is placed. Do not modify it here, use the ingame command '/shycommandsigns add <sign> [tagkey] [tagvalue]' instead.")
    var locations: MutableList<ShyCommandSignLocation> = ArrayList()
}
