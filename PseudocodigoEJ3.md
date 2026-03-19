# ENUNCIADO

## Ejercicio 3: Monitores

En este ejercicio se quiere simular mediante un programa concurrente la actividad en un gimnasio.
El flujo para entrenar es el siguiente:
        1. **Acceso al gimnasio mediante tornos**
            1. Un cliente llega al gimnasio y debe pasar por uno de los 3 tornos de acceso para validar su entrada. 
            2. Cada torno tiene su cola individual.
            3. El tiempo que tarda el cliente en completar el acceso (validación + entrada) es X milisegundos (valor aleatorio entre 1 y 5).
            4. El cliente debe situarse en el primer torno que encuentre libre; si los 3 están ocupados, esperará en la cola del torno que tenga menor tiempo de espera.
        2. **Elección de zona de entrenamiento**
            1. Tras pasar la zona de tornos, el cliente elige a qué zona de entrenamiento dirigirse en función del tiempo estimado de espera.
            2. El gimnasio tiene 4 zonas, y cada zona dispone de 5 máquinas: Zona 1 – Cardio, Zona 2 – Fuerza, Zona 3 – Funcional, Zona 4 - Estiramientos .
            3. Un cliente puede entrar a una zona si al menos una de sus máquinas está libre. Si hay varias zonas con máquinas libres elegirá una de forma aleatoria.
            4. Si las 5 máquinas de una zona están ocupadas, el cliente debe esperar en la cola de acceso de esa zona (una cola por zona).
            5. Dentro de la zona de Cardio existe una bicicleta premium que tiene una cola exclusiva propia, independiente de la cola general de la zona.
            6. Un cliente puede entrar en Cardio si hay alguna de las 5 máquinas libres (según el paso 2). Si el cliente desea usar la bicicleta premium (decisión con probabilidad del 30%) y está ocupada, deberá esperar en la cola exclusiva de la bicicleta premium, incluso aunque ya esté dentro.
            7. El entrenamiento tarda Y milisegundos (valor aleatorio por cliente), representado mediante el método Sleep() de Java.
        
Se realizará una simulación con 50 hilos cliente, con valores X e Y inicializados aleatoriamente. Desarrollar un programa concurrente en Java para resolver el problema anterior usando monitores como mecanismo para la sincronización.
Formato de salida obligatorio: Justo antes de decidir a qué zona dirigirse, cada cliente debe imprimir en pantalla:
--------------------------------------------------------------
Cliente id ha pasado por el torno: -----
Tiempo en el torno (acceso): X
Zona elegida: -----
Tiempo de entrenamiento: Y
Estimación de espera (sin incluirse a sí mismo):
Zona1(Cardio)=----, Zona2(Fuerza)=----, Zona3(Funcional)=----,
Zona4(Estiramientos)=----
Espera bicicleta premium (si aplica)=----
--------------------------------------------------------------


# PSEUDOCÓDIGO

MONITOR gimnasio:

    Entero tiempoTornos[3] = {0, 0, 0}
    Entero tiempoZonas[4] = {0, 0, 0, 0} // Tiempo de espera en cada zona
    Entero tiempoBicicletaPremium = 0

    Entero maquinasZona[4] = {5, 5, 5, 5} // Máquinas disponibles en cada zona
    Boolean bicicletaPremium = false // Estado de la bicicleta premium

    Condition tornoDisponible[3]
    Condition zonaDisponible[4]
    Condition bicicletaPremiumDisponible

    FUNCION accederTorno(Entero X):
        
        Entero tornoLibre = obtenerTornoLibre()

        SI tornoLibre != -1 ENTONCES
            tiempoTornos[tornoLibre] += X
            RETURN tornoLibre
        SINO
            Entero tornoMenorEspera = obtenerTornoMenorEspera()

            MIENTRAS tiempoTornos[tornoMenorEspera] != 0 HACER
                delay(tornoDisponible[tornoMenorEspera])
            FIN MIENTRAS

            tiempoTornos[tornoMenorEspera] += X
            RETURN tornoMenorEspera
        FIN SI
    FIN FUNCION

    FUNCION accederZona(Entero zonaElegida, Entero Y):
        SI maquinasZona[zonaElegida] > 0 ENTONCES
            maquinasZona[zonaElegida] -= 1
            tiempoZonas[zonaElegida] += Y
        SINO
            MIENTRAS maquinasZona[zonaElegida] == 0 HACER
                delay(zonaDisponible[zonaElegida])
            FIN MIENTRAS

            maquinasZona[zonaElegida] -= 1
            tiempoZonas[zonaElegida] += Y
        FIN SI
    FIN FUNCION

    FUNCION accederBicicletaPremium(Entero Y):
        SI bicicletaPremium == false ENTONCES
            bicicletaPremium = true
            tiempoBicicletaPremium += Y
        SINO
            MIENTRAS bicicletaPremium == true HACER
                delay(bicicletaPremiumDisponible)
            FIN MIENTRAS

            bicicletaPremium = true
            tiempoBicicletaPremium += Y
        FIN SI
    FIN FUNCION

FIN MONITOR


PROCESO Cliente:

    Entero id
    Entero X = aleatorio(1, 5) // Tiempo en el torno (en milisegundos)
    Entero Y = aleatorio(60, 300) // Tiempo de entrenamiento (en milisegundos)


    FUNCION entrenar():
        // Paso 1. Acceso al gimnasio mediante tornos
        Entero torno = gimnasio.accederTorno(X)
        Sleep(X)
        gimnasio.liberarTorno(torno)

        // Paso 2. Obtener estimacion actual y elegir zona (sin incluirse)
        Entero esperaZonas[4] = gimnasio.obtenerEstimacionZonas()
        Entero zona = gimnasio.elegirZona(esperaZonas)

        // Imprimir informacion despues de elegir zona y antes de entrenar
        Imprimir "--------------------------------------------------------------"
        Imprimir "Cliente " + id + " ha pasado por el torno: " + torno
        Imprimir "Tiempo en el torno (acceso): " + X
        Imprimir "Zona elegida: " + zona
        Imprimir "Tiempo de entrenamiento: " + Y
        Imprimir "Estimación de espera (sin incluirse a sí mismo):"
        Imprimir "Zona1(Cardio)=" + esperaZonas[0] + ", Zona2(Fuerza)=" + esperaZonas[1] + ", Zona3(Funcional)=" + esperaZonas[2] + ", Zona4(Estiramientos)=" + esperaZonas[3]
        SI zona == 0 ENTONCES
            Imprimir "Espera bicicleta premium (si aplica)=" + gimnasio.tiempoEsperaBicicletaPremium()
        SINO
            Imprimir "Espera bicicleta premium (si aplica)=0"
        FIN SI
        Imprimir "--------------------------------------------------------------"

        // Paso 3. Reservar recurso y entrenar
        gimnasio.accederZona(zona, Y)

        SI zona == 0 Y probabilidad(30) ENTONCES
            gimnasio.accederBicicletaPremium(Y)
            Sleep(Y)
            gimnasio.liberarBicicletaPremium()
        SINO
            Sleep(Y)
        FIN SI

        gimnasio.liberarZona(zona)
FIN PROCESO


PROCESO Principal:
    Hilo clientes[50]

    PARA i = 0 HASTA 49 HACER
        clientes[i] = crearHilo Cliente(i)
    FIN PARA

    PARA i = 0 HASTA 49 HACER
        join(clientes[i])
    FIN PARA
FIN PROCESO




