package Ejercicio4;
import messagepassing.MailBox;

public class ControladorAccesos implements Runnable {
    
    MailBox buzonControlador;
    MailBox buzonesAficionados[];
    int peticionesEsperadas;

    public ControladorAccesos(MailBox _buzonControlador,  MailBox[] _buzonesAficionados, int _peticionesEsperadas) {
        buzonControlador = _buzonControlador;
        buzonesAficionados = _buzonesAficionados;
        peticionesEsperadas = _peticionesEsperadas;
    }

    @Override
    public void run() {
        for (int i = 0; i < peticionesEsperadas; i++) {
            Object recibido = buzonControlador.receive();

            if (!(recibido instanceof Peticion)) {
                System.out.println("Controlador: Petición no válida recibida.");
                i--;
                continue;
            }

            Peticion peticion = (Peticion) recibido;
            char torno;

            if (peticion.tiempoEstimado() <= 5) torno = 'R';
            else torno = 'L';

            buzonesAficionados[peticion.idAficionado()].send(torno);
        }
    }

    



}
