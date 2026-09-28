//Felipe Venegas
public class Comuna {
    private int codigo;
    private String nombre;
    private Region region;

    public Comuna(int codigo, String nombre, Region region){
        this.codigo = codigo;
        this.nombre = nombre;
        this.region = region;
    }

    public int getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public void addEstacion(EstacionMeteorologica estacion){
        estaciones.add(estacion);
    }

    public EstacionMeteorologica findEstacionById(String codigo){

    }

    public Region getRegion(){

    }

    public int getCantidadEstaciones(){

    }

    public int getCantidadEstacionesActivas(){

    }
}
