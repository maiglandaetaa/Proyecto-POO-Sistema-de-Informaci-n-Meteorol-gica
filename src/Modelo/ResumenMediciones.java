package Modelo;
// Marcela Landaeta

import Controlador.MeteorologiaException;

import java.util.Arrays;
import java.util.Comparator;

public class ResumenMediciones {
    private final int cantidad;
    private final float promedio;
    private final Medicion minima, maxima;

    public ResumenMediciones(Medicion[] mediciones) throws MeteorologiaException{
        if (mediciones == null || mediciones.length == 0) {
            throw new MeteorologiaException("Arreglo de mediciones no puede estar vacío");
        }
        this.cantidad = mediciones.length;

        this.minima = Arrays.stream(mediciones).min(Comparator.comparing(Medicion::getValor)).get();
        this.maxima = Arrays.stream(mediciones).max(Comparator.comparing(Medicion::getValor).thenComparing(Medicion::getFechaHora, Comparator.reverseOrder())).get();
        this.promedio = (float) Arrays.stream(mediciones).mapToDouble(Medicion::getValor).average().orElse(0);
    }

    public int getCantidad() {
        return cantidad;
    }

    public float getPromedio() {
        return promedio;
    }

    public Medicion getMinimo() {
        return minima;
    }

    public Medicion getMaximo() {
        return maxima;
    }
}
