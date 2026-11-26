package Modelo;
// Marcela Landaeta

import java.io.Serializable;
import java.time.LocalDateTime;

public class AlertaMeteorologica implements Serializable {
    final private LocalDateTime fechaHora;
    final private float valor;
    final private NivelAlerta nivel;
    final private String fenomeno;
    final private Sensor sensor;
    private static final long serialVersionUID = 1L;

    public AlertaMeteorologica(LocalDateTime fechaHora, float valor, NivelAlerta nivel, String fenom, Sensor sensor) {
        this.fechaHora = fechaHora;
        this.valor = valor;
        this.nivel = nivel;
        this.fenomeno = fenom;
        this.sensor = sensor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public float getValor() {
        return valor;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getFenomeno() {
        return fenomeno;
    }

    public Sensor getSensor() {
        return sensor;
    }
}
