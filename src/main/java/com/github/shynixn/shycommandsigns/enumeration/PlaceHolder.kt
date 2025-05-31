package com.github.shynixn.shycommandsigns.enumeration

import com.github.shynixn.shycommandsigns.ShyCommandSignsPlugin
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignLocation
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService as PlaceHolderService1

enum class PlaceHolder(
    val text: String,
    val f: ((Player?, String?, ShyCommandSignLocation?) -> String?),
) {
    PLAYER_NAME("player_name", { p, _, _ -> p?.name }),

    TAG("tag_[key]", { p, key, signLocation ->
        if (signLocation != null) {
            val pair = signLocation.tags.firstOrNull { e -> e.key == key }
            pair?.value
        } else {
            null
        }
    });

    fun getFullPlaceHolder(plugin: Plugin): String {
        return "%${plugin.name.lowercase(Locale.ENGLISH)}_${text}%"
    }

    companion object {
        /**
         * Registers all placeHolder. Overrides previously registered placeholders.
         */
        fun registerAll(
            plugin: Plugin,
            placeHolderService: PlaceHolderService1,
        ) {
            for (placeHolder in PlaceHolder.values()) {
                placeHolderService.register(placeHolder.getFullPlaceHolder(plugin)) { player, context ->
                    val newContext = context.toMutableMap()
                    val signLocation = newContext[ShyCommandSignsPlugin.signLocationKey] as ShyCommandSignLocation?
                    val signKey = newContext[ShyCommandSignsPlugin.signKey] as String?
                    placeHolder.f.invoke(player, signKey, signLocation)
                }
            }
        }
    }
}
