import javax.swing.*;

public class Main {
    public static void main(String[] args) {
         JPanel panel =new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        boolean Salir = false;

        Animales refugio = new Animales();
            while (!Salir) {
                JTextField opciones = mostrarmenu();
        
        int opcionSeleccionada = 
                        Integer.parseInt(opciones.getText().trim());

                    switch (opcionSeleccionada) {
                    case 1:
                        refugio.espaciosAnimales();
                        break;
                    case 2:
                        registroAnimalesRescatados(refugio);
                        break;
                    case 3: 
                        Salir= true;

                    default:
                        JOptionPane.showMessageDialog(
                            null,
                            "Opcion invalida, intente de nuevo."
                        );
                        
                    }

            }

    }
    public static JTextField mostrarmenu(){
        JPanel panel =new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField opciones = new JTextField();

        panel.add(new JLabel("***SISTEMA DE REFUGIO***"));
        panel.add(new JLabel("1. Mostrar matriz "));
        panel.add(new JLabel("2. Registrar animal rescatado"));
        panel.add(new JLabel("3. salir"));
        panel.add(new JLabel("Ingrese una opción: "));
        panel.add(opciones);

        
        JOptionPane.showConfirmDialog(
            null,
             panel,
            "Menú principal",
            JOptionPane.OK_CANCEL_OPTION
        );

        return opciones;
    }

    public static void registroAnimalesRescatados(Animales refugio){
        

        while (true) {

            JTextField codigo = new JTextField();
            JTextField especie = new JTextField();
            JTextField edad = new JTextField();
            JTextField estadoClinico = new JTextField();
            JTextField estadoAdopcion = new JTextField();
        
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                panel.add(new JLabel("Código del animal: A-000"));
                panel.add(codigo);

                panel.add(new JLabel("Especie: Gato - Perro "));
                panel.add(especie);

                panel.add(new JLabel("Edad: 0-25 "));
                panel.add(edad);

                panel.add(new JLabel("Estado clínico: "));
                panel.add(estadoClinico);

                panel.add(new JLabel("Estado de Adopción: "));
                panel.add(estadoAdopcion);

                int opcion = JOptionPane.showConfirmDialog(
                    null, 
                    panel,
                    "Registro del animal",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
                );
                if (opcion != JOptionPane.OK_OPTION){
                    break;
                }                
                if (opcion == JOptionPane.OK_OPTION) {
                    String codigoIngresado = codigo.getText();
                    String especieIngresada = especie.getText();
                    String edadTexto = edad.getText();
                    String ClinicoIngresado = estadoClinico.getText();
                    String AdopcionIngresado = estadoAdopcion.getText();

                    boolean guardado = refugio.guardarAnimal(
                        codigoIngresado,
                        especieIngresada,
                        edadTexto,
                        ClinicoIngresado,
                        AdopcionIngresado
                    );
                    if (guardado) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Animal guardado correctamente"
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                            null,
                            "No hay espacios disponibles"
                        );
                        break;
                    }
                    try{
        
                        int edadIngresada = Integer.parseInt(edadTexto);

                        boolean codigoValido = 
                                Animales.validacionCodigo(codigoIngresado);
        
                        boolean especieValida =
                                Animales.validacionEspecie(especieIngresada);

                        boolean edadValida =
                                Animales.especieEdad(edadIngresada);

                        boolean clinicoValido =  
                                Animales.estadoclinico(ClinicoIngresado);

                        boolean adopcionValido =
                                Animales.estadoAdopcion(AdopcionIngresado);

                        if(codigoValido
                         && especieValida 
                            && edadValida
                            && clinicoValido
                            && adopcionValido) {
                            JOptionPane.showMessageDialog(null,
                              "Todos los datos son válidos"
                                + "\nCódigo: " + codigo.getText()
                                + "\nEspecie: " + especie.getText()
                                +"\nEdad: " + edad.getText()
                                + "\nEstado clínico: " + estadoClinico.getText()
                                + "\nEstado de adopción: " + estadoAdopcion.getText()
                            );
                        } else {
                            JOptionPane.showMessageDialog(null, 
                             "uno o varios datos no son válidos"
                             + "\nCódigo: " + codigo.getText()
                             + "\nEspecie: " + especie.getText()
                             +"\nEdad: " + edad.getText()
                             + "\nEstado clínico: " + estadoClinico.getText()
                             + "\nEstado de adopción: " + estadoAdopcion.getText()
                             + "\nVUELVA A INTENTARLO",
                                "Error", 
                                 JOptionPane.ERROR_MESSAGE
                                    );
                                }
                    } catch (NumberFormatException error) {
                        JOptionPane.showMessageDialog(
                            null,
                            "La edad debe ser un número entero",
                        "Error",
                    JOptionPane.ERROR_MESSAGE);
                    }
                }   
            }
        }
    }
