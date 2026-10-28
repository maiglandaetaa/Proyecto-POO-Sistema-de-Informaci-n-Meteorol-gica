package Modelo;
// Marcela Landaeta

import Controlador.MeteorologiaException;

public class ResumenMediciones {
    private final int cantidad;
    private final float promedio;
    private final Medicion minima, maxima;

    public ResumenMediciones(Medicion[] mediciones) throws MeteorologiaException{
        if (mediciones == null || mediciones.length == 0) {
            throw new MeteorologiaException("Arreglo de mediciones no puede estar vacío");
        }
        this.cantidad = mediciones.length;
        float suma = 0;
        Medicion menor = mediciones[0];
        Medicion mayor = mediciones[0];

        for (Medicion medicion : mediciones) {
            suma += medicion.getValor();
            if (medicion.getValor() > mayor.getValor() || (medicion.getValor() == mayor.getValor() && medicion.getFechaHora().isBefore(mayor.getFechaHora()))) {
                mayor = medicion;
            }
            if (medicion.getValor() < menor.getValor() || (medicion.getValor() == menor.getValor() && medicion.getFechaHora().isBefore(menor.getFechaHora()))) {
                menor = medicion;
            }
        }
        minima = menor;
        maxima = mayor;
        this.promedio = suma / cantidad;
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
