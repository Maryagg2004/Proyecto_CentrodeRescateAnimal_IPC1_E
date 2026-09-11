/**
 * Matriz de ubicaciones del refugio: fila 0 = Zona Perros, fila 1 = Zona
 * Gatos; cada columna es un espacio/jaula de esa zona. Una celda vacía se
 * representa como "" (no null) para simplificar las comparaciones de "libre".
 */
public class EspacioRefugio {
    private static final int FILA_PERROS = 0;
    private static final int FILA_GATOS = 1;
    private static final int FILAS = 2;
    // El enunciado no fija un tamaño, solo pide documentarlo: se eligió 5
    // columnas por zona (10 celdas en total) para que coincida con
    // CAPACIDAD_ANIMALES y el cuello de botella real sea siempre el registro
    // de animales, no el espacio físico.
    private static final int COLUMNAS_POR_ZONA = 5;

    private String[][] ubicaciones;

    public EspacioRefugio() {
        ubicaciones = new String[FILAS][COLUMNAS_POR_ZONA];
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS_POR_ZONA; j++) {
                ubicaciones[i][j] = "";
            }
        }
    }

    private int filaSegunEspecie(String especie) {
        if (especie == null) {
            return -1;
        }
        if (especie.equalsIgnoreCase("perro")) {
            return FILA_PERROS;
        }
        if (especie.equalsIgnoreCase("gato")) {
            return FILA_GATOS;
        }
        return -1;
    }

    private String nombreZona(int fila) {
        return fila == FILA_PERROS ? "Zona Perros" : "Zona Gatos";
    }

    public String asignarEspacio(String codigoAnimal, String especie) {
        int fila = filaSegunEspecie(especie);
        if (fila == -1) {
            return "VALIDACION:Especie inválida para asignar espacio";
        }
        for (int columna = 0; columna < COLUMNAS_POR_ZONA; columna++) {
            if (ubicaciones[fila][columna].isEmpty()) {
                ubicaciones[fila][columna] = codigoAnimal;
                return null;
            }
        }
        return "CAPACIDAD:No hay espacios disponibles en " + nombreZona(fila);
    }

    public String asignarManual(int fila, int columna, String codigoAnimal) {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS_POR_ZONA) {
            return "VALIDACION:Fila o columna fuera de rango";
        }
        if (!ubicaciones[fila][columna].isEmpty()) {
            return "CAPACIDAD:La celda [" + fila + "][" + columna + "] ya está ocupada por " + ubicaciones[fila][columna];
        }
        ubicaciones[fila][columna] = codigoAnimal;
        return null;
    }

    /** true si el animal tenía una celda y se liberó; false si no tenía ninguna (no es un error). */
    public boolean liberarEspacioPorAnimal(String codigoAnimal) {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS_POR_ZONA; j++) {
                if (ubicaciones[i][j].equals(codigoAnimal)) {
                    ubicaciones[i][j] = "";
                    return true;
                }
            }
        }
        return false;
    }

    public String obtenerAnimalEnCelda(int fila, int columna) {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS_POR_ZONA) {
            return null;
        }
        return ubicaciones[fila][columna];
    }

    public String listadoOcupacion() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < FILAS; i++) {
            texto.append(nombreZona(i)).append(":\n");
            for (int j = 0; j < COLUMNAS_POR_ZONA; j++) {
                String contenido = ubicaciones[i][j].isEmpty() ? "LIBRE" : ubicaciones[i][j];
                texto.append("  [").append(i).append("][").append(j).append("] ").append(contenido).append("\n");
            }
        }
        return texto.toString();
    }
}
