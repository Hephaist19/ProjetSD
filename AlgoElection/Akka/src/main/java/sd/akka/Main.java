package sd.akka;

import java.util.concurrent.ThreadLocalRandom;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import sd.akka.actor.AlgoElection;

public class Main {

    public static void main(String[] args) {

        // créer le système d'acteur
        ActorSystem actorSystem = ActorSystem.create("AlgorithmeElectionChangRoberts");

        int numProcess = 5;

        // création d'un tableau de numActeur qui formera l'anneau
        ActorRef[] process = new ActorRef[numProcess];

        // créer les autres acteurs
        for (int i = 0; i < numProcess; i++) {
            // int indexProchainProcess = (i+1) % numProcess;
            process[i] = actorSystem.actorOf(AlgoElection.props(i + 1), "processus" + (i + 1));
        }

        process[0].tell("Je suis le process 1", ActorRef.noSender());

        // Choix aléatoire du process qui commence
        int indicePremierProcess = ThreadLocalRandom.current().nextInt(numProcess);

        // définir les voisins de l'anneau
        for (int i = 0; i < numProcess; i++) {
            ActorRef processCourant = process[i];
            int idVoisin = (i + 1) % numProcess;
            ActorRef voisin = process[idVoisin];

            processCourant.tell(new AlgoElection.CreationAnneau(voisin), ActorRef.noSender());

        }

        // Le premier process envoi un message d'élection au deuxieme processus, lance
        // l'election
        process[indicePremierProcess].tell(new AlgoElection.DemarrerElection(), ActorRef.noSender());

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
