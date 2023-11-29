package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;

//Algorithme d'élection de Chang et Roberts
public class AlgoElection extends AbstractActor {
        private ActorRef suivant;
    private ChaineActeur(int nb){

        if (nb!=0){
            nb--;
            //création d'acteurs enfant
            this.suivant = getContext().actorOf(ChaineActeur.props(nb));
        }
        else{
            System.out.println("A pu place !");
        }
    }

    @Override
	public Receive createReceive() {
        return receiveBuilder()
            .match(CreateActor.class, message -> createActor(message))
            .build();
	}
    
    private void createActor(final CreateActor message){
        System.out.println("Creation chaine acteur");
    }

    // Méthode servant à la création d'un acteur
    public static Props props (int nb){
        return Props.create(ChaineActeur.class, nb);
    }

    // Définition des messages en inner classes
	public interface Message {}

    public static class CreateActor implements Message {
		public CreateActor() {}
	}	
}
