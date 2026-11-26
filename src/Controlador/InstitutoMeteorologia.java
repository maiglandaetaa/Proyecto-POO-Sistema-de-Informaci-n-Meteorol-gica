//Fernanda López
package Controlador;

import Modelo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.*;

public class InstitutoMeteorologia {
    private static InstitutoMeteorologia instance;
    private InstitutoMeteorologia() {}
    public static InstitutoMeteorologia getInstance() {
        if (instance == null) {
            instance = new InstitutoMeteorologia();
        }
        return instance;
    }

    private ArrayList<Region> regiones = new ArrayList<>();
    private ArrayList<EstacionMeteorologica> estaciones = new ArrayList<>();

    public void creaRegion(int codigo, String nombre) throws MeteorologiaException {

        if (regiones.stream().anyMatch(region -> region.getCodigo() == codigo &&
                region.getNombre().equalsIgnoreCase(nombre))) {
            throw new MeteorologiaException("Ya existe una región con ese código y nombre.");
        } else if (regiones.stream().anyMatch(region -> region.getCodigo() == codigo)) {
            throw new MeteorologiaException("Ya existe una región con el código " + codigo);
        } else if (regiones.stream().anyMatch(regiones -> regiones.getNombre().equalsIgnoreCase(nombre))) {
            throw new MeteorologiaException("Ya existe una región con el nombre " + nombre);
        }

        Region region = new Region(codigo, nombre);
        regiones.add(region);
    }

    public void creaComuna(int codigo, String nombre, int codigoRegion) throws MeteorologiaException {
        for (Region region : regiones) {
            if (region.getCodigo() == codigoRegion) {

                for (Comuna comuna : region.getComunas()) {
                    if (comuna.getCodigo() == codigo) {
                        throw new MeteorologiaException("La región " + region.getNombre() + " ya tiene una comuna con el código " + codigo);
                    } else if (comuna.getNombre().equalsIgnoreCase(nombre)) {
                        throw new MeteorologiaException("La región " + region.getNombre() + " ya tiene una comuna con el nombre " + nombre);
                    }
                }

                region.addComuna(codigo, nombre);
                return;
            }
        }
        throw new MeteorologiaException("No existe una región con el código" + codigoRegion);

    }

    public void creaEstacion(String cod, String nombre, float lon, float lat, float alt, int codRegion, int codComuna) throws MeteorologiaException {
        //Condicion: no existe otra estacion con el mismo codigo
        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equalsIgnoreCase(cod)) {
                throw new MeteorologiaException("Ya existe una estación con el código " + cod);
            }
        }

        //Condicion: que no exista una region con el codigo
        Region regionEncontrada = null;

        //Busca la región
        for (Region region : regiones) {
            if (region.getCodigo() == codRegion) {
                regionEncontrada = region;
                break;
            }
        }

        if (regionEncontrada == null) {
            throw new MeteorologiaException("No existe una región con el código " + codRegion);
        }

        //Condicion: que no exista una comuna con tal codigo en la region
        //Busca la comuna dentro de esa región
        Comuna comunaEncontrada = null;

        for (Comuna comuna : regionEncontrada.getComunas()) {
            if (comuna.getCodigo() == codComuna) {
                comunaEncontrada = comuna;
                break;
            }
        }

        if (comunaEncontrada == null) {
            throw new MeteorologiaException("No existe una comuna con el código " + codComuna + " en la región " + codRegion);
        }

        //Crea y agrega la estacion:
        EstacionMeteorologica nuevaEstacion = new EstacionMeteorologica(cod, nombre, lon, lat, alt, comunaEncontrada);
        estaciones.add(nuevaEstacion);
    }

    public void instalaSensor(String cod, String marca, String modelo, TipoSensor tipo, String codigoEstacion) throws MeteorologiaException {
        boolean encontrada = false;
        for (EstacionMeteorologica estacion : estaciones) {
            //permite crear el sensor si es que existe la estacion :)
            if (estacion.getCodigo().equals(codigoEstacion)) {
                estacion.instalaSensor(cod, marca, modelo, tipo);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new MeteorologiaException("No existe una estación con el código "+codigoEstacion);
        }
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) throws MeteorologiaException {
        boolean encontrada = false;

        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equals(codEstacion)) {
                estacion.registraMedicion(fechaHora, valor, codSensor);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new MeteorologiaException("No existe una estacion con el código "+codEstacion);
        }
    }

    public void cambiaEstadoSensor(String codEstacion, String codSensor) throws MeteorologiaException {
        boolean encontrado = false;
        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equals(codEstacion)) {
                encontrado = true;

                Sensor sensorEncontrado = null;
                for (Sensor sensor : estacion.getSensores()) {
                    if (sensor.getCodigo().equals(codSensor)) {
                        sensorEncontrado = sensor;
                        break;
                    }
                }

                if (sensorEncontrado == null) {
                    throw new MeteorologiaException("No existe un sensor con el código "+codSensor);
                }

                estacion.cambiaEstadoSensor(sensorEncontrado.getCodigo(), sensorEncontrado.getMarca(), sensorEncontrado.getModelo(), sensorEncontrado.getTipo());
                break;
            }
        }

        if (!encontrado) {
            throw new MeteorologiaException("No existe una estación con el código "+codEstacion);
        }
    }

    public void guardaDatos() throws MeteorologiaException {
       if (regiones.isEmpty()) {
           throw new MeteorologiaException("No existen datos para guardar");
       }
       PersistenciaObjetos.escribeObjeto("meteorologia.dat", regiones);
    }

    public void cargaDatos() throws MeteorologiaException {
        if (!regiones.isEmpty()) {
            throw new MeteorologiaException("La carga de datos solo puede realizarse cuando no existen datos registrados.");
        }
        Object objeto = PersistenciaObjetos.leeObjeto("meteorologia.dat");
        if (!(objeto instanceof List)) {
            throw new MeteorologiaException("El archivo meteorologia.dat no contiene datos válidos del sistema.");
        }
        regiones = new ArrayList<>((List<Region>) objeto);
        estaciones = regiones.stream()
                .flatMap(region -> Arrays.stream(region.getComunas()))
                .flatMap(comuna -> Arrays.stream(comuna.getEstaciones()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public void importaDatosTexto() throws MeteorologiaException {
        List<String[]> registrosRegiones = PersistenciaTexto.leeRegistros("regiones.txt");
        for (String[] registro : registrosRegiones) {
            int codigo = Integer.parseInt(registro[0]);
            String nombre = registro[1];

            creaRegion(codigo, nombre);
        }

        List<String[]> registrosComunas = PersistenciaTexto.leeRegistros("comunas.txt");
        for (String[] registro : registrosComunas) {
            int codigoRegion = Integer.parseInt(registro[0]);
            int codigoComuna = Integer.parseInt(registro[1]);
            String nombre = registro[2];

            creaComuna(codigoComuna, nombre, codigoRegion);
        }

        List<String[]> registrosEstaciones = PersistenciaTexto.leeRegistros("estaciones.txt");
        for (String[] registro : registrosEstaciones) {
            String codigo = registro[0];
            String nombre = registro[1];
            float longitud = Float.parseFloat(registro[2]);
            float latitud = Float.parseFloat(registro[3]);
            float altitud = Float.parseFloat(registro[4]);
            int codigoRegion = Integer.parseInt(registro[5]);
            int codigoComuna = Integer.parseInt(registro[6]);

            creaEstacion(codigo, nombre, longitud, latitud, altitud, codigoRegion, codigoComuna);
        }

        List<String[]> registrosSensores = PersistenciaTexto.leeRegistros("sensores.txt");
        List<String[]> registrosMediciones = PersistenciaTexto.leeRegistros("mediciones.txt");

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (String[] registro : registrosSensores) {
            String codigoEstacion = registro[0];
            String codigoSensor = registro[1];
            TipoSensor tipo = TipoSensor.valueOf(registro[2]);
            String marca = registro[3];
            String modelo = registro[4];
            Estado estado = Estado.valueOf(registro[5]);

            instalaSensor(codigoSensor, marca, modelo, tipo, codigoEstacion);

            for (String[] medicion : registrosMediciones) {
                if (medicion[0].equals(codigoEstacion) && medicion[1].equals(codigoSensor)) {
                    LocalDateTime fechaHora = LocalDateTime.parse(medicion[2], formato);

                    float valor = Float.parseFloat(medicion[3]);

                    registraMedicion(fechaHora, valor, codigoEstacion, codigoSensor);
                }
            }

            if (estado == Estado.INACTIVO) {
                cambiaEstadoSensor(codigoEstacion, codigoSensor);
            }
        }


    }

    public String[][] listaRegiones() {
            return regiones.stream().map(region -> new String[] {
                    String.valueOf(region.getCodigo()),
                    region.getNombre(), String.valueOf(region.getComunas().length),
                    String.valueOf(region.getCantidadEstaciones())
            }).toArray(String[][]::new);
    }

    public String[][] listaComunas() {
        return regiones.stream()
                .flatMap(region -> Arrays.stream(region.getComunas())
                        .map(comuna -> new String[] {
                                String.valueOf(comuna.getCodigo()),
                                String.valueOf(comuna.getNombre()),
                                String.valueOf(comuna.getEstaciones().length),
                                String.valueOf(comuna.getCantidadEstacionesActivas()),
                                region.getNombre()
                        }))
                .toArray(String[][]::new);
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {
        Optional<Comuna> comunaEncontrada = findComuna(codigoRegion, codigoComuna);

        if (comunaEncontrada.isEmpty()) {
            return new String[0][0];
        }

        Comuna comuna = comunaEncontrada.get();

        return estaciones.stream().filter(estacion -> comuna.findEstacionById(estacion.getCodigo()).isPresent())
                .map(estacion -> {
                    String[] datos = estacion.toString().split(";");
                    long sensoresActivos = Arrays.stream(estacion.getSensores())
                            .filter(sensor -> sensor.getEstado() == Estado.ACTIVO)
                            .count();
                    datos[4] = String.valueOf(sensoresActivos);
                    return datos;
                }).toArray(String[][]::new);
    }

    public String[][] listaSensores(String codigoEstacion) {
        return estaciones.stream().filter(estacion -> estacion.toString().split(";")[0].equals(codigoEstacion))
                .findFirst().map(EstacionMeteorologica::getResumenSensores).orElseGet(() -> new String[0][0]);
    }

    public String [][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {
        return estaciones.stream().filter(estacion -> estacion.toString().split(";")[0].equals(codEstacion))
                .findFirst().map(estacion -> estacion.getMedicionesSensorBetween(codSensor, inicio, fin))
                .orElseGet(() -> new String[0][0]);
    }

    public String[] listaResumenMediciones(String codigoEstacion, String codigoSensor, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd(MM/yyyy HH:mm");

        Optional<EstacionMeteorologica> resultadoEstacion = findEstacion(codigoEstacion);

        if (resultadoEstacion.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código "+ codigoEstacion + ".");
        }

        EstacionMeteorologica estacion = resultadoEstacion.get();

        Optional<Sensor> resultadoSensor = findSensor(codigoEstacion, codigoSensor);

        if (resultadoSensor.isEmpty()) {
            throw new MeteorologiaException("No existe el sensor "+ codigoSensor + " en la estación "+ codigoEstacion+ ".");
        }

        Sensor sensor = resultadoSensor.get();

        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" +
                    fin.format(formato)+ ") es anterior a la de inicio (" +
                    inicio.format(formato) + ").");
        }

        Optional<ResumenMediciones> resultado = sensor.getResumenBetween(inicio, fin);

        if (resultado.isEmpty()) {
            return new String[0];
        }

        ResumenMediciones resumen = resultado.get();

        Medicion minimo = resumen.getMinimo();
        Medicion maximo = resumen.getMaximo();

        return new String[] {
                sensor.getTipo().toString(),
                sensor.getUnidad(),
                String.valueOf(resumen.getCantidad()),
                String.format(java.util.Locale.US, "%.1f", minimo.getValor()),
                minimo.getFechaHora().format(formato),
                String.format(java.util.Locale.US, "%.1f", maximo.getValor()),
                maximo.getFechaHora().format(formato),
                String.format(java.util.Locale.US, "%.1f", resumen.getPromedio())
        };
    }

    public String[][] listaAlertas(String codigoEstacion, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        Optional<EstacionMeteorologica> resultadoEstacion = findEstacion(codigoEstacion);

        if (resultadoEstacion.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código "+ codigoEstacion+ ".");
        }

        EstacionMeteorologica estacion = resultadoEstacion.get();

        if (fin.isBefore(inicio)) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            throw new MeteorologiaException("La fecha y hora de término (" +
                    fin.format(formato) + ") es anterior a la fecha de inicio (" +
                    inicio.format(formato) + ").");
        }

        AlertaMeteorologica[] alertas = estacion.getAlertasBetween(inicio, fin);

        return Arrays.stream(alertas)
                .sorted(Comparator.comparing(AlertaMeteorologica::getFechaHora))
                .map(alerta -> {Sensor sensor = alerta.getSensor();
                    return new String[] {
                            alerta.getFechaHora().format(formatoFecha),
                            alerta.getFechaHora().format(formatoHora),
                            sensor.getCodigo(),
                            alerta.getFenomeno(),
                            String.format(java.util.Locale.US, "%.1f", alerta.getValor()),
                            sensor.getUnidad(),
                            alerta.getNivel().toString()
                    };
                })
                .toArray(String[][]::new);
    }

    private Optional<EstacionMeteorologica> findEstacion(String codigo) {
        return estaciones.stream().filter(estacion -> estacion.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }

    private Optional<Region> findRegion(int codigo) {
        return regiones.stream().filter(region -> region.getCodigo() == codigo).findFirst();
    }

    private Optional<Comuna> findComuna(int codigoRegion, int codigoComuna) {
        return findRegion(codigoRegion).flatMap(region -> region.findComunaById(codigoComuna));
    }

    private Optional<Sensor> findSensor(String codigoEstacion, String codigoSensor) {
        return findEstacion(codigoEstacion).flatMap(estacion -> estacion.findSensorById(codigoSensor));
    }

    //Métodos nuevos 3er avance
    public String[] getCodigosEstaciones() {
        return estaciones.stream().map(EstacionMeteorologica::getCodigo).sorted().toArray(String[]::new);
    }

    public String[] getCodigosSensores(String codEstacion) throws MeteorologiaException {
        Optional<EstacionMeteorologica> resultado = findEstacion(codEstacion);

        if (resultado.isEmpty()) {
            throw new MeteorologiaException("No existe una estació  con el código "+codEstacion+".");
        }

        return Arrays.stream(resultado.get().getSensores()).map(Sensor::getCodigo).toArray(String[]::new);
    }
}