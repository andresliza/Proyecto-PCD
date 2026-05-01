package Ejercicio3;

public class MonitorGimnasio {
	
	private static final int NUM_TORNOS = 3;
	private static final int NUM_ZONAS = 4;
	private static final int NUM_MAQUINAS_POR_ZONA = 5;
	
	private int[] tiempoTornos;
	private int[] tiempoZonas;
	private int tiempoBicicletaPremium = 0;
	
	private int[] maquinasZona;
	private boolean bicicletaPremiumEnUso = false;
	
	private final MonitorPantalla pantalla;
	
	public MonitorGimnasio(MonitorPantalla pantalla) {
		this.pantalla = pantalla;
		tiempoTornos = new int[NUM_TORNOS];
		tiempoZonas = new int[NUM_ZONAS];
		maquinasZona = new int[NUM_ZONAS];
		
		for (int i = 0; i < NUM_ZONAS; i++) {
			maquinasZona[i] = NUM_MAQUINAS_POR_ZONA;
		}
	}
	
	public synchronized int usarTorno(int X) throws InterruptedException {
		int torno = obtenerTornoLibre();
		
		if (torno == -1) {
			int tornoMenorEspera = obtenerTornoMenorEspera();
			while (tiempoTornos[tornoMenorEspera] > 0) {
				wait();
				tornoMenorEspera = obtenerTornoMenorEspera();
			}
			torno = tornoMenorEspera;
		}
		
		tiempoTornos[torno] += X;
		return torno;
	}
	
	public synchronized int[] usarZona(int clienteId, int X, int Y, int torno) throws InterruptedException {
	    int zona;
	    int[] zonasLibres = obtenerZonasLibres();

	    if (zonasLibres.length > 0) {
	        zona = obtenerZonaAleatoria(zonasLibres);
	    } else {
	        zona = obtenerZonaMenorEspera();
	        while (maquinasZona[zona] == 0) {
	            wait();
	            zona = obtenerZonaMenorEspera();
	        }
	    }
	    
	    boolean esPremium = (zona == 0) && Math.random() < 0.3;

	    int[] tiemposActualesZonas = tiempoZonas.clone();
	    int tiempoBiciActual = tiempoBicicletaPremium;
	    
	    pantalla.imprimirInforme(clienteId, X, Y, torno, zona, esPremium, tiemposActualesZonas, tiempoBiciActual);

	    tiempoZonas[zona] += Y;
	    maquinasZona[zona]--;

	    return new int[]{zona, esPremium ? 1 : 0};
	}
	
	public synchronized void usarBicicletaPremium(int Y) throws InterruptedException {
		while (bicicletaPremiumEnUso) {
			wait();
		}
		bicicletaPremiumEnUso = true;
		tiempoBicicletaPremium += Y;
	}
	
	public synchronized void liberarTorno(int idTorno, int X) {
		tiempoTornos[idTorno] -= X;
		notifyAll();
	}
	
	public synchronized void liberarZona(int idZona, int Y) {
		tiempoZonas[idZona] -= Y;
		maquinasZona[idZona]++;
		notifyAll();
	}
	
	public synchronized void liberarBicicletaPremium(int Y) {
		tiempoBicicletaPremium -= Y;
		bicicletaPremiumEnUso = false;
		notifyAll();
	}
	
	private int obtenerTornoLibre() {
		for (int i = 0; i < NUM_TORNOS; i++)
			if (tiempoTornos[i] == 0)
				return i;
		return -1;
	}
	
	private int obtenerTornoMenorEspera() {
		int menorEspera = Integer.MAX_VALUE;
		int tornoMenorEspera = -1;
		
		for (int i = 0; i < NUM_TORNOS; i++) {
			if (tiempoTornos[i] < menorEspera) {
				menorEspera = tiempoTornos[i];
				tornoMenorEspera = i;
			}
		}
		
		return tornoMenorEspera;
	}
	
	private int[] obtenerZonasLibres() {
		int[] zonasLibres = new int[NUM_ZONAS];
		int count = 0;
		
		for (int i = 0; i < NUM_ZONAS; i++) {
			if (maquinasZona[i] > 0) {
				zonasLibres[count++] = i;
			}
		}
		
		int[] resultado = new int[count];
		System.arraycopy(zonasLibres, 0, resultado, 0, count);
		return resultado;
	}
	
	private int obtenerZonaAleatoria(int[] zonasLibres) {
		int indiceAleatorio = (int) (Math.random() * zonasLibres.length);
		return zonasLibres[indiceAleatorio];
	}
	
	private int obtenerZonaMenorEspera() {
		int menorEspera = Integer.MAX_VALUE;
		int zonaMenorEspera = -1;
		
		for (int i = 0; i < NUM_ZONAS; i++) {
			if (tiempoZonas[i] < menorEspera) {
				menorEspera = tiempoZonas[i];
				zonaMenorEspera = i;
			}
		}
		
		return zonaMenorEspera;
	}
}
