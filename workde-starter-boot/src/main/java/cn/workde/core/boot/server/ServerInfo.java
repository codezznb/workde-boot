package cn.workde.core.boot.server;

import cn.hutool.core.net.NetUtil;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;

/**
 * 服务器信息
 * @author zhujingang
 * @date 2019/8/28 8:59 PM
 */
public class ServerInfo implements SmartInitializingSingleton {

    private final ServerProperties serverProperties;
    private String hostName;
    private String ip;
    private Integer port;
    private String ipWithPort;


    @Autowired(required = false)
    public ServerInfo(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.hostName = NetUtil.getLocalhost().getHostName();
        this.ip = NetUtil.getLocalhostStr();
        this.port = serverProperties.getPort();
        this.ipWithPort = String.format("%s:%s", ip, port);
    }
}
