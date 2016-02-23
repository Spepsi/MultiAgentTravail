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


import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;

public class Etat extends Agent {
	// The catalogue of books for sale (maps the title of a book to its price)
	private int[] offresAPourvoir = new int[3];
	private int[] offresPourvues = new int[3];
	private int[] totalTravail = new int[3];
	public static int revenuMinimum=300;
	public static int[] revenuesQualif = {500,1000,1500};
	
	public void pourvoir(int qualif){
		if(qualif>2 ||qualif<0){
			return;
		}
		if(offresAPourvoir[qualif]>0){
			offresAPourvoir[qualif]--;
			offresPourvues[qualif]++;
		}
	}
	
	public void demission(int qualif){
		if(qualif>2 ||qualif<0){
			return;
		}
		if(offresPourvues[qualif]>0){
			offresAPourvoir[qualif]++;
			offresPourvues[qualif]--;
		}
	}
	// Put agent initializations here
	protected void setup() {
		// Create the catalogue
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("etat");
		sd.setName("etat");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		this.addBehaviour(new TickerBehaviour(this,1000){

			@Override
			protected void onTick() {
				if(Math.random()>0.5){
					//CREER UN EMPLOI
					int qualif = (int)(3*Math.random());
					((Etat)this.myAgent).offresAPourvoir[qualif]++;
					// Envoyer l'emploi à pole emploi
					//TODO : Dire à pole emploi que j'existe
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("pole-emploi");
					template.addServices(sd);
					DFAgentDescription[] ser;
					try {
						ser = jade.domain.DFService.search(this.myAgent, template);
						ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
						aclMessage.addReceiver(ser[0].getName());
						aclMessage.setContent(new Emploi(qualif,100-(qualif+1)*30).toString());
						aclMessage.setConversationId("nouvelle offre");
						this.myAgent.send(aclMessage);
						
					} catch (FIPAException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		});
		
		this.addBehaviour(new CyclicBehaviour(this){
			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchConversationId("confirmation emploi");
				ACLMessage message = this.myAgent.receive(mt);
				if(message!=null){
					((Etat)this.myAgent).pourvoir(new Emploi(message.getContent()).getQualification());
				}else{
					block();
				}
				
			}
		});

	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Printout a dismissal message
		System.out.println("Etat"+getAID().getName()+" terminating.");
	}




}
