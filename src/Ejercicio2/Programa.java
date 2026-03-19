package Ejercicio2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;


public class Programa {
	
	static Panel panel1 = new Panel("Panel 1", 800, 800);
	static Panel panel2 = new Panel("Panel 2", 800, 800);
	static Panel panel3 = new Panel("Panel 3", 800, 800);
	
	
	static List<Panel> paneles = new ArrayList<>();
	static {
		Collections.addAll(paneles, panel1, panel2, panel3);
	}
	
	static boolean[] estado_paneles = {true, true, true};
	static Semaphore paneles_libres = new Semaphore(3);
	static Semaphore mutex = new Semaphore(1);
	
	
	public static void main(String[] args) {
		
		List<Thread> hilos = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
			Thread hilo = new Thread(new Equipo(i + 1));
			hilos.add(hilo);
			hilo.start();
		}
		
		for (Thread hilo : hilos) {
			try {
				hilo.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
