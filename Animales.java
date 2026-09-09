import javax.swing.JOptionPane;

public class Animales{
    private static final int TAM_FILA = 5;
    private static final int TAM_COLUMNA = 10;
    private String[][] animales;
    private int cantidadAnimales = 0;

    public static void main(String[] args){
        
     }

    public Animales(){
        animales = new String[TAM_FILA][TAM_COLUMNA];
    }

    private void espaciosAnimales() {
        for (int i=0; i < animales.length; i++) {
            for (int j=0; j < animales[i].length ; j++ ) {
                System.out.print(animales[i][j] + " ");
            }
          System.out.println();
        }
    }
    public boolean guardarAnimal(String codigo, String especie, String edad, String estadoClinico, String estadoAdopcion){
        
        if (cantidadAnimales >= animales.length) {
            return false;
        }
    
        animales[cantidadAnimales][0] = codigo;
        animales[cantidadAnimales][1] = especie;
        animales[cantidadAnimales][2] = edad;
        animales[cantidadAnimales][3] = estadoClinico;
        animales[cantidadAnimales][4] = estadoAdopcion;

        cantidadAnimales++;
        espaciosAnimales();
        return true;
    }
    
    public void mostrarAnimales(){

        boolean cerrarVista = false;

        while (!cerrarVista) {

            String datos = "";

            for (int fila = 0; fila <animales.length; fila ++) {
            
                if (animales[fila][0] != null) {

                    datos +="Código: " + animales[fila][0] + "\n";
                    datos +="Especie: " + animales[fila][1] + "\n";
                    datos +="Edad: " + animales[fila][2] + "\n";
                    datos +="Estado clínico: " + animales[fila][3] + "\n";
                    datos +="Estado adopción: " + animales[fila][4] + "\n";
                    datos += "=========================\n";

                }
            }
            
        if (datos.isEmpty()){
            datos = "No hay animales registrados.";
        }

        int opcion = opcionesparaeditarAnimales(datos);

        switch (opcion) {

            case 0:
            case JOptionPane.CLOSED_OPTION:
                cerrarVista = true;
                break;
            case 1:
                editarAdopciones();
                break;
            case 2:
                /*editarEstadoClinico();*/
                break;
        }   
    }
        
    }

    public static boolean validacionCodigo(String codigo) {
        if (codigo == null){
            return false;   
        }
        return codigo.matches("A-[0-9]{3}");
            
    }

    public static boolean validacionEspecie(String especie) {
        if (especie == null ){
            return false;
        }
        return especie.equalsIgnoreCase("perro")
        || especie.equalsIgnoreCase("gato");

    }

    public static boolean especieEdad(int edad) {
        return edad >= 0 && edad <= 25;
    }

    public static boolean estadoclinico(String estadoClinico){
        if (estadoClinico == null ){
            return false;
        }
        if (estadoClinico.equals("EN_OBSERVACION")
            || estadoClinico.equals("EN_TRATAMIENTO")  
            || estadoClinico.equals("APTO")){
            return true;
        }
        return false;
    }

    public static boolean estadoAdopcion(String Adopcion){
        if (Adopcion == null){
            return false; 
        }
        if (Adopcion.equals("DISPONIBLE")
        || Adopcion.equals("ADOPTADO")
        || Adopcion.equals("ELIMINADO")){
            return true;
        }
        return false;
    }
     
     public boolean animalEliminado(String codigo) {
        for (int fila = 0; fila < animales.length; fila ++) {
        if (animales[fila][0] != null
            && animales[fila][0].equalsIgnoreCase(codigo)) {
            
                String estadoAdopcion = animales[fila][4];

                if ("ELIMINADO".equalsIgnoreCase(estadoAdopcion)) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Este código perteneció a un animal eliminado "
                        + "y no puede reutilizarse."
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        null,"Este código ya está registrado."
                    );
                }

                return true;
            }
        }
          return false;
     }  
     
     public static int opcionesparaeditarAnimales(String datos){
       
        String[] botones = {
                "Aceptar",
                "Editar estado de adopción",
                "Editar estado clínico"
                };
                return JOptionPane.showOptionDialog(
                    null,
                    datos,
                    "Refugio de animales",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, 
                    null,
                    botones,
                    botones[0]
                );
    }

    public void editarAdopciones(){
         /*se selecciona que animal desea editar, se selecciona el código, y se edita, remplazando el dato anteriot*/
        String codigoBuscado = JOptionPane.showInputDialog(
            null,
            "ingrese el código del animal:"
        );

        if (codigoBuscado == null) {
             return;
        } 
        
        

        codigoBuscado = codigoBuscado.trim();

        for (int fila = 0; fila < animales.length; fila++) {
             
                if (animales[fila][0] != null
                    && animales[fila][0].equalsIgnoreCase(codigoBuscado)) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Código válido"
                            );

                        String nuevoEstado = JOptionPane.showInputDialog(
                            null,
                            "Estado adopción: " + animales[fila][4]
                            + "\nIngrese el estado al que se actualizará: "
                        );  

                        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "No se realizó ninguna modificación."
                );
                return;
            }
                }
                if (animales[fila][0] != "A-[0-9]{3}"){
                    JOptionPane.showMessageDialog(
                                null,
                                "Código invalido"
                            );
                            return;
                    
                }
                if (animales[fila][0] == null){
                    JOptionPane.showMessageDialog(
                                null,
                                "Código inexistente"
                            );
                            return;
                    
                }
        }
    }
}
