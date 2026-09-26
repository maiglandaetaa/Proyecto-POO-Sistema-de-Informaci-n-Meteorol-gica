import java.time.LocalDateTime;
import java.util.ArrayList;

// Uso de polimorfismo, al momento de hacer algun ArrayList de sensor (superclase),
//      se pueden guardar sus subclases en el (temperatura, humedad, etc)

public abstract class Sensor {
    private String codigo, marca, modelo;
    private Estado estado;
    private EstacionMeteorologica estacion; // Relación con EstacionMeteorologica
    private ArrayList<Medicion> medicionesArraylist; // Guardar multiples mediciones por 0...*

    protected Sensor(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        this.codigo = codigo;
        this.marca = marca;
        this.modelo = modelo;
        this.estacion = estacion;

        estado = Estado.ACTIVO;
        medicionesArraylist = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean addMedicion(LocalDateTime fechaHora, float valor) {
        if (!esValorAdmisible(valor) || estado != Estado.ACTIVO) {
            return false;
        }

        for (Medicion medicion : medicionesArraylist) {
            if (medicion.getFechaHora().equals(fechaHora)) {
                return false;
            }
        }

        medicionesArraylist.add(new Medicion(fechaHora, valor));
        return true;
    }

    public Medicion getLastMedicion() {
        if (medicionesArraylist.size() == 0) {
            return null;
        }
        Medicion ultimaMedicion = medicionesArraylist.get(0);

        for (Medicion medicion : medicionesArraylist) {
            if (medicion.getFechaHora().isAfter(ultimaMedicion.getFechaHora())) {
                ultimaMedicion = medicion;
            }
        }

        return ultimaMedicion;
    }

    // Se crea un arreglo de mediciones para luego comparar las fechas de cada medicion con las fechas
    //  vv  de inicio y fin. Las mediciones que cumplan con dicho intervalo, son guardadas en el arreglo

    public Medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin) {
        ArrayList<Medicion> resultadoArrayList = new ArrayList<>();

        for (Medicion medicion : medicionesArraylist) {
            LocalDateTime fecha = medicion.getFechaHora();
            if ((fecha.isEqual(inicio) || fecha.isAfter(inicio)) && (fecha.isBefore(fin) || fecha.isEqual(fin))) {
                resultadoArrayList.add(medicion);
            }
        }

        return resultadoArrayList.toArray(new Medicion[0]);
    }

    public abstract String getUnidad();

    public abstract boolean esValorAdmisible(float valor);
}