package Modelo;// Marcela Landaeta

import Controlador.MeteorologiaException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

// Uso de polimorfismo, al momento de hacer algun ArrayList de sensor (superclase),
//      se pueden guardar sus subclases en el (temperatura, humedad, etc)

public abstract class Sensor {
    private String codigo, marca, modelo;
    private Estado estado;
    private EstacionMeteorologica estacion; // Relación con Modelo.EstacionMeteorologica
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

    // Avance 3: Ciclos con lambda
    public void addMedicion(LocalDateTime fechaHora, float valor) throws MeteorologiaException {
        if (!esValorAdmisible(valor) || estado != Estado.ACTIVO) {
            throw new MeteorologiaException("Medición no pudo ser registrada.");
        }

        boolean existeMedicion = medicionesArraylist.stream().anyMatch(m -> m.getFechaHora().equals(fechaHora));

        if (existeMedicion) {
            throw new MeteorologiaException("Medición no pudo ser registrada.");
        }
        medicionesArraylist.add(new Medicion(fechaHora, valor));
    }

    // Avance 3: Ciclos con lambda
    public Medicion[] getMediciones() {
        ArrayList<Medicion> resultadoArrayList = new ArrayList<>(medicionesArraylist);
        return medicionesArraylist.stream().sorted(Comparator.comparing(Medicion::getFechaHora)).toArray(Medicion[]::new);
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

    public Optional<ResumenMediciones> getResumenBetween(LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        Medicion[] mediciones = medicionesArraylist.stream()
                .filter(m -> !m.getFechaHora().isBefore(inicio)
                        && !m.getFechaHora().isAfter(fin))
                .sorted(Comparator.comparing(Medicion::getFechaHora))
                .toArray(Medicion[]::new);
        ResumenMediciones resumen = new ResumenMediciones(mediciones);
        return Optional.of(resumen);
    }

    public abstract TipoSensor getTipo();

    public abstract String getUnidad();

    public abstract boolean esValorAdmisible(float valor);
}