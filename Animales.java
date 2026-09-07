
public class Animales{
    public static final int TAM_FILA = 5;
    public static final int TAM_COLUMNA = 10;

    private String[][] animales;

    public void espaciosAnimales() {
        animales = new String[TAM_FILA][TAM_COLUMNA];
        for (int i=0; i < animales.length; i++) {
            for (int j=0; j < animales[i].length ; j++ ) {
                System.out.print(animales[i][j] + " ");
            }
          System.out.println();
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
     public static void main(String[] args){

     }

}
