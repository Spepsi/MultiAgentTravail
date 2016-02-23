
public class Emploi {

	private static int varTempsLibre=10;
	private int qualification;
	private int tempsLibre;
	
	
	
	public int getTempsLibre(){
		return tempsLibre+((int)(varTempsLibre*(Math.random()-0.5)));
	}
	

	public String toString(){
		String result = "";
		result+=qualification+":";
		result+=tempsLibre;
		return result;
	}
	
	public Emploi(String toParse){
		String[] r = toParse.split(":");
		qualification = Integer.parseInt(r[0]);
		tempsLibre = Integer.parseInt(r[1]);
		
	}
	
	public int getQualification(){
		
		return qualification;
	}
}
