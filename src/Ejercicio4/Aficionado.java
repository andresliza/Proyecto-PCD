package Ejercicio4;

public class Aficionado implements Runnable {

    int idAficionado;

    public Aficionado(int _idAficionado) {
        idAficionado = _idAficionado;
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
            Programa.buzonControlador.send(peticion);
            Object recibido = Programa.buzonesAficionados[idAficionado].receive();

            if (!(recibido instanceof Character)) {
                System.out.println("Aficionado " + idAficionado + ": Respuesta no válida recibida.");
                continue;
            }

            char tornoAsignado = (Character) recibido;

            if (tornoAsignado == 'L')
                Programa.buzonTornoL.receive();
            else
                Programa.buzonTornoR.receive();

            try {
                Thread.sleep(tiempoEstimado);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (tornoAsignado == 'L')
                Programa.buzonTornoL.send("LIBRE");
            else
                Programa.buzonTornoR.send("LIBRE");

            Programa.buzonPantalla.receive();
            System.out.println("Aficionado " + this.idAficionado + " ha usado la cola " + tornoAsignado);
            System.out.println("Tiempo de validación = " + tiempoEstimado);
            System.out.println("Thread.sleep(" + tiempoEstimado + ")");
            System.out.println("Aficionado " + this.idAficionado + " liberando la cola " + tornoAsignado);
            Programa.buzonPantalla.send("LIBRE");
        }

    }

}
