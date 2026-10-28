package Modelo;
// Marcela Landaeta

import java.time.LocalDateTime;

public class AlertaMeteorologica {
    final private LocalDateTime fechaHora;
    final private float valor;
    final private NivelAlerta nivel;
    final private String fenomeno;
    final private Sensor sensor;

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
