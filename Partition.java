package pac.Counterpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.Sequence;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.InvalidMidiDataException;

public class Partition {
	
    private static final Logger logger = Logger.getLogger("MD5Checksum");


	Voix Basse;

	Voix Chant;

	int Tempo;

	int taille;

	int conjoint;
	int contraire;
	int croches;

	private static final int	VELOCITY = 64;


	public Partition(int mode){

		// Mode : 0 --> Mode chromatique, 1 --> Mode Majeur, 2 --> Mode Mineur Melodique


		Basse = new Voix(false,true,mode,8,taille);

		Chant = new Voix(false,false,mode,2,taille);

		Tempo = 120;

		taille = 80;

		//System.out.println("Construction d'une partition par default");

	}
	
	public void reinit(int mode){

		Basse = new Voix(false,true,mode,8,taille);

		Chant = new Voix(false,false,mode,2,taille);

		Tempo = 120;

		taille = 80;

		//System.out.println("Construction d'une partition par default");

	}
	

	// Methode de test qui genere une vector de toutes les notes et appelle estDo()
	public void test_CreerListe(){
		java.util.Vector<Note> g = new java.util.Vector<Note> ();
		g = Chant.creerListeNote();

		for (Note note : g){

			System.out.println(note.getCode());
			System.out.println(note.estDo_diese());
			System.out.println();

			// --> Test OK 3 juin 
		}
	}

	public Voix getChant(){

		return Chant;

	}

	public Voix getBasse(){

		return Basse;
	}

	public Partition (Voix b, Voix c, int T, int t ){

		Basse = b;
		Chant = c;
		Tempo = T;
		taille = t;

	}
	// Methode principale qui appelle les autres methodes afin d'ecrire une voix ˆ l'aide des regles de composition
	public void resoudre(){
		
		Chant.getNotes().removeAllElements();
		Chant.getNotes().add(new Note(72,4));
		Chant.getNotes().add(new Note(72,4));


		if (Basse.estCantusFirmus()){

			Chant.ecrireVoix(taille, Basse);
		}
		else if (Chant.estCantusFirmus()) {

			Basse.ecrireVoix(taille, Chant);

		}

		else {
			//Cas d'initialisation : Aucune des voix n'est definie.
			//On ecrit les deux voix en commenant par Basse de maniere aleatoire --> Chant est vide.
			Basse.ecrireVoix(taille, Chant);
			Chant.ecrireVoix(taille, Basse);

		}

		//System.out.println("Nb de recursions : " + Chant.test + Basse.test);
		

		conjoint = 0;
		for(int i=1;i<Chant.getNotes().size();i++){

			if (Chant.getNotes().elementAt(i-1).conjoint(Chant.getNotes().elementAt(i))){

				conjoint++;

			}	

		}
		
		croches = 0;
		for(int i=0;i<Chant.getNotes().size();i++){

			if (Chant.getNotes().elementAt(i).getRyhtme() == 1){

				croches++;

			}	

		}
		
		
		float tp = (float)conjoint/Chant.getNotes().size();
		tp = tp * taille;
		conjoint = (int) tp;
		contraire = 0;
		float Temps = Basse.getNotes().firstElement().getRyhtme()/2;
		for(int i=1;i<Basse.getNotes().size()-1;i++){

			Temps += Basse.getNotes().elementAt(i).getRyhtme()/2;


			if ((Basse.getNotes().elementAt(i).getCode()
					-Basse.getNotes().elementAt(i-1).getCode())*
					(Chant.getNotes().elementAt(Chant.harmonie(Temps)).getCode()
							-Chant.getNotes().elementAt(Chant.harmonie(Temps)-1).getCode()) <= 0){

				contraire++;

			}

		}

	}

	public void ecritureMidi()
	{

		File outputFile = new File("pop.mid");
		Sequence	sequence = null;
		try
		{
			sequence = new Sequence(Sequence.PPQ, 2);
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		/* Track objects cannot be created by invoking their constructor
		   directly. Instead, the Sequence object does the job. So we
		   obtain the Track there. This links the Track to the Sequence
		   automatically.
		 */
		Track	track = sequence.createTrack();
		Track	track2 = sequence.createTrack();


		int k = 0;
		for (Note note : Basse.getNotes()){

			track2.add(createNoteOnEvent(note.getCode(), k));
			track2.add(createNoteOffEvent(note.getCode(), k + note.getRyhtme()));
			k+= note.getRyhtme();

		}

		k = 4;
		for (Note note2 : Chant.getNotes()){
			if (! (note2 == Chant.getNotes().firstElement())){



				track.add(createNoteOnEvent(note2.getCode(), k));
				track.add(createNoteOffEvent(note2.getCode(), k + note2.getRyhtme()));
				k+= note2.getRyhtme();}

		}

		/* Now we just save the Sequence to the file we specified.
		   The '0' (second parameter) means saving as SMF type 0.
		   Since we have only one Track, this is actually the only option
		   (type 1 is for multiple tracks).
		 */
		try
		{
			MidiSystem.write(sequence, 1, outputFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void ecritureMidi2(String name)
	{

		File outputFile = new File(name + "temp");
		while (outputFile.exists()){
			name +="_2";
			outputFile = new File(name + ".mid");
		}
		Sequence	sequence = null;
		try
		{
			sequence = new Sequence(Sequence.PPQ, 2);
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		/* Track objects cannot be created by invoking their constructor
		   directly. Instead, the Sequence object does the job. So we
		   obtain the Track there. This links the Track to the Sequence
		   automatically.
		 */
		Track	track = sequence.createTrack();
		Track	track2 = sequence.createTrack();


		int k = 0;
		for (Note note : Basse.getNotes()){

			track2.add(createNoteOnEvent(note.getCode(), k));
			track2.add(createNoteOffEvent(note.getCode(), k + note.getRyhtme()));
			k+= note.getRyhtme();

		}

		k = 4;
		for (Note note2 : Chant.getNotes()){
			if (! (note2 == Chant.getNotes().firstElement())){

				track.add(createNoteOnEvent(note2.getCode(), k));
				track.add(createNoteOffEvent(note2.getCode(), k + note2.getRyhtme()));
				k+= note2.getRyhtme();}

		}

		/* Now we just save the Sequence to the file we specified.
		   The '0' (second parameter) means saving as SMF type 0.
		   Since we have only one Track, this is actually the only option
		   (type 1 is for multiple tracks).
		 */
		try
		{
			MidiSystem.write(sequence, 1, outputFile);
								 
			//Use MD5 algorithm
			MessageDigest md5Digest = MessageDigest.getInstance("MD5");
			 
			//Get the checksum
			String checksum = getFileChecksum(md5Digest, outputFile);
			 
			//see checksum			
			File newfile =new File(name + checksum + ".mid");
			outputFile.renameTo(newfile);

			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			System.out.println("PB MD5");
			e.printStackTrace();
		}
	}

	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	{
	    //Get file input stream for reading the file content
	    FileInputStream fis = new FileInputStream(file);
	     
	    //Create byte array to read data in chunks
	    byte[] byteArray = new byte[1024];
	    int bytesCount = 0;
	      
	    //Read file data and update in message digest
	    while ((bytesCount = fis.read(byteArray)) != -1) {
	        digest.update(byteArray, 0, bytesCount);
	    };
	     
	    //close the stream; We don't need it now.
	    fis.close();
	     
	    //Get the hash's bytes
	    byte[] bytes = digest.digest();
	     
	    //This bytes[] has bytes in decimal format;
	    //Convert it to hexadecimal format
	    StringBuilder sb = new StringBuilder();
	    for(int i=0; i< bytes.length ;i++)
	    {
	        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	     
	    //return complete hash
	   return sb.toString();
	}

	private static MidiEvent createNoteOnEvent(int nKey, long lTick)
	{
		return createNoteEvent(ShortMessage.NOTE_ON,
				nKey,
				VELOCITY,
				lTick);
	}



	private static MidiEvent createNoteOffEvent(int nKey, long lTick)
	{
		return createNoteEvent(ShortMessage.NOTE_OFF,
				nKey,
				0,
				lTick);
	}



	private static MidiEvent createNoteEvent(int nCommand,
			int nKey,
			int nVelocity,
			long lTick)
	{
		ShortMessage	message = new ShortMessage();
		try
		{
			message.setMessage(nCommand,
					0,	// always on channel 1
					nKey,
					nVelocity);
		}
		catch (InvalidMidiDataException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		MidiEvent	event = new MidiEvent(message,
				lTick);
		return event;
	}



	private static void printUsageAndExit()
	{
		out("usage:");
		out("java CreateSequence <midifile>");
		System.exit(1);
	}


	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
	
	

}
