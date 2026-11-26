package Controlador;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

public class PersistenciaObjetos {

    private static final String CARPETA = "datos";

    private PersistenciaObjetos(){
    }

    public static void escribeObjeto(String nombreArchivo, Object objeto) throws MeteorologiaException{
        File carpeta = new File(CARPETA);
        if(!carpeta.exists()){
            carpeta.mkdir();
        }
        try (ObjectOutputStream archivo = new ObjectOutputStream(new FileOutputStream(CARPETA + "/" + nombreArchivo))){
            archivo.writeObject(objeto);
        } catch (IOException e){
            throw new MeteorologiaException("No fue posible escribir el archivo " + nombreArchivo + ".");
        }
    }

    public static Object leeObjeto(String nombreArchivo) throws MeteorologiaException{
        File archivo = new File(CARPETA + "/" + nombreArchivo);
        if(!archivo.exists()){
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo + ".");
        }
        try (ObjectInputStream lector = new ObjectInputStream(new FileInputStream(archivo))){
            return lector.readObject();
        } catch (IOException | ClassNotFoundException e){
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");
        }
    }
}
