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
        for (Region region : regiones) {
            if(region.getCodigo() == codigo && region.getNombre().equalsIgnoreCase(nombre)) {
                throw new MeteorologiaException("Ya existe una región con ese código y nombre.");
            } else if (region.getCodigo() == codigo) {
                throw new MeteorologiaException("Ya existe una región con el código "+ codigo);
            } else if (region.getNombre().equalsIgnoreCase(nombre)) {
                throw new MeteorologiaException("Ya existe una región con el nombre " + nombre);
            }
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
        ArrayList<String> lineasRegiones = new ArrayList<>();
        ArrayList<String> lineasComunas = new ArrayList<>();
        ArrayList<String> lineasEstaciones = new ArrayList<>();
        ArrayList<String> lineasSensores = new ArrayList<>();
        ArrayList<String> lineasMediciones = new ArrayList<>();

        for (Region region : regiones) {
            lineasRegiones.add(region.getCodigo()+ ";" +region.getNombre());

            for (Comuna comuna : region.getComunas()) {
                lineasComunas.add(region.getCodigo()+";"+comuna.getCodigo()+";"+comuna.getNombre());
            }
        }

        for (EstacionMeteorologica estacion : estaciones) {
            String[] posicion = estacion.getPosicion().replace("(", "").replace(")", "").split(";");

            lineasEstaciones.add(estacion.getCodigo()+";"+estacion.getNombre()+";"+posicion[0]+";"+posicion[1]+";"+posicion[2]+";"+estacion.getComuna().getRegion().getCodigo()+";"+estacion.getComuna().getCodigo());

            for (Sensor sensor : estacion.getSensores()) {
                lineasSensores.add(estacion.getCodigo()+";"+sensor.getCodigo()+";"+sensor.getTipo()+";"+sensor.getMarca()+";"+sensor.getModelo()+";"+sensor.getEstado());

                for (Medicion medicion : sensor.getMediciones()) {
                    lineasMediciones.add(estacion.getCodigo()+";"+sensor.getCodigo()+";"+medicion.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))+";"+medicion.getValor());
                }
            }
        }

        PersistenciaTexto.escribeRegistros("regiones.txt", lineasRegiones);
        PersistenciaTexto.escribeRegistros("comunas.txt", lineasComunas);
        PersistenciaTexto.escribeRegistros("estaciones.txt", lineasEstaciones);
        PersistenciaTexto.escribeRegistros("sensores.txt", lineasSensores);
        PersistenciaTexto.escribeRegistros("mediciones.txt", lineasMediciones);
    }

    public void cargaDatos() throws MeteorologiaException {
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
        String[][] resultado = new String[regiones.size()][4];
        int i=0;

        for (Region region : regiones) { //por cada region devuelve:
            resultado[i][0] = String.valueOf(region.getCodigo()); //cod
            resultado[i][1] = region.getNombre();//nombre
            resultado[i][2] = String.valueOf(region.getComunas().length);//comunas
            resultado[i][3] = String.valueOf(region.getCantidadEstaciones());//cantidad de estaciones

            i++;
        }
        return resultado;
    }

    public String[][] listaComunas() {
        ArrayList<String[]> resultado = new ArrayList<>();

        for (Region region : regiones) {
            for (Comuna comuna : region.getComunas()) {
                String[] datos = new String[5];

                datos[0] = String.valueOf(comuna.getCodigo());
                datos[1] = String.valueOf(comuna.getNombre());
                datos[2] = String.valueOf(comuna.getEstaciones().length);
                datos[3] = String.valueOf(comuna.getCantidadEstacionesActivas());
                datos[4] = String.valueOf(region.getNombre());
                // Marce: Faltaba región ^^^
                resultado.add(datos);
            }
        }
        return resultado.toArray(new String[0][]);
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {
        for (Region region : regiones) {
            if (region.getCodigo() == codigoRegion) {
                Optional<Comuna> comunaEncontrada = region.findComunaById(codigoComuna);

                if (comunaEncontrada.isEmpty()) {return new String[0][0];}

                Comuna comuna = comunaEncontrada.get();
                ArrayList<String[]> resultado = new ArrayList<>();

                for (EstacionMeteorologica estacion : estaciones) {
                    String[] datos = estacion.toString().split("; ");
                    String codigoEstacion = datos[0];

                    Optional<EstacionMeteorologica> encontrada = comuna.findEstacionById(codigoEstacion);

                    if (encontrada.isPresent()) {
                        int sensoresActivos = 0;

                        for (Sensor sensor : estacion.getSensores()) {
                            if (sensor.getEstado() == Estado.ACTIVO) {
                                sensoresActivos++;
                            }
                        }

                        datos[4] = String.valueOf(sensoresActivos);
                        resultado.add(datos);
                    }
                }
                return resultado.toArray(new String[0][]);
            }
        }
        return new String[0][0];
    }

    public String[][] listaSensores(String codigoEstacion) {
        for (EstacionMeteorologica estacion : estaciones) {
            String codigoEstacionActual = estacion.toString().split(";")[0];

            if (codigoEstacionActual.equals(codigoEstacion)) {
                return estacion.getResumenSensores();
            }
        }
        return new String[0][0];
    }

    public String [][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {
        for (EstacionMeteorologica estacion: estaciones) {
            String codigoEstacionActual = estacion.toString().split(";")[0];

            if (codigoEstacionActual.equals(codEstacion)) {
                return estacion.getMedicionesSensorBetween(codSensor, inicio, fin);
            }
        }

        return new String[0][0];
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

        if (alertas.length == 0) {
            return new String [0][0];
        }

        Arrays.sort(alertas, Comparator.comparing(AlertaMeteorologica::getFechaHora));

        String[][] resultado = new String[alertas.length][7];

        for (int i = 0; i < alertas.length; i++) {
            AlertaMeteorologica alerta = alertas[i];
            Sensor sensor = alerta.getSensor();

            resultado[i][0] = alerta.getFechaHora().format(formatoFecha);
            resultado[i][1] = alerta.getFechaHora().format(formatoHora);
            resultado[i][2] = sensor.getCodigo();
            resultado[i][3] = alerta.getFenomeno();
            resultado[i][4] = String.format(java.util.Locale.US, "%.1f", alerta.getValor());
            resultado[i][5] = sensor.getUnidad();
            resultado[i][6] = alerta.getNivel().toString();
        }

        return resultado;
    }

    private Optional findEstacion(String codigo) {
        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equalsIgnoreCase(codigo)) {
                return Optional.of(estacion);
            }
        }
        return Optional.empty();
    }

    private Optional findRegion(int codigo) {
        for (Region region : regiones) {
            if (region.getCodigo() == codigo) {
                return Optional.of(region);
            }
        }
        return Optional.empty();
    }

    private Optional findComuna(int codigoRegion, int codigoComuna) {

        Optional<Region> resultadoRegion = findRegion(codigoRegion);
        if (resultadoRegion.isEmpty()) {
            return Optional.empty();
        }
        Region region = resultadoRegion.get();
        for (Comuna comuna : region.getComunas()) {
            if (comuna.getCodigo() == codigoComuna) {
                return Optional.of(comuna);
            }
        }
        return Optional.empty();
    }

    private Optional findSensor(String codigoEstacion, String codigoSensor) {
        Optional<EstacionMeteorologica> estacion = findEstacion(codigoEstacion);

        if (estacion.isEmpty()) {
            return Optional.empty();
        }

        for (Sensor sensor : estacion.get().getSensores()) {
            if (sensor.getCodigo().equalsIgnoreCase(codigoSensor)) {
                return Optional.of(sensor);
            }
        }

        return Optional.empty();
    }
}