package com.thesong.common;

import com.thesong.utils.ZKUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.I0Itec.zkclient.ZkClient;


import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @Author thesong
 * @Date 2020/11/3 11:45
 * @Version 1.0
 * @Describe
 */
public class AkkaUtils {
    public static Config getConfig(ZkClient zkClient) {
        Integer port = 3000;
        Integer id = 1;
        String ip = "localhost";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //注册引擎到zk中去
        ZKUtils.registerEngineInZookeeper(zkClient, id, ip, port);

        //封装Akka信息
        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.parseString("akka.actor.provider=akka.remote.RemoteActorRefprovider"))
                .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + ip))
                .withFallback(ConfigFactory.load());
        return config;


    }

}
