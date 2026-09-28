import java.time.LocalDateTime;
import java.util.ArrayList;

public class InstitutoMeteorologia {
    private ArrayList<Region> regiones = new ArrayList<>();
    private ArrayList<EstacionMeteorologica> estaciones = new ArrayList<>();

    public boolean creaRegion(int codigo, String nombre) {
        for (Region region : regiones) {
            if(region.getCodigo() == codigo || region.getNombre().equalsIgnoreCase(nombre)) {
                return false;
            }
        }
        Region region = new Region(codigo, nombre);
        regiones.add(region);
        return true;
    }

    public boolean creaComuna(int codigo, String nombre, int codigoRegion) {
        for (Region region : regiones) { //busca codigo en las regiones que coincida
            if (region.getCodigo() == codigoRegion) { //si la encuentra crea la comuna:
                Comuna comuna = new Comuna(codigo, nombre, region);
                //y aqui se agrega :)
                region.addComuna(comuna.getCodigo(), comuna.getNombre());
                //y devolvemos true obviouslyy
                return true;
            }
        }
        return false;
    }

    public boolean creaEstacion(String cod, String nombre, float lon, float lat, float alt, int codRegion, int codComuna) {

        //saca el codigo del toString
        for (EstacionMeteorologica estacion : estaciones) {
            String codigoEstacion = estacion.toString().split(";")[0];

            if (codigoEstacion.equals(cod)) {
                return false;
            }
        }

        for (Region region : regiones) {
            if (region.getCodigo() == codRegion) {
                Comuna comuna = region.findComunaById(codComuna);

                if (comuna == null) {
                    return false;
                }
                EstacionMeteorologica estacion = new EstacionMeteorologica(cod, nombre, lon, lat, alt, comuna);
                estaciones.add(estacion);

                return true;
            }
        }
        return false;
    }

    public boolean instalaSensor(String cod, String modelo, String marca, TipoSensor tipo, String codigoEstacion) {
        for (EstacionMeteorologica estacion : estaciones) {
            String codigoEstacionActual = estacion.toString().split(";")[0];
            //permite crear el sensor si es que existe la estacion :)
            if (codigoEstacionActual.equals(codigoEstacion)) {
                return estacion.instalaSensor(cod, modelo, marca, tipo);
            }
        }
        return false;
    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) {
        return false;
    }

    public String[][] listaRegiones() {
        return "";
    }

    public String[][] listaComunas() {
        return"";
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {
        return "";
    }

    public String[][] listaSensores(String codigoEstacion) {
        return"";
    }

    public String [][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {
        return "";
    }
}
