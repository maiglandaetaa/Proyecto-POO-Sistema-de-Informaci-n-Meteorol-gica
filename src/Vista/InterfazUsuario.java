package Vista;

import Controlador.InstitutoMeteorologia;
import Controlador.MeteorologiaException;
import Modelo.TipoSensor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class InterfazUsuario {
    private Scanner sc = new Scanner(System.in);

    //con patron singleton, solo crea un instituto :P
    private static InstitutoMeteorologia instance;
    private InstitutoMeteorologia instituto;

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
            System.out.print("Opción: ");
            opcion = sc.nextInt();


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
                    salir();
                }
                default -> {
                    System.out.println("Opción no válida, intentelo de nuevo.");
                }
            }
        } while (opcion != 10);
    }


    private void crearRegion() {
        System.out.print("Código de la región: ");
        int codigo = sc.nextInt();

        System.out.print("Nombre de la región: ");
        String nombre = sc.next();

        try {
            instituto.creaRegion(codigo, nombre);
            System.out.println("Región creada con éxito.");
        } catch (MeteorologiaException e) {
            System.out.println(e.getMessage());
        }
    }


    private void crearComuna() {
        System.out.print("Código de la comuna: ");
        int codigo = sc.nextInt();

        System.out.print("Nombre de la comuna: ");
        String nombre = sc.next();

        System.out.print("Código de la región: ");
        int codigoRegion = sc.nextInt();

        try {
            instituto.creaComuna(codigo, nombre, codigoRegion);
            System.out.println("Comuna creada con éxito.");
        } catch (MeteorologiaException e) {
            System.out.println(e.getMessage());
        }
    }


    private void crearEstacionMeteorologica() {
        System.out.print("Código de la estación: ");
        String cod = sc.next();

        System.out.print("Nombre de la estación: ");
        String nombre = sc.next();

        System.out.print("Ingrese longitud: ");
        float lon = sc.nextFloat();

        System.out.print("Ingrese latitud: ");
        float lat = sc.nextFloat();

        System.out.print("Ingrese altitud: ");
        float alt = sc.nextFloat();

        System.out.print("Código de la región perteneciente: ");
        int codRegion = sc.nextInt();

        System.out.print("Ingrese código de la comuna perteneciente: ");
        int codComuna = sc.nextInt();

        try {
            instituto.creaEstacion(cod, nombre, lon, lat, alt, codRegion, codComuna);
            System.out.println("Estación creada con éxito.");
        } catch (MeteorologiaException e) {
            System.out.println(e.getMessage());
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
        System.out.print("Seleccione: ");
        int tipo = sc.nextInt();

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
            System.out.println(e.getMessage());
        }
    }


    private void registrarMedicion() {
        System.out.print("Código de la estación: ");
        String codEstacion = sc.next();

        System.out.print("Código del sensor: ");
        String codSensor = sc.next();

        System.out.print("Ingrese fecha y hora (dd/MM/yyyy HH:mm): ");
        String fechaHoraTexto = sc.next() + " " + sc.next();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraTexto, formato);

        System.out.print("Ingrese valor de la medición: ");
        float valor = sc.nextFloat();

        boolean resultado = instituto.registraMedicion(fechaHora, valor, codEstacion, codSensor);

        if (resultado) {
            System.out.println("Medición registrada con éxito.");
        } else {
            System.out.println("No se pudo registrar la medición, inténtelo de nuevo.");
        }
    }

    //Clases nuevas 2do avance
    private void cambiarEstadoSensor() {

    }

    private void cargarDatos() {

    }

    private void guardarDatos() {

    }

    private void salir() {

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
            System.out.println("6. Salir");
            System.out.print("Opción: ");
            opcion2 = sc.nextInt();

            switch (opcion2) {
                case 1 -> {listarRegiones();}
                case 2 -> {listarComunas();}
                case 3 -> {listarEstaciones();}
                case 4 -> {listarSensores();}
                case 5 -> {listarMediciones();}
                case 6 -> {System.out.println("Saliendo...");}
                default -> {System.out.println("Opción no válida, inténtelo de nuevo.");}
            }
        } while (opcion2 != 6);
    }


    private void listarRegiones() {
        String[][] regiones = instituto.listaRegiones(); //matriz con datos de las regiones

        if (regiones.length ==0) {
            System.out.println("No existen regiones registradas.");
            return;
        }

        System.out.println("\nREGIONES");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-25s %-25s %-25s%n", "Código", "Nombre", "Comunas", "Estaciones");
        System.out.println("-------------------------------------------------------------------------------------");

        for (String[] region : regiones) {
            System.out.printf("%-20s %-25s %-25s %-25s%n", region[0], region[1], region[2], region[3]);
        }
    }


    private void listarComunas() {
        String[][] comunas = instituto.listaComunas();
        if (comunas.length == 0) {
            System.out.println("No existen comunas registradas");
        }

        // Marce: Faltaba región
        System.out.println("\nCOMUNAS");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-25s %-25s %-25s %-25s%n", "Código", "Nombre", "Estaciones", "Estaciones activas", "Región");
        System.out.println("---------------------------------------------------------------------------------------------------------------");

        for (String[] comuna : comunas) {
            System.out.printf("%-20s %-25s %-25s %-25s %-25s%n", comuna[0], comuna[1], comuna[2], comuna[3], comuna[4]);
        }

    }


    private void listarEstaciones() {
        System.out.print("Ingrese código de la región: ");
        int codigoRegion = sc.nextInt();

        System.out.print("Ingrese código de la comuna: ");
        int codigoComuna = sc.nextInt();

        String[][] estaciones = instituto.listaEstaciones(codigoRegion, codigoComuna);

        if (estaciones.length == 0) {
            System.out.println("No existen estaciones registradas para la región y comuna ingresadas.");
        }

        System.out.println("\nESTACIONES METEOROLÓGICAS");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-25s %-25s %-25s%n", "Código", "Nombre y ubicación", "Modelo.Estado", "Sensores");
        System.out.println("-------------------------------------------------------------------------------------");

        for (String[] estacion : estaciones) {
            System.out.printf("%-20s %-25s %-25s %-25s%n", estacion[0], estacion[1], estacion[2], estacion[3]);
        }
    }


    private void listarSensores() {
        System.out.print("Ingrese código de la estación: ");
        String codigoEstacion = sc.next();

        String[][] sensores = instituto.listaSensores(codigoEstacion);

        if (sensores.length == 0) {
            System.out.println("No existen sensores registrados en la estación.");
            return;
        }

        System.out.println("\nSENSORES");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s %-20s %-20s %-20s %-20s %-20s %-20s%n", "Código", "Tipo", "Marca", "Modelo", "Unidad", "Modelo.Estado", "Última medición");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------");

        for (String[] sensor : sensores) {
            System.out.printf("%-15s %-20s %-20s %-20s %-20s %-20s %-20s%n", sensor[0], sensor[1], sensor[2], sensor[3], sensor[4], sensor[5], sensor[6]);
        }
    }


    private void listarMediciones() {
        System.out.print("Ingrese código de la estación: ");
        String codEstacion = sc.next();

        System.out.print("Ingrese código del sensor: ");
        String codSensor = sc.next();

        System.out.print("Ingrese fecha y hora de inicio (dd/MM/yyyy HH:mm): ");
        String inicioTexto = sc.next() + " " + sc.next();

        System.out.print("Ingrese feha y hora de fin (dd//MM/yyyy HH:mm): ");
        String finTexto = sc.next() + " " + sc.next();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        LocalDateTime inicio = LocalDateTime.parse(inicioTexto, formato);
        LocalDateTime fin = LocalDateTime.parse(finTexto, formato);

        String[][] mediciones = instituto.listaMediciones(codEstacion, codSensor, inicio, fin);

        if (mediciones.length == 0) {
            System.out.println("No existen mediciones para los datos ingresados.");
        }

        System.out.println("\nMEDICIONES");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("%-20s %-25s %-25s %-25s%n", "Fecha", "Hora", "Valor", "Unidad");
        System.out.println("-------------------------------------------------------------------------------------");

        for (String[] medicion : mediciones) {
            System.out.printf("%-20s %-25s %-25s %-25s%n", medicion[0], medicion[1], medicion[2], medicion[3]);
        }
    }

    //Clases nuevas 2do avance
    private void listarResumenMediciones() {

    }

    private void listarAlertas() {

    }
}