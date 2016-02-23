
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

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
		
		
		// Lancement d'un agent
		
		AgentController test2 = mc.createNewAgent("etat", Etat.class.getName(), new String[]{});
		AgentController poleEmploi = mc.createNewAgent("pole-emploi", PoleEmploi.class.getName(), new String[]{});
		
		AgentController test = mc.createNewAgent("travailleur", Individu.class.getName(), new Integer[]{0,20});
		
		poleEmploi.start();
		test.start();
		test2.start();
		
		
	}
	
	public void generateListOfJobs(){
		
		
	}

}
