package Ejercicio3;

public class Programa {
	
	public static final int NUM_CLIENTES = 50;
	public static final MonitorGimnasio monitor = new MonitorGimnasio();
	public static final Panel panel = new Panel("Panel", 800, 800);
	
	public static void main(String[] args) {
		
		Thread[] clientes = new Thread[NUM_CLIENTES];
		
		for (int i = 0; i < NUM_CLIENTES; i++) {
			clientes[i] = new Thread(new Cliente(i, monitor));
			clientes[i].start();
		}
		
		for (int i = 0; i < NUM_CLIENTES; i++) {
			try {
				clientes[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Todos los clientes han terminado su rutina en el gimnasio.");
		
	}

}
