package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.actor.ActorRef;

//Algorithme d'élection de Chang et Roberts
public class AlgoElection extends AbstractActor {
    private ActorRef prochainProcess;

    private final int monNumero;
    private boolean participant = false;
    private int coordinateur = -1; // Initialisé à une valeur non valide

    private boolean aVoisin = false;
    private boolean electionDemarree = false;

    private boolean peutEnvoyer = false; // permetd'avoir un affichae plus cohérent malgré le système asynchrone

    private AlgoElection(int idProcess) {
        this.monNumero = idProcess;
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

    public class MessageElection {

        public final int candidatID;

        public MessageElection(int candidatID) {
            this.candidatID = candidatID;
        }
    }

    public class MessageElu {
        final int gagnantId;

        public MessageElu(int gagnantId) {
            this.gagnantId = gagnantId;
        }
    }

    public static class DemarrerElection {
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(CreationAnneau.class, this::creationAnneau)
                .match(DemarrerElection.class, this::demarrerElection)
                .match(MessageElection.class, this::election)
                .match(MessageElu.class, this::elu)
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
        if (!electionDemarree && !peutEnvoyer) {
            // Envoie l'id du process à la sortie
            System.out.println("Id du processus " + monNumero + " : " + recupId());
            afficherInfoAnneau();
            prochainProcess.tell(new MessageElection(monNumero), getSelf());
            electionDemarree = true;
            peutEnvoyer = true;
        }
    }

    // Fonction d'élection
    private void election(MessageElection message) {
        if (message.candidatID > monNumero) {
            prochainProcess.tell(new MessageElection(message.candidatID), getSelf());
            participant = true;
            System.out.println(
                    getSelf().path().name() + " : " + monNumero + ". Recoit " + message.candidatID
                            + ". Son Id est inferieur a celui recu. Il transmet alors lId: "
                            + message.candidatID);
        } else if (message.candidatID < monNumero && !participant) {
            prochainProcess.tell(new MessageElection(monNumero), getSelf());
            participant = true;
            System.out.println(
                    getSelf().path().name() + " : " + monNumero + ". Recoit " + message.candidatID
                            + ". Son Id est superieur a celui recu. Il transmet alors son Id: "
                            + monNumero);
        } else if (message.candidatID == monNumero) {
            prochainProcess.tell(new MessageElu(monNumero), getSelf());
            System.out.println(
                    getSelf().path().name() + " : " + monNumero + ". A recu : " + message.candidatID
                            + ". Qui est son propre Id." + getSelf().path().name() + " est donc elu.");

            peutEnvoyer = false;
        }
    }

    private void elu(MessageElu message) {
        coordinateur = message.gagnantId;
        participant = false;

        // fait passer le message d'élection si l'Id reçu est différent de celui courant
        if (message.gagnantId != monNumero) {
            prochainProcess.tell(new MessageElu(message.gagnantId), getSelf());
            System.out.println(getSelf().path().name() + " : " + monNumero
                    + ". A recu le message que l'acteur' "
                    + message.gagnantId
                    + ", est elu et le transmet");
            // arrete le processus
            getContext().stop(getSelf());
        } else {
            // arrete le processus lorsqu'il est élu
            System.out.println(getSelf().path().name() + " : " + message.gagnantId
                    + ", est elu. L'election est fini.");
            getContext().stop(getSelf());
        }
    }

    // affiche les info des processus de l'anneau
    private void afficherInfoAnneau() {
        // Affiche l'id du processus et l'id du prochain processus, son voisin
        System.out.println(getSelf().path().name() + " : " + monNumero + ", a pour voisin "
                + (prochainProcess != null ? prochainProcess.path().name() : " aucun processus"));
    }

    private int recupId() {
        return monNumero;
    }
}
