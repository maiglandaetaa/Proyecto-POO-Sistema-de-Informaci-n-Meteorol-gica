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
            // Marce: Se podían añadir comunas duplicadas porque la creaba antes de verificar
            if (region.getCodigo() == codigoRegion) { //si la encuentra crea la comuna:
                Comuna comuna = new Comuna(codigo, nombre, region);
                /* //y aqui se agrega :)
                region.addComuna(comuna.getCodigo(), comuna.getNombre());
                //y devolvemos true obviouslyy */
                return region.addComuna(comuna.getCodigo(), comuna.getNombre());
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

        for (EstacionMeteorologica estacion : estaciones) {
            String codigoEstacion = estacion.toString().split(";")[0];
            if (codigoEstacion.equals(codEstacion)) {
                return estacion.registraMedicion(fechaHora, valor, codSensor);
            }
        }
        return false;
    }

    public String[][] listaRegiones() {
        String[][] resultado = new String[regiones.size()][4];
        int i=0;

        for (Region region : regiones) { //por cada region devuelve:
            resultado[i][0] = String.valueOf(region.getCodigo()); //cod
            resultado[i][1] = region.getNombre();//nombre
            resultado[i][2] = String.valueOf(region.getComunas().length);//comunas
            resultado[i][3] = String.valueOf(region.getCantidadEstaciones());//cantidad de estaciones

            i++;
        }
        return resultado;
    }

    public String[][] listaComunas() {
        ArrayList<String[]> resultado = new ArrayList<>();

        for (Region region : regiones) {
            for (Comuna comuna : region.getComunas()) {
                String[] datos = new String[5];

                datos[0] = String.valueOf(comuna.getCodigo());
                datos[1] = String.valueOf(comuna.getNombre());
                datos[2] = String.valueOf(comuna.getCantidadEstaciones());
                datos[3] = String.valueOf(comuna.getCantidadEstacionesActivas());
                datos[4] = String.valueOf(comuna.getNombre());
                // Marce: Faltaba región ^^^
                resultado.add(datos);
            }
        }
        return resultado.toArray(new String[0][]);
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {
        for (Region region : regiones) {
            if (region.getCodigo() == codigoRegion) {
                Comuna comuna = region.findComunaById(codigoComuna);

                if (comuna == null) {
                    return new String[0][0];
                }

                ArrayList<String[]> resultado = new ArrayList<>();

                for (EstacionMeteorologica estacion : estaciones) {
                    String[] datos = estacion.toString().split("; ");
                    String codigoEstacion = datos[0];

                    EstacionMeteorologica encontrada = comuna.findEstacionById(codigoEstacion);
                    // Marce: No indica sensores activos, no lo supe solucionar
                    if (encontrada != null) {
                        resultado.add(datos);
                    }
                }
                return resultado.toArray(new String[0][]);
            }
        }
        return new String[0][0];
    }

    public String[][] listaSensores(String codigoEstacion) {
        for (EstacionMeteorologica estacion : estaciones) {
            String codigoEstacionActual = estacion.toString().split(";")[0];

            if (codigoEstacionActual.equals(codigoEstacion)) {
                return estacion.getResumenSensores();
            }
        }
        return new String[0][0];
    }

    public String [][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {
        for (EstacionMeteorologica estacion: estaciones) {
            String codigoEstacionActual = estacion.toString().split(";")[0];

            if (codigoEstacionActual.equals(codEstacion)) {
                return estacion.getMedicionesSensorBetween(codSensor, inicio, fin);
            }
        }

        return new String[0][0];
    }
}
