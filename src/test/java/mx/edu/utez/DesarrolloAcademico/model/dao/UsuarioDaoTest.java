package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.model.Evento;
import mx.edu.utez.DesarrolloAcademico.model.Usuario;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;
import mx.edu.utez.DesarrolloAcademico.utils.HashUtils;
import mx.edu.utez.DesarrolloAcademico.AbstractDaoContainerTest;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para el DAO {@link UsuarioDao}.
 *
 * Aquí se prueba el CRUD completo de Usuario:
 *   CREATE  -> registrarUsuario()
 *   READ    -> buscarPorId(), buscarPorEmailOEmpleado(), login(), GestionDocentes()
 *   UPDATE  -> actualizarPerfil(), cambiarEstado(), actualizarPasswordEnCuenta()
 *   DELETE  -> eliminarUsuario()
 *
 * @author Luis Gerardo Barrón Flores
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Pruebas de Usuario")
class UsuarioDaoTest extends AbstractDaoContainerTest {

    private static UsuarioDao dao;

    /** Datos del usuario de prueba que se crea y elimina en esta clase. */
    private static final String CORREO_TEST = "test_junit_usuario@utez.edu.mx";
    private static final String EMPLEADO_TEST = "JUNIT-USR-001";
    private static final String PASS_TEST = "Password123!";

    private static int idUsuarioCreado = -1;

    /**
     * Prepara el DAO y limpia cualquier residuo de ejecuciones anteriores.
     * La base de datos ya viene levantada dentro del contenedor Docker por
     * la clase base {@link AbstractDaoContainerTest}.
     */
    @BeforeAll
    static void verificarConexion() {
        dao = new UsuarioDao();
        // El contenedor Docker ya fue levantado por AbstractDaoContainerTest,
        // por lo que la conexion siempre debe estar disponible aqui.
        try (Connection con = DatabaseConnection.getConnection()) {
            assertNotNull(con, "El contenedor Oracle debe proveer una conexion valida");
        } catch (SQLException e) {
            throw new IllegalStateException("Error preparando los datos de prueba", e);
        }
        limpiarUsuarioDePrueba();
    }

    /** Elimina cualquier residuo del usuario de prueba al terminar. */
    @AfterAll
    static void limpiar() {
        limpiarUsuarioDePrueba();
    }

    /**
     * Borra directamente por SQL el usuario de prueba, por si una prueba falló
     * antes de llegar al paso de eliminación.
     */
    private static void limpiarUsuarioDePrueba() {
        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) return;
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM usuario WHERE correo_institucional = ? OR numero_empleado = ?")) {
                ps.setString(1, CORREO_TEST);
                ps.setString(2, EMPLEADO_TEST);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Aviso al limpiar usuario de prueba: " + e.getMessage());
        }
    }

    /** Arma el usuario que se usa en las pruebas. */
    private Usuario construirUsuarioDePrueba() {
        Usuario u = new Usuario();
        u.setNombre("JUnit");
        u.setApellidoPaterno("Prueba");
        u.setApellidoMaterno("Automatica");
        u.setRol("docente");
        u.setIdDivision(1);
        u.setNumeroEmpleado(EMPLEADO_TEST);
        u.setTelefono("7770000000");
        u.setCorreoInstitucional(CORREO_TEST);
        u.setContrasena(HashUtils.hashSHA256(PASS_TEST));
        u.setActivo(1);
        u.setCreadoPor(1);
        return u;
    }

    // ----- Crear -----

    @Test
    @Order(1)
    @DisplayName("Crear: registrarUsuario() inserta un usuario nuevo")
    void testRegistrarUsuario() {
        Usuario nuevo = construirUsuarioDePrueba();

        boolean resultado = dao.registrarUsuario(nuevo);

        assertTrue(resultado, "registrarUsuario() debería devolver true si se guardó bien");
    }

    // ----- Consultar -----

    @Test
    @Order(2)
    @DisplayName("Consultar: buscarPorEmailOEmpleado() encuentra el usuario recién creado")
    void testBuscarPorEmailOEmpleado() {
        Usuario encontrado = dao.buscarPorEmailOEmpleado(CORREO_TEST);

        assertNotNull(encontrado, "El usuario creado debe poder recuperarse por su correo");
        assertEquals(CORREO_TEST, encontrado.getCorreoInstitucional());
        assertEquals("JUnit", encontrado.getNombre());

        // Guardamos el id generado para las siguientes pruebas
        idUsuarioCreado = encontrado.getIdUsuario();
        assertTrue(idUsuarioCreado > 0, "El id del usuario creado debe ser mayor a 0");
    }

    @Test
    @Order(3)
    @DisplayName("Consultar: buscarPorId() devuelve el mismo usuario")
    void testBuscarPorId() {
        Assumptions.assumeTrue(idUsuarioCreado > 0, "Hace falta el id del usuario creado");

        Usuario encontrado = dao.buscarPorId(idUsuarioCreado);

        assertNotNull(encontrado, "buscarPorId() debe encontrar el usuario existente");
        assertEquals(idUsuarioCreado, encontrado.getIdUsuario());
        assertEquals(EMPLEADO_TEST, encontrado.getNumeroEmpleado());
    }

    @Test
    @Order(4)
    @DisplayName("Consultar: buscarPorId() devuelve null con un id inexistente")
    void testBuscarPorIdInexistente() {
        Usuario inexistente = dao.buscarPorId(-9999);

        assertNull(inexistente, "Un id inexistente debe devolver null");
    }

    @Test
    @Order(5)
    @DisplayName("Consultar: login() autentica con credenciales correctas")
    void testLoginCorrecto() {
        Usuario autenticado = dao.login(CORREO_TEST, HashUtils.hashSHA256(PASS_TEST));

        assertNotNull(autenticado, "El login debe funcionar con la contraseña correcta");
        assertEquals(CORREO_TEST, autenticado.getCorreoInstitucional());
    }

    @Test
    @Order(6)
    @DisplayName("Consultar: login() rechaza credenciales incorrectas")
    void testLoginIncorrecto() {
        Usuario resultado = dao.login(CORREO_TEST, HashUtils.hashSHA256("ContrasenaEquivocada"));

        assertNull(resultado, "El login debe fallar con una contraseña incorrecta");
    }

    @Test
    @Order(7)
    @DisplayName("Consultar: GestionDocentes() devuelve una lista no nula")
    void testGestionDocentes() {
        List<Usuario> docentes = dao.GestionDocentes();

        assertNotNull(docentes, "La lista de docentes no debería llegar null");
    }

    @Test
    @Order(8)
    @DisplayName("Consultar: obtenerProximosEventos() devuelve una lista no nula")
    void testObtenerProximosEventos() {
        List<Evento> eventos = dao.obtenerProximosEventos(1);

        assertNotNull(eventos, "La lista de próximos eventos no debería llegar null");
    }

    // ----- Actualizar -----

    @Test
    @Order(9)
    @DisplayName("Actualizar: actualizarPerfil() cambia el teléfono del usuario")
    void testActualizarPerfil() {
        Assumptions.assumeTrue(idUsuarioCreado > 0, "Hace falta el id del usuario creado");
        String nuevoTelefono = "7771111111";

        boolean actualizado = dao.actualizarPerfil(idUsuarioCreado, nuevoTelefono, null);
        assertTrue(actualizado, "actualizarPerfil() debe devolver true");

        Usuario recargado = dao.buscarPorId(idUsuarioCreado);
        assertNotNull(recargado);
        assertEquals(nuevoTelefono, recargado.getTelefono(),
                "El teléfono debe quedar actualizado en la base de datos");
    }

    @Test
    @Order(10)
    @DisplayName("Actualizar: cambiarEstado() desactiva y reactiva al usuario")
    void testCambiarEstado() {
        Assumptions.assumeTrue(idUsuarioCreado > 0, "Hace falta el id del usuario creado");

        assertTrue(dao.cambiarEstado(idUsuarioCreado, 0), "Debe poder desactivarse");
        assertEquals(0, dao.buscarPorId(idUsuarioCreado).getActivo(), "El usuario debe quedar inactivo");

        assertTrue(dao.cambiarEstado(idUsuarioCreado, 1), "Debe poder reactivarse");
        assertEquals(1, dao.buscarPorId(idUsuarioCreado).getActivo(), "El usuario debe quedar activo");
    }

    @Test
    @Order(11)
    @DisplayName("Actualizar: actualizarPasswordEnCuenta() rechaza la contraseña actual incorrecta")
    void testActualizarPasswordConActualIncorrecta() {
        Assumptions.assumeTrue(idUsuarioCreado > 0, "Hace falta el id del usuario creado");

        boolean resultado = dao.actualizarPasswordEnCuenta(
                idUsuarioCreado,
                HashUtils.hashSHA256("NoEsLaActual"),
                HashUtils.hashSHA256("NuevaPass456!"));

        assertFalse(resultado, "No debe permitir el cambio si la contraseña actual no coincide");
    }

    @Test
    @Order(12)
    @DisplayName("Actualizar: guardarCodigoRecuperacion() y verificarCodigo() funcionan en conjunto")
    void testCodigoRecuperacion() {
        Assumptions.assumeTrue(idUsuarioCreado > 0, "Hace falta el id del usuario creado");
        String codigo = "JU1234";

        boolean guardado = dao.guardarCodigoRecuperacion(idUsuarioCreado, codigo);
        assertTrue(guardado, "El código de recuperación debe guardarse correctamente");

        Usuario porCodigo = dao.verificarCodigo(codigo);
        assertNotNull(porCodigo, "verificarCodigo() debe encontrar al usuario por su código");
        assertEquals(idUsuarioCreado, porCodigo.getIdUsuario());
    }

    // ----- Eliminar -----

    @Test
    @Order(13)
    @DisplayName("Eliminar: eliminarUsuario() borra el usuario de prueba")
    void testEliminarUsuario() {
        Assumptions.assumeTrue(idUsuarioCreado > 0, "Hace falta el id del usuario creado");

        boolean eliminado = dao.eliminarUsuario(idUsuarioCreado);
        assertTrue(eliminado, "eliminarUsuario() debe devolver true");

        Usuario yaNoExiste = dao.buscarPorId(idUsuarioCreado);
        assertNull(yaNoExiste, "El usuario ya no debe existir después de eliminarlo");
    }
}
