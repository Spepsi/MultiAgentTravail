import java.util.HashMap;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class PoleEmploi  extends Agent{


	//L'état lui envoie ses offres d'emplois lorsqu'il y en a

	public Vector<Emploi> listeEmplois;

	public HashMap<AID,Integer> qualificationParAID =  new HashMap<AID,Integer>();
	//Vrai si au chomage
	public HashMap<AID,Boolean> situation = new HashMap<AID,Boolean>(); 


	public void setup(){

		// Create the catalogue
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("pole-emploi");
		sd.setName("pole-emploi");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}


		// Regarder ses messages tous les 1000
		//Reception d'un nouvel utilisateur
		this.addBehaviour(new CyclicBehaviour(this){

			@Override
			public void action() {
				//Recevoir tous les nouveaux arrivants
				MessageTemplate mt = MessageTemplate.MatchConversationId("coucou");
				ACLMessage message = this.myAgent.receive(mt);
				if(message!=null){
					//TODO : completer les hashmap
					((PoleEmploi)this.myAgent).qualificationParAID.put(message.getSender(), Individu.qualifFromString(message.getContent()));
					((PoleEmploi)this.myAgent).situation.put(message.getSender(), true);
				}else{
					block();
				}
			}
		});
		// Réception d'un nouvel emploi
		this.addBehaviour(new CyclicBehaviour(this){
			@Override
			public void action() {
				//Reception de la part de l'état d'une nouvelle offre
				MessageTemplate mt = MessageTemplate.MatchConversationId("nouvelle offre");
				ACLMessage message = this.myAgent.receive(mt);
				if(message!=null){
					Emploi e= new Emploi(message.getContent());
					((PoleEmploi)this.myAgent).listeEmplois.add(e);
					this.myAgent.addBehaviour(new BehaviourPropositionEmploi(this.myAgent,e));	
				}else{
					block();
				}
			}
		});

	}
	public AID  proposerEmploi(Emploi e){

		Vector<AID> toSend = new Vector<AID>();
		Vector<AID> finalToSend = new Vector<AID>();
		// Avoir les AID correspondant au niveau de qualification
		for(AID i : qualificationParAID.keySet()){
			if(qualificationParAID.get(i).intValue()==e.getQualification()){
				toSend.add(i);
			}
		}
		// On récupère ceux qui sont au chomage
		for(AID i : toSend){
			if(situation.get(i)){
				finalToSend.addElement(i);
			}
		}
		//On trie le vecteur aléatoirement 
		int taille = finalToSend.size();
		int selection = 0;
		if(taille==0){
			return null;
		}else{
			selection = (int)(Math.random()*taille);
		}
		// Envoyer message seulement aux personnes étant au chomage
		//			DFAgentDescription template = new DFAgentDescription();
		//			ServiceDescription sd = new ServiceDescription();
		//			sd.setType("individu");
		//			template.addServices(sd);
		//			DFAgentDescription[] ser = jade.domain.DFService.search(this, template);
		ACLMessage aclMessage = new ACLMessage(ACLMessage.CFP);
		aclMessage.addReceiver(finalToSend.get(selection));
		aclMessage.setContent(e.toString());
		aclMessage.setConversationId("proposition emploi");
		aclMessage.setReplyWith("pole emploi propose un emploi");
		this.send(aclMessage);
		return finalToSend.get(selection);
	}

	public class BehaviourPropositionEmploi extends Behaviour{
		int step = 0;
		Emploi e ;
		AID receveur;
		public BehaviourPropositionEmploi(Agent a, Emploi e){
			this.myAgent = a;
			this.e = e;
		}

		@Override
		public void action() {
			switch(step){
			case 0:
				// Etape 1 proposer emploi à une personne
				this.receveur = ((PoleEmploi)this.myAgent).proposerEmploi(e);
				step++;
				break;
			case 1:
				// Attendre réponse
				MessageTemplate mt = MessageTemplate.MatchConversationId("reponse emploi");
				ACLMessage message = this.myAgent.receive(mt);
				if(message!=null && this.receveur.getName().equals(message.getSender().getName()) ){
					String msg = message.getContent();
					AID aid = message.getSender();
					if(msg.charAt(0)=='1'){
						//Emploi pris
						((PoleEmploi)this.myAgent).listeEmplois.remove(e);
						((PoleEmploi)this.myAgent).situation.put(aid, false);
						//Dire à l'état que l'emploi est pourvu
						ACLMessage aclMessage = new ACLMessage(ACLMessage.CFP);

						//Chercher l'état
						DFAgentDescription template = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						sd.setType("etat");
						template.addServices(sd);
						DFAgentDescription[] ser;
						try {
							ser = jade.domain.DFService.search(this.myAgent, template);
							for(DFAgentDescription s : ser){
								aclMessage.addReceiver(s.getName());
								aclMessage.setContent(e.toString());
								aclMessage.setConversationId("confirmation emploi");
								aclMessage.setReplyWith("pole emploi propose un emploi");
								this.myAgent.send(aclMessage);
							}
						} catch (FIPAException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						step++;
					}else{
						block();
					}
				}
				break;
			case 2:
				// Assignait l'emploi
				step++;
				break;
			default:
				break;
			}

		}

		@Override
		public boolean done() {

			return step>=3;
		}
	}	

}
