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
		this.addBehaviour(new CyclicBehaviour(this){

			@Override
			public void action() {
				//Recevoir tous les nouveaux arrivants
				MessageTemplate mt = MessageTemplate.MatchConversationId("coucou");
				ACLMessage message = this.myAgent.receive(mt);
				if(message!=null){
					//TODO : completer les hashmap
				}else{
					block();
				}
			}
		});
		
		this.addBehaviour(new CyclicBehaviour(this){

			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchConversationId("nouvelle offre");
				ACLMessage message = this.myAgent.receive(mt);
				if(message!=null){
					
					Emploi e= new Emploi(message.getContent());
					((PoleEmploi)this.myAgent).listeEmplois.add(e);
					
					this.myAgent.addBehaviour(new Behaviour(){
						int step = 0;
						@Override
						public void action() {
							switch(step){
							case 0:
								break;
							case 1:
								break;
							case 2:
								break;
							default:
								break;
							}
							
						}

						@Override
						public boolean done() {
							// TODO Auto-generated method stub
							return false;
						}
						
						
					});
					
					
					
					
				}else{
					block();
				}
				
			}
			
			
			
		});

	}
	public void proposerEmploi(Emploi e){

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
			return;
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
		aclMessage.setConversationId("proposition-emploi");
		aclMessage.setReplyWith("pole emploi propose un emploi");
		this.send(aclMessage);
	}

}
