package Ejercicio3;


public class Cliente implements Runnable {
	
	private int id;
	private MonitorGimnasio monitor;
	private int X = (int) (Math.random() * 5) + 1;
	private int Y = (int) (Math.random() * 240) + 60;
	
	public Cliente(int id, MonitorGimnasio monitor) {
		this.id = id;
		this.monitor = monitor;
	}
	
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
		
		try {
			int torno = monitor.usarTorno(X);

			Thread.sleep(X);
			
			monitor.liberarTorno(torno, X);
			
			int[] resultado = monitor.usarZona(id, X, Y, torno);
			boolean esPremium = (resultado[1] == 1);
			int zona = resultado[0];
			
			
			if (esPremium)
				monitor.usarBicicletaPremium(Y);
			
			Thread.sleep(Y);
			
			if (esPremium)
				monitor.liberarBicicletaPremium(Y);
			
			monitor.liberarZona(zona, Y);
		} catch (InterruptedException e) {
			System.err.println("Cliente " + id + " interrumpido.");
			Thread.currentThread().interrupt();
		}
		
	}
}
