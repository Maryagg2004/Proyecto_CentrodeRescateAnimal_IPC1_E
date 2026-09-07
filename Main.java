import javax.swing.JOptionPane;
public class Main {
    public static void main(String[] args) {
        String codigo = JOptionPane.showInputDialog(
                null,
                "Ingrese el código del animal: ''A-000''"
        );

         if (Animales.validacionCodigo(codigo)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Código válido"
            );
        } else {
             JOptionPane.showMessageDialog(
                    null,
                    "Código inválido. Use el formato A-000",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        
        String especie = JOptionPane.showInputDialog(
            null,
            "Especie: "
        );

        if (Animales.validacionEspecie(especie)) {
            JOptionPane.showMessageDialog(
                null, 
                "Especie válida"
            );
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Especie inválida. Ingrese una especie válida",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
