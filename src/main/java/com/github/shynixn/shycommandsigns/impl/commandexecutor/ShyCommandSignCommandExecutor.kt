package com.github.shynixn.shycommandsigns.impl.commandexecutor

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.CoroutineHandler
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandBuilder
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.command.Validator
import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignSettings
import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.UUID

class ShyCommandSignCommandExecutor(
    private val settings: ShyCommandSignSettings,
    private val plugin: Plugin,
    private val signService: ShyCommandSignService,
    private val language: ShyCommandSignsLanguage,
    private val chatMessageService: ChatMessageService,
    private val repository: CacheRepository<ShyCommandSignMeta>,
    private val placeHolderService: PlaceHolderService,
    coroutineHandler: CoroutineHandler,
    commandService: CommandService
) {
    private val senderHasToBePlayer: () -> String = {
        language.shyCommandSignsCommandSenderHasToBePlayer.text
    }

    private val signTabs: (CommandSender) -> List<String> = {
        repository.getCache()?.map { e -> e.name } ?: emptyList()
    }

    private val signMustExist = object : Validator<ShyCommandSignMeta> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): ShyCommandSignMeta? {
            return repository.getAll().firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.shyCommandSignsNotFoundMessage.text.format(openArgs[0])
        }
    }

    private val onlinePlayerTabs: ((CommandSender) -> List<String>) = {
        Bukkit.getOnlinePlayers().map { e -> e.name }
    }

    private val playerMustExist = object : Validator<Player> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): Player? {
            try {
                val playerId = openArgs[0]
                val player = Bukkit.getPlayer(playerId)

                if (player != null) {
                    return player
                }
                return Bukkit.getPlayer(UUID.fromString(playerId))
            } catch (e: Exception) {
                return null
            }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return placeHolderService.resolvePlaceHolder(
                language.shyCommandSignsPlayerNotFoundMessage.text,
                null,
                mapOf("0" to openArgs[0])
            )
        }
    }

    init {
        commandService.registerCommand(
            CommandBuilder(
                coroutineHandler,
                plugin,
                settings.baseCommand,
                chatMessageService
            ) {
                usage(language.shyCommandSignsCommandUsage.text)
                description(language.shyCommandSignsCommandDescription.text)
                aliases(settings.commandAliases)
                permission(settings.commandPermission)
                permissionMessage(language.shyCommandSignsNoPermissionCommand.text)
                subCommand("add") {
                    permission(settings.addPermission)
                    toolTip { language.shyCommandSignsAddCommandHint.text }
                    builder().argument("sign").validator(signMustExist)
                        .tabs(signTabs).executePlayer(senderHasToBePlayer) { player, signMeta ->
                            plugin.launch {
                                addSign(player, signMeta)
                            }
                        }.argument("tagkey").tabs({ listOf("[tagkey]") })
                        .argument("tagvalue").tabs({ listOf("[tagvalue]") })
                        .executePlayer(senderHasToBePlayer) { player, signMeta, tagKey, tagValue ->
                            plugin.launch {
                                addSign(player, signMeta, tagKey, tagValue)
                            }
                        }
                }
                subCommand("server") {
                    permission(settings.serverPermission)
                    toolTip {
                        language.shyCommandSignsServerCommandHint.text
                    }
                    builder().argument("server").executePlayer(senderHasToBePlayer) { player, server ->
                        sendPlayerToServer(player, player, server)
                    }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                        .permission { settings.otherPlayerPermission }.execute { sender, server, player ->
                            sendPlayerToServer(sender, player, server)
                        }
                }
                subCommand("reload") {
                    permission(settings.reloadPermission)
                    toolTip {
                        language.shyCommandSignsReloadCommandHint.text
                    }
                    builder().execute { sender ->
                        plugin.saveDefaultConfig()
                        plugin.reloadConfig()
                        plugin.reloadTranslation(language)
                        signService.reload()
                        sender.sendLanguageMessage(language.shyCommandSignsReloadMessage)
                    }
                }.helpCommand()
            })
    }

    private fun sendPlayerToServer(sender: CommandSender, player: Player, server: String) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(server)
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
        sender.sendLanguageMessage(language.shyCommandSignsServerMessage, server)
    }

    private fun addSign(
        sender: Player,
        signMeta: ShyCommandSignMeta,
        tagKey: String = "",
        tagValue: String = ""
    ) {
        signService.addSignRequest(sender, signMeta.name, Pair(tagKey, tagValue))
        sender.sendLanguageMessage(language.shyCommandSignsRightClickOnSign, signMeta.name)
    }

    private fun CommandSender.sendLanguageMessage(languageItem: LanguageItem, vararg args: String) {
        val sender = this
        plugin.launch(plugin.globalRegionDispatcher) {
            chatMessageService.sendLanguageMessage(sender, languageItem, *args)
        }
    }
}
