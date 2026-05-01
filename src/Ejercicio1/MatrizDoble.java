package Ejercicio1;

import java.util.Random;

public class MatrizDoble implements Runnable {
	private int[][] matriz;
	private int tamanoMatriz;

	public MatrizDoble(int tamano) {
		this.tamanoMatriz = tamano;
		this.matriz = new int[tamanoMatriz][tamanoMatriz];
	}
	
	private void generarMatriz() {
		Random random = new Random();
		for(int i = 0; i < tamanoMatriz; i++) {
			for(int j = 0; j < tamanoMatriz; j++) {
				matriz[i][j] = random.nextInt(10);
			}
		}
	}
	
	private int[][] calcularDobleMatriz() {
	    int[][] resultado = new int[tamanoMatriz][tamanoMatriz];
	    
	    for (int i = 0; i < tamanoMatriz; i++) {
	        for (int j = 0; j < tamanoMatriz; j++) {
	        	resultado[i][j] = matriz[i][j] * 2;
	        }
	    }
	    return resultado;
	}
	
	private void imprimeMatriz(int[][] impresa) {
		for(int i = 0; i < tamanoMatriz; i++) {
			for(int j = 0; j < tamanoMatriz; j++) {
				System.out.print(impresa[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public void run() {
		for (int x = 0; x < 10; x++) {
			generarMatriz();
			int[][] matrizDoble = calcularDobleMatriz();
			
			Programa.pantalla.lock();
			//INICIA SECCIÓN CRÍTICA
			
			try {
				System.out.println("A + A");
				imprimeMatriz(matriz);
				System.out.println("+");
				imprimeMatriz(matriz);
				System.out.println("= ");
				System.out.println("2A");
				imprimeMatriz(matrizDoble);
				//FIN SECCIÓN CRÍTICA
			} finally {
				Programa.pantalla.unlock();
			}

			try {
				Random random = new Random();
				Thread.sleep(random.nextInt(20));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}
