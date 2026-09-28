import java.time.LocalDateTime;
import java.util.ArrayList;

public class EstacionMeteorologica {
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;

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
    }

    public boolean instalaSensor(String codigo, String modelo, String marca, TipoSensor tipo) {
        //CONDICIONES PARA INSTALAR SENSOR

        //No se creara si no se encuentra activo
        if (estado != Estado.ACTIVO) {
            return false;
        }

        for (Sensor sensor : sensores) {
            //No se crea si ya existe un sensor con el mismo código
            if (sensor.getCodigo().equals(codigo)) {
                return false;
            }

            //No lo crea si ya hay un sensor activo del mismo tipo
            if (sensor.getEstado() == Estado.ACTIVO) {
                if (tipo == TipoSensor.HUMEDAD || sensor instanceof SensorHumedad) {
                    return false;
                }
                if (tipo == TipoSensor.TEMPERATURA || sensor instanceof SensorTemperatura) {
                    return false;
                }
                if (tipo == TipoSensor.PRESION || sensor instanceof SensorPresion) {
                    return false;
                }
                if (tipo == TipoSensor.VIENTO || sensor instanceof SensorViento) {
                    return false;
                }
                if (tipo == TipoSensor.PRECIPITACION || sensor instanceof SensorPrecipitacion) {
                    return false;
                }
            }
        }
        Sensor sensor = null;
        switch (tipo) {                                                             //this pk ya estamos en la estacion xd
            case HUMEDAD -> {sensor = new SensorHumedad(codigo, marca, modelo,this);}
            case TEMPERATURA -> {sensor = new SensorTemperatura(codigo, marca, modelo, this);}
            case PRESION -> {sensor = new SensorPresion(codigo, marca, modelo, this);}
            case VIENTO -> {sensor = new SensorViento(codigo, marca, modelo, this);}
            case PRECIPITACION -> {sensor = new SensorPrecipitacion(codigo, marca, modelo, this);}
        }
        return true;
    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) {
        return false;
    }

    public String toString () {
        return codigo + "; " + nombre + ": (" + longitud + ", " + latitud + ", " +
                altitud + "); " + estado + "; " + sensores.size();
    }

    public String[][] getResumenSensores() {
        return "";
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin) {
        return "";
    }
}
