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
        if (findComunaById(cod).isPresent()) {
            throw new MeteorologiaException("La región de " + nombre + " ya tiene una comuna con el código " + cod + ".");
        }
        for (Comuna comuna : comunas) {
            if (comuna.getNombre().equalsIgnoreCase(nom)) {
                throw new MeteorologiaException("La región de " + nombre + " ya tiene una comuna con el nombre " + nom + ".");
            }
        }
        comunas.add(new Comuna(cod, nom, this));
    }

    public Optional findComunaById(int codigo){
        for (Comuna comuna : comunas){
            if(comuna.getCodigo() == codigo){
                return comuna;
            }
        }
        return null;
    }

    public Comuna[] getComunas(){
        return comunas.toArray(new Comuna[0]);
    }

    public int getCantidadEstaciones(){
        int total =0;
        for(Comuna comuna : comunas){
            total+=comuna.getCantidadEstaciones();
        }
        return total;
    }

    private boolean existeComuna(int cod, String nom){
        for(Comuna comuna : comunas){
            if(comuna.getCodigo() == cod || comuna.getNombre().equalsIgnoreCase(nom)){
                return true;
            }
        }
        return false;
    }
}
