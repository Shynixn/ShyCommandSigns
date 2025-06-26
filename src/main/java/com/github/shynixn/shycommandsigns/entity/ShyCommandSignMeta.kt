package com.github.shynixn.shycommandsigns.entity

import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.repository.Element

@Comment(
    "###############",
    "",
    "This is the configuration for one type of sign.",
    "",
    "###############"
)
class ShyCommandSignMeta : Element {
    @Comment("Unique identifier of a sign")
    override var name: String = ""

    @Comment("The 4 lines, which are displayed on the sign. They support color codes, HTML color codes and PlaceHolderApi placeholders.")
    var lines: List<String> = listOf("", "", "", "")

    @Comment("A list of commands, which get executed on right or left click. A command always starts with a slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER.")
    var clickCommands: List<CommandMeta> = ArrayList()

    @Comment("A list of commands, which get executed on left click. A command always starts with a slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER.")
    var leftClickCommands: List<CommandMeta> = ArrayList()

    @Comment("A list of commands, which get executed on right click. A command always starts with a slash. Possible values for type SERVER, SERVER_PER_PLAYER, PER_PLAYER.")
    var rightClickCommands: List<CommandMeta> = ArrayList()

    @Comment("How often this sign is updated in ticks. e.g. resolving placeholder values.")
    var refreshTicks: Int = 20 * 3

    @Comment("Do not modify this location list. Use the command '/shycommandsigns add sample_sign' ingame instead. This list stores all sign locations.")
    var locations: MutableList<ShyCommandSignLocation> = ArrayList()
}
