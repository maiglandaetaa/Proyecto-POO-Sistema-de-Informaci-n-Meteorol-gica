package Vista;

import javax.swing.*;

public class Main {
    //patron singleton, solo permite crear una interfaz:
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}