package example.java.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.*;

/**
 * @Author thesong
 * @Date 2020/10/28 13:03
 * @Version 1.0
 */
public class LookUpActor extends UntypedActor {

    private ActorRef targetActor = null;

    {
        targetActor = getContext().actorOf(Props.create(TargetActor.class), "targetActor");
    }

    @Override
    public void onReceive(Object message) throws Throwable, Throwable {
        if(message instanceof String){
            if("find".equals(message)){
                ActorSelection as = getContext().actorSelection("targetActor");
                as.tell(new Identify("A001"),getSelf());

            }else if( message instanceof ActorIdentity){
                ActorIdentity ai = (ActorIdentity) message;
                if(ai.correlationId().equals("A001")){
                    ActorRef ref = ai.getRef();
                    if(ref!=null){
                        System.out.println("ActorIdentity:"+ai.correlationId()+" "+ref);
                        ref.tell("hello targetActor", getSelf());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("sys");
        ActorRef lookupActor = actorSystem.actorOf(Props.create(LookUpActor.class),"lookup Actor");
        lookupActor.tell("find",ActorRef.noSender());
    }
}
