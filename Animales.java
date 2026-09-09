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

    

    public void editarAdopciones() {

            String codigoBuscado = JOptionPane.showInputDialog(
           null,
            "Ingrese el código del animal:"
    );

    // El usuario presionó Cancelar
    if (codigoBuscado == null) {
        return;
    }

    codigoBuscado = codigoBuscado.trim().toUpperCase();

    // Validar el formato del código ingresado
    if (!codigoBuscado.matches("A-[0-9]{3}")) {
        JOptionPane.showMessageDialog(
            null,
            "Código inválido.\nEjemplo correcto: A-000"
        );
        return;
    }

    // Buscar el código dentro de toda la matriz
    for (int fila = 0; fila < animales.length; fila++) {

        if (animales[fila][0] != null
                && animales[fila][0].equalsIgnoreCase(codigoBuscado)) {

            String nuevoEstado = JOptionPane.showInputDialog(
                null,
                "Estado de adopción actual: "
                    + animales[fila][4]
                    + "\nIngrese el nuevo estado:"
            );

            
            // El usuario canceló o dejó el campo vacío
            if (nuevoEstado == null
                    || nuevoEstado.trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                    null,
                    "No se realizó ninguna modificación."
                );
                return;
            }
            boolean estadoValido = 
                            estadoAdopcion(nuevoEstado);
            if (!estadoValido) {
                JOptionPane.showMessageDialog(
                    null,
                    "Estado de adopción invalido"
                );
                return;
            }

            // Reemplazar el estado anterior
            animales[fila][4] =
                    nuevoEstado.trim().toUpperCase();

            JOptionPane.showMessageDialog(
                null,
                "Estado de adopción actualizado correctamente."
            );

            return;
        }
    }

        // Este mensaje se muestra después de revisar toda la matriz
        JOptionPane.showMessageDialog(
            null,
            "pero no existe un animal registrado con ese código."
        );
    }
                
                
        
    
}
