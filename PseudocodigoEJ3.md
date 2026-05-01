# ENUNCIADO

## Ejercicio 3: Monitores

En este ejercicio se quiere simular mediante un programa concurrente la actividad en un gimnasio. <br>
El flujo para entrenar es el siguiente:<br>
        1. **Acceso al gimnasio mediante tornos**<br>
            1. Un cliente llega al gimnasio y debe pasar por uno de los 3 tornos de acceso para validar su entrada. <br>
            2. Cada torno tiene su cola individual.<br>
            3. El tiempo que tarda el cliente en completar el acceso (validación + entrada) es X milisegundos (valor aleatorio entre 1 y 5).<br>
            4. El cliente debe situarse en el primer torno que encuentre libre; si los 3 están ocupados, esperará en la cola del torno que tenga menor tiempo de espera.<br><br>
        2. **Elección de zona de entrenamiento**<br>
            1. Tras pasar la zona de tornos, el cliente elige a qué zona de entrenamiento dirigirse en función del tiempo estimado de espera.<br>
            2. El gimnasio tiene 4 zonas, y cada zona dispone de 5 máquinas: Zona 1 – Cardio, Zona 2 – Fuerza, Zona 3 – Funcional, Zona 4 - Estiramientos.<br>
            3. Un cliente puede entrar a una zona si al menos una de sus máquinas está libre. Si hay varias zonas con máquinas libres elegirá una de forma aleatoria.<br>
            4. Si las 5 máquinas de una zona están ocupadas, el cliente debe esperar en la cola de acceso de esa zona (una cola por zona).<br>
            5. Dentro de la zona de Cardio existe una bicicleta premium que tiene una cola exclusiva propia, independiente de la cola general de la zona.<br>
            6. Un cliente puede entrar en Cardio si hay alguna de las 5 máquinas libres (según el paso 2). Si el cliente desea usar la bicicleta premium (decisión con probabilidad del 30%) y está ocupada, deberá esperar en la cola exclusiva de la bicicleta premium, incluso aunque ya esté dentro.<br>
            7. El entrenamiento tarda Y milisegundos (valor aleatorio por cliente), representado mediante el método Sleep() de Java.<br><br>
        
Se realizará una simulación con 50 hilos cliente, con valores X e Y inicializados aleatoriamente. Desarrollar un programa concurrente en Java para resolver el problema anterior usando monitores como mecanismo para la sincronización.<br>
Formato de salida obligatorio: Justo antes de decidir a qué zona dirigirse, cada cliente debe imprimir en pantalla:<br><br>
--------------------------------------------------------------<br>
Cliente id ha pasado por el torno: -----<br>
Tiempo en el torno (acceso): X<br>
Zona elegida: -----<br>
Tiempo de entrenamiento: Y<br>
Estimación de espera (sin incluirse a sí mismo):<br>
Zona1(Cardio)=----, Zona2(Fuerza)=----, Zona3(Funcional)=----,<br>
Zona4(Estiramientos)=----<br>
Espera bicicleta premium (si aplica)=----<br>
--------------------------------------------------------------<br><br>


# PSEUDOCÓDIGO

MONITOR MonitorPantalla:
    
    FUNCION imprimirInforme(id, X, Y, torno, zona, esPremium, esperasZonas, esperaBici):
        Imprimir "--------------------------------------------------------------"
        Imprimir "Cliente " + id + " ha pasado por el torno: " + torno
        Imprimir "Tiempo en el torno (acceso): " + X
        Imprimir "Zona elegida: " + (zona + 1)
        Imprimir "Tiempo de entrenamiento: " + Y
        Imprimir "Estimación de espera (sin incluirse a sí mismo):"
        Imprimir "Zona1(Cardio)=" + esperasZonas[0] + ", Zona2(Fuerza)=" + esperasZonas[1] + ", Zona3(Funcional)=" + esperasZonas[2] + ", Zona4(Estiramientos)=" + esperasZonas[3]
        SI esPremium ENTONCES
            Imprimir "Espera bicicleta premium: " + esperaBici
        FIN SI
        Imprimir "--------------------------------------------------------------"
    FIN FUNCION

FIN MONITOR

MONITOR MonitorGimnasio:

    Entero tiempoTornos[3] = {0, 0, 0}
    Entero tiempoZonas[4] = {0, 0, 0, 0}
    Entero tiempoBicicletaPremium = 0
    Entero maquinasZona[4] = {5, 5, 5, 5}
    Boolean bicicletaPremiumEnUso = falso
    MonitorPantalla pantalla

    FUNCION usarTorno(Entero X):
        Entero torno = obtenerTornoLibre()

        SI torno == -1 ENTONCES
            Entero tornoMenorEspera = obtenerTornoMenorEspera()
            MIENTRAS tiempoTornos[tornoMenorEspera] > 0 HACER
                wait()
                tornoMenorEspera = obtenerTornoMenorEspera()
            FIN MIENTRAS
            torno = tornoMenorEspera
        FIN SI

        tiempoTornos[torno] += X
        RETORNAR torno
    FIN FUNCION

    FUNCION liberarTorno(Entero torno, Entero X):
        tiempoTornos[torno] -= X
        signalAll()
    FIN FUNCION

    FUNCION usarZona(Entero clienteId, Entero X, Entero Y, Entero torno):
        Entero zona
        Enteros zonasLibres[] = obtenerZonasLibres()

        SI longitud(zonasLibres) > 0 ENTONCES
            zona = elegirAleatoria(zonasLibres)
        SINO
            zona = obtenerZonaMenorEspera()
            MIENTRAS maquinasZona[zona] == 0 HACER
                wait()
                zona = obtenerZonaMenorEspera()
            FIN MIENTRAS
        FIN SI

        Boolean esPremium = (zona == 0 Y aleatorio() < 0.3)
        
        pantalla.imprimirInforme(clienteId, X, Y, torno, zona, esPremium, tiempoZonas, tiempoBicicletaPremium)

        tiempoZonas[zona] += Y
        maquinasZona[zona] -= 1

        RETORNAR {zona, esPremium}
    FIN FUNCION

    FUNCION liberarZona(Entero zona, Entero Y):
        tiempoZonas[zona] -= Y
        maquinasZona[zona] += 1
        signalAll()
    FIN FUNCION

    FUNCION usarBicicletaPremium(Entero Y):
        MIENTRAS bicicletaPremiumEnUso HACER
            wait()
        FIN MIENTRAS
        bicicletaPremiumEnUso = verdadero
        tiempoBicicletaPremium += Y
    FIN FUNCION

    FUNCION liberarBicicletaPremium(Entero Y):
        tiempoBicicletaPremium -= Y
        bicicletaPremiumEnUso = falso
        signalAll()
    FIN FUNCION

FIN MONITOR


PROCESO Cliente:

    Entero id
    MonitorGimnasio gimnasio
    Entero X = aleatorio(1, 5)
    Entero Y = aleatorio(60, 300)

    FUNCION run():
        // Paso 1: Torno
        Entero torno = gimnasio.usarTorno(X)
        Sleep(X)
        gimnasio.liberarTorno(torno, X)

        // Paso 2: Zona
        {zona, esPremium} = gimnasio.usarZona(id, X, Y, torno)

        SI esPremium ENTONCES
            gimnasio.usarBicicletaPremium(Y)
        FIN SI

        Sleep(Y)

        SI esPremium ENTONCES
            gimnasio.liberarBicicletaPremium(Y)
        FIN SI

        gimnasio.liberarZona(zona, Y)
    FIN FUNCION

FIN PROCESO


PROCESO Principal:
    MonitorPantalla pantalla = NUEVO MonitorPantalla()
    MonitorGimnasio gimnasio = NUEVO MonitorGimnasio(pantalla)
    Hilo clientes[50]

    PARA i = 0 HASTA 49 HACER
        clientes[i] = crearHilo Cliente(i, gimnasio)
        clientes[i].start()
    FIN PARA

    PARA i = 0 HASTA 49 HACER
        join(clientes[i])
    FIN PARA
FIN PROCESO
