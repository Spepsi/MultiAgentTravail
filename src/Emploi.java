
public class Emploi {
	
	private static int varTempsLibre=10;
	public static int incrementEmploi  = 0;
	public int id;
	private int qualification;
	private int tempsLibre;
	
	
	
	public int getTempsLibre(){
		return tempsLibre+((int)(varTempsLibre*(Math.random()-0.5)));
	}
	
	public Emploi(int qualification,int tempsLibre){
		this.qualification = qualification;
		this.tempsLibre = tempsLibre;
		this.id = incrementEmploi;
		incrementEmploi++;
		
	}
	
	public String toString(){
		String result = "";
		result+=qualification+":";
		result+=tempsLibre+":";
		result+=id+":";
		return result;
	}
	
	public Emploi(String toParse){
		String[] r = toParse.split(":");
		qualification = Integer.parseInt(r[0]);
		tempsLibre = Integer.parseInt(r[1]);
		id = Integer.parseInt(r[2]);
	}
	
	public int getQualification(){
		
		return qualification;
	}
	public int getId(){
		return id;
	}
}
