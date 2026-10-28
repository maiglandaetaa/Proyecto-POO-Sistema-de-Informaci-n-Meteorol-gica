//Felipe Venegas
package Controlador;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

public class PersistenciaTexto {

    public static final String SEPARADOR = ";";
    private static PersistenciaTexto instancia;

    private PersistenciaTexto (){
    }

    public static PersistenciaTexto getInstance() {
        if (instancia == null) {
            instancia = new PersistenciaTexto();
        }
        return instancia;
    }

    public static List<String[]> leeRegistros(String nombreArchivo) throws MeteorologiaException{
        File archivo = new File("datos/" + nombreArchivo);
        if (!archivo.exists()){
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo + ".");
        }
        List<String[]> registros = new ArrayList<>();
        try{
            Scanner sc = new Scanner(archivo);
            while (sc.hasNext()){
                String linea = sc.nextLine();
                registros.add(linea.split(SEPARADOR));
            }
            sc.close();
        } catch (FileNotFoundException e) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");
        }
        return registros;
    }

    public static void escribeRegistros(String nombreArchivo, List<String> lineas)throws MeteorologiaException{
        File carpeta = new File("datos");
        if(!carpeta.exists()){
            carpeta.mkdir();
        }
        try{
            PrintStream archivo = new PrintStream(new File("datos/") + nombreArchivo);
            for(String linea : lineas){
                archivo.println(linea);
            }
        } catch (FileNotFoundException e){
            throw new MeteorologiaException("No fue posible escribir el archivo " + nombreArchivo + ".");
        }
    }

}
