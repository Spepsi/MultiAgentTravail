/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Individu extends Agent {

	public static int multiplicateurSalaire = 500;
	public static int multiplicateurTempsLibre = 10;
	private static int NombreDeMoisRefuse=3;
	private static int resistanceBurnout = 3;
	private static float tauxReevaluation = 0.80f;
	// The list of known seller agents
	private AID[] sellerAgents;
	//Characs
	private boolean chomage;
	private int niveauQualif;
	private int besoinRevenu;
	private int besoinTempsLibre;
	private int age;
	//Nombre de mois consécutifs en travaillant trop
	private int burnoutRate;
	//Estime de soi : baisse son salaire au bout de n_mois
	private int estimeDeSoi;
	private Emploi emploi;

	// Put agent initializations here
	protected void setup() {
		// Printout a welcome message

		// Create the catalogue
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType("individu");
		sd1.setName("individu");
		dfd.addServices(sd1);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		//TODO : Dire à pole emploi que j'existe
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("pole-emploi");
		template.addServices(sd);
		DFAgentDescription[] ser;
		try {
			ser = jade.domain.DFService.search(this, template);
			ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
			aclMessage.addReceiver(ser[0].getName());
			aclMessage.setContent(this.toString());
			aclMessage.setConversationId("coucou");
			this.send(aclMessage);

		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		chomage = true;
		// Parametrize agent
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			niveauQualif = (int) args[0];
			besoinRevenu = (int) ((niveauQualif+1)*(multiplicateurSalaire*2*(0.5-Math.random())));
			besoinTempsLibre = (int) (multiplicateurTempsLibre*2*(0.5-Math.random()));
			age = (int) args[1]; 


			// Add a TickerBehaviour that look to job offer and accept or refuse
			addBehaviour(new CyclicBehaviour(this) {

				@Override
				public void action() {
					MessageTemplate mt = MessageTemplate.MatchConversationId("proposition emploi");
					ACLMessage message = this.myAgent.receive(mt);
					if(message!=null){
						Emploi e= new Emploi(message.getContent());

						DFAgentDescription template = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						sd.setType("pole-emploi");
						template.addServices(sd);
						DFAgentDescription[] ser;
						try {
							ser = jade.domain.DFService.search(this.myAgent, template);
							ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
							aclMessage.addReceiver(ser[0].getName());
							if((e.getQualification()+1)*1000>=besoinRevenu){
							aclMessage.setContent("1");
							}else{
								aclMessage.setContent("0");
							}
							aclMessage.setConversationId("reponse emploi");
							this.myAgent.send(aclMessage);

						} catch (FIPAException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}

					}else{
						block();
					}

				}

			} );
			// Add a TickerBehaviour that query free time every month
			addBehaviour(new TickerBehaviour(this, 30000) {
				protected void onTick() {

					if(emploi!=null && emploi.getTempsLibre()<besoinTempsLibre){
						burnoutRate++;

						if(burnoutRate>resistanceBurnout){
							//TODO : Demissionner
						}
					}

				}
			} );
		}
		else {
			// Make the agent terminate
			System.out.println("No target book title specified");
			doDelete();
		}
	}
	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent "+getAID().getName()+" terminating.");

		//TODO : quand un individu meurt il le signifie à pole emploi et à l'état
	}
	/**
	   Inner class RequestPerformer.
	   This is the behaviour used by Book-buyer agents to request seller 
	   agents the target book.
	 */

	public String toString(){
		String result = "";
		result+=getAID().getName()+"okay";
		result+=niveauQualif+"okay";	
		return result;
	}
	public static int qualifFromString(String s){
		System.out.println("okay: "+ s);
		return Integer.parseInt(s.split("okay")[1]);
	}
}
