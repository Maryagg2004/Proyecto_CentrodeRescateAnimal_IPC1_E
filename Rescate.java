/**
 * Módulo de Rescates urgentes. No hay eliminación de registros (ni física ni
 * lógica). Al atender un caso se vincula o genera un registro en Animales;
 * ese vínculo lo resuelve Main (que sí conoce a Animales) y aquí solo se
 * guarda el código resultante en codigoAnimalVinculado.
 */
public class Rescate {
    private static final int CAPACIDAD_RESCATES = 10;
    private static final int CANTIDAD_ATRIBUTOS = 5;

    private static final int COL_CODIGO = 0;
    private static final int COL_PRIORIDAD = 1;
    private static final int COL_ESTADO = 2;
    private static final int COL_FECHA_REPORTE = 3;
    private static final int COL_CODIGO_ANIMAL_VINCULADO = 4; // "" hasta que se atiende el caso

    private String[][] rescates;
    private int cantidadRescates = 0;

    public Rescate() {
        rescates = new String[CAPACIDAD_RESCATES][CANTIDAD_ATRIBUTOS];
    }

    public static boolean validarCodigo(String codigo) {
        if (codigo == null) {
            return false;
        }
        return codigo.matches("R-[0-9]{3}");
    }

    public static boolean validarPrioridad(String prioridad) {
        if (prioridad == null) {
            return false;
        }
        return prioridad.equals("ALTA") || prioridad.equals("MEDIA") || prioridad.equals("BAJA");
    }

    public boolean existeCodigo(String codigo) {
        return buscarRescate(codigo) != -1;
    }

    private int buscarRescate(String codigo) {
        for (int i = 0; i < cantidadRescates; i++) {
            if (rescates[i][COL_CODIGO].equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    public String registrarRescate(String codigo, String prioridad, String fechaReporte) {
        if (!validarCodigo(codigo)) {
            return "VALIDACION:Código inválido, use el formato R-000";
        }
        if (!validarPrioridad(prioridad)) {
            return "VALIDACION:Prioridad inválida, use ALTA, MEDIA o BAJA";
        }
        if (!Solicitud.validarFecha(fechaReporte)) {
            return "VALIDACION:Fecha inválida, use el formato dd/mm/aaaa";
        }
        if (existeCodigo(codigo)) {
            return "DUPLICADO:Ya existe un rescate con el código " + codigo;
        }
        if (cantidadRescates >= CAPACIDAD_RESCATES) {
            return "CAPACIDAD:No hay espacio para registrar más rescates";
        }

        rescates[cantidadRescates][COL_CODIGO] = codigo;
        rescates[cantidadRescates][COL_PRIORIDAD] = prioridad;
        rescates[cantidadRescates][COL_ESTADO] = "PENDIENTE";
        rescates[cantidadRescates][COL_FECHA_REPORTE] = fechaReporte;
        rescates[cantidadRescates][COL_CODIGO_ANIMAL_VINCULADO] = "";
        cantidadRescates++;
        return null;
    }

    /** Marca el rescate como ATENDIDO y guarda el código de animal que Main ya generó o vinculó. */
    public String atenderRescate(String codigo, String codigoAnimalVinculado) {
        int fila = buscarRescate(codigo);
        if (fila == -1) {
            return "VALIDACION:No existe un rescate con el código " + codigo;
        }
        if (!rescates[fila][COL_ESTADO].equals("PENDIENTE")) {
            return "ESTADO:El rescate " + codigo + " ya fue atendido";
        }
        if (!Animales.validacionCodigo(codigoAnimalVinculado)) {
            return "VALIDACION:Código de animal vinculado inválido";
        }
        rescates[fila][COL_ESTADO] = "ATENDIDO";
        rescates[fila][COL_CODIGO_ANIMAL_VINCULADO] = codigoAnimalVinculado;
        return null;
    }

    public String obtenerCodigoAnimalVinculado(String codigo) {
        int fila = buscarRescate(codigo);
        return fila == -1 ? null : rescates[fila][COL_CODIGO_ANIMAL_VINCULADO];
    }

    public String obtenerEstado(String codigo) {
        int fila = buscarRescate(codigo);
        return fila == -1 ? null : rescates[fila][COL_ESTADO];
    }

    /**
     * Reportes activos (PENDIENTE) con prioridad ALTA primero. No se ordena de
     * verdad: son tres pasadas sobre el mismo arreglo fijo (ALTA, luego MEDIA,
     * luego BAJA), coherente con no usar estructuras dinámicas ni algoritmos
     * de orden genérico.
     */
    public String listarReportesActivos() {
        StringBuilder texto = new StringBuilder();
        String[] ordenPrioridad = {"ALTA", "MEDIA", "BAJA"};
        for (String prioridad : ordenPrioridad) {
            for (int i = 0; i < cantidadRescates; i++) {
                if (rescates[i][COL_ESTADO].equals("PENDIENTE") && rescates[i][COL_PRIORIDAD].equals(prioridad)) {
                    texto.append(formatear(i)).append("\n");
                }
            }
        }
        return texto.length() == 0 ? "No hay reportes activos." : texto.toString();
    }

    public String listarTodos() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < cantidadRescates; i++) {
            texto.append(formatear(i)).append("\n");
        }
        return texto.length() == 0 ? "No hay rescates registrados." : texto.toString();
    }

    private String formatear(int i) {
        String vinculo = rescates[i][COL_CODIGO_ANIMAL_VINCULADO].isEmpty()
            ? "sin vincular" : rescates[i][COL_CODIGO_ANIMAL_VINCULADO];
        return rescates[i][COL_CODIGO] + " | " + rescates[i][COL_PRIORIDAD] + " | "
            + rescates[i][COL_ESTADO] + " | " + rescates[i][COL_FECHA_REPORTE] + " | " + vinculo;
    }
}
