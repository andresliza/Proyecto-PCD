package Ejercicio2;

import java.util.ArrayList;
import java.util.List;

public class Equipo implements Runnable {
	
	private int id;
	
	/* 
	 * Lista de 4 matrices, 1 por deportista
	 * Cada matriz es 5x5 -> 5 intentos por cada una de las 5 actividades
	 * */
	private List<int[][]> puntuaciones_deportistas;
	
	/* 
	 * Lista de 4 vectores, 1 por deportista
	 * Cada vector es de tamaño 5 -> mejor marca por cada uno de los 5 ejercicios
	 * */
	private List<int[]> mejores_marcas;
	
	/* 
	 * Matriz resumen de 4x5 -> mejores marcas de cada uno de los 4 deportistas en las 5 actividades
	 * */
	private int[][] R;
	
	public Equipo(int id) {
		this.id = id;
		this.puntuaciones_deportistas = new ArrayList<>();
		this.mejores_marcas = new ArrayList<>();
		this.R = new int[4][5];
	}
	
	@Override
	public void run() {
		
		
		// Repetimos 3 veces el proceso
		for (int i = 0; i < 3; i++) {
			
			// Limpiamos las listas para cada iteración
			mejores_marcas.clear();
			puntuaciones_deportistas.clear();
			
			// Generamos las matrices de puntuaciones para cada deportista y calculamos sus mejores marcas
			for (int d = 0; d < 4; d++) {
				int[][] puntuaciones = new int[5][5];
				puntuaciones = matrizAleatoria();
				puntuaciones_deportistas.add(puntuaciones);
				
				int[] mejores = new int[5];
				mejores = mejorMarca(puntuaciones);
				mejores_marcas.add(mejores);
			}
			
			R = matrizResumen(mejores_marcas);
			
			// SECCIÓN CRÍTICA: Esperamos hasta que haya un panel libre y que sea nuestro turno
			try {
				Programa.paneles_libres.acquire();
				Programa.mutex.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Buscamos un panel libre
			int panel_libre = -1;
			for (int j = 0; j < Programa.estado_paneles.length; j++) {
				if (Programa.estado_paneles[j]) {
					panel_libre = j;
					Programa.estado_paneles[j] = false;
					break;
				}
			}
			
			// Liberamos el mutex para que otros equipos puedan acceder a los paneles
			Programa.mutex.release();
			
			Panel panel = Programa.paneles.get(panel_libre);
			
			panel.escribir_mensaje("Usando panel " + (panel_libre + 1) + " el hilo (equipo) " + id + "\n");
			panel.escribir_mensaje("Matriz R (resumen del equipo " + id + ": mejores marcas = )" + "\n");
			imprimeMatriz(panel, R);
			panel.escribir_mensaje("Terminando de usar panel " + (panel_libre + 1) + " el hilo (equipo) " + id + "\n");
			
			// Cuando sea nuestro turno de nuevo, liberamos el panel para que otros equipos puedan usarlo
			try {
				Programa.mutex.acquire();
				Programa.estado_paneles[panel_libre] = true;
				Programa.mutex.release();
				Programa.paneles_libres.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	private void imprimeMatriz(Panel panel, int[][] r2) {
		
		for (int i = 0; i < r2.length; i++) {
			for (int j = 0; j < r2[i].length; j++) {
				panel.escribir_mensaje(r2[i][j] + " ");
			}
			panel.escribir_mensaje("\n");
		}
		
	}

	private int[][] matrizResumen(List<int[]> mejores_marcas2) {
		
		int[][] resumen = new int[4][5];
		
		for (int i = 0; i < mejores_marcas2.size(); i++) {
			for (int j = 0; j < mejores_marcas2.get(i).length; j++) {
				resumen[i][j] = mejores_marcas2.get(i)[j];
			}
		}
		
		return resumen;
		
		
	}

	private int[] mejorMarca(int[][] puntuaciones) {
		int[] maximos = new int[5];
		
		for (int i = 0; i < puntuaciones.length; i++) {
			for (int j = 0; j < puntuaciones[i].length; j++) {
				if (puntuaciones[i][j] > maximos[j]) {
					maximos[j] = puntuaciones[i][j];
				}
			}
		}
		
		return maximos;
	}

	private int[][] matrizAleatoria() {
		int[][] matriz = new int[5][5];
		for (int i = 0; i < matriz.length; i++) {
			for (int j = 0; j < matriz[i].length; j++) {
				matriz[i][j] = (int) (Math.random() * 10) + 1;
			}
		}
		return matriz;
	}
	
	

}
