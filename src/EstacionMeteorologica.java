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
        String[][] datos = new String [sensores.size()][7];

        for (int i=0; i<sensores.size(); i++) {
            Sensor sensor = sensores.get(i);

            datos[i][0] = sensor.getCodigo();
            datos[i][1] = sensor.getClass().getSimpleName();
            datos[i][2] = sensor.getMarca();
            datos[i][3] = sensor.getModelo();
            datos[i][4] = sensor.getUnidad();
            datos[i][5] = sensor.getEstado().toString();

            Medicion ultima = sensor.getLastMedicion();

            if (ultima == null) {
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
