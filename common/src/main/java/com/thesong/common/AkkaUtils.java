package com.thesong.common;

import com.thesong.domain.engine;
import com.thesong.utils.ZKUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.I0Itec.zkclient.ZkClient;
import scala.collection.Iterator;
import scala.collection.Seq;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


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

        final Seq<engine.PlatEngine> platEngineInCluster = ZKUtils.getPlatEngineInCluster(zkClient);
        /**
         * 首先创建集合把已经注册的id保存起来，用于对id进行顺序增加，防止重复的id多次进行注册
         * (有可能在一台机器上启动多个引擎，同时引擎端口不能重复，防止端口冲突)
         */

        List engineIds = new ArrayList<Integer>();
        List enginePorts = new ArrayList<Integer>();

        Iterator<engine.PlatEngine> iterator = platEngineInCluster.iterator();
        while (iterator.hasNext()) {
            engine.PlatEngine engine = iterator.next();
            engineIds.add(engine.engineId());
            if (engine.engineInfo().contains(ip)) {
                enginePorts.add(Integer.parseInt(engine.engineInfo().split(":")[1]));
            }
        }

        // port递增
        while (enginePorts.size() != 0) {
            while (enginePorts.contains(port)) {
                port += 1;
            }
        }

        // id递增
        while (engineIds.size() != 0) {
            while (enginePorts.contains(id)) {
                id += 1;
            }
        }

        //引擎注册到zk中去
        ZKUtils.registerEngineInZookeeper(zkClient, id, ip, port);

        //封装Akka config 信息
        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.parseString("akka.actor.provider=akka.remote.RemoteActorRefProvider"))
                .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + ip))
                .withFallback(ConfigFactory.load());
        return config;

    }

}
