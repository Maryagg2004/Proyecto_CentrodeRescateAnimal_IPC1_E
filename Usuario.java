/**
 * Módulo de autenticación. Los usuarios están precargados en memoria (arreglos
 * paralelos); no se registran usuarios nuevos, eso no está en el alcance del
 * proyecto.
 */
public class Usuario {
    private static final int CAPACIDAD_USUARIOS = 2;
    private static final int MAX_INTENTOS = 3;

    // Arreglos paralelos: usuarios[i] va siempre con contrasenas[i] y roles[i].
    private String[] usuarios = {"admin1", "auxiliar1"};
    private String[] contrasenas = {"Refugio2026", "Auxiliar2026"};
    private String[] roles = {"ADMIN", "AUXILIAR"};

    private int intentosFallidos = 0;
    // El bloqueo dura toda la ejecución del proceso (el enunciado indica que se
    // levanta al reiniciar la aplicación), por eso no existe un método para desbloquear.
    private boolean bloqueado = false;

    private String usuarioActual = null;
    private String rolActual = null;

    public static boolean validarUsuario(String usuario) {
        if (usuario == null) {
            return false;
        }
        return usuario.matches("[A-Za-z0-9]{4,15}");
    }

    public static boolean validarContrasena(String contrasena) {
        if (contrasena == null) {
            return false;
        }
        return contrasena.length() >= 6;
    }

    /**
     * Intenta iniciar sesión. Devuelve null si el login fue exitoso; si no,
     * devuelve el mensaje que se debe mostrar al usuario (bloqueo, formato
     * inválido o credenciales incorrectas).
     */
    public String iniciarSesion(String usuario, String contrasena) {
        if (bloqueado) {
            return "Sesión bloqueada, reinicie la aplicación.";
        }

        // Un formato inválido también cuenta como intento fallido: no se
        // regala un intento gratis por escribir mal el usuario o la contraseña.
        if (!validarUsuario(usuario) || !validarContrasena(contrasena)) {
            return registrarIntentoFallido();
        }

        for (int i = 0; i < CAPACIDAD_USUARIOS; i++) {
            if (usuarios[i].equals(usuario) && contrasenas[i].equals(contrasena)) {
                intentosFallidos = 0;
                usuarioActual = usuario;
                rolActual = roles[i];
                return null;
            }
        }
        return registrarIntentoFallido();
    }

    private String registrarIntentoFallido() {
        intentosFallidos++;
        if (intentosFallidos >= MAX_INTENTOS) {
            bloqueado = true;
            return "Usuario o contraseña incorrectos. Se alcanzó el máximo de intentos, sesión bloqueada.";
        }
        return "Usuario o contraseña incorrectos (intento " + intentosFallidos + " de " + MAX_INTENTOS + ").";
    }

    public boolean estaBloqueado() {
        return bloqueado;
    }

    public String obtenerUsuarioActual() {
        return usuarioActual;
    }

    public String obtenerRolActual() {
        return rolActual;
    }
}
