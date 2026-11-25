//Fernanda López
package Modelo;

import Controlador.MeteorologiaException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class EstacionMeteorologica {
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;
    //atributo extra
    private Comuna comuna;
    private ArrayList<Sensor> sensores;
    private ArrayList<AlertaMeteorologica> alertas;

    public EstacionMeteorologica(String cod, String nombre, float lon, float lat, float alt, Comuna comuna) {
        codigo = cod;
        this.nombre = nombre;
        longitud = lon;
        latitud = lat;
        altitud = alt;
        //atributo extra
        estado = Estado.ACTIVO; //queda con un estado activo al crearlo
        sensores = new ArrayList<>();
        alertas = new ArrayList<>();
        this.comuna = comuna;
        comuna.addEstacion(this);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPosicion() {
        return "(" + longitud + ";" + latitud + ";" + altitud + ")";
    }

    public Estado getEstado() {
        return estado;
    }

    public Comuna getComuna() {
        return comuna;
    }

    //CAMBIAR A VOID
    public void instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo) throws MeteorologiaException {
        //CONDICIONES PARA INSTALAR SENSOR

        //No se creara si no se encuentra activo
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estación "+codigo+" no esta activa.");
        }

        // Comprobar código duplicado
        if (sensores.stream().anyMatch(sensor ->
                sensor.getCodigo().equals(codigo))) {
            throw new MeteorologiaException("La estación " + codigo +
                    " ya tiene un sensor con el código " + codigo);
        }

        // Comprobar sensor activo del mismo tipo
        if (sensores.stream().anyMatch(sensor ->
                sensor.getEstado() == Estado.ACTIVO &&
                        (
                                (tipo == TipoSensor.HUMEDAD && sensor instanceof SensorHumedad) ||
                                        (tipo == TipoSensor.TEMPERATURA && sensor instanceof SensorTemperatura) ||
                                        (tipo == TipoSensor.PRESION && sensor instanceof SensorPresion) ||
                                        (tipo == TipoSensor.VIENTO && sensor instanceof SensorViento) ||
                                        (tipo == TipoSensor.PRECIPITACION && sensor instanceof SensorPrecipitacion)
                        ))) {
            throw new MeteorologiaException("La estación " + codigo +
                    " ya tiene un sensor ACTIVO de tipo " + tipo);
        }

        Sensor sensor = null;
        switch (tipo) {                                                             //this pk ya estamos en la estacion xd
            case TipoSensor.HUMEDAD -> {sensor = new SensorHumedad(codigo, marca, modelo,this);}
            case TipoSensor.TEMPERATURA -> {sensor = new SensorTemperatura(codigo, marca, modelo, this);}
            case TipoSensor.PRESION -> {sensor = new SensorPresion(codigo, marca, modelo, this);}
            case TipoSensor.VIENTO -> {sensor = new SensorViento(codigo, marca, modelo, this);}
            case TipoSensor.PRECIPITACION -> {sensor = new SensorPrecipitacion(codigo, marca, modelo, this);}
        }
        sensores.add(sensor);
    }

    public void cambiaEstadoSensor(String codigo, String marca, String modelo, TipoSensor tipo) throws MeteorologiaException {
        Optional<Sensor> sensorEncontrado = sensores.stream().filter(sensor -> sensor.getCodigo().equals(codigo) && sensor.getMarca().equals(marca) &&
                                sensor.getModelo().equals(modelo) && sensor.getTipo() == tipo)
                .findFirst();

        if (sensorEncontrado.isEmpty()) {
            throw new MeteorologiaException("No existe un sensor con el código " + codigo);
        }

        Sensor sensor = sensorEncontrado.get();

        if (sensor.getEstado() == Estado.ACTIVO) {
            sensor.setEstado(Estado.INACTIVO);
        } else {
            boolean existeActivo = sensores.stream().anyMatch(otro -> otro != sensor &&
                                    otro.getTipo() == tipo && otro.getEstado() == Estado.ACTIVO);

            if (existeActivo) {
                throw new MeteorologiaException("Ya existe un sensor activo de tipo " + tipo);
            }

            sensor.setEstado(Estado.ACTIVO);
        }
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) throws MeteorologiaException{
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estacion "+this.getCodigo()+" no está activa.");
        }

        // Avance 2 Marce: addMedicion cambiado a void y usa exception. Implementar try-catch
        boolean encontrada = false;

        for (Sensor sensor : sensores) { //valueOf pero no tengo idea de como hacerlo lmao
            if (sensor.getCodigo().equals(codigoSensor)) {
                encontrada = true;
                if (sensor.getEstado() != Estado.ACTIVO) {
                    throw new MeteorologiaException("El sensor "+sensor.getCodigo()+" no está activo.");
                }
                sensor.addMedicion(fechaHora, valor);
                if (sensor instanceof Alertable alertable) {
                    if (alertable.generaAlerta(valor)) {
                        NivelAlerta nivel = alertable.evaluaAlerta(valor).get();
                        AlertaMeteorologica alerta = new AlertaMeteorologica(fechaHora, valor, nivel, alertable.getFenomeno(valor), sensor);
                        alertas.add(alerta);
                    }
                }
                break;
            }
        }

        if (!encontrada) {
            throw new MeteorologiaException("El sensor"+codigoSensor+" no existe en la estación "+this.getCodigo()+".");
        }

    }

    public String toString () {
        return codigo + "; " + nombre + "; (" + longitud + ", " + latitud + ", " + altitud + "); " + estado + "; " + sensores.size();
    }

    public Sensor[] getSensores() { //no no tengo idea wei me quiero ir a acostar me duele la cabeza me voy a morir, perdon equipo hoy fallé
        Sensor[] sensor = new Sensor[sensores.size()];

        for (int i=0; i<sensores.size(); i++) {
            sensor[i] = sensores.get(i);
        }
        return sensor;
    }

    public String[][] getResumenSensores() {
        String[][] datos = new String [sensores.size()][7];

        for (int i=0; i<sensores.size(); i++) {
            Sensor sensor = sensores.get(i);

            datos[i][0] = sensor.getCodigo();
            datos[i][1] = sensor.getClass().getSimpleName();
            datos[i][2] = sensor.getMarca();
            datos[i][3] = sensor.getModelo();
            datos[i][4] = sensor.getUnidad();
            datos[i][5] = sensor.getEstado().toString();
            // Avance 2 Marce: Metodo eliminado, modifique código
            Medicion[] mediciones = sensor.getMediciones();

            if (mediciones.length == 0) {
                datos[i][6] = "Sin mediciones.";
            } else {
                Medicion ultima = mediciones[mediciones.length - 1];
                datos[i][6] = ultima.toString() + " " + sensor.getUnidad();
            }

        }
        return datos;
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin) {
        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equals(codigoSensor)) {
                Medicion[] mediciones = sensor.getMedicionesBetween(inicio, fin);

                String[][] datos = new String[mediciones.length][4];

                DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm");

                for (int i=0; i< mediciones.length; i++) {
                    datos[i][0] = mediciones[i].getFechaHora().format(fecha);
                    datos[i][1] = mediciones[i].getFechaHora().format(hora);
                    datos[i][2] = String.valueOf(mediciones[i].getValor());
                    datos[i][3] = sensor.getUnidad();
                }

                return datos;
            }
        }
        return new String[0][0];
    }

    public AlertaMeteorologica[] getAlertasBetween(LocalDateTime inicio, LocalDateTime fin) {
        return alertas.stream().filter(alerta -> {LocalDateTime fecha = alerta.getFechaHora();
            return !fecha.isBefore(inicio) && !fecha.isAfter(fin);}).sorted(Comparator.comparing(AlertaMeteorologica::getFechaHora))
                .toArray(AlertaMeteorologica[]::new);
    }

    public Optional<Sensor> findSensorById(String codigo) {
        return sensores.stream().filter(sensor -> sensor.getCodigo().equals(codigo)).findFirst();
    }
}