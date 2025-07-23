package app.simplecloud.plugin.proxy.bungeecord.listener

import app.simplecloud.plugin.proxy.bungeecord.ProxyBungeeCordPlugin
import app.simplecloud.plugin.proxy.bungeecord.event.ConfigureTagResolversEvent
import app.simplecloud.plugin.proxy.shared.resolver.TagResolverHelper
import kotlinx.coroutines.runBlocking
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

class ConfigureTagResolversListener(
    private val plugin: ProxyBungeeCordPlugin
) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onConfigureTagResolvers(event: ConfigureTagResolversEvent) {

        runBlocking {
            val player = event.player
            val server = player?.server
            val serverInfo = server?.info
            val serverName = serverInfo?.name ?: "unknown"

            val ping = player?.ping ?: -1
            val pingColors = plugin.proxyPlugin.placeHolderConfiguration.get().pingColors

            val onlinePlayers = plugin.proxy.players.size
            val localOnlinePlayers = serverInfo?.players?.size ?: 0
            val realMaxPlayers = plugin.proxy.config.playerLimit

            val numericalId = serverName.let { //shame on simplecloud for not providing a function to fetch a server object from a server name
                plugin.proxyPlugin.cloudControllerHandler.controllerApi.getServers().getAllServers().firstOrNull { server ->
                    val builtServerName = "${server.group}-${server.numericalId}"

                    serverName.equals(builtServerName, true)
                }?.numericalId
            } ?: -1

            event.withTagResolvers(
                TagResolverHelper.getDefaultTagResolvers(
                    serverName,
                    ping.toLong(),
                    pingColors,
                    onlinePlayers,
                    localOnlinePlayers,
                    realMaxPlayers,
                    numericalId,
                    plugin.proxyPlugin.motdLayoutHandler.getCurrentMotdLayout()
                )
            )
        }
    }

}
