//Felipe Venegas
package Modelo;

import java.util.ArrayList;
import java.util.Optional;
import java.io.Serializable;

public class Comuna implements Serializable{

    private static final long serialVersionUID = 1L;

    private int codigo;
    private String nombre;
    private Region region;
    private ArrayList<EstacionMeteorologica> estaciones;

    public Comuna(int codigo, String nombre, Region region){
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
        this.estaciones = new ArrayList<>();
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public Region getRegion(){
        return region;
    }

    public void addEstacion(EstacionMeteorologica estacion){
        estaciones.add(estacion);
    }

    public Optional<EstacionMeteorologica> findEstacionById(String codigo) {
        return estaciones.stream()
                .filter(estacion -> estacion.getCodigo().equals(codigo))
                .findFirst();
    }

    public EstacionMeteorologica[] getEstaciones(){
        return estaciones.toArray(new EstacionMeteorologica[0]);
    }

    public int getCantidadEstacionesActivas(){
        return (int) estaciones.stream()
                .filter(estacion ->estacion.getEstado() == Estado.ACTIVO)
                .count();
    }
}
