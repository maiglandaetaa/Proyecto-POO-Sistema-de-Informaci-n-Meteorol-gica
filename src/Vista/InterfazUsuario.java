//Fernanda López
package Vista;

import Controlador.InstitutoMeteorologia;
import Controlador.MeteorologiaException;
import Modelo.Estado;
import Modelo.TipoSensor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InterfazUsuario {
    private Scanner sc = new Scanner(System.in);

    //con patron singleton, solo crea un instituto :P
    private static InterfazUsuario instance;
    private InstitutoMeteorologia instituto;

    private InterfazUsuario() {
        instituto = InstitutoMeteorologia.getInstance();
    }
    public static InterfazUsuario getInstance() {
        if (instance == null) {
            instance = new InterfazUsuario();
        }
        return instance;
    }

    public void menuPrincipal() {

        int opcion;
        do {
            System.out.println("\n    SISTEMA DE INFORMACIÓN METEOROLÓGICA");
            System.out.println("____________________________________________");
            System.out.println("1. Crear región");
            System.out.println("2. Crear comuna");
            System.out.println("3. Crear estación meteorológica");
            System.out.println("4. Instalar sensor");
            System.out.println("5. Registrar medición");
            System.out.println("6. Cambiar estado de sensor");
            System.out.println("7. Generar listados");
            System.out.println("8. Cargar datos");
            System.out.println("9. Guardar datos");
            System.out.println("10. Salir");
            opcion = leerEntero("Opción: ");

            switch (opcion) {
                case 1 -> {
                    crearRegion();
                }
                case 2 -> {
                    crearComuna();
                }
                case 3 -> {
                    crearEstacionMeteorologica();
                }
                case 4 -> {
                    instalarSensor();
                }
                case 5 -> {
                    registrarMedicion();
                }
                case 6 -> {
                    cambiarEstadoSensor();
                }
                case 7 -> {
                    menuListados();
                }

                case 8 -> {
                    cargarDatos();
                }

                case 9 -> {
                    guardarDatos();
                }

                case 10 -> {
                    try {
                        salir();
                    } catch (MeteorologiaException e) {
                        System.out.println("Error: " + e.getMessage());
                        opcion = 0;
                    }
                }
                default -> {
                    System.out.println("Opción no válida, intentelo de nuevo.");
                }
            }
        } while (opcion != 10);
    }

    // M: Cambio a público
    public void crearRegion() {
        int codigo = leerEntero("Código de la región: ");

        System.out.print("Nombre de la región: ");
        String nombre = sc.next();

        try {
            instituto.creaRegion(codigo, nombre);
            System.out.println("Región creada con éxito.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // M: Cambio a público
    public void crearComuna() {
        int codigo = leerEntero("Código de la comuna: ");

        System.out.print("Nombre de la comuna: ");
        String nombre = sc.next();

        int codigoRegion = leerEntero("Código de la región: ");

        try {
            instituto.creaComuna(codigo, nombre, codigoRegion);
            System.out.println("Comuna creada con éxito.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // M: Cambio a público
    public void crearEstacionMeteorologica() {
        System.out.print("Código de la estación: ");
        String cod = sc.next();

        System.out.print("Nombre de la estación: ");
        String nombre = sc.next();

        float lon = leerFloat("Ingrese longitud: ");

        float lat = leerFloat("Ingrese latitud: ");

        float alt = leerFloat("Ingrese altitud: ");

        int codRegion = leerEntero("Código de la región perteneciente: ");

        int codComuna = leerEntero("Ingrese código de la comuna perteneciente: ");

        try {
            instituto.creaEstacion(cod, nombre, lon, lat, alt, codRegion, codComuna);
            System.out.println("Estación creada con éxito.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void instalarSensor() {
        System.out.print("Código de la estación: ");
        String codEstacion = sc.next();

        System.out.print("Ingrese código sensor: ");
        String cod = sc.next();

        System.out.print("Ingrese marca: ");
        String marca = sc.next();

        System.out.print("Ingrese modelo: ");
        String modelo = sc.next();

        System.out.println("Ingrese tipo de sensor: ");
        System.out.println("1. Humedad");
        System.out.println("2. Temperatura");
        System.out.println("3. Presion");
        System.out.println("4. Viento");
        System.out.println("5. Precipitación");
        int tipo = leerEntero("Seleccione: ");

        TipoSensor sensor;

        switch (tipo) {
            case 1 -> {sensor = TipoSensor.HUMEDAD;}
            case 2 -> {sensor = TipoSensor.TEMPERATURA;}
            case 3 -> {sensor = TipoSensor.PRESION;}
            case 4 -> {sensor = TipoSensor.VIENTO;}
            case 5 -> {sensor = TipoSensor.PRECIPITACION;}
            default -> {System.out.println("Tipo inválido."); return;}
        }

        try {
            instituto.instalaSensor(cod, marca, modelo, sensor, codEstacion);
            System.out.println("Sensor creado con exito.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void registrarMedicion() {
        System.out.print("Código de la estación: ");
        String codEstacion = sc.next();

        System.out.print("Código del sensor: ");
        String codSensor = sc.next();

        sc.nextLine();

        LocalDateTime fechaHora = leerFechaHora("Ingrese fecha y hora (dd/MM/yyyy HH:mm): ");

        float valor = leerFloat("Ingrese valor de la medición: ");

        try {
            instituto.registraMedicion(fechaHora, valor, codEstacion, codSensor);
            System.out.println("Medición registrada con éxito.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cambiarEstadoSensor() {
        System.out.print("Código de la estación: ");
        String codEstacion = sc.next();

        System.out.print("Código del sensor: ");
        String codSensor = sc.next();

        System.out.println("Seleccione el nuevo estado:");
        System.out.println("1. ACTIVO");
        System.out.println("2. INACTIVO");
        int opcion = leerEntero("Opción: ");

        Estado nuevoEstado;
        if (opcion == 1) {
            nuevoEstado = Estado.ACTIVO;
        } else if (opcion == 2) {
            nuevoEstado = Estado.INACTIVO;
        } else {
            System.out.println("Opción no válida.");
            return;
        }

        try {
            instituto.cambiaEstadoSensor(codEstacion, codSensor, nuevoEstado);
            System.out.println("Estado del sensor cambiado con éxito.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            instituto.cargaDatos();
            System.out.println("Datos cargados correctamente.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void guardarDatos() {
        try {
            instituto.guardaDatos();
            System.out.println("Datos guardados correctamente.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void salir() throws MeteorologiaException {
        System.out.println("¿Desea guardar los datos antes de salir? (s/n): ");
        String respuesta = sc.next();

        if (respuesta.equalsIgnoreCase("s")) {
            instituto.guardaDatos();
            System.out.println("Datos guardados correctamente.");
        } else if (respuesta.equalsIgnoreCase("n")) {
            System.out.println("Saliendo del programa...");
        } else {
            System.out.println("Respuesta no válida.");
            throw new MeteorologiaException("Debe ingresar s o n");
        }
    }

    //clases 1er avance
    private void menuListados() {
        int opcion2;
        do {
            System.out.println("\n-------MENÚ LISTADOS-------");
            System.out.println("1. Listar regiones");
            System.out.println("2. Listar comunas");
            System.out.println("3. Listar estaciones");
            System.out.println("4. Listar sensores");
            System.out.println("5. Listar mediciones");
            System.out.println("6. Listar resumen estandístico de un sensor.");
            System.out.println("7. Listar alertas de una estación.");
            System.out.println("8. Volver al menú principal.");
            System.out.print("Opción: ");
            opcion2 = leerEntero("Seleccione un listado: ");

            switch (opcion2) {
                case 1 -> {listarRegiones();}
                case 2 -> {listarComunas();}
                case 3 -> {listarEstaciones();}
                case 4 -> {listarSensores();}
                case 5 -> {listarMediciones();}
                case 6 -> {listarResumenMediciones();}
                case 7 -> {listarAlertas();}
                case 8 -> {System.out.println("Volviendo al menú principal...");}
                default -> {System.out.println("Opción no válida, inténtelo de nuevo.");}
            }
        } while (opcion2 != 8);
    }

    // M: Cambio a público
    public void listarRegiones() {
        String[][] regiones = instituto.listaRegiones();

        if (regiones.length == 0) {
            System.out.println("No existen regiones registradas.");
        } else {
            System.out.println("\nREGIONES");
            System.out.println("-----------------------------------------------------------------------------------------------");
            System.out.printf("%-12s %-25s %-15s %-20s %-20s%n",
                    "Código", "Nombre", "Comunas", "Estaciones", "Est. activas");
            System.out.println("-----------------------------------------------------------------------------------------------");

            for (String[] region : regiones) {
                System.out.printf("%-12s %-25s %-15s %-20s %-20s%n", region[0], region[1], region[2], region[3], region[4]);
            }
        }
    }

    // M: Cambio a público
    public void listarComunas() {
        String[][] comunas = instituto.listaComunas();
        if (comunas.length == 0) {
            System.out.println("No existen comunas registradas.");
            return;
        }

        System.out.println("\nCOMUNAS");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-25s %-25s %-25s %-25s%n", "Código", "Nombre", "Región", "Estaciones", "Estaciones activas");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

        for (String[] comuna : comunas) {
            System.out.printf("%-20s %-25s %-25s %-25s %-25s%n", comuna[0], comuna[1], comuna[2], comuna[3], comuna[4]);
        }
    }

    // M: Cambio a público
    public void listarEstaciones() {
        int codigoRegion = leerEntero("Ingrese código de la región: ");

        int codigoComuna = leerEntero("Ingrese código de la comuna: ");

        String[][] estaciones;
        try {
            estaciones = instituto.listaEstaciones(codigoRegion, codigoComuna);
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        if (estaciones.length == 0) {
            System.out.println("No existen estaciones registradas para la región y comuna ingresadas.");
            return;
        }

        System.out.println("\nESTACIONES METEOROLÓGICAS");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-25s %-35s %-15s %-25s%n", "Código", "Nombre", "Ubicación", "Estado", "Sensores operativos");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------");

        for (String[] estacion : estaciones) {
            System.out.printf("%-20s %-25s %-35s %-15s %-25s%n", estacion[0], estacion[1], estacion[2], estacion[3], estacion[4]);
        }
    }

    // M: Cambio a público
    public void listarSensores() {
        System.out.print("Ingrese código de la estación: ");
        String codigoEstacion = sc.next();

        String[][] sensores;
        try {
            sensores = instituto.listaSensores(codigoEstacion);
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        if (sensores.length == 0) {
            System.out.println("No existen sensores registrados en la estación.");
            return;
        }

        System.out.println("\nSENSORES");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-20s %-20s %-20s %-20s %-20s%n", "Código", "Tipo", "Marca", "Modelo", "Unidad", "Estado", "Última medición");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");

        for (String[] sensor : sensores) {
            System.out.printf("%-15s %-20s %-20s %-20s %-20s %-20s %-20s%n", sensor[0], sensor[1], sensor[2], sensor[3], sensor[4], sensor[5], sensor[6]);
        }
    }

    // M: Cambio a público
    public void listarMediciones() {
        System.out.print("Ingrese código de la estación: ");
        String codEstacion = sc.next();

        System.out.print("Ingrese código del sensor: ");
        String codSensor = sc.next();
        sc.nextLine();

        LocalDateTime inicio = leerFechaHora("Ingrese fecha y hora de inicio (dd/MM/yyyy HH:mm): ");
        LocalDateTime fin = leerFechaHora("Ingrese fecha y hora de fin (dd/MM/yyyy HH:mm): ");

        String[][] mediciones;
        try {
            mediciones = instituto.listaMediciones(codEstacion, codSensor, inicio, fin);
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        if (mediciones.length == 0) {
            System.out.println("No existen mediciones para los datos ingresados.");
            return;
        }

        System.out.println("\nMEDICIONES");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-25s %-25s %-25s%n", "Fecha", "Hora", "Valor", "Unidad");
        System.out.println("-------------------------------------------------------------------------------------");

        for (String[] medicion : mediciones) {
            System.out.printf("%-20s %-25s %-25s %-25s%n", medicion[0], medicion[1], medicion[2], medicion[3]);
        }
    }

    // M: Cambio a público
    //Clases nuevas 2do avance
    public void listarResumenMediciones() {
        System.out.print("Ingrese el código de la estación: ");
        String codEstacion = sc.next();

        System.out.print("Ingrese el código del sensor: ");
        String codSensor = sc.next();
        sc.nextLine();

        LocalDateTime inicio = leerFechaHora("Ingrese la fecha y hora de inicio (dd/MM/yyyy HH:mm): ");
        LocalDateTime fin = leerFechaHora("Ingrese la fecha y hora de término (dd/MM/yyyy HH:mm): ");

        try {
            String[] resumen = instituto.listaResumenMediciones(codEstacion, codSensor, inicio, fin);

            if (resumen.length == 0) {
                System.out.println("No hay mediciones en el período indicado.");
            } else {
                System.out.println("\n---RESUMEN DE MEDICIONES---");
                System.out.println("Tipo de sensor: " + resumen[0]);
                System.out.println("Unidad: " + resumen[1]);
                System.out.println("Cantidad de mediciones: " + resumen[2]);
                System.out.println("Valor mínimo: " + resumen[3] + " (" + resumen[4] + ")");
                System.out.println("Valor máximo: " + resumen[5] + " (" + resumen[6] + ")");
                System.out.println("Promedio: " + resumen[7] + " " + resumen[1]);
            }
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarAlertas() {
        System.out.print("Ingrese el código de la estación: ");
        String codEstacion = sc.next();

        sc.nextLine();

        LocalDateTime inicio = leerFechaHora("Ingrese la fecha y hora de inicio (dd/MM/yyyy HH:mm): ");
        LocalDateTime fin = leerFechaHora("Ingrese la fecha y hora de término (dd/MM/yyyy HH:mm): ");

        try {
            String[][] alertas = instituto.listaAlertas(codEstacion, inicio, fin);

            if (alertas.length == 0) {
                System.out.println("No hay alertas en el período indicado.");
            } else { //arreglar el printeo ;P !!!
                System.out.println("\n---LISTADO DE ALERTAS---");

                for (int i = 0; i < alertas.length; i++) {
                    System.out.println("\nAlerta " + (i + 1));
                    System.out.println("Fecha: " + alertas[i][0]);
                    System.out.println("Hora: " + alertas[i][1]);
                    System.out.println("Código del sensor: " + alertas[i][2]);
                    System.out.println("Fenómeno: " + alertas[i][3]);
                    System.out.println("Valor: " + alertas[i][4] + " " + alertas[i][5]);
                    System.out.println("Nivel de alerta: " + alertas[i][6]);
                }
            }
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Marce: Métodos auxiliares para validacion de datos
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = sc.next();

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número entero.");
            }
        }
    }
    private float leerFloat(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = sc.next();

            try {
                return Float.parseFloat(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            }
        }
    }
    private LocalDateTime leerFechaHora(String mensaje) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        while (true) {
            System.out.print(mensaje);
            String fecha = sc.nextLine();

            try {
                return LocalDateTime.parse(fecha, formato);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Fecha u hora no válida. Utilice dd/MM/yyyy HH:mm.");
            }
        }
    }
}
