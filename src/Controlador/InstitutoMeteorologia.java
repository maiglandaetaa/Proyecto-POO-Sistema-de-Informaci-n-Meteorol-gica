//Fernanda López
package Controlador;

import Modelo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
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
        if (regiones.stream().anyMatch(region -> region.getCodigo() == codigo)) {
            throw new MeteorologiaException("Ya existe una región con el código " + codigo);
        } else if (regiones.stream().anyMatch(region -> region.getNombre().equalsIgnoreCase(nombre))) {
            throw new MeteorologiaException("Ya existe una región con el nombre " + nombre);
        }

        Region region = new Region(codigo, nombre);
        regiones.add(region);
    }

    public void creaComuna(int codigo, String nombre, int codigoRegion) throws MeteorologiaException {
        Optional<Region> resultado = findRegion(codigoRegion);
        if (resultado.isEmpty()) {
            throw new MeteorologiaException("No existe una región con el código " + codigoRegion);
        }
        resultado.get().addComuna(codigo, nombre);
    }

    public void creaEstacion(String cod, String nombre, float lon, float lat, float alt, int codRegion, int codComuna) throws MeteorologiaException {
        //Condicion: no existe otra estacion con el mismo codigo
        if (findEstacion(cod).isPresent()) {
            throw new MeteorologiaException("Ya existe una estación con el código " + cod);
        }

        //Condicion: que no exista una region con el codigo
        Optional<Region> regionEncontrada = findRegion(codRegion);

        if (regionEncontrada.isEmpty()) {
            throw new MeteorologiaException(
                    "No existe una región con el código " + codRegion
            );
        }

        //Condicion: que no exista una comuna con tal codigo en la region
        //Busca la comuna dentro de esa región
        Optional<Comuna> comunaEncontrada = findComuna(codRegion, codComuna);

        if (comunaEncontrada.isEmpty()) {
            throw new MeteorologiaException(
                    "No existe una comuna con el código " + codComuna
                            + " en la región " + codRegion
            );
        }

        //Crea y agrega la estacion:
        EstacionMeteorologica nuevaEstacion = new EstacionMeteorologica(cod, nombre, lon, lat, alt, comunaEncontrada.get());
        estaciones.add(nuevaEstacion);
    }

    public void instalaSensor(String cod, String marca, String modelo, TipoSensor tipo, String codigoEstacion) throws MeteorologiaException {
        Optional<EstacionMeteorologica> resultado = findEstacion(codigoEstacion);
        if (resultado.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion);
        }
        resultado.get().instalaSensor(cod, marca, modelo, tipo);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) throws MeteorologiaException {
        Optional<EstacionMeteorologica> resultado = findEstacion(codEstacion);
        if (resultado.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código " + codEstacion);
        }
        resultado.get().registraMedicion(fechaHora, valor, codSensor);
    }


    public void cambiaEstadoSensor(String codEstacion, String codSensor, Estado nuevoEstado) throws MeteorologiaException {
        Optional<EstacionMeteorologica> estacionEncontrada = estaciones.stream().filter(estacion -> estacion.getCodigo().equals(codEstacion)).findFirst();

        if (estacionEncontrada.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código " + codEstacion);
        }

        EstacionMeteorologica estacion = estacionEncontrada.get();
        Optional<Sensor> sensorEncontrado = estacion.findSensorById(codSensor);

        if (sensorEncontrado.isEmpty()) {
            throw new MeteorologiaException("No existe un sensor con el código " + codSensor);
        }

        Sensor sensor = sensorEncontrado.get();
        estacion.cambiaEstadoSensor(sensor.getCodigo(), sensor.getMarca(), sensor.getModelo(), sensor.getTipo(), nuevoEstado);
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
        try {
            Object objeto = PersistenciaObjetos.leeObjeto("meteorologia.dat");
            if (!(objeto instanceof List)) {
                throw new MeteorologiaException("El archivo meteorologia.dat no contiene datos válidos del sistema.");
            }
            regiones = new ArrayList<>((List<Region>) objeto);
            estaciones = regiones.stream()
                    .flatMap(region -> Arrays.stream(region.getComunas()))
                    .flatMap(comuna -> Arrays.stream(comuna.getEstaciones()))
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        } catch (MeteorologiaException e) {
            regiones.clear();
            estaciones.clear();
            throw e;
        } catch (Exception e) {
            regiones.clear();
            estaciones.clear();
            throw new MeteorologiaException("No fue posible leer el archivo meteorologia.dat.");
        }
    }


    public void importaDatosTexto() throws MeteorologiaException {
        if (!regiones.isEmpty()) {
            throw new MeteorologiaException("La importación solo puede realizarse cuando no existen datos registrados.");
        }

        PersistenciaTexto persistencia = PersistenciaTexto.getInstance();
        List<String[]> registrosRegiones = persistencia.leeRegistros("regiones.txt");
        List<String[]> registrosComunas = persistencia.leeRegistros("comunas.txt");
        List<String[]> registrosEstaciones = persistencia.leeRegistros("estaciones.txt");
        List<String[]> registrosSensores = persistencia.leeRegistros("sensores.txt");
        List<String[]> registrosMediciones = persistencia.leeRegistros("mediciones.txt");

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

        List<String[]> sensoresActivos = new ArrayList<>();
        try {
            for (String[] registro : registrosRegiones) {
                int codigo = Integer.parseInt(registro[0]);
                String nombre = registro[1];
                creaRegion(codigo, nombre);
            }

            for (String[] registro : registrosComunas) {
                int codigoRegion = Integer.parseInt(registro[0]);
                int codigoComuna = Integer.parseInt(registro[1]);
                String nombre = registro[2];
                creaComuna(codigoComuna, nombre, codigoRegion);
            }

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

            for (String[] registro : registrosSensores) {
                String codigoEstacion = registro[0];
                String codigoSensor = registro[1];
                TipoSensor tipo = TipoSensor.valueOf(registro[2]);
                String marca = registro[3];
                String modelo = registro[4];
                Estado estado = Estado.valueOf(registro[5]);
                instalaSensor(codigoSensor, marca, modelo, tipo, codigoEstacion);

                for (String[] medicion : registrosMediciones) {if (medicion[0].equals(codigoEstacion) && medicion[1].equals(codigoSensor)) {
                        LocalDateTime fechaHora = LocalDateTime.parse(medicion[2], formato);
                        float valor = Float.parseFloat(medicion[3]);
                        registraMedicion(fechaHora, valor, codigoEstacion, codigoSensor);
                    }
                }
                cambiaEstadoSensor(codigoEstacion, codigoSensor, Estado.INACTIVO);
                if (estado == Estado.ACTIVO) {
                    sensoresActivos.add(registro);
                }
            }

            for (String[] registro : sensoresActivos) {
                cambiaEstadoSensor(registro[0], registro[1], Estado.ACTIVO);
            }

        } catch (MeteorologiaException | DateTimeParseException | IllegalArgumentException e) {
            regiones.clear();
            estaciones.clear();
            throw new MeteorologiaException("Error durante la importación de datos: " + e.getMessage() + ". No se cargó ningún dato.");
        }
    }


    public boolean existenDatos() {
        return !regiones.isEmpty();
    }

    public String[][] listaRegiones() {
        return regiones.stream().map(region -> {
            int estacionesActivas = Arrays.stream(region.getComunas())
                    .mapToInt(Comuna::getCantidadEstacionesActivas)
                    .sum();

            return new String[]{
                    String.valueOf(region.getCodigo()),
                    region.getNombre(),
                    String.valueOf(region.getComunas().length),
                    String.valueOf(region.getCantidadEstaciones()),
                    String.valueOf(estacionesActivas)
            };
        }).toArray(String[][]::new);
    }

    public String[][] listaComunas() {
        return regiones.stream()
                .flatMap(region -> Arrays.stream(region.getComunas())
                        .map(comuna -> new String[] {
                                String.valueOf(comuna.getCodigo()),
                                comuna.getNombre(),
                                region.getNombre(),
                                String.valueOf(comuna.getEstaciones().length),
                                String.valueOf(comuna.getCantidadEstacionesActivas())
                        }))
                .toArray(String[][]::new);
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) throws MeteorologiaException {
        Optional<Region> regionEncontrada = findRegion(codigoRegion);

        if (regionEncontrada.isEmpty()) {
            throw new MeteorologiaException("No existe una región con el código " + codigoRegion + ".");
        }
        Optional<Comuna> comunaEncontrada = findComuna(codigoRegion, codigoComuna);
        if (comunaEncontrada.isEmpty()) {
            throw new MeteorologiaException("No existe una comuna con el código " + codigoComuna + " en la región " + regionEncontrada.get().getNombre() + ".");
        }

        Comuna comuna = comunaEncontrada.get();

        return Arrays.stream(comuna.getEstaciones())
                .map(estacion -> {
                    long sensoresActivos = Arrays.stream(estacion.getSensores())
                            .filter(sensor -> sensor.getEstado() == Estado.ACTIVO)
                            .count();

                    return new String[]{
                            estacion.getCodigo(),
                            estacion.getNombre(),
                            estacion.getPosicion(),
                            estacion.getEstado().toString(),
                            String.valueOf(sensoresActivos)
                    };
                }).toArray(String[][]::new);
    }

    public String[][] listaSensores(String codigoEstacion) throws MeteorologiaException {
        Optional<EstacionMeteorologica> resultado = findEstacion(codigoEstacion);

        if (resultado.isEmpty()) {throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion + ".");
        }
        return resultado.get().getResumenSensores();
    }

    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {

        Optional<EstacionMeteorologica> resultadoEstacion = findEstacion(codEstacion);
        if (resultadoEstacion.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código " + codEstacion + ".");
        }

        Optional<Sensor> resultadoSensor = findSensor(codEstacion, codSensor);
        if (resultadoSensor.isEmpty()) {
            throw new MeteorologiaException("No existe el sensor " + codSensor + " en la estación " + codEstacion + ".");
        }

        if (fin.isBefore(inicio)) {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            throw new MeteorologiaException("La fecha y hora de término (" + fin.format(formato) + ") es anterior a la de inicio (" + inicio.format(formato) + ").");
        }
        return resultadoEstacion.get().getMedicionesSensorBetween(codSensor, inicio, fin);
    }

    public String[] listaResumenMediciones(String codigoEstacion, String codigoSensor, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
            throw new MeteorologiaException("No existe una estación con el código "+codEstacion+".");
        }

        return Arrays.stream(resultado.get().getSensores()).map(Sensor::getCodigo).toArray(String[]::new);
    }

    public String[] getDatosSensor(String codEstacion, String codSensor) throws MeteorologiaException {
        Optional<EstacionMeteorologica> estacionEncontrada = findEstacion(codEstacion);

        if (estacionEncontrada.isEmpty()) {
            throw new MeteorologiaException("No existe una estación con el código " + codEstacion + ".");
        }

        Optional<Sensor> sensorEncontrado = estacionEncontrada.get().findSensorById(codSensor);

        if (sensorEncontrado.isEmpty()) {
            throw new MeteorologiaException("No existe el sensor " + codSensor + " en la estación " + codEstacion + ".");
        }

        Sensor sensor = sensorEncontrado.get();
        String[] datos = {sensor.getTipo().toString(), sensor.getUnidad(), sensor.getEstado().toString()};
        return datos;
    }
}
