
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.Random;

public class Main {

	public static void main(String[] args){
		System.out.println("Initialisation");
		try {
			methode();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("En cours d'execution");
	}
	
	static void methode() throws StaleProxyException{
		
		// Runtime : 
		
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		
		// Lancement de la plateforme : 
		Profile pMain = new ProfileImpl("localhost",8888,null);
		AgentContainer mc = rt.createMainContainer(pMain);

		AgentController poleEmploi = mc.createNewAgent("pole-emploi", PoleEmploi.class.getName(), new String[]{});
        poleEmploi.start();

        final int individuNumber = 10;
        final int ageMax = 60;
        final int ageMin = 20;
        final Random random = new Random();
		for(int i = 0 ; i < individuNumber ; i++) {
            final int qualification = random.nextInt(3);
            final int age = random.nextInt((ageMax - ageMin) + 1) + ageMin;
            AgentController individu = mc.createNewAgent(
                    "travailleur"+i,
                    Individu.class.getName(),
                    new Integer[]{qualification, age});
            individu.start();
        }

        AgentController etat = mc.createNewAgent("etat", Etat.class.getName(), new String[]{});
		etat.start();
	}


	
	public void generateListOfJobs(){
		
		
	}

}
