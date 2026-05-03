package Ejercicio4;

import messagepassing.MailBox;

public class Programa {

    private static final int NUM_AFICIONADOS = 50;
    private static final int REPETICIONES_POR_AFICIONADO = 5;

    public static void main(String[] args) {

        MailBox buzonControlador = new MailBox(NUM_AFICIONADOS); // Capacidad para recibir peticiones de todos los aficionados

        // cada torno solo puede atender a un aficionao a la vez
        MailBox buzonTornoL = new MailBox(1);
        MailBox buzonTornoR = new MailBox(1);
        MailBox buzonPantalla = new MailBox(1);

        MailBox[] buzonesAficionados = new MailBox[NUM_AFICIONADOS];    // 1 buzón por aficionado
        Thread[] hilosAficionados = new Thread[NUM_AFICIONADOS]; // 1 hilo por aficionado

        for (int i = 0; i < NUM_AFICIONADOS; i++) {
            buzonesAficionados[i] = new MailBox(1);
        }

        ControladorAccesos controlador = new ControladorAccesos(
                buzonControlador,
                buzonesAficionados,
                NUM_AFICIONADOS * REPETICIONES_POR_AFICIONADO);
        Thread hiloControlador = new Thread(controlador);

        buzonTornoL.send("LIBRE");
        buzonTornoR.send("LIBRE");
        buzonPantalla.send("LIBRE");

        hiloControlador.start();

        for (int i = 0; i < NUM_AFICIONADOS; i++) {
            Aficionado aficionado = new Aficionado(i, buzonControlador, buzonTornoL, buzonTornoR, buzonesAficionados, buzonPantalla);
            Thread hiloAficionado = new Thread(aficionado, Integer.toString(i+1));
            hilosAficionados[i] = hiloAficionado;
            hiloAficionado.start();
        }

        for (Thread hiloAficionado : hilosAficionados) {
            try {
                hiloAficionado.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        try {
            hiloControlador.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
