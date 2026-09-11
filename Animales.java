public class Animales {
    private static final int CAPACIDAD_ANIMALES = 10;  // antes TAM_COLUMNA )
    private static final int CANTIDAD_ATRIBUTOS = 5;    // antes TAM_FILA 

    private static final int COL_CODIGO = 0;
    private static final int COL_ESPECIE = 1;
    private static final int COL_EDAD = 2;
    private static final int COL_ESTADO_CLINICO = 3;
    private static final int COL_ESTADO_ADOPCION = 4;

    private String[][] animales;
    private int cantidadAnimales = 0;

    public Animales() {
        animales = new String[CAPACIDAD_ANIMALES][CANTIDAD_ATRIBUTOS];
    }

    public static boolean validacionCodigo(String codigo) {
        if (codigo == null) {
            return false;
        }
        return codigo.matches("A-[0-9]{3}");
    }

    public static boolean validacionEspecie(String especie) {
        if (especie == null) {
            return false;
        }
        return especie.equalsIgnoreCase("perro") || especie.equalsIgnoreCase("gato");
    }

    public static boolean especieEdad(int edad) {
        return edad >= 0 && edad <= 25;
    }

    public static boolean estadoclinico(String estadoClinico) {
        if (estadoClinico == null) {
            return false;
        }
        return estadoClinico.equals("EN_OBSERVACION")
            || estadoClinico.equals("EN_TRATAMIENTO")
            || estadoClinico.equals("APTO");
    }

    public static boolean estadoAdopcion(String adopcion) {
        if (adopcion == null) {
            return false;
        }
        return adopcion.equals("DISPONIBLE")
            || adopcion.equals("ADOPTADO")
            || adopcion.equals("ELIMINADO");
    }

    public boolean existeCodigo(String codigo) {
        return buscarAnimal(codigo) != -1;
    }

    public int buscarAnimal(String codigo) {
        for (int i = 0; i < cantidadAnimales; i++) {
            if (animales[i][COL_CODIGO].equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Registra un animal nuevo. A diferencia de la versión original (que
     * guardaba primero y validaba después), aquí se valida todo ANTES de
     * escribir en el arreglo. El estado de adopción inicial siempre es
     * DISPONIBLE y se fija por código: un animal recién ingresado nunca puede
     * nacer ADOPTADO ni ELIMINADO.
     */
    public String registrarAnimal(String codigo, String especie, String edadTexto, String estadoClinico) {
        if (!validacionCodigo(codigo)) {
            return "VALIDACION:Código inválido, use el formato A-000";
        }
        if (!validacionEspecie(especie)) {
            return "VALIDACION:Especie inválida, use Perro o Gato";
        }
        int edad;
        try {
            edad = Integer.parseInt(edadTexto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return "VALIDACION:La edad debe ser un número entero";
        }
        if (!especieEdad(edad)) {
            return "VALIDACION:La edad debe estar entre 0 y 25";
        }
        if (!estadoclinico(estadoClinico)) {
            return "VALIDACION:Estado clínico inválido, use EN_OBSERVACION, EN_TRATAMIENTO o APTO";
        }
        if (existeCodigo(codigo)) {
            return "DUPLICADO:Ya existe un animal con el código " + codigo;
        }
        if (cantidadAnimales >= CAPACIDAD_ANIMALES) {
            return "CAPACIDAD:No hay espacio para registrar más animales";
        }

        // Normaliza la especie a forma canónica (Perro/Gato) sin importar cómo la haya escrito el usuario.
        String especieCanonica = especie.equalsIgnoreCase("perro") ? "Perro" : "Gato";

        animales[cantidadAnimales][COL_CODIGO] = codigo;
        animales[cantidadAnimales][COL_ESPECIE] = especieCanonica;
        animales[cantidadAnimales][COL_EDAD] = String.valueOf(edad);
        animales[cantidadAnimales][COL_ESTADO_CLINICO] = estadoClinico;
        animales[cantidadAnimales][COL_ESTADO_ADOPCION] = "DISPONIBLE";
        cantidadAnimales++;
        return null;
    }

    public String obtenerEstadoAdopcion(String codigo) {
        int fila = buscarAnimal(codigo);
        return fila == -1 ? null : animales[fila][COL_ESTADO_ADOPCION];
    }

    public String obtenerEspecie(String codigo) {
        int fila = buscarAnimal(codigo);
        return fila == -1 ? null : animales[fila][COL_ESPECIE];
    }

    public String obtenerAnimalFormateado(String codigo) {
        int fila = buscarAnimal(codigo);
        if (fila == -1) {
            return null;
        }
        return "Código: " + animales[fila][COL_CODIGO]
            + "\nEspecie: " + animales[fila][COL_ESPECIE]
            + "\nEdad: " + animales[fila][COL_EDAD]
            + "\nEstado clínico: " + animales[fila][COL_ESTADO_CLINICO]
            + "\nEstado de adopción: " + animales[fila][COL_ESTADO_ADOPCION];
    }

    /**
     * Un animal ELIMINADO es un estado final: nunca vuelve a cambiar, por eso
     * este método rechaza la edición si el animal ya fue dado de baja.
     */
    public String editarEstadoClinico(String codigo, String nuevoEstado) {
        int fila = buscarAnimal(codigo);
        if (fila == -1) {
            return "VALIDACION:No existe un animal con el código " + codigo;
        }
        if (animales[fila][COL_ESTADO_ADOPCION].equals("ELIMINADO")) {
            return "ESTADO:El animal " + codigo + " está eliminado, no se puede modificar";
        }
        if (!estadoclinico(nuevoEstado)) {
            return "VALIDACION:Estado clínico inválido, use EN_OBSERVACION, EN_TRATAMIENTO o APTO";
        }
        animales[fila][COL_ESTADO_CLINICO] = nuevoEstado;
        return null;
    }

    /**
     * Edición manual del estado de adopción (por ejemplo, un animal ADOPTADO
     * que se devuelve al refugio y vuelve a DISPONIBLE). La transición a
     * ELIMINADO no se permite por esta vía: solo eliminarAnimalLogico da de
     * baja un registro.
     */
    public String editarEstadoAdopcion(String codigo, String nuevoEstado) {
        int fila = buscarAnimal(codigo);
        if (fila == -1) {
            return "VALIDACION:No existe un animal con el código " + codigo;
        }
        if (animales[fila][COL_ESTADO_ADOPCION].equals("ELIMINADO")) {
            return "ESTADO:El animal " + codigo + " está eliminado, no se puede modificar";
        }
        if ("ELIMINADO".equals(nuevoEstado)) {
            return "VALIDACION:Use la opción Eliminar animal para dar de baja un registro";
        }
        if (!estadoAdopcion(nuevoEstado)) {
            return "VALIDACION:Estado de adopción inválido, use DISPONIBLE o ADOPTADO";
        }
        animales[fila][COL_ESTADO_ADOPCION] = nuevoEstado;
        return null;
    }

    public String eliminarAnimalLogico(String codigo) {
        int fila = buscarAnimal(codigo);
        if (fila == -1) {
            return "VALIDACION:No existe un animal con el código " + codigo;
        }
        if (animales[fila][COL_ESTADO_ADOPCION].equals("ELIMINADO")) {
            return "ESTADO:El animal " + codigo + " ya estaba eliminado";
        }
        animales[fila][COL_ESTADO_ADOPCION] = "ELIMINADO";
        return null;
    }

    public String listarAnimales() {
        if (cantidadAnimales == 0) {
            return "No hay animales registrados.";
        }
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < cantidadAnimales; i++) {
            texto.append(animales[i][COL_CODIGO]).append(" | ")
                 .append(animales[i][COL_ESPECIE]).append(" | ")
                 .append(animales[i][COL_EDAD]).append(" años | ")
                 .append(animales[i][COL_ESTADO_CLINICO]).append(" | ")
                 .append(animales[i][COL_ESTADO_ADOPCION]).append("\n");
        }
        return texto.toString();
    }
}
