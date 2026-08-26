package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.model.agregarEvento_co;
import mx.edu.utez.DesarrolloAcademico.utils.DatabaseConnection;
import mx.edu.utez.DesarrolloAcademico.AbstractDaoContainerTest;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



/**
 * Pruebas para el DAO {@link AgregarEvento_Co}.
 *
 * Aquí se prueba el CRUD completo de Evento:
 *   CREATE  -> registrarEventoError() / registrarEvento()
 *   READ    -> obtenerPorId(), listarEventos(), listarTodosLosEventos()
 *   UPDATE  -> actualizarEvento()
 *   DELETE  -> eliminarEvento()
 *
 * @author Luis Gerardo Barrón Flores
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Pruebas de Eventos")
class AgregarEvento_CoTest extends AbstractDaoContainerTest {

    private static AgregarEvento_Co dao;

    private static final String NOMBRE_TEST = "Evento JUnit de Prueba";
    private static final String NOMBRE_ACTUALIZADO = "Evento JUnit Actualizado";

    private static int idEventoCreado = -1;

    @BeforeAll
    static void verificarConexion() {
        dao = new AgregarEvento_Co();
        // El contenedor Docker ya fue levantado por AbstractDaoContainerTest,
        // por lo que la conexion siempre debe estar disponible aqui.
        try (Connection con = DatabaseConnection.getConnection()) {
            assertNotNull(con, "El contenedor Oracle debe proveer una conexion valida");
        } catch (SQLException e) {
            throw new IllegalStateException("Error preparando los datos de prueba", e);
        }
        limpiarEventosDePrueba();
    }

    @AfterAll
    static void limpiar() {
        limpiarEventosDePrueba();
    }

    /** Borra por SQL cualquier evento de prueba que haya quedado colgado. */
    private static void limpiarEventosDePrueba() {
        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) return;
            // Primero los participantes (llave foránea), luego el evento
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM participante_evento WHERE id_evento IN " +
                            "(SELECT id_evento FROM evento WHERE nombre IN (?, ?))")) {
                ps.setString(1, NOMBRE_TEST);
                ps.setString(2, NOMBRE_ACTUALIZADO);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM evento WHERE nombre IN (?, ?)")) {
                ps.setString(1, NOMBRE_TEST);
                ps.setString(2, NOMBRE_ACTUALIZADO);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Aviso al limpiar eventos de prueba: " + e.getMessage());
        }
    }

    /** Arma un evento con fechas a futuro para las pruebas. */
    private agregarEvento_co construirEventoDePrueba() {
        agregarEvento_co e = new agregarEvento_co();
        e.setNombre(NOMBRE_TEST);
        e.setLugar("Aula JUnit");
        e.setInstitucion("UTEZ");
        e.setTipo("curso");
        e.setDescripcion("Evento creado automáticamente por las pruebas unitarias.");
        e.setFechaInicio(LocalDate.now().plusDays(10).toString());
        e.setFechaFin(LocalDate.now().plusDays(15).toString());
        e.setModalidad("presencial");
        e.setIdDivision(1);
        e.setCreadoPor(1);
        return e;
    }

    // ----- Crear -----

    @Test
    @Order(1)
    @DisplayName("Crear: registrarEventoError() inserta un evento nuevo")
    void testRegistrarEvento() {
        agregarEvento_co nuevo = construirEventoDePrueba();

        String error = dao.registrarEventoError(nuevo);

        assertNull(error, "registrarEventoError() debe devolver null cuando la inserción es exitosa");
    }

    // ----- Consultar -----

    @Test
    @Order(2)
    @DisplayName("Consultar: listarTodosLosEventos() incluye el evento recién creado")
    void testListarTodosLosEventos() {
        List<agregarEvento_co> eventos = dao.listarTodosLosEventos();

        assertNotNull(eventos, "La lista de eventos no debería llegar null");

        agregarEvento_co encontrado = eventos.stream()
                .filter(e -> NOMBRE_TEST.equals(e.getNombre()))
                .findFirst()
                .orElse(null);

        assertNotNull(encontrado, "El evento de prueba debe aparecer en el listado");

        idEventoCreado = encontrado.getId();
        assertTrue(idEventoCreado > 0, "El id del evento creado debe ser mayor a 0");
    }

    @Test
    @Order(3)
    @DisplayName("Consultar: obtenerPorId() devuelve el evento con sus datos correctos")
    void testObtenerPorId() {
        Assumptions.assumeTrue(idEventoCreado > 0, "Hace falta el id del evento creado");

        agregarEvento_co evento = dao.obtenerPorId(idEventoCreado);

        assertNotNull(evento, "obtenerPorId() debe encontrar el evento existente");
        assertEquals(NOMBRE_TEST, evento.getNombre());
        assertEquals("Aula JUnit", evento.getLugar());
        assertEquals("UTEZ", evento.getInstitucion());
    }

    @Test
    @Order(4)
    @DisplayName("Consultar: obtenerPorId() devuelve null con un id inexistente")
    void testObtenerPorIdInexistente() {
        agregarEvento_co inexistente = dao.obtenerPorId(-9999);

        assertNull(inexistente, "Un id inexistente debe devolver null");
    }

    @Test
    @Order(5)
    @DisplayName("Consultar: listarEventos() filtra por división sin devolver null")
    void testListarEventosPorDivision() {
        List<agregarEvento_co> eventos = dao.listarEventos(1);

        assertNotNull(eventos, "La lista filtrada por división no debería llegar null");
        // Todos los eventos devueltos deben ser vigentes (fecha_fin >= hoy)
        eventos.forEach(e -> assertNotNull(e.getFechaFin(), "Cada evento debe traer su fecha de fin"));
    }

    // ----- Actualizar -----

    @Test
    @Order(6)
    @DisplayName("Actualizar: actualizarEvento() modifica los datos del evento")
    void testActualizarEvento() {
        Assumptions.assumeTrue(idEventoCreado > 0, "Hace falta el id del evento creado");

        agregarEvento_co evento = dao.obtenerPorId(idEventoCreado);
        assertNotNull(evento);

        evento.setNombre(NOMBRE_ACTUALIZADO);
        evento.setLugar("Auditorio JUnit");
        evento.setModalidad("en linea");

        boolean actualizado = dao.actualizarEvento(evento);
        assertTrue(actualizado, "actualizarEvento() debe devolver true");

        agregarEvento_co recargado = dao.obtenerPorId(idEventoCreado);
        assertNotNull(recargado);
        assertEquals(NOMBRE_ACTUALIZADO, recargado.getNombre(), "El nombre debe quedar actualizado");
        assertEquals("Auditorio JUnit", recargado.getLugar(), "El lugar debe quedar actualizado");
    }

    @Test
    @Order(7)
    @DisplayName("Consultar: obtenerFechaLimitePorDivision() no lanza excepción")
    void testObtenerFechaLimitePorDivision() {
        // Puede devolver null si no hay periodo activo; lo importante es que no truene
        assertDoesNotThrow(() -> dao.obtenerFechaLimitePorDivision(1),
                "obtenerFechaLimitePorDivision() no debe lanzar excepciones");
    }

    // ----- Eliminar -----

    @Test
    @Order(8)
    @DisplayName("Eliminar: eliminarEvento() borra el evento de prueba")
    void testEliminarEvento() {
        Assumptions.assumeTrue(idEventoCreado > 0, "Hace falta el id del evento creado");

        boolean eliminado = dao.eliminarEvento(idEventoCreado);
        assertTrue(eliminado, "eliminarEvento() debe devolver true");

        agregarEvento_co yaNoExiste = dao.obtenerPorId(idEventoCreado);
        assertNull(yaNoExiste, "El evento ya no debe existir después de eliminarlo");
    }
}
