package Controlador;

import Modelo.Comuna;
import Modelo.EstacionMeteorologica;
import Modelo.Region;
import Modelo.TipoSensor;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class InstitutoMeteorologia {
    private ArrayList<Region> regiones = new ArrayList<>();
    private ArrayList<EstacionMeteorologica> estaciones = new ArrayList<>();

    public void creaRegion(int codigo, String nombre) throws MeteorologiaException {
        for (Region region : regiones) {
            if(region.getCodigo() == codigo && region.getNombre().equalsIgnoreCase(nombre)) {
                throw new MeteorologiaException("Ya existe una región con ese código y nombre.");
            } else if (region.getCodigo() == codigo) {
                throw new MeteorologiaException("Ya existe una región con el código "+ codigo);
            } else if (region.getNombre().equalsIgnoreCase(nombre)) {
                throw new MeteorologiaException("Ya existe una región con el nombre " + nombre);
            }
        }

        Region region = new Region(codigo, nombre);
        regiones.add(region);
    }

    public void creaComuna(int codigo, String nombre, int codigoRegion) throws MeteorologiaException {
        boolean existeRegion = false;

        for (Region region : regiones) {
            if (region.getCodigo() == codigoRegion) {
                existeRegion = true;
            }

            for (Comuna comuna : region.getComunas()) {
                if (comuna.getCodigo() == codigo) {
                    throw new MeteorologiaException("La región " + region.getNombre() + " ya tiene una comuna con el código " + codigo);
                } else if(comuna.getNombre().equalsIgnoreCase(nombre)) {
                    throw new MeteorologiaException("La región "+ region.getNombre()+" ya tiene una comuna con el nombre "+nombre);
                }
            }

            region.addComuna(codigo, nombre);
            return;
        }

        if (!existeRegion) {
            throw new MeteorologiaException("No existe una región con el código" + codigoRegion);
        }

    }

    public void creaEstacion(String cod, String nombre, float lon, float lat, float alt, int codRegion, int codComuna) throws MeteorologiaException {
        //Condicion: no existe otra estacion con el mismo codigo
        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equalsIgnoreCase(cod)) {
                throw new MeteorologiaException("Ya existe una estación con el código " + cod);
            }
        }

        //Condicion: que no exista una region con el codigo
        Region regionEncontrada = null;

        //Busca la región
        for (Region region : regiones) {
            if (region.getCodigo() == codRegion) {
                regionEncontrada = region;
                break;
            }
        }

        if (regionEncontrada == null) {
            throw new MeteorologiaException("No existe una región con el código " + codRegion);
        }

        //Condicion: que no exista una comuna con tal codigo en la region
        //Busca la comuna dentro de esa región
        Comuna comunaEncontrada = null;

        for (Comuna comuna : regionEncontrada.getComunas()) {
            if (comuna.getCodigo() == codComuna) {
                comunaEncontrada = comuna;
                break;
            }
        }

        if (comunaEncontrada == null) {
            throw new MeteorologiaException("No existe una comuna con el código " + codComuna + " en la región " + codRegion);
        }

        //Crea y agrega la estacion:
        EstacionMeteorologica nuevaEstacion = new EstacionMeteorologica(cod, nombre, lon, lat, alt, comunaEncontrada);
        estaciones.add(nuevaEstacion);
    }

    public void instalaSensor(String cod, String marca, String modelo, TipoSensor tipo, String codigoEstacion) throws MeteorologiaException {
        for (EstacionMeteorologica estacion : estaciones) {
            //permite crear el sensor si es que existe la estacion :)
            if (estacion.getCodigo().equals(codigoEstacion)) {
                estacion.instalaSensor(cod, marca, modelo, tipo);
            } else {
                throw new MeteorologiaException("No existe una estación con el código "+codigoEstacion);
            }
        }
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) throws MeteorologiaException {

        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equals(codEstacion)) {
                estacion.registraMedicion(fechaHora, valor, codSensor);
            } else {
                throw new MeteorologiaException("No existe una estacion con el código "+codEstacion);
            }.
        }

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
                datos[4] = String.valueOf(region.getNombre());
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
