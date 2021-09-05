package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson g = new Gson();
		Input input = null;
		try{
			Reader reader = new FileReader(args[0]);
			input = g.fromJson(reader, Input.class);
		} catch (Exception e) { }
		Ewoks.getInstance(); //creating the Ewoks object
		Ewoks.getInstance().setEwoks(input.getEwoks());
		Thread HanSolo = new Thread(new HanSoloMicroservice());
		Thread C3PO = new Thread(new C3POMicroservice());
		Thread Lando = new Thread(new LandoMicroservice(input.getLando()));
		Thread R2D2 = new Thread(new R2D2Microservice(input.getR2D2()));
		Thread Leia = new Thread(new LeiaMicroservice(input.getAttacks()));
		HanSolo.start();
		C3PO.start();
		Lando.start();
		R2D2.start();
		Leia.start();
		try { HanSolo.join(); }
		catch (InterruptedException ex){} //all threads now finished
		try{
			Writer writer = new FileWriter(args[1]);
			g.toJson(Diary.getInstance(), writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {}
	}
}
