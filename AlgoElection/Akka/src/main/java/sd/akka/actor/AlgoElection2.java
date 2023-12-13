/*package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;

//Algorithme d'élection de Chang et Roberts
public class AlgoElection2 extends AbstractActor {
    private final int idProcess;
    private final int totalProcess;

    private ActorRef prochainProcess;

    private AlgoElection2(int idProcess, int totalProcess) {
        this.idProcess = idProcess;
        this.totalProcess = totalProcess;

        System.out.println("Création du processus " + idProcess);
    }

    // Méthode servant à la création d'un acteur
    static public Props props(int idProcess, int totalProcess) {
        return Props.create(AlgoElection.class, () -> new AlgoElection(idProcess, totalProcess));
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

    // Fonction d'élection
    private void election(MessageElection message) {
        int receivedId = message.envoiID;

        System.out.println("Nouvelle election");

        // Comparaison avec l'ID du processus courant
        if (receivedId > idProcess) {
            // Si l'ID reçu est plus grand, transmettre le message à l'acteur suivant dans
            // l'anneau

            System.out.println(
            "Processus " + idProcess + " transmet l'élection à Processus " + prochainProcess.path().name());
            
            // Calcule l'identifiant du prochain processus dans l'anneau d'élection
            int idProchainProcess = (idProcess % totalProcess) + 1;

            //donner au prochain process l'id obtenu, que le prochain process correspond a celui qu'on veut

            prochainProcess = getContext().actorOf(AlgoElection.props(idProchainProcess, totalProcess),
                    "processus" + idProchainProcess);

            prochainProcess.tell(new MessageElection(idProcess), getSelf());

        } else if (receivedId < idProcess) {
            // Si l'ID reçu est plus petit, devenir le leader temporaire
            // processus
            System.out.println("Processus " + idProcess + " devient le leader temporaire avec l'ID " + idProcess);

            // Continuer l'election
            System.out.println("Continue election");

            //si on est sur le dernier process
            if (idProcess == totalProcess){
                
            }
            else{
                int idProchainProcess = (idProcess % totalProcess) + 1;
                prochainProcess = getContext().actorOf(AlgoElection.props(idProchainProcess, totalProcess),
                    "processus" + idProchainProcess);
                prochainProcess.tell(new MessageElection(idProcess), getSelf());
            }
            
        } else {
            // Si les ID sont égaux cela signifi qu'un acteur a reçu son propre ID et qu'il
            // a donc l'ID le plus grand de la boucle
            System.out.println("Processus " + idProcess
                    + " a reçu un message d'élection avec le même ID. Le processus " + idProcess + " est le leader");

            // envoi un message pour arreter tout les processus et finir l'election
            envoiMessageLeader(idProcess);
        }
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
*/