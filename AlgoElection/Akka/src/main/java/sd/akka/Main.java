package sd.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import sd.akka.actor.AlgoElection;
import sd.akka.actor.MessageElection;

public class Main {

    public static void main(String[] args) {

        // créer le système d'acteur
        ActorSystem actorSystem = ActorSystem.create("AlgorithmeElectionChangRoberts");

        // créer le premier acteur
        ActorRef premierProcess = actorSystem.actorOf(AlgoElection.props(1, 5), "processus1");

        // créer les autres acteurs
        for (int i = 2; i <= 5; i++) {
            actorSystem.actorOf(AlgoElection.props(i, 5), "processus" + i);
        }

        // envoie un message d'élection au premier processus (id = 1), lance l'election
        premierProcess.tell(new MessageElection(1), ActorRef.noSender());

        // Ajout d'un délai pour permettre aux acteurs de traiter les messages
        try {
            Thread.sleep(5000); // 5 secondes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Arrêt du système d'acteurs
        actorSystem.terminate();
    }

}
