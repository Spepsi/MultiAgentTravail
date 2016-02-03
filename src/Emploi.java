
public class Emploi {

	private static int varTempsLibre=10;
	private int qualificationRequise;
	private int revenu;
	private int tempsLibre;
	
	
	
	public int getTempsLibre(){
		return tempsLibre+((int)(varTempsLibre*(Math.random()-0.5)));
	}
	
	
}
