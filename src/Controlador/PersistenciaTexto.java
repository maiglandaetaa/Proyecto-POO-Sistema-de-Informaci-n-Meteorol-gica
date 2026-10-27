package Controlador;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

public class PersistenciaTexto {

    public static String SEPARADOR = ";";
    private static PersistenciaTexto instancia;

    private PersistenciaTexto (){
    }

    public static PersistenciaTexto getInstance(){
       if(instancia == null){
           instancia = new PersistenciaTexto();
       }
       return instancia;
    }

    public static List<String[]> leeRegistros(String nombreArchivo)throws MeteorologiaException{
        File archivo = new File ("datos/" + nombreArchivo);
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
        }catch (FileNotFoundException e) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");
        }
        return registros;
    }

}
