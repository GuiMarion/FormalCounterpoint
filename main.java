package pac.Counterpoint;

import java.io.File;

public class main {

	public static void main(String[] args)
	{
		/*
	// Main pour une unique partition
	  	long deb = System.nanoTime();
		Partition P = new Partition(1); // 0 --> Mode chromatique, 1 --> Mode Majeur, 2 --> Mode Mineur Melodique
		P.resoudre();
		long end = System.nanoTime();
		P.ecritureMidi();
		System.out.println("Done in " +  (double)(end-deb)/(float)1000000000 + "s."); 

		 */

		Voix Cantus = new Voix(true,true,1,8,36);
		int [] monTableau = {60,57,55,52,53,57,55,52,50,48};
		int [] monTableau2 = {48,47,45,48,50,53,52,50,48};
		int [] monTableau3 = {48,55,57,53,55,52,53,50,48};


		Cantus.getNotes().removeAllElements();
		for (int i = 0; i<monTableau2.length;i++){

			Cantus.getNotes().add(new Note(monTableau2[i],8));
		}

		//String s = "/Users/Guilhem/Documents/Exemple1";
		String s = "./Exemple4";
		File fb = new File(s); 
		if (!fb.isDirectory()){
			fb.mkdirs();}
		//Partition P = new Partition(1);
		Partition P = new Partition (Cantus,new Voix(false,false,1,2,36), 120, 36 ); // 0 --> Mode chromatique, 1 --> Mode Majeur, 2 --> Mode Mineur Melodique

		while (true){


			P.resoudre();
			
			/*
		if (P.conjoint +P.contraire == Maximiser){
			P.ecritureMidi2(s +"/"+ Integer.toString(k));
			System.out.println("OK");
			k++;
		}
			 */

			
			if (P.conjoint +P.contraire >= 0
					&& P.croches < 5){
				//s = "/Users/Guilhem/Documents/Dossier_test";
				s = "./Exemple4";
				s +="/" + Integer.toString((P.conjoint +P.contraire));
				//Maximiser = P.conjoint +P.contraire;
				File fc = new File(s); 
				if (!fc.isDirectory()){
					fc.mkdirs();}
				//File[] f = fc.listFiles();
				P.ecritureMidi2(s +"/");

			}


		}





	}



}
