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

PROCESO ControladorAccesos

    Buzón buzónControlador, buzónTornoR, buzónTornoL
    Buzón[50] buzonesAficionados

    PARA SIEMPRE HACER
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

PROCESO Aficionado

    Entero id

    PARA i = 0 HASTA 4 HACER
        // Acción previa y solicitar validación
        Sleep(aleatorioEntre(1, 10))
        Entero tiempoEstimado = aleatorioEntre(1, 10)

        // Enviar petición al controlador de accesos
        Mensaje peticion = {
            id: id,
            tiempoEstimado: tiempoEstimado
        }
        buzónControlador.enviar(peticion)
        Caracter tornoAsignado = buzonesAficionados[id].recibir()

        SI tornoAsignado == 'R' ENTONCES
            buzonTornoR.recibir()
        SINO
            buzonTornoL.recibir()
        FIN SI

        Imprimir "Aficionado " + id + " ha usado la cola " + tornoAsignado
        Imprimir "Tiempo de validación = " + tiempoEstimado
        Imprimir "Thread.sleep(" + tiempoEstimado + ")" 
        Sleep(tiempoEstimado)

        SI tornoAsignado == 'R' ENTONCES
            buzonTornoR.enviar("LIBRE")
        SINO
            buzonTornoL.enviar("LIBRE")
        FIN SI

        Imprimir "Aficionado " + id + " liberando la cola " + tornoAsignado
    FIN PARA
FIN PROCESO





PROCESO Principal
    
    Buzón buzónControlador = crearBuzón(50)
    Buzón buzónTornoR, buzónTornoL = crearBuzón(1)

    Buzón[50] buzonesAficionados
    Hilo[50] aficionados

    PARA i = 0 HASTA 49 HACER
        buzonesAficionados[i] = crearBuzón(1)
    FIN PARA

    Hilo controlador = ControladorAccesos(buzónControlador, buzónTornoR, buzónTornoL, buzonesAficionados)

    buzonTornoR.enviar("LIBRE")
    buzonTornoL.enviar("LIBRE")

    controlador.iniciar()
    PARA i = 0 HASTA 49 HACER
        aficionados[i] = Aficionado(i, buzónControlador, buzonesAficionados)
        aficionados[i].iniciar()
    FIN PARA

FIN PROCESO

