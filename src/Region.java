public class Region {
    private int codigo;
    private String nombre;

    public Region (int cod, String nom){
        this.codigo = cod;
        this.nombre = nom;
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public boolean addComuna(int cod, String nom){

    }

    public Comuna findComunaByld(int codigo){

    }
    public Comuna[] getComunas(){

    }

    public int getCantidadEstaciones(){

    }
}