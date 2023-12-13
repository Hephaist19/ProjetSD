package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;

//Algorithme d'élection de Chang et Roberts
public class AlgoElection extends AbstractActor {
    private final int idProcess;
    private final int totalProcess;

    private final ActorRef prochainProcess;

    private AlgoElection(int idProcess, ActorRef prochainProcess) {
        this.idProcess = idProcess;
        this.prochainProcess = prochainProcess;

        System.out.println("Création du processus " + idProcess);
    }

    // Méthode servant à la création d'un acteur
    static public Props props(int idProcess, ActorRef prochainProcess) {
        return Props.create(AlgoElection.class, () -> new AlgoElection(idProcess, prochainProcess));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MessageElection.class, this::election)
                .match(FinProcessus.class, this::finProcess)
                .matchAny(message -> {
                    System.out.println("Processus " + idProcess + " a reçu un message non traité : " + message);
                })
                .build();
    }

    private static int getId(String chemin) {
        // Extraction de l'identifiant à partir du chemin de l'acteur
        String[] chemin = path.chemin("/");
        String nomProcess = chemin[chemin.length - 1];
        return Integer.parseInt(nomProcess.substring(6)); // Supprimer "acteur" du nom
    }

    // Fonction d'élection
    private void election(MessageElection message) {
        
    }

    // Fonction qui va annoncer l'élu
    private void envoiMessageLeader(int idLeader) {

        // envoi le message d'élection aux autres processus
        for (int i = 1; i <= totalProcess; i++) {
            if (i != idLeader) {
                ActorRef process = getContext().actorOf(AlgoElection.props(i, totalProcess), "Processus : " + i);
                process.tell(new FinProcessus(), getSelf());
            }
        }
    }

    // fonction qui va terminer un processus
    private void finProcess(FinProcessus message) {
        // Fini le processus
        System.out.println("Le processus " + idProcess + " sest termine");
        getContext().stop(getSelf());
    }

}
