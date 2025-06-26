package com.github.shynixn.shycommandsigns

import com.github.shynixn.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.mccoroutine.folia.CoroutineTimings
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutineExecutor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.command.CommandServiceImpl
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.language.globalChatMessageService
import com.github.shynixn.mcutils.common.language.globalPlaceHolderService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.repository.CachedRepositoryImpl
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.repository.YamlFileRepositoryImpl
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.impl.service.ChatMessageServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.PacketServiceImpl
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignFactory
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignMeta
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignSettings
import com.github.shynixn.shycommandsigns.impl.commandexecutor.ShyCommandSignCommandExecutor
import com.github.shynixn.shycommandsigns.impl.listener.ShyCommandSignListener
import com.github.shynixn.shycommandsigns.impl.service.ShyCommandSignFactoryImpl
import com.github.shynixn.shycommandsigns.impl.service.ShyCommandSignServiceImpl
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

class ShyCommandSignsDependencyInjectionModule(
    private val plugin: Plugin,
    private val settings: ShyCommandSignSettings,
    private val language: ShyCommandSignsLanguage,
    private val placeHolderService: PlaceHolderService
) {
    fun build(): DependencyInjectionModule {
        val module = DependencyInjectionModule()

        // Params
        module.addService<Plugin>(plugin)
        module.addService<ShyCommandSignsLanguage>(language)
        module.addService<ShyCommandSignSettings>(settings)

        // Repositories
        val templateRepositoryImpl = YamlFileRepositoryImpl<ShyCommandSignMeta>(
            plugin,
            "sign",
            plugin.dataFolder.toPath().resolve("sign"),
            settings.defaultSigns,
            emptyList(),
            object : TypeReference<ShyCommandSignMeta>() {})
        val cacheTemplateRepository = CachedRepositoryImpl(templateRepositoryImpl)
        module.addService<Repository<ShyCommandSignMeta>>(cacheTemplateRepository)
        module.addService<CacheRepository<ShyCommandSignMeta>>(cacheTemplateRepository)

        // Services
        module.addService<ShyCommandSignCommandExecutor> {
            ShyCommandSignCommandExecutor(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        module.addService<ShyCommandSignListener> {
            ShyCommandSignListener(module.getService(), module.getService(), module.getService(), module.getService())
        }
        module.addService<ShyCommandSignFactory> {
            ShyCommandSignFactoryImpl(module.getService(), module.getService())
        }
        module.addService<ShyCommandSignService> {
            ShyCommandSignServiceImpl(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }

        // Library Services
        module.addService<ConfigurationService>(ConfigurationServiceImpl(plugin))
        module.addService<PacketService>(PacketServiceImpl(plugin))
        module.addService<PlaceHolderService>(placeHolderService)
        module.addService<CommandService>(
            CommandServiceImpl(
                object : CoroutineExecutor {
                    override fun execute(f: suspend () -> Unit) {
                        plugin.launch(object : CoroutineTimings() {}) {
                            f.invoke()
                        }
                    }
                })
        )
        val chatMessageService = ChatMessageServiceImpl(plugin)
        module.addService<ChatMessageService>(chatMessageService)
        plugin.globalChatMessageService = chatMessageService
        plugin.globalPlaceHolderService = placeHolderService

        // Developer Api.
        Bukkit.getServicesManager().register(
            ShyCommandSignService::class.java,
            module.getService<ShyCommandSignService>(),
            plugin,
            ServicePriority.Normal
        )
        Bukkit.getServicesManager().register(
            ShyCommandSignFactory::class.java,
            module.getService<ShyCommandSignFactory>(),
            plugin,
            ServicePriority.Normal
        )

        return module
    }
}
