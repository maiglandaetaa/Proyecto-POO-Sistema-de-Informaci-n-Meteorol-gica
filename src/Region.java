import java.util.ArrayList;

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

    public boolean addComuna(int cod, String nom){
        if(existeComuna(cod, nom)){
            return false;
        }
        Comuna comunaNueva = new Comuna(cod, nom, this);
        return comunas.add(comunaNueva);
    }

    public Comuna findComunaById(int codigo){
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
