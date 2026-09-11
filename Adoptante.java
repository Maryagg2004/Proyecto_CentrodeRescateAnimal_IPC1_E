public class Adoptante {
    private static final int CAPACIDAD_ADOPTANTES = 10;
    private static final int CANTIDAD_ATRIBUTOS = 4;

    private static final int COL_CODIGO = 0;
    private static final int COL_NOMBRE = 1;
    private static final int COL_DPI = 2;
    private static final int COL_TELEFONO = 3;

    private String[][] adoptantes;
    private int cantidadAdoptantes = 0;

    public Adoptante() {
        adoptantes = new String[CAPACIDAD_ADOPTANTES][CANTIDAD_ATRIBUTOS];
    }

    public static boolean validarCodigo(String codigo) {
        if (codigo == null) {
            return false;
        }
        return codigo.matches("AD-[0-9]{3}");
    }

    public static boolean validarNombre(String nombre) {
        if (nombre == null) {
            return false;
        }
        String limpio = nombre.trim();
        return !limpio.isEmpty() && limpio.matches("[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+");
    }

    public static boolean validarDpi(String dpi) {
        if (dpi == null) {
            return false;
        }
        return dpi.matches("[0-9]{13}");
    }

    public static boolean validarTelefono(String telefono) {
        if (telefono == null) {
            return false;
        }
        return telefono.matches("[0-9]{8}");
    }

    public boolean existeCodigo(String codigo) {
        return buscarAdoptante(codigo) != -1;
    }

    public int buscarAdoptante(String codigo) {
        for (int i = 0; i < cantidadAdoptantes; i++) {
            if (adoptantes[i][COL_CODIGO].equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    private boolean existeDpi(String dpi) {
        for (int i = 0; i < cantidadAdoptantes; i++) {
            if (adoptantes[i][COL_DPI].equals(dpi)) {
                return true;
            }
        }
        return false;
    }

    /**
     * El DPI es el criterio de duplicado del módulo (además del código): dos
     * adoptantes distintos no pueden compartir DPI.
     */
    public String registrarAdoptante(String codigo, String nombre, String dpi, String telefono) {
        if (!validarCodigo(codigo)) {
            return "VALIDACION:Código inválido, use el formato AD-000";
        }
        if (!validarNombre(nombre)) {
            return "VALIDACION:El nombre solo puede contener letras y espacios";
        }
        if (!validarDpi(dpi)) {
            return "VALIDACION:El DPI debe tener 13 dígitos numéricos";
        }
        if (!validarTelefono(telefono)) {
            return "VALIDACION:El teléfono debe tener 8 dígitos numéricos";
        }
        if (existeCodigo(codigo)) {
            return "DUPLICADO:Ya existe un adoptante con el código " + codigo;
        }
        if (existeDpi(dpi)) {
            return "DUPLICADO:Ya existe un adoptante con el DPI " + dpi;
        }
        if (cantidadAdoptantes >= CAPACIDAD_ADOPTANTES) {
            return "CAPACIDAD:No hay espacio para registrar más adoptantes";
        }

        adoptantes[cantidadAdoptantes][COL_CODIGO] = codigo;
        adoptantes[cantidadAdoptantes][COL_NOMBRE] = nombre.trim();
        adoptantes[cantidadAdoptantes][COL_DPI] = dpi;
        adoptantes[cantidadAdoptantes][COL_TELEFONO] = telefono;
        cantidadAdoptantes++;
        return null;
    }

    /**
     * Código y DPI son la identidad del registro y no se editan; solo nombre
     * y teléfono pueden cambiar.
     */
    public String editarAdoptante(String codigo, String nuevoNombre, String nuevoTelefono) {
        int fila = buscarAdoptante(codigo);
        if (fila == -1) {
            return "VALIDACION:No existe un adoptante con el código " + codigo;
        }
        if (!validarNombre(nuevoNombre)) {
            return "VALIDACION:El nombre solo puede contener letras y espacios";
        }
        if (!validarTelefono(nuevoTelefono)) {
            return "VALIDACION:El teléfono debe tener 8 dígitos numéricos";
        }
        adoptantes[fila][COL_NOMBRE] = nuevoNombre.trim();
        adoptantes[fila][COL_TELEFONO] = nuevoTelefono;
        return null;
    }

    public String obtenerAdoptanteFormateado(String codigo) {
        int fila = buscarAdoptante(codigo);
        if (fila == -1) {
            return null;
        }
        return "Código: " + adoptantes[fila][COL_CODIGO]
            + "\nNombre: " + adoptantes[fila][COL_NOMBRE]
            + "\nDPI: " + adoptantes[fila][COL_DPI]
            + "\nTeléfono: " + adoptantes[fila][COL_TELEFONO];
    }

    public String listarAdoptantes() {
        if (cantidadAdoptantes == 0) {
            return "No hay adoptantes registrados.";
        }
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < cantidadAdoptantes; i++) {
            texto.append(adoptantes[i][COL_CODIGO]).append(" | ")
                 .append(adoptantes[i][COL_NOMBRE]).append(" | DPI ")
                 .append(adoptantes[i][COL_DPI]).append(" | Tel. ")
                 .append(adoptantes[i][COL_TELEFONO]).append("\n");
        }
        return texto.toString();
    }
}
