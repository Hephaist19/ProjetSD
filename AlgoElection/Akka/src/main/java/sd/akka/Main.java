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
        /* test mel */
        int[] dejaTire = new int[numProcess]; // tableau des chiffres déjà tiré
        /* fin test mel */

        // création d'un tableau de numActeur qui formera l'anneau
        ActorRef[] process = new ActorRef[numProcess];

        /* Test mel */
        // Liste des 5 acteurs désirant participer
        String[] noms = { "Lion", "Leopard", "Jaguar", "Panthere", "Serpent" };
        /* Fin test mel */

        // créer les autres acteurs
        for (int i = 0; i < numProcess; i++) {
            /* test mel */
            boolean tire; // boolean pour l'attribution des noms aux ids
            int alea; // nombre aléatoire pour déterminer le nom correspond à l'id

            do {
                alea = ThreadLocalRandom.current().nextInt(numProcess);
                tire = true;
                System.out.print("On a tire " + alea + ". ");

                for (int k = 0; k < i; k++) { // on vérifie dans le tableau dejaTire
                    if (alea == dejaTire[k]) {
                        tire = false;
                        System.out.println("Deja tire, on re-tire");
                        break;
                    }
                }
            } while (!tire); // on vérifie qu'on ne l'a pas déjà tiré
            // Si le chiffre tiré est déjà présent dans le tableau alors on retire à nouveau
            // jusqu'à avoir un chiffre non tiré

            dejaTire[i] = alea;
            System.out.println("On met " + noms[alea]);
            /* fin test mel */

            // int indexProchainProcess = (i+1) % numProcess;
            // process[i] = actorSystem.actorOf(AlgoElection.props(i + 1), "processus" + (i
            // + 1));
            process[i] = actorSystem.actorOf(AlgoElection.props(i + 1), noms[alea]);
        }

        for (int i = 0; i < numProcess; i++) {
            System.out.println("Acteur " + (i + 1) + " : " + process[i].path().name());
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