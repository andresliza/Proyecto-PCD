package Ejercicio4;

public class ControladorAccesos implements Runnable {

    int peticionesEsperadas;

    public ControladorAccesos(int _peticionesEsperadas) {
        peticionesEsperadas = _peticionesEsperadas;
    }

    @Override
    public void run() {
        for (int i = 0; i < peticionesEsperadas; i++) {
            Object recibido = Programa.buzonControlador.receive();

            if (!(recibido instanceof Peticion)) {
                System.out.println("Controlador: Petición no válida recibida.");
                i--;
                continue;
            }

            Peticion peticion = (Peticion) recibido;
            char torno;

            if (peticion.tiempoEstimado() <= 5)
                torno = 'R';
            else
                torno = 'L';

            Programa.buzonesAficionados[peticion.idAficionado()].send(torno);
        }
    }

}
