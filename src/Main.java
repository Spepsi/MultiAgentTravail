
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {

	public static void main(String[] args){
		System.out.println("Test JADE");
		try {
			methode();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	static void methode() throws StaleProxyException{
		
		// Runtime : 
		
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		
		// Lancement de la plateforme : 
		Profile pMain = new ProfileImpl("localhost",8888,null);
		AgentContainer mc = rt.createMainContainer(pMain);
		
		
		// Lancement d'un agent
		AgentController test = mc.createNewAgent("acheteur", Individu.class.getName(), new String[]{"Oui oui marche sur la lune"});
		AgentController test2 = mc.createNewAgent("vendeur", Etat.class.getName(), new String[]{"Oui oui marche sur la lune"});
		
		test.start();
		test2.start();
		
	}
	
	public void generateListOfJobs(){
		
		
	}

}
