package pac.Counterpoint;

public class Note {
	
	private int code;
	private int rythme;
	
	public Note(){
		
		code = 60;
		rythme = 1;
		System.out.println("Creation d'une note par default.");
	}
	
	
	public Note( int c, int r){
		
		code = c;
		rythme = r;
		
	}
	
	public int getCode(){
		return code;
	}
	
	public int getRyhtme(){
		
		return rythme;
	}
	
	public boolean estDo(){
		
		return (code % 12 == 0);
		
	}
	public boolean estDo_diese(){
		
		return (code % 12 == 1);
		
	}
	public boolean estRe(){
		
		return (code % 12 == 2);
		
	}
	public boolean estRe_diese(){
		
		return (code % 12 == 3);
		
	}
	public boolean estMi(){
		
		return (code % 12 == 4);
		
	}
	public boolean estFa(){
		
		return (code % 12 == 5);
		
	}
	
	public boolean estFa_diese(){
		
		return (code % 12 == 6);
		
	}
	
	public boolean estSol(){
		
		return (code % 12 == 7);
		
	}
	
	public boolean estSol_diese(){
		
		return (code % 12 == 8);
		
	}
	
	public boolean estLa(){
		
		return (code % 12 == 9);
		
	}
	
	public boolean estLa_diese(){
		
		return (code % 12 == 10);
		
	}
	
	public boolean estSi(){
		
		return (code % 12 == 11);
		
	}

	public int intervalle(Note n){
		
		return Math.abs(code - n.getCode());
	}
	
	// On doit appeler la methode de la derniere note de la voix
	public boolean memeDirection(Note premiere,Note seconde){
		
		if ((code-seconde.getCode() > 0
			&& seconde.getCode() - premiere.getCode() > 0)
			|| 
			(code-seconde.getCode() < 0
			&& seconde.getCode() - premiere.getCode() < 0)){
			
			return true;
		}
		
		
		return false;
	}
	
	public boolean conjoint(Note n){
		
		if (this.intervalle(n) == 1 
			|| this.intervalle(n) == 2){
			
			return true;
		}
		
		return false;
	}
	
	public boolean estUnisson(Note n)
	{
		
		return intervalle(n) == 0;
	}
	
	public void afficher(){
		
		System.out.println(code);
		System.out.println(rythme);
		System.out.println();


	}
	
}
