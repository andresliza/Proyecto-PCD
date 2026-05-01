package Ejercicio3;

public class MonitorPantalla {
    public synchronized void imprimirInforme(int cliente, int X, int Y, int torno, int zona, boolean esPremium, int[] tiemposZonas, int tiempoBiciPremium) {
        System.out.println("--------------------------------------------------------------");
        System.out.println("Cliente " + cliente + " ha pasado por el torno " + torno);
        System.out.println("Tiempo en el torno (acceso): " + X + " ms");
        System.out.println("Zona elegida: " + (zona + 1));
        System.out.println("Tiempo de entrenamiento: " + Y + " ms");
        System.out.println("Estimación de espera (sin incluirse a sí mismo):");
        System.out.println("\tZona1(Cardio): " + tiemposZonas[0] + " ms");
        System.out.println("\tZona2(Fuerza): " + tiemposZonas[1] + " ms");
        System.out.println("\tZona3(Funcional): " + tiemposZonas[2] + " ms");
        System.out.println("\tZona4(Estiramientos): " + tiemposZonas[3] + " ms");
        
        if (esPremium) {
            System.out.println("Espera bicicleta premium: " + tiempoBiciPremium + " ms");
        }
        
        System.out.println("--------------------------------------------------------------");
    }
}
