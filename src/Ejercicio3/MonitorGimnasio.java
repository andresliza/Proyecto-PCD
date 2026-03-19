package Ejercicio3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorGimnasio {
	
	// CONSTANTES
	private static final int NUM_TORNOS = 3;
	private static final int NUM_ZONAS = 4;
	private static final int NUM_MAQUINAS_POR_ZONA = 5;
	
	// Para asegurar exclusión mutua en el acceso a los recursos compartidos
	private ReentrantLock cerrojo = new ReentrantLock();
	
	// Tiempo de espera para cada torno y zona, y para la bicicleta premium
	private int[] tiempoTornos;
	private int[] tiempoZonas;
	private int tiempoBicicletaPremium = 0;
	
	private int[] maquinasZona;
	private boolean bicicletaPremiumEnUso = false; // Estado de la bicicleta premium
	
	private Condition[] condTornos;
	private Condition[] condZonas;
	private Condition condBicicletaPremium;
	
	// Constructor
	public MonitorGimnasio() {
		tiempoTornos = new int[NUM_TORNOS];
		tiempoZonas = new int[NUM_ZONAS];
		maquinasZona = new int[NUM_ZONAS];
		
		condTornos = new Condition[NUM_TORNOS];
		for (int i = 0; i < NUM_TORNOS; i++) {
			condTornos[i] = cerrojo.newCondition();
		}
		
		condZonas = new Condition[NUM_ZONAS];
		for (int i = 0; i < NUM_ZONAS; i++) {
			condZonas[i] = cerrojo.newCondition();
			maquinasZona[i] = NUM_MAQUINAS_POR_ZONA; // Inicialmente todas las máquinas están disponibles
		}
		
		condBicicletaPremium = cerrojo.newCondition();
	}
	
	// Getters
	public int getNumTornos() {
		return NUM_TORNOS;
	}
	
	public int getNumZonas() {
		return NUM_ZONAS;
	}
	
	public int getNumMaquinasPorZona() {
		return NUM_MAQUINAS_POR_ZONA;
	}
	
	public int getTiempoTorno(int idTorno) {
		return tiempoTornos[idTorno];
	}
	
	public int getTiempoZona(int idZona) {
		return tiempoZonas[idZona];
	}
	
	public int getTiempoBicicletaPremium() {
		return tiempoBicicletaPremium;
	}
	
	public int getMaquinasZona(int idZona) {
		return maquinasZona[idZona];
	}
	
	public boolean isBicicletaPremiumEnUso() {
		return bicicletaPremiumEnUso;
	}
	
	public Condition getCondTorno(int idTorno) {
		return condTornos[idTorno];
	}
	
	public Condition getCondZona(int idZona) {
		return condZonas[idZona];
	}
	
	public Condition getCondBicicletaPremium() {
		return condBicicletaPremium;
	}
	
	// Procedimientos principales - uso de recursos
	public int usarTorno(int X) {
		cerrojo.lock();
		try {
			int torno = obtenerTornoLibre();
			
			if (torno != -1) {
				tiempoTornos[torno] += X;
				return torno;
			} else {
				int tornoMenorEspera = obtenerTornoMenorEspera();
				
				while (tiempoTornos[tornoMenorEspera] > 0) {
					condTornos[tornoMenorEspera].await();
				}
				
				tiempoTornos[tornoMenorEspera] += X;
				return tornoMenorEspera;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			return -1; // Indica que el hilo fue interrumpido
		} finally {
			cerrojo.unlock();
		}
	}
	
	public int[] usarZona(int clienteId, int X, int Y, int torno) {
	    cerrojo.lock();
	    try {
	        int zona;
	        int[] zonasLibres = obtenerZonasLibres();

	        if (zonasLibres.length > 0) {
	            zona = obtenerZonaAleatoria(zonasLibres);
	        } else {
	            zona = obtenerZonaMenorEspera();
	            while (maquinasZona[zona] == 0) {
	                condZonas[zona].await();
	            }
	        }
	        
	        boolean esPremium = (zona == 0) && Math.random() < 0.3;

	        // Debug aquí: tiempoZonas aún NO incluye a este cliente
	        imprimirEstado(clienteId, X, Y, torno, zona, esPremium);

	        // Ahora sí reservamos
	        tiempoZonas[zona] += Y;
	        maquinasZona[zona]--;

	        return new int[]{zona, esPremium ? 1 : 0}; // Retorna zona y si es premium

	    } catch (InterruptedException e) {
	        e.printStackTrace();
	        Thread.currentThread().interrupt();
	        return null;
	    } finally {
	        cerrojo.unlock();
	    }
	}
	
	public void usarBicicletaPremium(int Y) {
		cerrojo.lock();
		try {
			if (!bicicletaPremiumEnUso) {
				bicicletaPremiumEnUso = true;
				tiempoBicicletaPremium += Y;
			} else {
				while (bicicletaPremiumEnUso) {
					condBicicletaPremium.await();
				}
				bicicletaPremiumEnUso = true;
				tiempoBicicletaPremium += Y;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			return; // Indica que el hilo fue interrumpido
		} finally {
			cerrojo.unlock();
		}
	}
	
	private void imprimirEstado(int cliente, int X, int Y, int torno, int zona, boolean esPremium) {		
		Programa.panel.escribir_mensaje("--------------------------------------------------------------\n");
		Programa.panel.escribir_mensaje("Cliente " + cliente + " ha pasado por el torno " + torno + "\n");
		Programa.panel.escribir_mensaje("Tiempo en el torno (acceso): " + X + " ms\n");
		Programa.panel.escribir_mensaje("Zona elegida: " + (zona + 1)+ "\n");
		Programa.panel.escribir_mensaje("Tiempo de entrenamiento: " + Y + " ms\n");
		Programa.panel.escribir_mensaje("Estimación de espera (sin incluirse a sí mismo):\n");
		Programa.panel.escribir_mensaje("\tZona1(Cardio): " + getTiempoZona(0) + " ms\n");
		Programa.panel.escribir_mensaje("\tZona2(Fuerza): " + getTiempoZona(1) + " ms\n");
		Programa.panel.escribir_mensaje("\tZona3(Funcional): " + getTiempoZona(2) + " ms\n");
		Programa.panel.escribir_mensaje("\tZona4(Estiramientos): " + getTiempoZona(3) + " ms\n");
		
		if (esPremium) {
			Programa.panel.escribir_mensaje("Espera bicicleta premium: " + getTiempoBicicletaPremium() + " ms\n");
		}
		
		Programa.panel.escribir_mensaje("--------------------------------------------------------------\n");
	}
	
	// Procedimientos para liberar recursos
	public void liberarTorno(int idTorno, int X) {
		cerrojo.lock();
		try {
			tiempoTornos[idTorno] -= X;
			condTornos[idTorno].signal();
		} finally {
			cerrojo.unlock();
		}
	}
	
	public void liberarZona(int idZona, int Y) {
		cerrojo.lock();
		try {
			tiempoZonas[idZona] -= Y;
			maquinasZona[idZona]++;
			condZonas[idZona].signal();
		} finally {
			cerrojo.unlock();
		}
	}
	
	public void liberarBicicletaPremium(int Y) {
		cerrojo.lock();
		try {
			tiempoBicicletaPremium -= Y;
			bicicletaPremiumEnUso = false;
			condBicicletaPremium.signal();
		} finally {
			cerrojo.unlock();
		}
	}
	
	// Procedimientos auxiliares
	private int obtenerTornoLibre() {
		for (int i = 0; i < NUM_TORNOS; i++)
			if (tiempoTornos[i] == 0)
				return i;
		return -1; // No hay torno libre
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
