/**
 * Módulo de Solicitudes de adopción. Esta clase valida y aplica sus propios
 * invariantes (formato de sus campos, unicidad de código, y la regla "solo una
 * solicitud APROBADA por animal", que es un dato de su propio arreglo). Lo que
 * depende del estado ACTUAL de otro módulo (¿existe el animal?, ¿está
 * DISPONIBLE?, ¿existe el adoptante?) lo valida Main antes de llamar aquí,
 * para no acoplar esta clase a Animales/Adoptante por composición.
 */
public class Solicitud {
    private static final int CAPACIDAD_SOLICITUDES = 15;
    private static final int CANTIDAD_ATRIBUTOS = 5;

    private static final int COL_CODIGO = 0;
    private static final int COL_CODIGO_ANIMAL = 1;
    private static final int COL_CODIGO_ADOPTANTE = 2;
    private static final int COL_FECHA = 3;
    private static final int COL_ESTADO = 4;

    private static final int[] DIAS_POR_MES = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private String[][] solicitudes;
    private int cantidadSolicitudes = 0;

    public Solicitud() {
        solicitudes = new String[CAPACIDAD_SOLICITUDES][CANTIDAD_ATRIBUTOS];
    }

    public static boolean validarCodigo(String codigo) {
        if (codigo == null) {
            return false;
        }
        return codigo.matches("S-[0-9]{3}");
    }

    public static boolean validarEstado(String estado) {
        if (estado == null) {
            return false;
        }
        return estado.equals("PENDIENTE") || estado.equals("APROBADA")
            || estado.equals("RECHAZADA") || estado.equals("COMPLETADA");
    }

    /** Valida dd/mm/aaaa con calendario real (incluye bisiestos), para que no se acepte por ejemplo 31/02/2026. */
    public static boolean validarFecha(String fecha) {
        if (fecha == null || !fecha.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
            return false;
        }
        String[] partes = fecha.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int anio = Integer.parseInt(partes[2]);
        if (mes < 1 || mes > 12) {
            return false;
        }
        int diasMax = DIAS_POR_MES[mes - 1];
        boolean esBisiesto = (anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0);
        if (mes == 2 && esBisiesto) {
            diasMax = 29;
        }
        return dia >= 1 && dia <= diasMax;
    }

    public boolean existeSolicitud(String codigo) {
        return buscarSolicitud(codigo) != -1;
    }

    private int buscarSolicitud(String codigo) {
        for (int i = 0; i < cantidadSolicitudes; i++) {
            if (solicitudes[i][COL_CODIGO].equals(codigo)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Registra la solicitud validando solo SUS propios campos. La existencia y
     * disponibilidad del animal, y la existencia del adoptante, ya fueron
     * verificadas por Main antes de llamar a este método (llamada a los
     * validadores estáticos de Animales/Adoptante, que es reutilización pura,
     * no acoplamiento de instancia).
     */
    public String registrarSolicitud(String codigo, String codigoAnimal, String codigoAdoptante, String fecha) {
        if (!validarCodigo(codigo)) {
            return "VALIDACION:Código inválido, use el formato S-000";
        }
        if (!Animales.validacionCodigo(codigoAnimal)) {
            return "VALIDACION:Código de animal inválido, use el formato A-000";
        }
        if (!Adoptante.validarCodigo(codigoAdoptante)) {
            return "VALIDACION:Código de adoptante inválido, use el formato AD-000";
        }
        if (!validarFecha(fecha)) {
            return "VALIDACION:Fecha inválida, use el formato dd/mm/aaaa";
        }
        if (existeSolicitud(codigo)) {
            return "DUPLICADO:Ya existe una solicitud con el código " + codigo;
        }
        if (cantidadSolicitudes >= CAPACIDAD_SOLICITUDES) {
            return "CAPACIDAD:No hay espacio para registrar más solicitudes";
        }

        solicitudes[cantidadSolicitudes][COL_CODIGO] = codigo;
        solicitudes[cantidadSolicitudes][COL_CODIGO_ANIMAL] = codigoAnimal;
        solicitudes[cantidadSolicitudes][COL_CODIGO_ADOPTANTE] = codigoAdoptante;
        solicitudes[cantidadSolicitudes][COL_FECHA] = fecha;
        solicitudes[cantidadSolicitudes][COL_ESTADO] = "PENDIENTE";
        cantidadSolicitudes++;
        return null;
    }

    /**
     * Aprueba una solicitud PENDIENTE y, como cascada dentro de este mismo
     * arreglo, rechaza automáticamente cualquier otra solicitud PENDIENTE del
     * mismo animal (solo puede haber una APROBADA por animal a la vez). El
     * efecto sobre Animales/EspacioRefugio (pasar el animal a ADOPTADO y
     * liberar su celda) lo aplica Main después de recibir null aquí.
     */
    public String aprobarSolicitud(String codigo) {
        int fila = buscarSolicitud(codigo);
        if (fila == -1) {
            return "VALIDACION:No existe una solicitud con el código " + codigo;
        }
        if (!solicitudes[fila][COL_ESTADO].equals("PENDIENTE")) {
            return "ESTADO:La solicitud " + codigo + " no está pendiente";
        }
        String codigoAnimal = solicitudes[fila][COL_CODIGO_ANIMAL];
        solicitudes[fila][COL_ESTADO] = "APROBADA";
        for (int i = 0; i < cantidadSolicitudes; i++) {
            if (i != fila
                && solicitudes[i][COL_CODIGO_ANIMAL].equals(codigoAnimal)
                && solicitudes[i][COL_ESTADO].equals("PENDIENTE")) {
                solicitudes[i][COL_ESTADO] = "RECHAZADA";
            }
        }
        return null;
    }

    public String rechazarSolicitud(String codigo) {
        int fila = buscarSolicitud(codigo);
        if (fila == -1) {
            return "VALIDACION:No existe una solicitud con el código " + codigo;
        }
        if (!solicitudes[fila][COL_ESTADO].equals("PENDIENTE")) {
            return "ESTADO:La solicitud " + codigo + " no está pendiente";
        }
        solicitudes[fila][COL_ESTADO] = "RECHAZADA";
        return null;
    }

    public String completarSolicitud(String codigo) {
        int fila = buscarSolicitud(codigo);
        if (fila == -1) {
            return "VALIDACION:No existe una solicitud con el código " + codigo;
        }
        if (!solicitudes[fila][COL_ESTADO].equals("APROBADA")) {
            return "ESTADO:La solicitud " + codigo + " no está aprobada";
        }
        solicitudes[fila][COL_ESTADO] = "COMPLETADA";
        return null;
    }

    public String obtenerCodigoAnimal(String codigo) {
        int fila = buscarSolicitud(codigo);
        return fila == -1 ? null : solicitudes[fila][COL_CODIGO_ANIMAL];
    }

    public String obtenerEstado(String codigo) {
        int fila = buscarSolicitud(codigo);
        return fila == -1 ? null : solicitudes[fila][COL_ESTADO];
    }

    public String listarSolicitudes() {
        return listar(false);
    }

    public String listarPendientes() {
        return listar(true);
    }

    private String listar(boolean soloPendientes) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < cantidadSolicitudes; i++) {
            if (soloPendientes && !solicitudes[i][COL_ESTADO].equals("PENDIENTE")) {
                continue;
            }
            texto.append(solicitudes[i][COL_CODIGO]).append(" | Animal ")
                 .append(solicitudes[i][COL_CODIGO_ANIMAL]).append(" | Adoptante ")
                 .append(solicitudes[i][COL_CODIGO_ADOPTANTE]).append(" | ")
                 .append(solicitudes[i][COL_FECHA]).append(" | ")
                 .append(solicitudes[i][COL_ESTADO]).append("\n");
        }
        return texto.length() == 0 ? "No hay solicitudes que mostrar." : texto.toString();
    }
}
