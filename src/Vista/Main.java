package Vista;

public class Main {
    //patron singleton, solo permite crear una interfaz:
    public static void main(String[] args) {
        InterfazUsuario interfaz = InterfazUsuario.getInstance();
        interfaz.menuPrincipal();
    }
}