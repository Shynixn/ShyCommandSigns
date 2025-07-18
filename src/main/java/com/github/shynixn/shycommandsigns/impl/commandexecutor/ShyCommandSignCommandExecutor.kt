package com.github.shynixn.shycommandsigns.impl.commandexecutor

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.CoroutinePlugin
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandBuilder
import com.github.shynixn.mcutils.common.command.Validator
import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignSettings
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ShyCommandSignCommandExecutor(
    private val settings: ShyCommandSignSettings,
    private val plugin: CoroutinePlugin,
    private val signService: ShyCommandSignService,
    private val language: ShyCommandSignsLanguage,
    private val chatMessageService: ChatMessageService,
    private val repository: CacheRepository<ShyCommandSignMeta>,
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

    init {
        CommandBuilder(plugin, settings.baseCommand, chatMessageService) {
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
                    sender.sendLanguageMessage(language.shyCommandSignsReloadMessage)
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
        sender.sendLanguageMessage(language.shyCommandSignsRightClickOnSign, signMeta.name)
    }

    private fun CommandSender.sendLanguageMessage(languageItem: LanguageItem, vararg args: String) {
        val sender = this
        plugin.launch(plugin.globalRegionDispatcher) {
            chatMessageService.sendLanguageMessage(sender, languageItem, *args)
        }
    }
}
