package projetogamao;

import projetogamao.Controller.ControladorJogo;
import projetogamao.View.ViewSwing;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ViewSwing view = new ViewSwing(24);
            new ControladorJogo(view).iniciar();
        });
    }
}
