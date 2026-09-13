import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Bitácora de Acciones y Errores. Es la única parte del proyecto que persiste
 * en disco (el resto vive en memoria): el enunciado exige guardar ambas
 * bitácoras en archivos de texto separados y poder exportarlas como reportes
 * HTML.
 */
public class Bitacora {
    private static final String ARCHIVO_ACCIONES = "bitacora_acciones.txt";
    private static final String ARCHIVO_ERRORES = "bitacora_errores.txt";
    private static final String REPORTE_ACCIONES = "reporte_acciones.html";
    private static final String REPORTE_ERRORES = "reporte_errores.html";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Bitacora() {
    }

    public static void registrarAccion(String usuario, String modulo, String evento, String descripcion) {
        escribirLinea(ARCHIVO_ACCIONES, usuario, modulo, evento, descripcion);
    }

    public static void registrarError(String usuario, String modulo, String evento, String motivo) {
        escribirLinea(ARCHIVO_ERRORES, usuario, modulo, evento, motivo);
    }

    private static void escribirLinea(String archivo, String usuario, String modulo, String evento, String detalle) {
        String linea = FORMATO_FECHA.format(LocalDateTime.now()) + "|" + usuario + "|" + modulo + "|" + evento + "|" + detalle;
        // Si falla la escritura de la bitácora se ignora silenciosamente: no
        // tiene sentido "loguear el fallo de logueo" y arriesgar un error en cascada.
        try (FileWriter escritor = new FileWriter(archivo, true)) {
            escritor.write(linea + System.lineSeparator());
        } catch (IOException e) {
            // Intencionalmente ignorado, ver comentario arriba.
        }
    }

    public static String exportarAccionesHtml() {
        return exportarHtml(ARCHIVO_ACCIONES, REPORTE_ACCIONES, "Bitácora de Acciones");
    }

    public static String exportarErroresHtml() {
        return exportarHtml(ARCHIVO_ERRORES, REPORTE_ERRORES, "Bitácora de Errores");
    }

    private static String exportarHtml(String archivoOrigen, String archivoDestino, String titulo) {
        StringBuilder filas = new StringBuilder();
        int cantidadLineas = 0;
        try (BufferedReader lector = new BufferedReader(new FileReader(archivoOrigen))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                // "|" es un carácter especial de regex, hay que escaparlo al hacer split.
                String[] campos = linea.split("\\|", -1);
                filas.append("<tr>");
                for (String campo : campos) {
                    filas.append("<td>").append(campo).append("</td>");
                }
                filas.append("</tr>\n");
                cantidadLineas++;
            }
        } catch (IOException e) {
            return "VALIDACION:La " + titulo + " aún no tiene registros";
        }
        if (cantidadLineas == 0) {
            return "VALIDACION:La " + titulo + " aún no tiene registros";
        }

        String html = "<html><head><meta charset=\"UTF-8\"><title>" + titulo + "</title>"
            + "<style>table{border-collapse:collapse;font-family:sans-serif}"
            + "td,th{border:1px solid #999;padding:4px 8px}</style></head><body>"
            + "<h1>" + titulo + "</h1><table>"
            + "<tr><th>Fecha</th><th>Usuario</th><th>Módulo</th><th>Evento</th><th>Detalle</th></tr>"
            + filas + "</table></body></html>";

        try (FileWriter escritor = new FileWriter(archivoDestino, false)) {
            escritor.write(html);
        } catch (IOException e) {
            return "VALIDACION:No se pudo escribir el archivo " + archivoDestino;
        }
        return null;
    }
}
