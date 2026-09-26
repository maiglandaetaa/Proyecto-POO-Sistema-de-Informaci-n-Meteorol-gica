import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Medicion {
    private final LocalDateTime fechaHora;
    private final float valor;

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
        if (!(o instanceof Medicion medicion)) {
            return false;
        }
        return Objects.equals(fechaHora, medicion.fechaHora);
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaHora.format(formato) + "; " + valor;
    }
}