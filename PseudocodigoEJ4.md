# ENUNCIADO
## Ejercicio 4: Paso de Mensajes Asíncrono
Consideremos un conjunto de 50 aficionados que llegan a un estadio de fútbol para entrar a un
partido y deben pasar por el control de acceso tras validar su entrada. Para acceder hay dos
tornos con sus respectivas colas: la cola R y la cola L, siendo la cola R más rápida que la L (por
ejemplo, porque el torno R tiene lector más eficiente o un agente adicional). Cada aficionado
tiene que usar una cola para validar su entrada durante un tiempo estimado (valor aleatorio
entre 1 y 10). El controlador de accesos, cuando llega un nuevo aficionado a validar la entrada,
estima su tiempo de validación y a aquellos aficionados cuyo tiempo estimado sea menor o
igual a 5, se les asignará el torno más rápido, es decir, la cola R, a los demás aficionados se les
asignará el torno L. Durante el partido, cada aficionado puede salir de su grada para ir a los
baños o a la zona de compra de comida y bebida. Para volver a su zona, debe pasar de nuevo
por los tornos, repitiendo el proceso de validación. Este ciclo de “salir y volver a entrar” lo
realizará 5 veces a lo largo del partido. Es decir, cada hilo repite 5 veces la siguiente secuencia
de acciones: <br> <br> 
1. Realiza una acción previa (usando Sleep ()), por ejemplo caminar hacia los baños. <br>
2. Solicita ponerse en una cola (R o L según el controlador de accesos le indique). <br>
3. Realiza la validación en el torno asignado. <br>
4. Libera la cola (deja libre el torno), <br>
5. Imprime en pantalla la información que se indica a continuación: <br>
```
“Aficionado id ha usado la cola X
Tiempo de validación = T
Thread.sleep(T)
Aficionado id liberando la cola X”
```
<br><br>

# PSEUDOCÓDIGO

PROCESO ControladorAccesos(Entero peticionesEsperadas)

    PARA i = 1 HASTA peticionesEsperadas HACER
        Mensaje peticion = buzónControlador.recibir()
        Caracter torno

        SI peticion.tiempoEstimado <= 5 ENTONCES
            torno = 'R'
        SINO
            torno = 'L'
        FIN SI

        buzonesAficionados[peticion.id].enviar(torno)
    FIN PARA

FIN PROCESO

PROCESO Aficionado(Entero id)

    PARA i = 1 HASTA 5 HACER
        // 1. Acción previa
        Entero tiempoPrevio = aleatorioEntre(1, 10)
        Sleep(tiempoPrevio)

        // 2. Solicitar cola
        Entero tiempoEstimado = aleatorioEntre(1, 10)
        Mensaje peticion = { id: id, tiempoEstimado: tiempoEstimado }
        buzónControlador.enviar(peticion)
        Caracter tornoAsignado = buzonesAficionados[id].recibir()

        // 3. Validación en el torno asignado
        SI tornoAsignado == 'R' ENTONCES
            buzónTornoR.recibir()
        SINO
            buzónTornoL.recibir()
        FIN SI

        Sleep(tiempoEstimado)

        // 4. Libera la cola
        SI tornoAsignado == 'R' ENTONCES
            buzónTornoR.enviar("LIBRE")
        SINO
            buzónTornoL.enviar("LIBRE")
        FIN SI

        // 5. Imprimir información (bloque de pantalla)
        buzónPantalla.recibir()
        Imprimir "Aficionado " + id + " ha usado la cola " + tornoAsignado
        Imprimir "Tiempo de validación = " + tiempoEstimado
        Imprimir "Thread.sleep(" + tiempoEstimado + ")" 
        Imprimir "Aficionado " + id + " liberando la cola " + tornoAsignado
        buzónPantalla.enviar("LIBRE")
    FIN PARA
FIN PROCESO

PROCESO Principal

    Buzón buzónControlador = crearBuzón(50)
    Buzón buzónTornoR = crearBuzón(1)
    Buzón buzónTornoL = crearBuzón(1)
    Buzón buzónPantalla = crearBuzón(1)
    Buzón[50] buzonesAficionados
    
    PARA i = 0 HASTA 49 HACER
        buzonesAficionados[i] = crearBuzón(1)
    FIN PARA

    buzónTornoR.enviar("LIBRE")
    buzónTornoL.enviar("LIBRE")
    buzónPantalla.enviar("LIBRE")

    Iniciar ControladorAccesos(50 * 5)

    PARA i = 0 HASTA 49 HACER
        Iniciar Aficionado(i)
    FIN PARA

FIN PROCESO
