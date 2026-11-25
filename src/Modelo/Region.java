//Felipe Venegas
package Modelo;

import java.util.ArrayList;
import Controlador.MeteorologiaException;
import java.util.Optional;
import java.io.Serializable;

public class Region implements Serializable {

    private static final long serialVersionUID = 1L;

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
            throw new MeteorologiaException("La región " + nombre + " ya tiene una comuna con el código " + cod + ".");
        }
        if (comunas.stream().map(Comuna::getNombre).anyMatch(nom::equalsIgnoreCase)) {
                throw new MeteorologiaException("La región " + nombre + " ya tiene una comuna con el nombre " + nom + ".");
        }
        comunas.add(new Comuna(cod, nom, this));
    }

    public Optional<Comuna> findComunaById(int codigo){
        return comunas.stream()
                .filter(comuna -> comuna.getCodigo() == codigo)
                .findFirst();
    }

    public Comuna[] getComunas(){
        return comunas.toArray(new Comuna[0]);
    }

    public int getCantidadEstaciones(){
      return comunas.stream()
              .mapToInt(comuna -> comuna.getEstaciones().length)
              .sum();
    }
}
