package Modelo;

import Controlador.MeteorologiaException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    public EstacionMeteorologica(String cod, String nombre, float lon, float lat, float alt, Comuna comuna) {
        codigo = cod;
        this.nombre = nombre;
        longitud = lon;
        latitud = lat;
        altitud = alt;
        //atributo extra
        estado = Estado.ACTIVO; //queda con un estado activo al crearlo
        sensores = new ArrayList<>();
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

        for (Sensor sensor : sensores) {
            //No se crea si ya existe un sensor con el mismo código
            if (sensor.getCodigo().equals(codigo)) {
                throw new MeteorologiaException("La estación "+codigo+" ya tiene un sensor con el código "+sensor.getCodigo());
            }

            /* Marce: Se deben cumplir ambas condiciones para no añadirlo -> ||xx , &&oo
                Si el tipo del nuevo sensor es X, y el sensor revisado ya es X, no crear */
            //No lo crea si ya hay un sensor activo del mismo tipo
            if (sensor.getEstado() == Estado.ACTIVO) {
                if (tipo == TipoSensor.HUMEDAD && sensor instanceof SensorHumedad) {
                    throw new MeteorologiaException("La estación "+ codigo+" ya tiene un sensor ACTIVO de tipo "+tipo);
                }
                if (tipo == TipoSensor.TEMPERATURA && sensor instanceof SensorTemperatura) {
                    throw new MeteorologiaException("La estación "+ codigo+" ya tiene un sensor ACTIVO de tipo "+tipo);
                }
                if (tipo == TipoSensor.PRESION && sensor instanceof SensorPresion) {
                    throw new MeteorologiaException("La estación "+ codigo+" ya tiene un sensor ACTIVO de tipo "+tipo);
                }
                if (tipo == TipoSensor.VIENTO && sensor instanceof SensorViento) {
                    throw new MeteorologiaException("La estación "+ codigo+" ya tiene un sensor ACTIVO de tipo "+tipo);
                }
                if (tipo == TipoSensor.PRECIPITACION && sensor instanceof SensorPrecipitacion) {
                    throw new MeteorologiaException("La estación "+ codigo+" ya tiene un sensor ACTIVO de tipo "+tipo);
                }
            }
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
    public void cambiaEstadoSensor(String codigo, String marca, String modelo, TipoSensor tipo) {

    }
    public void registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) throws MeteorologiaException{
        if (estado != Estado.ACTIVO) {
            throw new MeteorologiaException("La estacion "+this.getCodigo()+" no está activa.");
        }

        // Avance 2 Marce: addMedicion cambiado a void y usa exception. Implementar try-catch
        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equals(codigoSensor)) {
                throw new MeteorologiaException("La estación "+this.getCodigo()+" ya tiene un sensor con el codigo "+codigoSensor);
            } else if(sensor.getEstado() != Estado.ACTIVO) {
                throw new MeteorologiaException("El sensor "+sensor.getCodigo()+" no está activo.");
            } else if () //valueOf pero no tengo idea de como hacerlo lmao
        }

    }

    public String toString () {
        return codigo + "; " + nombre + "; (" + longitud + ", " + latitud + ", " +
                altitud + "); " + estado + "; " + sensores.size();
    }
    public Sensor[] getSensores() { //no no tengo idea wei me quiero ir a acostar me duele la cabeza me voy a morir, perdon equipo hoy fallé
        Sensor[] sensor = new Sensores[sensores.size()];

        for (int i=0; i<sensores.size(); i++) {
            sensor
        }
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
            Medicion ultima = sensor.getLastMedicion();

            if (ultima == null) {
                datos[i][6] = "Sin mediciones.";
            } else {
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
}
