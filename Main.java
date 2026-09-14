import javax.swing.*;
import java.awt.Font;

/**
 * Controlador principal: orquesta el login y los submenús de cada módulo.
 * Las clases modelo (Animales, Adoptante, Solicitud, Rescate, EspacioRefugio)
 * se autovalidan y devuelven un resultado con la convención "null = éxito /
 * EVENTO:descripcion = fallo"; esta clase solo coordina las reglas que cruzan
 * varios módulos (por ejemplo, una solicitud necesita que el animal exista y
 * esté DISPONIBLE) y traduce cada resultado a bitácora + diálogo.
 */
public class Main {
    private static Animales refugio = new Animales();
    private static Adoptante adoptantes = new Adoptante();
    private static Solicitud solicitudes = new Solicitud();
    private static Rescate rescates = new Rescate();
    private static EspacioRefugio ubicaciones = new EspacioRefugio();
    private static Usuario sesion = new Usuario();

    public static void main(String[] args) {
        if (!iniciarSesionInteractiva()) {
            return;
        }

        boolean salir = false;
        while (!salir) {
            Integer opcion = mostrarMenu("*** SISTEMA DE REFUGIO ***", new String[]{
                "1. Módulo Animales",
                "2. Módulo Adoptantes",
                "3. Módulo Solicitudes de Adopción",
                "4. Módulo Rescates Urgentes",
                "5. Módulo Ubicaciones",
                "6. Módulo Bitácora",
                "7. Salir"
            });
            if (opcion == null) {
                JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
                continue;
            }
            switch (opcion) {
                case 1: menuAnimales(); break;
                case 2: menuAdoptantes(); break;
                case 3: menuSolicitudes(); break;
                case 4: menuRescates(); break;
                case 5: menuUbicaciones(); break;
                case 6: menuBitacora(); break;
                case 7:
                    Bitacora.registrarAccion(sesion.obtenerUsuarioActual(), "AUTENTICACION", "LOGOUT", "Cierre de sesión");
                    salir = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
            }
        }
    }

    // ------------------------------------------------------------------
    // Autenticación
    // ------------------------------------------------------------------

    private static boolean iniciarSesionInteractiva() {
        while (!sesion.estaBloqueado()) {
            String[] datos = pedirDatos("Inicio de sesión", new String[]{"Usuario:", "Contraseña:"});
            if (datos == null) {
                return false; // el usuario canceló el login
            }
            String resultado = sesion.iniciarSesion(datos[0], datos[1]);
            if (resultado == null) {
                Bitacora.registrarAccion(sesion.obtenerUsuarioActual(), "AUTENTICACION", "LOGIN_OK", "Inicio de sesión correcto");
                return true;
            }
            // El usuario aún no está autenticado, así que se registra con el
            // nombre que escribió (puede no existir, pero identifica el intento).
            Bitacora.registrarError(datos[0], "AUTENTICACION", "LOGIN_FALLIDO", resultado);
            JOptionPane.showMessageDialog(null, resultado, "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Módulo Animales
    // ------------------------------------------------------------------

    private static void menuAnimales() {
        while (true) {
            Integer opcion = mostrarMenu("--- MÓDULO ANIMALES ---", new String[]{
                "1. Registrar animal",
                "2. Buscar animal",
                "3. Editar estado clínico",
                "4. Editar estado de adopción",
                "5. Eliminar animal (baja lógica)",
                "6. Listar animales",
                "0. Volver"
            });
            if (opcion == null) {
                JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
                continue;
            }
            switch (opcion) {
                case 1: registrarAnimal(); break;
                case 2: buscarAnimal(); break;
                case 3: editarEstadoClinicoAnimal(); break;
                case 4: editarEstadoAdopcionAnimal(); break;
                case 5: eliminarAnimal(); break;
                case 6: mostrarListado(refugio.listarAnimales(), "Listado de animales"); break;
                case 0: return;
                default: JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
            }
        }
    }

    private static void registrarAnimal() {
        String[] datos = pedirDatos("Registrar animal", new String[]{
            "Código: A-000", "Especie: Perro o Gato", "Edad: 0-25",
            "Estado clínico: EN_OBSERVACION/EN_TRATAMIENTO/APTO"
        });
        if (datos == null) {
            return;
        }
        String resultado = refugio.registrarAnimal(datos[0], datos[1], datos[2], datos[3]);
        if (procesarResultado(resultado, "ANIMALES", "ALTA", "Animal " + datos[0] + " registrado correctamente")) {
            // Un animal recién ingresado se intenta ubicar de una vez en su
            // zona; si no hay espacio libre, queda registrado igual y se
            // puede asignar manualmente después desde el módulo Ubicaciones.
            String resultadoEspacio = ubicaciones.asignarEspacio(datos[0], datos[1]);
            procesarResultado(resultadoEspacio, "UBICACIONES", "ASIGNAR", "Animal " + datos[0] + " asignado a un espacio libre");
        }
    }

    private static void buscarAnimal() {
        String[] datos = pedirDatos("Buscar animal", new String[]{"Código:"});
        if (datos == null) {
            return;
        }
        String encontrado = refugio.obtenerAnimalFormateado(datos[0]);
        if (encontrado == null) {
            JOptionPane.showMessageDialog(null, "No existe un animal con ese código", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mostrarListado(encontrado, "Animal encontrado");
    }

    private static void editarEstadoClinicoAnimal() {
        String[] datos = pedirDatos("Editar estado clínico", new String[]{
            "Código:", "Nuevo estado clínico: EN_OBSERVACION/EN_TRATAMIENTO/APTO"
        });
        if (datos == null) {
            return;
        }
        String resultado = refugio.editarEstadoClinico(datos[0], datos[1]);
        procesarResultado(resultado, "ANIMALES", "EDITAR_CLINICO", "Estado clínico de " + datos[0] + " actualizado");
    }

    private static void editarEstadoAdopcionAnimal() {
        String[] datos = pedirDatos("Editar estado de adopción", new String[]{
            "Código:", "Nuevo estado de adopción: DISPONIBLE/ADOPTADO"
        });
        if (datos == null) {
            return;
        }
        String resultado = refugio.editarEstadoAdopcion(datos[0], datos[1]);
        procesarResultado(resultado, "ANIMALES", "EDITAR_ADOPCION", "Estado de adopción de " + datos[0] + " actualizado");
    }

    private static void eliminarAnimal() {
        // Regla del enunciado: el rol determina si se puede eliminar registros;
        // solo ADMIN puede dar de baja animales, AUXILIAR no.
        if (!"ADMIN".equals(sesion.obtenerRolActual())) {
            procesarResultado("PERMISO:Solo un administrador puede eliminar animales", "ANIMALES", "BAJA_LOGICA", null);
            return;
        }
        String[] datos = pedirDatos("Eliminar animal", new String[]{"Código:"});
        if (datos == null) {
            return;
        }
        String resultado = refugio.eliminarAnimalLogico(datos[0]);
        if (procesarResultado(resultado, "ANIMALES", "BAJA_LOGICA", "Animal " + datos[0] + " eliminado (baja lógica)")) {
            ubicaciones.liberarEspacioPorAnimal(datos[0]);
        }
    }

    // ------------------------------------------------------------------
    // Módulo Adoptantes
    // ------------------------------------------------------------------

    private static void menuAdoptantes() {
        while (true) {
            Integer opcion = mostrarMenu("--- MÓDULO ADOPTANTES ---", new String[]{
                "1. Registrar adoptante",
                "2. Buscar adoptante",
                "3. Editar adoptante",
                "4. Listar adoptantes",
                "0. Volver"
            });
            if (opcion == null) {
                JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
                continue;
            }
            switch (opcion) {
                case 1: registrarAdoptante(); break;
                case 2: buscarAdoptante(); break;
                case 3: editarAdoptante(); break;
                case 4: mostrarListado(adoptantes.listarAdoptantes(), "Listado de adoptantes"); break;
                case 0: return;
                default: JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
            }
        }
    }

    private static void registrarAdoptante() {
        String[] datos = pedirDatos("Registrar adoptante", new String[]{
            "Código: AD-000", "Nombre:", "DPI: 13 dígitos", "Teléfono: 8 dígitos"
        });
        if (datos == null) {
            return;
        }
        String resultado = adoptantes.registrarAdoptante(datos[0], datos[1], datos[2], datos[3]);
        procesarResultado(resultado, "ADOPTANTES", "ALTA", "Adoptante " + datos[0] + " registrado correctamente");
    }

    private static void buscarAdoptante() {
        String[] datos = pedirDatos("Buscar adoptante", new String[]{"Código:"});
        if (datos == null) {
            return;
        }
        String encontrado = adoptantes.obtenerAdoptanteFormateado(datos[0]);
        if (encontrado == null) {
            JOptionPane.showMessageDialog(null, "No existe un adoptante con ese código", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mostrarListado(encontrado, "Adoptante encontrado");
    }

    private static void editarAdoptante() {
        String[] datos = pedirDatos("Editar adoptante", new String[]{
            "Código:", "Nuevo nombre:", "Nuevo teléfono:"
        });
        if (datos == null) {
            return;
        }
        String resultado = adoptantes.editarAdoptante(datos[0], datos[1], datos[2]);
        procesarResultado(resultado, "ADOPTANTES", "EDITAR", "Adoptante " + datos[0] + " actualizado");
    }

    // ------------------------------------------------------------------
    // Módulo Solicitudes de adopción
    // ------------------------------------------------------------------

    private static void menuSolicitudes() {
        while (true) {
            Integer opcion = mostrarMenu("--- MÓDULO SOLICITUDES DE ADOPCIÓN ---", new String[]{
                "1. Registrar solicitud",
                "2. Aprobar solicitud",
                "3. Rechazar solicitud",
                "4. Completar solicitud",
                "5. Listar pendientes",
                "6. Listar historial",
                "0. Volver"
            });
            if (opcion == null) {
                JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
                continue;
            }
            switch (opcion) {
                case 1: registrarSolicitud(); break;
                case 2: aprobarSolicitud(); break;
                case 3: rechazarSolicitud(); break;
                case 4: completarSolicitud(); break;
                case 5: mostrarListado(solicitudes.listarPendientes(), "Solicitudes pendientes"); break;
                case 6: mostrarListado(solicitudes.listarSolicitudes(), "Historial de solicitudes"); break;
                case 0: return;
                default: JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
            }
        }
    }

    private static void registrarSolicitud() {
        String[] datos = pedirDatos("Registrar solicitud", new String[]{
            "Código: S-000", "Código de animal: A-000", "Código de adoptante: AD-000", "Fecha: dd/mm/aaaa"
        });
        if (datos == null) {
            return;
        }
        String codigo = datos[0], codigoAnimal = datos[1], codigoAdoptante = datos[2], fecha = datos[3];

        // Reglas cruzadas con otros módulos: Solicitud no conoce a Animales ni
        // a Adoptante, así que Main valida esto ANTES de pedirle que registre.
        if (!refugio.existeCodigo(codigoAnimal)) {
            procesarResultado("VALIDACION:No existe un animal con el código " + codigoAnimal, "SOLICITUDES", "ALTA", null);
            return;
        }
        if (!"DISPONIBLE".equals(refugio.obtenerEstadoAdopcion(codigoAnimal))) {
            procesarResultado("ESTADO:El animal " + codigoAnimal + " no está disponible", "SOLICITUDES", "ALTA", null);
            return;
        }
        if (!adoptantes.existeCodigo(codigoAdoptante)) {
            procesarResultado("VALIDACION:No existe un adoptante con el código " + codigoAdoptante, "SOLICITUDES", "ALTA", null);
            return;
        }
        String resultado = solicitudes.registrarSolicitud(codigo, codigoAnimal, codigoAdoptante, fecha);
        procesarResultado(resultado, "SOLICITUDES", "ALTA", "Solicitud " + codigo + " registrada correctamente");
    }

    private static void aprobarSolicitud() {
        String[] datos = pedirDatos("Aprobar solicitud", new String[]{"Código:"});
        if (datos == null) {
            return;
        }
        String codigoAnimal = solicitudes.obtenerCodigoAnimal(datos[0]); // se lee antes de aprobar
        String resultado = solicitudes.aprobarSolicitud(datos[0]);
        if (procesarResultado(resultado, "SOLICITUDES", "APROBAR",
                "Solicitud " + datos[0] + " aprobada, " + codigoAnimal + " pasa a ADOPTADO")) {
            // Efecto cruzado sobre otros módulos: el animal pasa a ADOPTADO y libera su celda.
            refugio.editarEstadoAdopcion(codigoAnimal, "ADOPTADO");
            ubicaciones.liberarEspacioPorAnimal(codigoAnimal);
        }
    }

    private static void rechazarSolicitud() {
        String[] datos = pedirDatos("Rechazar solicitud", new String[]{"Código:"});
        if (datos == null) {
            return;
        }
        String resultado = solicitudes.rechazarSolicitud(datos[0]);
        procesarResultado(resultado, "SOLICITUDES", "RECHAZAR", "Solicitud " + datos[0] + " rechazada");
    }

    private static void completarSolicitud() {
        String[] datos = pedirDatos("Completar solicitud", new String[]{"Código:"});
        if (datos == null) {
            return;
        }
        String resultado = solicitudes.completarSolicitud(datos[0]);
        procesarResultado(resultado, "SOLICITUDES", "COMPLETAR", "Solicitud " + datos[0] + " completada");
    }

    // ------------------------------------------------------------------
    // Módulo Rescates urgentes
    // ------------------------------------------------------------------

    private static void menuRescates() {
        while (true) {
            Integer opcion = mostrarMenu("--- MÓDULO RESCATES URGENTES ---", new String[]{
                "1. Registrar rescate",
                "2. Atender rescate",
                "3. Consultar reportes activos",
                "4. Listar todos",
                "0. Volver"
            });
            if (opcion == null) {
                JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
                continue;
            }
            switch (opcion) {
                case 1: registrarRescate(); break;
                case 2: atenderRescate(); break;
                case 3: mostrarListado(rescates.listarReportesActivos(), "Reportes activos"); break;
                case 4: mostrarListado(rescates.listarTodos(), "Historial de rescates"); break;
                case 0: return;
                default: JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
            }
        }
    }

    private static void registrarRescate() {
        String[] datos = pedirDatos("Registrar rescate", new String[]{
            "Código: R-000", "Prioridad: ALTA/MEDIA/BAJA", "Fecha de reporte: dd/mm/aaaa"
        });
        if (datos == null) {
            return;
        }
        String resultado = rescates.registrarRescate(datos[0], datos[1], datos[2]);
        procesarResultado(resultado, "RESCATES", "ALTA", "Rescate " + datos[0] + " registrado correctamente");
    }

    /**
     * Al atender un caso se reutiliza el mismo consecutivo numérico del
     * rescate para el animal (ejemplo del enunciado: el rescate "R-009"
     * atendido genera el animal "A-009"). Si ese código de animal ya existe,
     * solo se vincula; si no existe, se piden los datos clínicos y se crea con
     * registrarAnimal(...), igual que un alta normal.
     */
    private static void atenderRescate() {
        String[] datosCodigo = pedirDatos("Atender rescate", new String[]{"Código del rescate: R-000"});
        if (datosCodigo == null) {
            return;
        }
        String codigoRescate = datosCodigo[0];

        if (!Rescate.validarCodigo(codigoRescate)) {
            procesarResultado("VALIDACION:Código de rescate inválido, use el formato R-000", "RESCATES", "ATENDER", null);
            return;
        }
        if (!rescates.existeCodigo(codigoRescate)) {
            procesarResultado("VALIDACION:No existe un rescate con el código " + codigoRescate, "RESCATES", "ATENDER", null);
            return;
        }

        String codigoAnimalGenerado = "A-" + codigoRescate.substring(2);
        String codigoAnimalFinal;

        if (refugio.existeCodigo(codigoAnimalGenerado)) {
            codigoAnimalFinal = codigoAnimalGenerado;
        } else {
            String[] datosAnimal = pedirDatos("Datos del animal rescatado (" + codigoAnimalGenerado + ")", new String[]{
                "Especie: Perro o Gato", "Edad: 0-25", "Estado clínico: EN_OBSERVACION/EN_TRATAMIENTO/APTO"
            });
            if (datosAnimal == null) {
                return;
            }
            String resultadoAnimal = refugio.registrarAnimal(codigoAnimalGenerado, datosAnimal[0], datosAnimal[1], datosAnimal[2]);
            if (!procesarResultado(resultadoAnimal, "ANIMALES", "ALTA", "Animal " + codigoAnimalGenerado + " registrado")) {
                return;
            }
            String resultadoEspacio = ubicaciones.asignarEspacio(codigoAnimalGenerado, datosAnimal[0]);
            procesarResultado(resultadoEspacio, "UBICACIONES", "ASIGNAR", "Animal " + codigoAnimalGenerado + " asignado a un espacio libre");
            codigoAnimalFinal = codigoAnimalGenerado;
        }

        String resultado = rescates.atenderRescate(codigoRescate, codigoAnimalFinal);
        procesarResultado(resultado, "RESCATES", "ATENDER", "Rescate " + codigoRescate + " atendido, vinculado a " + codigoAnimalFinal);
    }

    // ------------------------------------------------------------------
    // Módulo Ubicaciones (matriz del refugio)
    // ------------------------------------------------------------------

    private static void menuUbicaciones() {
        while (true) {
            Integer opcion = mostrarMenu("--- MÓDULO UBICACIONES ---", new String[]{
                "1. Consultar disponibilidad",
                "2. Asignar espacio manualmente",
                "3. Liberar espacio manualmente",
                "0. Volver"
            });
            if (opcion == null) {
                JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
                continue;
            }
            switch (opcion) {
                case 1: mostrarListado(ubicaciones.listadoOcupacion(), "Disponibilidad del refugio"); break;
                case 2: asignarEspacioManual(); break;
                case 3: liberarEspacioManual(); break;
                case 0: return;
                default: JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
            }
        }
    }

    private static void asignarEspacioManual() {
        String[] datos = pedirDatos("Asignar espacio", new String[]{
            "Fila (0=Perros, 1=Gatos):", "Columna:", "Código de animal:"
        });
        if (datos == null) {
            return;
        }
        try {
            int fila = Integer.parseInt(datos[0].trim());
            int columna = Integer.parseInt(datos[1].trim());
            String resultado = ubicaciones.asignarManual(fila, columna, datos[2]);
            procesarResultado(resultado, "UBICACIONES", "ASIGNAR", "Animal " + datos[2] + " asignado a [" + fila + "][" + columna + "]");
        } catch (NumberFormatException e) {
            procesarResultado("VALIDACION:Fila y columna deben ser números enteros", "UBICACIONES", "ASIGNAR", null);
        }
    }

    private static void liberarEspacioManual() {
        String[] datos = pedirDatos("Liberar espacio", new String[]{"Código de animal:"});
        if (datos == null) {
            return;
        }
        boolean liberado = ubicaciones.liberarEspacioPorAnimal(datos[0]);
        if (liberado) {
            procesarResultado(null, "UBICACIONES", "LIBERAR", "Espacio de " + datos[0] + " liberado");
        } else {
            JOptionPane.showMessageDialog(null, "El animal " + datos[0] + " no ocupaba ningún espacio");
        }
    }

    // ------------------------------------------------------------------
    // Módulo Bitácora
    // ------------------------------------------------------------------

    private static void menuBitacora() {
        while (true) {
            Integer opcion = mostrarMenu("--- MÓDULO BITÁCORA ---", new String[]{
                "1. Exportar bitácora de acciones a HTML",
                "2. Exportar bitácora de errores a HTML",
                "0. Volver"
            });
            if (opcion == null) {
                JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
                continue;
            }
            switch (opcion) {
                case 1: exportar(Bitacora.exportarAccionesHtml(), "reporte_acciones.html"); break;
                case 2: exportar(Bitacora.exportarErroresHtml(), "reporte_errores.html"); break;
                case 0: return;
                default: JOptionPane.showMessageDialog(null, "Opción inválida, intente de nuevo.");
            }
        }
    }

    // Exportar es una operación de solo lectura/reporte, no una acción de
    // negocio: no genera su propia entrada de bitácora (evita el caso raro de
    // "la bitácora registrando que se exportó la bitácora").
    private static void exportar(String resultado, String archivoGenerado) {
        if (resultado == null) {
            JOptionPane.showMessageDialog(null, "Reporte generado: " + archivoGenerado);
            return;
        }
        int separador = resultado.indexOf(':');
        String descripcion = separador == -1 ? resultado : resultado.substring(separador + 1);
        JOptionPane.showMessageDialog(null, descripcion, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ------------------------------------------------------------------
    // Helpers de interfaz reutilizados por todos los módulos
    // ------------------------------------------------------------------

    /** Arma un menú de opciones con el mismo patrón JPanel+JLabel+JTextField que ya usaba el proyecto. */
    private static Integer mostrarMenu(String titulo, String[] opciones) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField campoOpcion = new JTextField();

        panel.add(new JLabel(titulo));
        for (String opcion : opciones) {
            panel.add(new JLabel(opcion));
        }
        panel.add(new JLabel("Ingrese una opción: "));
        panel.add(campoOpcion);

        JOptionPane.showConfirmDialog(null, panel, titulo, JOptionPane.OK_CANCEL_OPTION);
        return leerOpcion(campoOpcion);
    }

    // Envuelve el parseo en try/catch: cancelar el diálogo o escribir texto ya
    // no revienta el programa (bug que sí tenía la versión original), se trata
    // como una opción inválida más.
    private static Integer leerOpcion(JTextField campo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Generaliza el formulario JPanel+JLabel+JTextField+JOptionPane que ya
     * existía en el proyecto (registroAnimalesRescatados original) para
     * reutilizarlo en cualquier módulo. Devuelve null si el usuario canceló.
     */
    private static String[] pedirDatos(String titulo, String[] etiquetas) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField[] campos = new JTextField[etiquetas.length];

        for (int i = 0; i < etiquetas.length; i++) {
            campos[i] = new JTextField();
            panel.add(new JLabel(etiquetas[i]));
            panel.add(campos[i]);
        }

        int opcion = JOptionPane.showConfirmDialog(null, panel, titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        String[] valores = new String[etiquetas.length];
        for (int i = 0; i < etiquetas.length; i++) {
            valores[i] = campos[i].getText().trim();
        }
        return valores;
    }

    /** Reemplaza el System.out.println original por una ventana con scroll, para listados largos. */
    private static void mostrarListado(String contenido, String titulo) {
        JTextArea area = new JTextArea(contenido, 15, 50);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(null, new JScrollPane(area), titulo, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Aplica la convención "null = éxito / EVENTO:descripcion = fallo" que
     * usan todos los métodos de las clases modelo: registra en la bitácora
     * correspondiente y muestra el diálogo, sin repetir esta lógica en cada
     * una de las acciones del menú. Devuelve true si fue éxito (para que el
     * llamador pueda encadenar un efecto secundario, como liberar una celda).
     */
    private static boolean procesarResultado(String resultado, String modulo, String eventoExito, String descripcionExito) {
        if (resultado == null) {
            Bitacora.registrarAccion(sesion.obtenerUsuarioActual(), modulo, eventoExito, descripcionExito);
            JOptionPane.showMessageDialog(null, descripcionExito);
            return true;
        }
        int separador = resultado.indexOf(':');
        String evento = separador == -1 ? "VALIDACION" : resultado.substring(0, separador);
        String descripcion = separador == -1 ? resultado : resultado.substring(separador + 1);
        Bitacora.registrarError(sesion.obtenerUsuarioActual(), modulo, evento, descripcion);
        JOptionPane.showMessageDialog(null, descripcion, "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}