package com.github.shynixn.shycommandsigns.impl.commandexecutor

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.CoroutineExecutor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandBuilder
import com.github.shynixn.mcutils.common.command.Validator
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.language.sendPluginMessage
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignSettings
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ShyCommandSignCommandExecutor(
    private val settings: ShyCommandSignSettings,
    private val plugin: Plugin,
    private val signService: ShyCommandSignService,
    private val language: ShyCommandSignsLanguage,
    chatMessageService: ChatMessageService,
    private val repository: CacheRepository<ShyCommandSignMeta>,
) {

    private val coroutineExecutor = object : CoroutineExecutor {
        override fun execute(f: suspend () -> Unit) {
            plugin.launch(plugin.globalRegionDispatcher) {
                f.invoke()
            }
        }
    }

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

    init {
        CommandBuilder(plugin, coroutineExecutor, settings.baseCommand, chatMessageService) {
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
                    sender.sendPluginMessage(language.shyCommandSignsReloadMessage)
                }
            }.helpCommand()
        }.build()
    }

    private fun addSign(
        sender: Player,
        signMeta: ShyCommandSignMeta,
        tagKey: String = "",
        tagValue: String = ""
    ) {
        signService.addSignRequest(sender, signMeta.name, Pair(tagKey, tagValue))
        sender.sendPluginMessage(language.shyCommandSignsRightClickOnSign, signMeta.name)
    }
}
