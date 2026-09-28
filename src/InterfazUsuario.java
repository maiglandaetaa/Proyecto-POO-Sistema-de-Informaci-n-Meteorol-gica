import java.util.Scanner;

public class InterfazUsuario {
    private Scanner sc = new Scanner(System.in);
    private InstitutoMeteorologia instituto;

    public static void main(String[] args) {
        System.out.println("Holi :D...");
    }


    private void menuPrincipal() {
        instituto = new InstitutoMeteorologia();

        System.out.println("SYSTEMA DE INFORMACIÓN METEOROLÓGICA");
        System.out.println("_____________________________________________");
        System.out.println("\n1. Crear región");
        System.out.println("2. Crear comuna");
        System.out.println("3. Crear estación");
        System.out.println("4. Instalar sensor");
        System.out.println("5. Registrar medición");
        System.out.println("6. Generar listados");
        System.out.println("7. Salir");
        System.out.print("Opción: ");
        int opcion = sc.nextInt();


        switch (opcion) {
            case 1 -> {crearRegion();}
            case 2 -> {crearComuna();}
            case 3 -> {crearEstacionMeteorologica();}
            case 4 -> {instalarSensor();}
            case 5 -> {registrarMedicion();}
            case 6 -> {menuListados();}
            case 7 -> {System.out.println("Saliendo...");}
            default -> {System.out.println("Opción no válida, intentelo de nuevo.");}
        }
    }


    private void crearRegion() {
        System.out.print("Ingrese código de la región: ");
        int codigo = sc.nextInt();

        System.out.print("Ingrese nombre de la región: ");
        String nombre = sc.next();

        boolean creada = instituto.creaRegion(codigo, nombre);

        if (creada) { //si se pudo crear devuelve mensaje:
            System.out.println("Región creada con éxito.");
        } else { //y si si no, devuelve mensaje:
            System.out.println("No se pudo crear la región, inténtelo de nuevo.");
        }
    }


    private void crearComuna() {
        System.out.print("Ingrese código de la comuna: ");
        int codigo = sc.nextInt();

        System.out.print("Ingrese nombre de la comuna: ");
        String nombre = sc.next();

        System.out.print("Ingrese código de la región de la comuna: ");
        int codigoRegion = sc.nextInt();

        boolean resultado = instituto.creaComuna(codigo, nombre, codigoRegion);

        if (resultado) {
            System.out.println("Comuna creada con éxito.");
        } else {
            System.out.println("No se pudo crear la comuna, inténtelo de nuevo.");
        }

    }


    private void crearEstacionMeteorologica() {
        System.out.print("Ingrese código de la estación: ");
        String cod = sc.next();

        System.out.print("Ingrese nombre de la estación: ");
        String nombre = sc.next();

        System.out.print("Ingrese longitud: ");
        float lon = sc.nextFloat();

        System.out.print("Ingrese latitud: ");
        float lat = sc.nextFloat();

        System.out.print("Ingrese altitud: ");
        float alt = sc.nextFloat();

        System.out.print("Ingrese código de la región en la que se encuentra la estación: ");
        int codRegion = sc.nextInt();

        System.out.print("Ingrese código de la comuna en la que se encuentra la estación: ");
        int codComuna = sc.nextInt();

        boolean creada = instituto.creaEstacion(cod, nombre, lon, lat, alt, codRegion, codComuna);

        if (creada) {
            System.out.println("Estación meteorológica creada con éxito.");
        } else {
            System.out.println("No se pudo crear la estación meteorológica, inténtelo de nuevo.");
        }

    }


    private void instalarSensor() {
        System.out.print("Ingrese código de la estación: ");
        String codEstacion = sc.next();

        System.out.print("Ingrese código: ");
        String cod = sc.next();

        System.out.print("Ingrese marca: ");
        String marca = sc.next();

        System.out.print("Ingrese modelo: ");
        String modelo = sc.next();

        System.out.print("Ingrese tipo de sensor: ");
        System.out.println("1. Humedad");
        System.out.println("2. Temperatura");
        System.out.println("3. Presion");
        System.out.println("4. Viento");
        System.out.println("5. Precipitación");
        int tipo = sc.nextInt();

        TipoSensor sensor;

        switch (tipo) {
            case 1 -> {sensor = TipoSensor.HUMEDAD;}
            case 2 -> {sensor = TipoSensor.TEMPERATURA;}
            case 3 -> {sensor = TipoSensor.PRESION;}
            case 4 -> {sensor = TipoSensor.VIENTO;}
            case 5 -> {sensor = TipoSensor.PRECIPITACION;}
            default -> {sensor = null;}
        }

        boolean resultado = instituto.instalaSensor(cod, modelo, marca, sensor, codEstacion);

        if (resultado) {
            System.out.println("Sensor instalado con éxito.");
        } else {
            System.out.println("No se pudo instalar el sensor, inténtelo de nuevo.");
        }
    }


    private void registrarMedicion() {


    }


    private void menuListados() {
        System.out.println("-------MENÚ-------");
        System.out.println("1. Listar regiones");
        System.out.println("2. Listar comunas");
        System.out.println("3. Listar estaciones");
        System.out.println("4. Listar sensores");
        System.out.println("5. Listar mediciones");
        System.out.print("6. Salir");
        int opcion2 = sc.nextInt();

        switch (opcion2) {
            case 1 -> {listarRegiones();}
            case 2 -> {listarComunas();}
            case 3 -> {listarEstaciones();}
            case 4 -> {listarSensores();}
            case 5 -> {listarMediciones();}
            case 6 -> {System.out.println("Saliendo...");}
            default -> {System.out.println("Opción no válida, inténtelo de nuevo.");}
        }
    }


    private void listarRegiones() {
        instituto.listaRegiones();
    }


    private void listarComunas() {
        instituto.listaComunas();
    }


    private void listarEstaciones() {
        instituto.listaEstaciones();
    }


    private void listarSensores() {
        instituto.listaSensores();
    }


    private void listarMediciones() {
        instituto.listaMediciones();

    }

}
