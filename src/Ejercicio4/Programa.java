package Ejercicio4;

import messagepassing.MailBox;

public class Programa {

    public static final int NUM_AFICIONADOS = 50;
    public static final int REPETICIONES_POR_AFICIONADO = 5;

    public static MailBox buzonControlador;
    public static MailBox buzonTornoL;
    public static MailBox buzonTornoR;
    public static MailBox buzonPantalla;
    public static MailBox[] buzonesAficionados;

    public static void main(String[] args) {

        buzonControlador = new MailBox(NUM_AFICIONADOS);
        buzonTornoL = new MailBox(1);
        buzonTornoR = new MailBox(1);
        buzonPantalla = new MailBox(1);

        buzonesAficionados = new MailBox[NUM_AFICIONADOS];
        for (int i = 0; i < NUM_AFICIONADOS; i++) {
            buzonesAficionados[i] = new MailBox(1);
        }

        ControladorAccesos controlador = new ControladorAccesos(NUM_AFICIONADOS * REPETICIONES_POR_AFICIONADO);
        Thread hiloControlador = new Thread(controlador);

        buzonTornoL.send("LIBRE");
        buzonTornoR.send("LIBRE");
        buzonPantalla.send("LIBRE");

        hiloControlador.start();

        Thread[] hilosAficionados = new Thread[NUM_AFICIONADOS];
        for (int i = 0; i < NUM_AFICIONADOS; i++) {
            Aficionado aficionado = new Aficionado(i);
            Thread hiloAficionado = new Thread(aficionado, Integer.toString(i + 1));
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
