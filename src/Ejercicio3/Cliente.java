package Ejercicio3;


public class Cliente implements Runnable {
	
	private int id;
	private MonitorGimnasio monitor;
	private int X = (int) (Math.random() * 5) + 1; // Tiempo en torno entre 1 y 5 segundos
	private int Y = (int) (Math.random() * 240) + 60; // Tiempo en zona (60 a 300 segundos)
	
	// Constructor
	public Cliente(int id, MonitorGimnasio monitor) {
		this.id = id;
		this.monitor = monitor;
	}
	
	// Getters
	public int getId() {
		return id;
	}
	
	public MonitorGimnasio getMonitor() {
		return monitor;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}
	
	@Override
	public void run() {
		
		int torno = monitor.usarTorno(X);

		try {
			Thread.sleep(X);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		monitor.liberarTorno(torno, X);
		
		int[] resultado = monitor.usarZona(id, X, Y, torno);
		boolean esPremium = (resultado[1] == 1);
		int zona = resultado[0];
		
		
		if (esPremium)
			monitor.usarBicicletaPremium(Y);
		
		try {
			Thread.sleep(Y);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (esPremium)
			monitor.liberarBicicletaPremium(Y);
		
		monitor.liberarZona(zona, Y);
		
	}
}
