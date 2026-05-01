package Ejercicio1;
import java.util.concurrent.locks.ReentrantLock;

public class Programa {
	public static ReentrantLock pantalla = new ReentrantLock();
		
	public static void main(String args[]) {
		MatrizCuadrada a = new MatrizCuadrada(3);
		MatrizDoble b = new MatrizDoble(3);
		
		Thread t1 = new Thread(a);
		Thread t2 = new Thread(b);
		
		t1.start();
		t2.start();
		
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
