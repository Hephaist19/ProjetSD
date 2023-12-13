package sd.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import sd.akka.actor.AlgoElection;
import sd.akka.actor.MessageElection;

public class Main {

    public static void main(String[] args) {

        // créer le système d'acteur
        ActorSystem actorSystem = ActorSystem.create("AlgorithmeElectionChangRoberts");

        int numProcess = 5;

        //création d'un tableau de numActeur qui formera l'anneau
        ActorRef[] process = new ActorRef[numProcess];

        // créer les autres acteurs
        for (int i = 0; i <= numProcess ; i++) {
            int indexProchainProcess = (i+1) % numProcess;
            process[i] = actorSystem.actorOf(AlgoElection.props(i + 1, process[indexProchainProcess]), "processus" + (i+1));
        
            //envoie message initial
            System.out.println("Id du process " + i + " : " + getIdFromPath(process[i].path().toString()););
        
        }  

        process[0].tell("Je suis le process 1", ActorRef.noSender());

        int idProcess0 = getIdFromPath(process[0].path().toString());

        // Le premier process envoi un message d'élection au deuxieme processus, lance l'election
        process[0].tell(new MessageElection(process[0].getIdProcess()), ActorRef.noSender());

        // Ajout d'un délai pour permettre aux acteurs de traiter les messages
        try {
            Thread.sleep(1000); // 1 secondes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Arrêt du système d'acteurs
        actorSystem.terminate();
    }

}
