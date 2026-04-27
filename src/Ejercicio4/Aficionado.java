package Ejercicio4;

import messagepassing.MailBox;

public class Aficionado implements Runnable {

    private static final Object PANTALLA_LOCK = new Object();

    int idAficionado;
    MailBox buzonControlador, buzonTornoL, buzonTornoR;
    MailBox[] buzonesAficionados;

    public Aficionado(int _idAficionado, MailBox _buzonControlador, MailBox _buzonTornoL, MailBox _buzonTornoR, MailBox[] _buzonesAficionados) {
        idAficionado = _idAficionado;
        buzonControlador = _buzonControlador;
        buzonTornoL = _buzonTornoL;
        buzonTornoR = _buzonTornoR;
        buzonesAficionados = _buzonesAficionados;
    }

    @Override
    public void run() {

        for (int i = 0; i < 5; i++) {

            int tiempoValidacion = (int) (Math.random() * 10) + 1;

            try {
                Thread.sleep(tiempoValidacion);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int tiempoEstimado = (int) (Math.random() * 10) + 1;

            Peticion peticion = new Peticion(idAficionado, tiempoEstimado);
            buzonControlador.send(peticion);
            Object recibido = buzonesAficionados[idAficionado].receive();

            if (!(recibido instanceof Character)) {
                System.out.println("Aficionado " + idAficionado + ": Respuesta no válida recibida.");
                continue;
            }

            char tornoAsignado = (Character) recibido;

            if (tornoAsignado == 'L') buzonTornoL.receive();
            else buzonTornoR.receive();

            synchronized (PANTALLA_LOCK) {
                System.out.println("Aficionado " + this.idAficionado + " ha usado la cola " + tornoAsignado);
                System.out.println("Tiempo de validación = " + tiempoValidacion);
                System.out.println("Thread.sleep(" + tiempoEstimado + ")");
            }

            try {
                Thread.sleep(tiempoEstimado);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (tornoAsignado == 'L') buzonTornoL.send("LIBRE");
            else buzonTornoR.send("LIBRE");

            synchronized (PANTALLA_LOCK) {
                System.out.println("Aficionado " + this.idAficionado + " liberando la cola " + tornoAsignado);
            }
        }

    }




}
