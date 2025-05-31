package com.github.shynixn.shycommandsigns.entity

import com.github.shynixn.shycommandsigns.enumeration.Permission

class ShyCommandSignSettings(private val reloadFun: (ShyCommandSignSettings) -> Unit) {
    /**
     * Delay when joining the server.
     */
    var joinDelaySeconds = 3

    /**
     * Base Command.
     */
    var baseCommand: String = "shycommandsigns"

    /**
     * Command aliases.
     */
    var commandAliases: List<String> = ArrayList()

    var commandPermission: String = Permission.COMMAND.text

    var reloadPermission: String = Permission.RELOAD.text

    var addPermission: String = Permission.ADD.text

    var defaultSigns: List<Pair<String, String>> = listOf(
        "sign/sample_sign.yml" to "sample_sign.yml"
    )

    /**
     * Reloads the config.
     */
    fun reload() {
        reloadFun.invoke(this)
    }
}
