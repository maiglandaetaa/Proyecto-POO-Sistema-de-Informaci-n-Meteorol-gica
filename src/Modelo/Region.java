//Felipe Venegas
package Modelo;

import java.util.ArrayList;
import Controlador.MeteorologiaException;
import java.util.Optional;

public class Region {
    private int codigo;
    private String nombre;
    private ArrayList<Comuna> comunas;

    public Region(int cod, String nom){
        this.codigo = cod;
        this.nombre = nom;
        this.comunas = new ArrayList<>();
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public void addComuna(int cod, String nom) throws MeteorologiaException {

        if (comunas.stream().anyMatch(comuna -> comuna.getCodigo() == cod)) {
            throw new MeteorologiaException("La región " + nombre + " ya tiene una comuna con el código "+ cod + ".");
        }

        if (comunas.stream().anyMatch(comuna -> comuna.getNombre().equalsIgnoreCase(nom))) {
            throw new MeteorologiaException("La región "+ nombre+ " ya tiene una comuna con el nombre "+ nom+ ".");
        }
        comunas.add(new Comuna(cod, nom, this));
    }

    public Optional<Comuna> findComunaById(int codigo){
        return comunas.stream().filter(comuna -> comuna.getCodigo() == codigo).findFirst();
    }

    public Comuna[] getComunas(){
        return comunas.toArray(new Comuna[0]);
    }

    public int getCantidadEstaciones(){
        int total =0;
        for(Comuna comuna : comunas){
            total+=comuna.getEstaciones().length;
        }
        return total;
    }

}
