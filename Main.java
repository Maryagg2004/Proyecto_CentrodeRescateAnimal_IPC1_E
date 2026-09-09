import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Animales refugio = new Animales(); 
        boolean Salir = false;

            while (!Salir) {
        
        int opcionSeleccionada = mostrarmenu(); 

                    switch (opcionSeleccionada) {
                    case 0:
                        refugio.mostrarAnimales();
                        break;
                    case 1:
                        registroAnimalesRescatados(refugio);
                        break;
                    case 2: 
                        Salir= true;

                    default:
                        
                    }

            }

    }
    public static int mostrarmenu(){
        String[] botones = {
            "Mostrar Albergue",
            "Registrar animal rescatado",
            "Salir"
    };
        return JOptionPane.showOptionDialog(
            null,
            "Seleccione una opción", 
            "Refugio de animales", 
            JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                botones,
                botones[0]
        );
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

                                if (refugio.animalEliminado(codigo.getText().trim())) {
                                return ;
                            }

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
                                "Error",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                            );
                    }
                }   
            }
        }
    }
