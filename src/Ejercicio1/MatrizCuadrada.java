package Ejercicio1;

import java.util.Random;

public class MatrizCuadrada implements Runnable {
	private int[][] matriz;
	private int tamanoMatriz;

	public MatrizCuadrada(int tamano) {
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
	
	private int[][] calcularCuadradoMatriz() {
	    int[][] resultado = new int[tamanoMatriz][tamanoMatriz];
	    
	    for (int i = 0; i < tamanoMatriz; i++) {
	        for (int j = 0; j < tamanoMatriz; j++) {
	            int suma = 0;
	            for (int k = 0; k < tamanoMatriz; k++) {
	                suma += matriz[i][k] * matriz[k][j];
	            }
	            resultado[i][j] = suma;
	        }
	    }
	    return resultado;
	}
	
	private void imprimeMatriz(int[][] impresa) {
		for(int i = 0; i < tamanoMatriz; i++) {
			for(int j = 0; j < tamanoMatriz; j++) {
				Programa.mensaje.escribir_mensaje(impresa[i][j] + " ");
			}
			Programa.mensaje.escribir_mensaje("\n");
		}
	}
	
	public void run() {
		for (int x = 0; x < 10; x++) {
			generarMatriz();
			int[][] matrizCuadrada = calcularCuadradoMatriz();
			
			Programa.pantalla.lock();
			//INICIA SECCIÓN CRÍTICA
			
			try {
				Programa.mensaje.escribir_mensaje("A x A \n");
				imprimeMatriz(matriz);
				Programa.mensaje.escribir_mensaje("X \n");
				imprimeMatriz(matriz);
				Programa.mensaje.escribir_mensaje("= \n");
				Programa.mensaje.escribir_mensaje("A2 \n");
				imprimeMatriz(matrizCuadrada);
				Programa.mensaje.escribir_mensaje("\n . ݁₊ ⊹ . ݁ ⟡ ݁ . ⊹ ₊ ݁. \n \n");
				//FIN SECCIÓN CRÍTICA
			} finally {
				Programa.pantalla.unlock();
			}

			try {
				Random random = new Random();
				Thread.sleep(random.nextInt(20));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
