package Controlador;

import Modelo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class InstitutoMeteorologia {
    private static InstitutoMeteorologia instance;
    private InstitutoMeteorologia() {}
    public static InstitutoMeteorologia getInstance() {
        if (instance == null) {
            instance = new InstitutoMeteorologia();
        }
        return instance;
    }

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
        for (Region region : regiones) {
            if (region.getCodigo() == codigoRegion) {

                for (Comuna comuna : region.getComunas()) {
                    if (comuna.getCodigo() == codigo) {
                        throw new MeteorologiaException("La región " + region.getNombre() + " ya tiene una comuna con el código " + codigo);
                    } else if (comuna.getNombre().equalsIgnoreCase(nombre)) {
                        throw new MeteorologiaException("La región " + region.getNombre() + " ya tiene una comuna con el nombre " + nombre);
                    }
                }

                region.addComuna(codigo, nombre);
                return;
            }
        }
        throw new MeteorologiaException("No existe una región con el código" + codigoRegion);

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
        boolean encontrada = false;
        for (EstacionMeteorologica estacion : estaciones) {
            //permite crear el sensor si es que existe la estacion :)
            if (estacion.getCodigo().equals(codigoEstacion)) {
                estacion.instalaSensor(cod, marca, modelo, tipo);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new MeteorologiaException("No existe una estación con el código "+codigoEstacion);
        }
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) throws MeteorologiaException {
        boolean encontrada = false;

        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equals(codEstacion)) {
                estacion.registraMedicion(fechaHora, valor, codSensor);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new MeteorologiaException("No existe una estacion con el código "+codEstacion);
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
                datos[2] = String.valueOf(comuna.getEstaciones().length);
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
                Optional<Comuna> comunaEncontrada = region.findComunaById(codigoComuna);

                if (comunaEncontrada.isEmpty()) {return new String[0][0];}

                Comuna comuna = comunaEncontrada.get();
                ArrayList<String[]> resultado = new ArrayList<>();

                for (EstacionMeteorologica estacion : estaciones) {
                    String[] datos = estacion.toString().split("; ");
                    String codigoEstacion = datos[0];

                    Optional<EstacionMeteorologica> encontrada = comuna.findEstacionById(codigoEstacion);

                    if (encontrada.isPresent()) {
                        int sensoresActivos = 0;

                        for (Sensor sensor : estacion.getSensores()) {
                            if (sensor.getEstado() == Estado.ACTIVO) {
                                sensoresActivos++;
                            }
                        }

                        datos[4] = String.valueOf(sensoresActivos);
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