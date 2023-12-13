package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.actor.ActorRef;

//Algorithme d'élection de Chang et Roberts
public class AlgoElection extends AbstractActor {
    private final int idProcess;
    private ActorRef prochainProcess;

    private boolean aVoisin = false;
    private boolean electionDemarree = false;

    private AlgoElection(int idProcess) {
        this.idProcess = idProcess;
    }

    // Méthode servant à la création d'un acteur
    static public Props props(int idProcess) {
        return Props.create(AlgoElection.class, () -> new AlgoElection(idProcess));
    }

    // Messages pris en charge par l'acteur
    public static class CreationAnneau {
        final ActorRef voisin;

        public CreationAnneau(ActorRef voisin) {
            this.voisin = voisin;
        }
    }

    public static class DemarrerElection {
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(CreationAnneau.class, this::creationAnneau)
                .match(DemarrerElection.class, this::demarrerElection)
                .build();
    }

    // Défini le voisin de l'acteur
    public void creationAnneau(CreationAnneau message) {
        if (!aVoisin) {
            this.prochainProcess = message.voisin;
            aVoisin = true;
        }

    }

    // démarre le passage du jeton dans l'anneau, début de l'élection
    private void demarrerElection(DemarrerElection message) {
        if (!electionDemarree) {
            // Envoie l'id du process à la sortie
            System.out.println("Id du processus " + idProcess + " : " + recupId());
            afficherInfoAnneau();
            prochainProcess.tell(message, getSelf());
            electionDemarree = true;
        }

    }

    // Fonction d'élection
    private void election(MessageElection message) {

    }

    // affiche les info des processus de l'anneau
    private void afficherInfoAnneau() {
        // Affiche l'id du processus et l'id du prochain processus, son voisin
        System.out.println("Processus " + idProcess + " a pour voisin le processus "
                + (prochainProcess != null ? prochainProcess.path().name() : "aucun processus"));
    }

    private int recupId() {
        return idProcess;
    }
}
