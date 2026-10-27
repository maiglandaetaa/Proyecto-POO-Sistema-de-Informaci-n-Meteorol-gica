package Vista;

import Controlador.InstitutoMeteorologia;

public class Main {

    //patron singleton, solo permite crear una interfaz:
    private static InterfazUsuario instance;
    private static InterfazUsuario interfaz;

    public static void main(String[] args) {
        interfaz.menuPrincipal();
    }
}
