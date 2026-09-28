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
        if (estado != Estado.ACTIVO) {
            return false;
        }

        for (Sensor sensor : sensores) {
            if (sensor.getCodigo().equals(codigo)) {
                return false;
            }

            if (sensor.getEstado() == tipo && sensor.getEstado() == Estado.ACTIVO) {
                return false;
            }
        }

        return false;
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
