//Felipe Venegas
import java.util.ArrayList;
public class Comuna {
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

    public void addEstacion(EstacionMeteorologica estacion){
        estaciones.add(estacion);
    }

    public EstacionMeteorologica findEstacionById(String codigo){
        for(EstacionMeteorologica estacion : estaciones){
            String codigoEstacion = estacion.toString().split(";")[0].trim();
            if(codigoEstacion.equals(codigo)){
                return estacion;
            }
        }
        return null;
    }

    public Region getRegion(){
        return region;
    }

    public int getCantidadEstaciones(){
        return estaciones.size();

    }

    public int getCantidadEstacionesActivas(){
        int CantEstActivas = 0;
        for(EstacionMeteorologica estacion : estaciones){
            if (estacion.toString().split(";")[3].trim().equals("ACTIVO")){
                CantEstActivas++;
            }
        }
        return CantEstActivas;
    }
}
