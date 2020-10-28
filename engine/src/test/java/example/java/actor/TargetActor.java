package example.java.actor;

import akka.actor.UntypedActor;

/**
 * @Author thesong
 * @Date 2020/10/28 13:00
 * @Version 1.0
 */
public class TargetActor extends UntypedActor{

    @Override
    public void onReceive(Object message) throws Throwable, Throwable {
        System.out.println("target receive:" + message);
    }
}
