
public class Animales{
    private static final int TAM_FILA = 5;
    private static final int TAM_COLUMNA = 10;
    private String[][] animales;
    private int cantidadAnimales = 0;

    public Animales(){
        animales = new String[TAM_COLUMNA][TAM_FILA];
    }

    public void espaciosAnimales() {
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
        Animales refugio = new Animales();
        refugio.espaciosAnimales();
     }

}
