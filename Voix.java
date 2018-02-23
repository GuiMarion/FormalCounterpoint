package pac.Counterpoint;

import java.util.Vector;

public class Voix {

	private java.util.Vector<Note> notes;

	int test;
	
	int taille;
	java.util.Vector <Note> p = new java.util.Vector <Note> ();
	
	java.util.Vector <Note> n = new java.util.Vector <Note> ();
	
	java.util.Vector<Note> n2 = new java.util.Vector<Note>();


	private boolean cantus; // Vrai si la voix est le cantus Firmus

	private boolean basse; // Vrai si la voix est une basse, on change donc la teissiture dans creerListeNote

	private int mode; // Donne le mode utilisŽ, change le mode dans creerListeNote
	// 0 --> Mode chromatique, 1 --> Mode Majeur, 2 --> Mode Mineur Melodique

	private int rythme ; // Donne le rythme de la voix, elle depend de l'espece du contrepoint


	public Voix(boolean c, boolean b, int m, int ry, int t) {

		notes = new java.util.Vector <Note> ();
		rythme = ry;

		if (!b){notes.add(new Note(72,4));notes.add(new Note(72,4));}
		else{notes.add(new Note(48,rythme));}
		basse = b;
		cantus = c;
		mode = m;
		test = 0;
		taille = t;


	}

	public boolean estBasse(){

		return basse;
	}

	public int rythme(){

		return rythme;
	}

	public int mode(){

		return mode;
	}
	
	public boolean lastNote(java.util.Vector <Note> n,Note note){
		if (nbTemps() == taille - 4){
			for (int i = 0; i<n.size(); i++){
				if (n.elementAt(i).getCode() == note.getCode()){

					return true;
				}

			}

		}
		return false;
	}

	public boolean ajouterNote(Voix v, int pos){

		java.util.Vector <Note> n = appRegle(creerListeNote(),v, pos);
		//if (estConjoint(n,pos)){notes.add(Conjoint(n,pos));return true;}
		int nombreAleatoire =(int)(Math.random() *n.size());
		if (nbTemps() == taille - 4) {
			if (lastNote(n,new Note(72,8))){
				notes.add(new Note(72,8));
				return true;}
			else {return false;}

		}

		if (n.size() != 0){
			notes.add(n.elementAt(nombreAleatoire));
			return true;}
		//System.out.println("Liste vide.");
		test++;
		return false;


	}

	public int noteMin(){

		int min = 250;

		for (Note note : notes){

			if (note.getCode()<min) {
				min = note.getCode();
			}

		}


		return min;
	}

	public int noteMax(){

		int max = 0;

		for (Note note : notes){

			if (note.getCode()>max) {
				max = note.getCode();
			}

		}


		return max;
	}

	// A construire avec regles compositionnelles
	public java.util.Vector<Note> appRegle(java.util.Vector<Note> n, Voix v, int pos){

		// Regle 32_1 : Organisation de la melodie 
		//				On evite les mouvements disjoint ˆ intervalle regulier

		if (repetitionDisjoint(notes,pos)){

			for (int i = 0; i<n.size(); i++ ){

				if ( !n.elementAt(i).conjoint(notes.elementAt(pos-1))){

					n.remove(n.elementAt(i));i--;}		
			}
		}


		// Regles Harmmoniques, ˆ ne pas utiliser si v est vide.
		else if (v.getNotes().size() > 2 && barreMesure())

		{

			// Regle n¡7 : Intervalles harmoniques
			for (int i = 0; i<n.size(); i++ ){

				Note note = n.elementAt(i);
				Vector<Note> vnotes = v.getNotes();
				int posHarmonie = v.harmonie(nbTemps());


				if (note.intervalle(v.getNotes().elementAt(posHarmonie)) % 12 != 0 && note.intervalle(v.getNotes().elementAt(posHarmonie)) % 12 !=7 &&
						note.intervalle(v.getNotes().elementAt(posHarmonie)) % 12 !=3 && note.intervalle(v.getNotes().elementAt(posHarmonie)) % 12 != 4 &&
						note.intervalle(v.getNotes().elementAt(posHarmonie)) % 12 !=8 && note.intervalle(v.getNotes().elementAt(posHarmonie)) % 12 !=9){
					n.remove(note);i--;
				}

				//Regle n¡49 : Distances entre parties voisines
				else if (note.intervalle(vnotes.elementAt(posHarmonie)) >= 24
						&& barreMesure()){

					n.remove(note);i--;
				}

				// Regle n¡44 : Quinte et octaves directes interdites 
				else if (notes.size() >=1
						&& Direct(vnotes.elementAt(posHarmonie-1),vnotes.elementAt(posHarmonie),notes.elementAt(pos-1),note)
						&& (vnotes.elementAt(posHarmonie).intervalle(note) % 12 == 6
						|| vnotes.elementAt(posHarmonie).intervalle(note) % 12 == 7
						|| vnotes.elementAt(posHarmonie).intervalle(note) % 12 == 8
						|| vnotes.elementAt(posHarmonie).intervalle(note) % 12 == 0)){

					n.remove(note);i--;
				}


				//Regle n¡46
				// Secondes consecutives interdites
				else if (vnotes.size()>1 && notes.size()>1 &&
						((vnotes.elementAt(posHarmonie-1).intervalle(notes.elementAt(pos-1)) %12 ==1
						|| vnotes.elementAt(posHarmonie-1).intervalle(notes.elementAt(pos-1)) %12 ==2)
						&& (vnotes.elementAt(posHarmonie).intervalle(note) %12 ==1
						|| vnotes.elementAt(posHarmonie).intervalle(note) %12 ==2))){

					n.remove(note);i--;
				}

				// Regle n¡47 : Seconde, septieme, neuvieme directes

				//47_A : secondes directes interdites
				else if (notes.size() >= 1
						&& Direct(vnotes.elementAt(posHarmonie-1),vnotes.elementAt(posHarmonie),notes.elementAt(pos-1),note)
						&&(vnotes.elementAt(posHarmonie).intervalle(note) %12 == 1
						|| vnotes.elementAt(posHarmonie).intervalle(note) %12 == 2)){

					n.remove(note);i--;

				}



				//Regle n¡53 A et B : Unisson
				else if (notes.size() >= 1
						&& note.intervalle(vnotes.elementAt(posHarmonie))%12 == 0
						&& 
						((barreMesure()
								&& nbTemps() >= 4
								&& nbTemps() < v.nbTemps() -4)
								|| vnotes.elementAt(posHarmonie -1).intervalle(notes.elementAt(pos-1)) %12 == 1)){

					n.remove(note);i--;
				}

				//Regle n¡53_C : On quite un unisson par seconde majeure ou mineure

				else if (notes.size() >= 1
						&& vnotes.elementAt(posHarmonie -1).intervalle(notes.elementAt(pos-1)) %12 == 0
						&& note.intervalle(vnotes.elementAt(posHarmonie))%12 != 1
						&& note.intervalle(vnotes.elementAt(posHarmonie))%12 == 2){

					n.remove(note);i--;


				}


			}


		}
		//Regles rythmiques 

		if (!basse){
			int posHarmonie = v.harmonie(nbTemps());
			int add = v.harmonie(nbTemps());

			for (int i = 0; i<n.size(); i++ ){		

				Note note = n.elementAt(i);
				//Regles rythmiques 

				//La blanche syncoopŽe
				if (note.getRyhtme() == 8
						&& 
						(nbTemps()%4 != 2
						|| nbTemps() >= taille -6 )){

					n.remove(note);i--;
				}

				// il est interdit d'utiliser noire blanche
				else if(
						note.getRyhtme() == 4
						&& notes.size() >= 1
						&& nbTemps()%4 != 0
						&& notes.elementAt(pos-1).getRyhtme() == 2){
					n.remove(note); i--;
				}

				// Il est interdit d'ecrire une noire syncopŽe
				else if (note.getRyhtme() == 4
						&& nbTemps() % 4 == 3){

					n.remove(note); i--;
				}


				//La blanche pointŽe est decoupŽe par la barre de mesure au deux tiers ˆ gauche
				else if (note.getRyhtme() == 6
						&& (nbTemps()%4 != 2
						|| nbTemps() >= taille -6 )){

					n.remove(note);i--;
				}

				
				// Utilisation de la consonnance harmonique pour la blanche syncopŽe
				else if (note.getRyhtme() == 8 
						&& (note.intervalle(v.getNotes().elementAt(add)) % 12 !=3 && note.intervalle(v.getNotes().elementAt(+add)) % 12 != 4 
						&& note.intervalle(v.getNotes().elementAt(+add)) % 12 !=8 && note.intervalle(v.getNotes().elementAt(+add)) % 12 !=9)){
					n.remove(note);i--;
				}

				// Utilisation de la consonnance harmonique pour la blanche pointŽe
				else if (note.getRyhtme() == 6 && (note.intervalle(v.getNotes().elementAt(+add)) % 12 != 0 && note.intervalle(v.getNotes().elementAt(+1)) % 12 !=7 &&
						note.intervalle(v.getNotes().elementAt(+add)) % 12 !=3 && note.intervalle(v.getNotes().elementAt(+add)) % 12 != 4 &&
						note.intervalle(v.getNotes().elementAt(+add)) % 12 !=8 && note.intervalle(v.getNotes().elementAt(+add)) % 12 !=9)){
					n.remove(note);i--;
				}
				
				// Utilisation de la consonnance harmonique pour le retard
				else if (notes.size() >= 1
						&& notes.lastElement().getRyhtme() == 8 
						&& ((note.intervalle(v.getNotes().elementAt(+add)) % 12 !=7 
						&& note.intervalle(v.getNotes().elementAt(add)) % 12 !=3 && note.intervalle(v.getNotes().elementAt(+add)) % 12 != 4 
						&& note.intervalle(v.getNotes().elementAt(+add)) % 12 !=8 && note.intervalle(v.getNotes().elementAt(+add)) % 12 !=9)
						|| !note.conjoint(notes.elementAt(pos-1)))
						){
					n.remove(note);i--;
				}

				// Utilisation de la consonnance harmonique pour le retard
				else if (notes.size() >= 2
						&& notes.elementAt(pos-2).getRyhtme() == 6 
						&& ((note.intervalle(v.getNotes().elementAt(+1)) % 12 !=7 &&
						note.intervalle(v.getNotes().elementAt(+add)) % 12 !=3 && note.intervalle(v.getNotes().elementAt(+add)) % 12 != 4 &&
						note.intervalle(v.getNotes().elementAt(+add)) % 12 !=8 && note.intervalle(v.getNotes().elementAt(+add)) % 12 !=9)
						|| !note.conjoint(notes.elementAt(pos-2)))){
					n.remove(note);i--;
				}
			
				/*
				// Utilisation de la consonnance harmonique pour le retard
				// La syncope doit provoquer une dissonance avec le prochaine note du cantus
				else if (note.getRyhtme() == 8 
						&& (note.intervalle(v.getNotes().elementAt(+add +1)) % 12 == 0 || note.intervalle(v.getNotes().elementAt(add+1)) % 12 ==7
						|| note.intervalle(v.getNotes().elementAt(add+1)) % 12 ==3 || note.intervalle(v.getNotes().elementAt(+add+1)) % 12 == 4 
						|| note.intervalle(v.getNotes().elementAt(+add+1)) % 12 ==8 || note.intervalle(v.getNotes().elementAt(+add+1)) % 12 ==9)){
					n.remove(note);i--;
				}

				// Utilisation de la consonnance harmonique pour le retard
				// La syncope doit provoquer une dissonance avec le prochaine note du cantus
				else if (note.getRyhtme() == 6
						&& (note.intervalle(v.getNotes().elementAt(+add+1)) % 12 == 0 || note.intervalle(v.getNotes().elementAt(add+1)) % 12 ==7
						|| note.intervalle(v.getNotes().elementAt(add+1)) % 12 ==3 || note.intervalle(v.getNotes().elementAt(+add+1)) % 12 == 4 
						|| note.intervalle(v.getNotes().elementAt(+add+1)) % 12 ==8 || note.intervalle(v.getNotes().elementAt(+add+1)) % 12 ==9)){
					n.remove(note);i--;
				}
*/
				// Utilisation de la consonnance harmonique pour le retard
				// La syncope doit provoquer une dissonance avec le prochaine note du cantus
				else if (note.getRyhtme() == 8 
						&& note.intervalle(v.getNotes().elementAt(+add+1)) % 12 == 0){
					n.remove(note);i--;
				}

				// Utilisation de la consonnance harmonique pour le retard
				// La syncope doit provoquer une dissonance avec le prochaine note du cantus
				else if (note.getRyhtme() == 6
						&& note.intervalle(v.getNotes().elementAt(+add+1)) % 12 == 0){
					n.remove(note);i--;
				}
				// La blanche pointŽe est suivie de 3 noires
				else if (note.getRyhtme() != 2
						&& notes.size() >= 1
						&& notes.elementAt(pos-1).getRyhtme() == 6){

					n.remove(note);i--;
				}


				else if (note.getRyhtme() != 2
						&& notes.size() >= 2
						&& notes.elementAt(pos-2).getRyhtme() == 6){

					n.remove(note);i--;
				}

				else if (note.getRyhtme() != 2
						&& notes.size() >= 3
						&& notes.elementAt(pos-3).getRyhtme() == 6){

					n.remove(note);i--;
				}


				// Regles croches

				//Croches precedŽe de noire ou croche
				else if (notes.size()>= 1
						&& note.getRyhtme() == 1
						&& notes.elementAt(pos-1).getRyhtme() != 2
						&& notes.elementAt(pos-1).getRyhtme() != 1){

					n.remove(note); i--;
				}


				// Croches en fin de mesures ou  au second temps
				else if (note.getRyhtme() == 1
						&& nbTemps() % 4 != 3
						&& nbTemps() % 4 != 3.5
						&& nbTemps() % 4 != 1
						&& nbTemps() % 4 != 1.5){

					n.remove(note);i--;
				}




				// On utilise les croches toujours par deux
				else if (note.getRyhtme() != 1
						&& notes.size()>= 1
						&& notes.elementAt(pos-1).getRyhtme() == 1
						&& nbTemps() % 4 != 0){

					n.remove(note); i--;
				}

				// Deux croches en milieu de mesure sont toujours suivies d'une blanche pointŽe

				else if (note.getRyhtme() != 6
						&& nbTemps() % 4 == 2
						&& notes.elementAt(pos-1).getRyhtme() == 1){

					n.remove(note);i--;
				}

				// Si l'on utilise une croche en fin de mesure, la mesure ne peut pas commencer pas une noire
				else if (note.getRyhtme() == 1
						&& nbTemps() % 4 != 1
						&& notes.size() >= 2
						&& notes.elementAt(pos-1).getRyhtme() != 1
						&& notes.elementAt(pos-2).getRyhtme() == 2){

					n.remove(note); i--;
				}


				// Croches et suivante sont conjointes

				else if (notes.size() >= 1
						&& notes.elementAt(pos-1).getRyhtme() == 1
						&& !note.conjoint(notes.elementAt(pos-1))){

					n.remove(note); i--;
				}

				//Pas de blanches pointŽe aprŽs une blanche syncopŽe
				
				else if (notes.size()>=1
						&& note.getRyhtme() == 6
						&& notes.elementAt(pos-1).getRyhtme() == 8){
					
					n.remove(note);i--;
				}

				else if(notes.size()>=1
						&& note.getRyhtme() == 8
						&& notes.elementAt(pos-1).getRyhtme() == 8){
					n.remove(note);i--;
				}
				
				else if (notes.size() == 2
						&& note.getRyhtme() != 2
						&& note.getRyhtme() != 8
						&& note.getRyhtme() != 6){
					
					n.remove(note);i--;
				}
				
				
				else if ((note.getRyhtme() == 6
						|| note.getRyhtme() == 8)
						&& nbTemps()+note.getRyhtme()>=taille - 4){
					
					n.remove(note); i--;
				}
			}
			
			
		

		}
		//Regles Melodiques

		for (int i = 0; i<n.size(); i++ ){		

			Note note = n.elementAt(i);

			// Il est alors impossible de repecter la regle 28_D stipulant la preferance d'effectuer un mouvement
			// de direction contraire en cas d'octave.
			//Regle 11 : Ambitus maximum
			if (Math.abs(note.getCode()-noteMin())>18 || Math.abs(noteMax() - note.getCode())>18){n.remove(note); i--;}

			//Regle 27 : Regles concernant deux notes succesives
			else if (note.intervalle(notes.elementAt(pos-1)) > 8 && note.intervalle(notes.elementAt(pos-1))!=12){
				n.remove(note); i--;

			}	



			//Regles Melodiques
			// L'unisson est interdit
			else if (note.intervalle(notes.elementAt(pos-1)) == 0){
				n.remove(note); i--; 
			}	

			//Regle 28 : Regles concernant plus de deux notes succevives : 

			//28_A : Quarte augmentŽe
			else if (notes.size()>4 && (notes.elementAt(pos-1).intervalle(notes.elementAt(pos-3)) == 6 ||
					notes.elementAt(pos-1).intervalle(notes.elementAt(pos-4)) == 6) 
					&& (note.intervalle(notes.elementAt(pos-1)) == 1 || note.intervalle(notes.elementAt(pos-1)) == 2)
					&& ((notes.elementAt(pos-1).getCode()-notes.elementAt(pos-3).getCode())*(note.getCode()
							-notes.elementAt(pos-1).getCode())) > 0)
			{p.removeAllElements(); p.add(note); return p; }
			// A revoir, la quarte peut aussi tre precedŽe d'un mouvement conjoint




			//28_B : Quinte augmentŽe
			else if (notes.size()>4 && notes.elementAt(pos-1).intervalle(notes.elementAt(pos-4)) == 8 
					&& (note.intervalle(notes.elementAt(pos-1)) == 1 
					|| note.intervalle(notes.elementAt(pos-1)) == 2)
					&& ((notes.elementAt(pos-1).getCode()-notes.elementAt(pos-4).getCode())*(note.getCode()
							-notes.elementAt(pos-1).getCode())) > 0 ){
				p.removeAllElements(); p.add(note); return p; 
			}


			// 28_C : Septieme et neuvime 

			//Septieme

			else if ((notes.size()>2 && note.intervalle(notes.elementAt(pos-2)) == 10 
					|| notes.size()>2 && note.intervalle(notes.elementAt(pos-2)) == 11)
					&& !note.conjoint(notes.elementAt(pos-1))
					&& !notes.elementAt(pos-1).conjoint(notes.elementAt(pos-2))){
				n.remove(note);i--;}


			//Neuvime
			else if (notes.size()>2 && (note.intervalle(notes.elementAt(pos-2)) == 13 
					|| note.intervalle(notes.elementAt(pos-2)) == 14)  
					&& !note.conjoint(notes.elementAt(pos-1))
					&& !notes.elementAt(pos-1).conjoint(notes.elementAt(pos-2))){
				n.remove(note);i--;}



			// Regle ajoutŽe pour ne pas avoir le pb entre harmonie et mouvement conjoint

			else if (notes.size()>4 
					&& v.getNotes().size()>pos+1
					&& rythme*pos+1 % v.rythme ==0
					&& (note.intervalle(notes.elementAt(pos-2)) == 6 
					|| note.intervalle(notes.elementAt(pos-3)) == 6
					|| note.intervalle(notes.elementAt(pos-3)) == 8)
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 != 0 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=7 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=3 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 != 4 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=8 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=9){

				n.remove(note);i--;
			}

			else if (notes.size()>4 
					&& v.getNotes().size()>pos+1
					&& rythme*pos+1 % v.rythme ==0
					&& (note.intervalle(notes.elementAt(pos-2)) == 10
					|| note.intervalle(notes.elementAt(pos-3)) == 11
					|| note.intervalle(notes.elementAt(pos-3)) == 13
					|| note.intervalle(notes.elementAt(pos-3)) == 14)
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 != 0 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=7 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=3 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 != 4 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=8 
					&& noteConjointeDir(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=9
					&& noteConjointeDirOpp(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 != 0 
					&& noteConjointeDirOpp(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=7 
					&& noteConjointeDirOpp(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=3 
					&& noteConjointeDirOpp(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 != 4 
					&& noteConjointeDirOpp(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=8 
					&& noteConjointeDirOpp(note,notes.elementAt(pos-2)).intervalle(v.getNotes().elementAt(pos+1)) % 12 !=9){

				n.remove(note);i--;
			}

			/* else if (notes.size()>4 
			&& (note.intervalle(notes.elementAt(pos-2)) == 6 
			|| note.intervalle(notes.elementAt(pos-3)) == 6
			|| note.intervalle(notes.elementAt(pos-3)) == 8
			|| note.intervalle(notes.elementAt(pos-2)) == 10
			|| note.intervalle(notes.elementAt(pos-2)) == 11
			|| note.intervalle(notes.elementAt(pos-2)) == 13
			|| note.intervalle(notes.elementAt(pos-2)) == 14)
			&& note.intervalle(v.getNotes().elementAt(pos+1)) % 12 != 0 
			&& note.intervalle(v.getNotes().elementAt(pos+1)) % 12 !=7 
			&& note.intervalle(v.getNotes().elementAt(pos+1)) % 12 !=3 
			&& note.intervalle(v.getNotes().elementAt(pos+1)) % 12 != 4 
			&& note.intervalle(v.getNotes().elementAt(pos+1)) % 12 !=8 
			&& note.intervalle(v.getNotes().elementAt(pos+1)) % 12 !=9){


	}
			 */



			else if (notes.size()>2 && notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)) == 12 
					&& ((notes.elementAt(pos-1).getCode()-notes.elementAt(pos-2).getCode())*(note.getCode()
							-notes.elementAt(pos-1).getCode()))>0){
				n.remove(note);i--;}

			//L'octave est precŽdŽe par un mouvement contraire
			else if (notes.size()>2 && note.intervalle(notes.elementAt(pos-1)) == 12 
					&& ((notes.elementAt(pos-1).getCode()-notes.elementAt(pos-2).getCode())*(note.getCode()
							-notes.elementAt(pos-1).getCode()))>0){
				n.remove(note);i--;}


			//Regle 40 : On ne peut pas ecrire plus de 3 tierces, quartes ou sixte concecutives
			else if (notes.size()>4 &&
					(((notes.elementAt(pos-2).intervalle(notes.elementAt(pos-3)) == 3
					||notes.elementAt(pos-2).intervalle(notes.elementAt(pos-3)) == 4)
					&& (notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)) == 3
					||notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)) == 4)
					&& (note.intervalle(notes.elementAt(pos-1)) == 3
					||note.intervalle(notes.elementAt(pos-1)) == 4))
					||
					((notes.elementAt(pos-2).intervalle(notes.elementAt(pos-3)) == 8
					||notes.elementAt(pos-2).intervalle(notes.elementAt(pos-3)) == 9)
					&& (notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)) == 8
					||notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)) == 9)
					&& (note.intervalle(notes.elementAt(pos-1)) == 8
					||note.intervalle(notes.elementAt(pos-1)) == 9))
					||
					(notes.elementAt(pos-2).intervalle(notes.elementAt(pos-3)) == 5
					&& notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)) == 5
					&& note.intervalle(notes.elementAt(pos-1)) == 5))
					)
			{

				n.remove(note);i--;

			}

			//Regle 42 : On ne peut pas ecrire plus de 2 quintes ou octaves concecutives
			else if (notes.size()>3 &&
					(((notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)) == 12
					&& note.intervalle(notes.elementAt(pos-1)) == 12))
					||
					(notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)) == 5
					&& note.intervalle(notes.elementAt(pos-1)) == 5)
							))
			{

				n.remove(note);i--;

			}

			// Regle 26 : Pas de mouvement disjoint ˆ la barre de mesure precŽdŽ d'un mouvement de meme direction
			else if (notes.size() > 1
					&& barreMesure()
					&& note.intervalle(notes.elementAt(pos-1)) != 1 
					&& note.intervalle(notes.elementAt(pos-1)) != 2
					&& !note.memeDirection(notes.elementAt(pos-2),notes.elementAt(pos-1))
					){

				n.remove(note);i--;
			}

			//Regle 32 : Organisation de la melodie.


			// 32_3 Pas de retour 3 fois ˆ la meme note
			else if (notes.size()>4
					&& note.getCode() == notes.elementAt(pos-2).getCode()
					&& note.getCode() == notes.elementAt(pos-4).getCode()){

				n.remove(note);i--;
			}

			//32_2 Pas de repetition de motif melodique de taille 2
			else if (notes.size() > 3
					&&
					(notes.elementAt(pos-2).intervalle(notes.elementAt(pos-3))
							== note.intervalle(notes.elementAt(pos-1))
							&&
							!(note.conjoint(notes.elementAt(pos-1))&&notes.elementAt(pos-1).conjoint(notes.elementAt(pos-2))
									&& notes.elementAt(pos-2).conjoint(notes.elementAt(pos-3))))){

				n.remove(note);i--;
			}

			//32_2 Pas de repetition de motif melodique de taille 3 sauf si tout les mouvement sont conjoints
			else if (notes.size() > 5
					&&
					((notes.elementAt(pos-3).intervalle(notes.elementAt(pos-4))
							== note.intervalle(notes.elementAt(pos-1))
							&& notes.elementAt(pos-4).intervalle(notes.elementAt(pos-5))
							== notes.elementAt(pos-1).intervalle(notes.elementAt(pos-2)))
							&&
							!(note.conjoint(notes.elementAt(pos-1))&&notes.elementAt(pos-1).conjoint(notes.elementAt(pos-2))
									&& notes.elementAt(pos-2).conjoint(notes.elementAt(pos-3)))
									&& notes.elementAt(pos-3).conjoint(notes.elementAt(pos-4))
									&& notes.elementAt(pos-4).conjoint(notes.elementAt(pos-5)))){

				n.remove(note);i--;
			}


		}


		return n;

	}


	// A revoir, generation grossiere
	// On met 1 en deuxieme parametre --> Ronde correpond au contrepoint de premier ordre
	public java.util.Vector<Note> creerListeNote(){

		n.removeAllElements();
		int k = 0;

		if (basse){

			k = 41;
			while (k<60){
				n.add(new Note(k,rythme));
				k++;}
		}
		else{
			k = 60;
			while (k<77){
				n.add(new Note(k,8));
				n.add(new Note(k,4));
				n.add(new Note(k,6));
				n.add(new Note(k,2));
				n.add(new Note(k,1));

				k++;
			}}


		// Mode 1 --> Mode majeur
		if (mode == 1) {

			for (int i = 0; i<n.size(); i++ ){

				Note note = n.elementAt(i);


				if (!note.estDo() && !note.estRe() && !note.estMi() && !note.estFa() && !note.estSol() && !note.estLa() && !note.estSi() ){
					n.remove(note);i--;
				}}

		}

		//Mode 2 --> Mode mineur melodique
		if (mode == 2 ) {

			for (int i = 0; i<n.size(); i++ ){

				Note note = n.elementAt(i);


				if (!note.estDo() && !note.estRe() && !note.estMi() && !note.estFa_diese() && !note.estSol_diese() && !note.estLa() && !note.estSi() ){
					n.remove(note);i--;
				}}

		}
		return n;

	}

	public void ecrireVoix(int taille, Voix v){
		int f = 0;
		int p = 0;
		int k = 1;
		if (!basse){k++;}
		while (nbTemps()<taille){

			// Deux mesures concecutives ne peuvent tre egales
			// Pas plus de 7 noires et pas plus de 3 blanches
			if (!basse
					&& nbTemps() % 4 == 0
					&& nbTemps() >= 8
					&& (mesureVoisine())){

				float temp = nbTemps() - 4;

				while (nbTemps() > temp){

					//System.out.println((temp) + " "+ nbTemps() + " "+ notes.size() + " " + k);
					notes.removeElementAt(notes.size() - 1);
					k--;
				}

			}

			if (ajouterNote(v,k)){
				k++;}


			else {//notes.elementAt(k-1).afficher();
				//notes.elementAt(k-2).afficher();
				
				if (p>10 && notes.size() >= 3){
					
					f++;
					notes.removeElementAt(notes.size() - 3);
					notes.removeElementAt(notes.size() - 2);
					notes.removeElementAt(notes.size() - 1);
					k = k - 3;
					p=0;
				}
				if (notes.size() >= 2){
				f++;
				p++;
				notes.removeElementAt(notes.size() - 2);
				notes.removeElementAt(notes.size() - 1);
				k = k -2;}
				
				else if(notes.size() == 0){
					
					if (!basse){notes.add(new Note(60,4));notes.add(new Note(72,4));}
					else{notes.add(new Note(48,rythme));}
					k+=2;
					f++;
				}
				
				else {
					f++;
					notes.removeElementAt(notes.size() - 1);
					k = k - 1;	
					
				}
			}		
		}
			if(basse){notes.add(new Note(48,8));}


	}

	public boolean estCantusFirmus(){

		return cantus;
	}

	public java.util.Vector<Note> getNotes(){

		return notes;
	}


	public Note noteConjointeDir(Note note, Note note2){

		if (note.getCode() - note2.getCode() >0) {

			if (Appartient(note.getCode()+1)){

				return new Note(note.getCode()+1,1);

			}

			else if (Appartient(note.getCode()+2)){

				return new Note(note.getCode()+2,1);

			}
			else {
				System.out.println("Probleme, Pas de note conjointe --> noteConnjointeDir() if");
				return new Note();
			}
		}

		else {

			if (Appartient(note.getCode()-1)){

				return new Note(note.getCode()-1,1);

			}

			else if (Appartient(note.getCode()-2)){

				return new Note(note.getCode()-2,1);

			}
			else {
				System.out.println("Probleme, Pas de note conjointe --> noteConnjointeDir()");
				return new Note();

			}
		}

	}


	public Note noteConjointeDirOpp(Note note, Note note2){

		if (note.getCode() - note2.getCode() <0) {

			if (Appartient(note.getCode()+1)){

				return new Note(note.getCode()+1,1);

			}

			else if (Appartient(note.getCode()+2)){

				return new Note(note.getCode()+2,1);

			}
			else {
				System.out.println("Probleme, Pas de note conjointe --> noteConjointeDirOpp()");

				return new Note();
			}
		}

		else {

			if (Appartient(note.getCode()-1)){

				return new Note(note.getCode()-1,1);

			}

			else if (Appartient(note.getCode()-2)){

				return new Note(note.getCode()-2,1);

			}
			else {
				System.out.println("Probleme, Pas de note conjointe --> noteConjointeDirOpp()");
				return new Note();

			}
		}

	}

	public boolean Appartient(int code){

		for (Note note : creerListeNote())
		{
			if (note.getCode() == code){
				return true;
			}
		}
		return false;
	}


	public boolean estConjoint(java.util.Vector<Note> n, int pos ){

		for (Note note : n){
			if (note.intervalle(notes.elementAt(pos-1)) == 1 
					|| note.intervalle(notes.elementAt(pos-1)) == 2)
			{

				return true;
			}
		}

		return false;
	}


	// Methode permettant de preferer un mouvement conjoit ˆ un mouvement disjoint.
	// A ponderer en fonction de la taille de n
	public Note  Conjoint(java.util.Vector<Note> n, int pos ){
		n2.removeAllElements();
		for (Note note : n){
			if (note.intervalle(notes.elementAt(pos-1)) == 1 
					|| note.intervalle(notes.elementAt(pos-1)) == 2)
			{
				int k =0;
				while (k<10){n2.add(note);k++;}}

			else {

				n2.add(note);

			}
		}

		int nombreAleatoire =(int)(Math.random() *n2.size());
		return n2.elementAt(nombreAleatoire);

	}

	public int min(int a, int b){

		if (a<b){
			return a;
		}
		return b;
	}

	public boolean repetitionDisjoint(java.util.Vector<Note> notes, int pos ){

		int max = min((pos)/2 -1 ,20);

		// Faire commencer x ˆ 1 pour empecher les mouvements disjoints succecifs
		for (int x = 2; x<max; x++){

			if (!notes.elementAt(pos-x).conjoint(notes.elementAt(pos-x-1))
					&& !notes.elementAt(pos-2*x).conjoint(notes.elementAt(pos-2*x-1))
					&& compteurDisjoint(notes,pos-2*x -1)==2){	

				return true;
			}		
		}

		return false;

	}


	public int compteurDisjoint(java.util.Vector<Note> notes, int deb){

		int compteur = 0;

		for (int i = deb+1; i< notes.size() -1 ; i++){

			if (notes.elementAt(i).conjoint(notes.elementAt(i-1))){

				compteur ++;
			}	
		}

		return compteur;
	}

	// Les notes sont mises de gauche ˆ droite
	// La fonction renvoie si les notes forment un mouvement direct
	public boolean Direct(Note n1, Note n2, Note n3, Note n4){

		if ((n2.getCode()-n1.getCode() < 0 && n4.getCode()-n3.getCode() < 0)
				|| (n2.getCode()-n1.getCode() > 0 && n4.getCode()-n3.getCode() > 0)) {
			return true;
		}

		return false;
	}

	public boolean barreMesure(){

		float compteur = 0;

		for (Note note : notes){

			compteur += (float)note.getRyhtme() /2;
		}

		if (compteur % 4 == 0){
			return true;
		}
		return false;
	}

	public float nbTemps(){

		float compteur = 0;

		for (Note note : notes){

			compteur += (float)note.getRyhtme() /2;
		}

		return compteur;
	}

	public int harmonie(float t){

		if (basse){

			float compt = 0;

			int k = 0;

			t = t - t % 1 ;

			if (t%2 != 0){

				t--;
			}


			for (Note note : notes){

				/*
			if (compt == t
					&& t%4 == 2){

				return k-1;
			}

				 */

				if (compt == t){

					return k;
				}

				compt += (float)note.getRyhtme()/2;
				k++;
			}


			if ((notes.size()>=2
					&& notes.elementAt(k-2).getRyhtme() == 6)
					&& compt == t){

				//System.out.println("PB " + k + " " + notes.size());
				return k-1;
			}

			compt = 0;

			k = 0;

			for (Note note : notes){

				if (compt == t-2){

					return k;
				}

				compt += (float)note.getRyhtme()/2;
				k++;
			}

			//System.out.println("La fonction harmonie renvoie la derniere valeur du vecteur.");
			//System.out.println(compt+" "+ k + " " + t);
			return k;
		}
		else {


			float compt = 0;

			int k = 0;

			t = t - t % 1 ;

			if (t%2 != 0){

				t--;
			}


			for (Note note : notes){

				if (compt >= t){

					return k;
				}

				compt += (float)note.getRyhtme()/2;
				k++;
			}

			//System.out.println("Par default");
			return k;
		}

	}

	boolean mesureVoisine(){

		int pos = notes.size();
		int posi = harmonie(nbTemps() - 8);
		int posy = harmonie(nbTemps() - 4);
/*
		if ((pos -posi) % 2 == 0 
				&& notes.elementAt(posi).getRyhtme() == notes.elementAt(posy).getRyhtme()
				&& notes.elementAt(pos-1).getRyhtme() == notes.elementAt(posy-1).getRyhtme()){

			return true;
		}
*/
		return false;
	}



	int rythmesConsecutifs(int rythme){

		int pos2 = harmonie(nbTemps()-8);		
		int compteur = 0;

		while (pos2 < notes.size()){

			if (notes.elementAt(pos2).getRyhtme() == rythme){compteur ++;}
			pos2++;
		}

		return compteur;
	}


}

