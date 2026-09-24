package Sensores;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Medicion {
    private LocalDateTime fechaHora;
    private float valor;

    public Medicion(LocalDateTime fechaHora, float valor) {
        this.fechaHora = fechaHora;
        this.valor = valor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public float getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Medicion medicion)) return false;
        return Float.compare(valor, medicion.valor) == 0 && Objects.equals(fechaHora, medicion.fechaHora);
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/mm/yyyy hh:mm");
        return fechaHora.format(formato) + "; " + valor;
    }
}
