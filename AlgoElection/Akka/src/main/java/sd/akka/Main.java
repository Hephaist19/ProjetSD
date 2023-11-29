package sd.akka;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.routing.RoundRobinPool;

import sd.akka.actor.ChaineActeur;

public class Main {

        public static void main(String[] args) {

        ActorSystem actorSystem = ActorSystem.create();

        ActorRef chaineActorRef = actorSystem.actorOf(ChaineActeur.props(2));

        chaineActorRef.tell(new ChaineActeur.CreateActor(), ActorRef.noSender());

        // Arrêt du système d'acteurs
        actorSystem.terminate();
    }
    
}
