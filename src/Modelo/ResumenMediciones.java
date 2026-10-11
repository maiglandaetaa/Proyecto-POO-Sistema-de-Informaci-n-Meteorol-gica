package Modelo;

import Controlador.MeteorologiaException;

public class ResumenMediciones {
    private final int cantidad;
    private final float promedio;
    private final Medicion medicionMinima, medicionMaxima;

    public ResumenMediciones(Medicion[] mediciones) throws MeteorologiaException{
        if (mediciones == null || mediciones.length == 0) {
            throw new MeteorologiaException("Arreglo de mediciones no puede estar vacío");
        }
        this.cantidad = mediciones.length;
        float suma = 0;
        Medicion minima = mediciones[0];
        Medicion maxima = mediciones[0];

        for (Medicion medicion : mediciones) {
            suma += medicion.getValor();
            if (medicion.getValor() > maxima.getValor() || (medicion.getValor() == maxima.getValor() && medicion.getFechaHora().isBefore(maxima.getFechaHora()))) {
                maxima = medicion;
            }
            if (medicion.getValor() < minima.getValor() || (medicion.getValor() == minima.getValor() && medicion.getFechaHora().isBefore(minima.getFechaHora()))) {
                minima = medicion;
            }
        }
        medicionMinima = minima;
        medicionMaxima = maxima;
        this.promedio = suma / cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public float getPromedio() {
        return promedio;
    }

    public Medicion getMinimo() {
        return medicionMinima;
    }

    public Medicion getMaximo() {
        return medicionMaxima;
    }
}
