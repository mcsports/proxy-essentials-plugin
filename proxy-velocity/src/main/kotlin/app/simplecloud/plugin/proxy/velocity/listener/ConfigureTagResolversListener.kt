package app.simplecloud.plugin.proxy.velocity.listener

import app.simplecloud.plugin.proxy.shared.resolver.TagResolverHelper
import app.simplecloud.plugin.proxy.velocity.ProxyVelocityPlugin
import app.simplecloud.plugin.proxy.velocity.event.ConfigureTagResolversEvent
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import kotlinx.coroutines.runBlocking
import kotlin.jvm.optionals.getOrNull

class ConfigureTagResolversListener(
    private val plugin: ProxyVelocityPlugin
) {

    @Subscribe(order = PostOrder.FIRST)
    fun onConfigureTagResolvers(event: ConfigureTagResolversEvent) {
        runBlocking {
            val player = event.player
            val server = player?.currentServer?.getOrNull()
            val registeredServer = server?.server
            val serverInfo = registeredServer?.serverInfo
            val serverName = serverInfo?.name ?: "unknown"

            val ping = player?.ping ?: -1
            val pingColors = plugin.placeHolderConfiguration.get().pingColors

            val onlinePlayers = plugin.proxyServer.allPlayers.size
            val localOnlinePlayers = registeredServer?.playersConnected?.size ?: 0
            val realMaxPlayers = plugin.proxyServer.configuration.showMaxPlayers

            val numericalId = serverName.let { //shame on simplecloud for not providing a function to fetch a server object from a server name
                plugin.cloudControllerHandler.controllerApi.getServers().getAllServers().firstOrNull { server ->
                    val builtServerName = "${server.group}-${server.numericalId}"

                    serverName.equals(builtServerName, true)
                }?.numericalId
            } ?: -1

            event.withTagResolvers(
                TagResolverHelper.getDefaultTagResolvers(
                    serverName,
                    ping,
                    pingColors,
                    onlinePlayers,
                    localOnlinePlayers,
                    realMaxPlayers,
                    numericalId,
                    plugin.motdLayoutHandler.getCurrentMotdLayout()
                )
            )
        }
    }

}
