package mx.edu.utez.DesarrolloAcademico.model.dao;

import mx.edu.utez.DesarrolloAcademico.model.Periodo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioListaDaoTest {

    private UsuarioListaDao dao;

    @BeforeEach
    public void setUp() {
        dao = new UsuarioListaDao();
    }

    @AfterEach
    public void tearDown() {
        dao = null;
    }

    @Test
    public void testObtenerNombreDivision() {
        // La división 1 siempre suele ser DATID o similar, pero al menos no debe ser nula
        String nombre = dao.obtenerNombreDivision("1");
        assertNotNull(nombre, "El nombre de la división no debería ser nulo");
        assertFalse(nombre.isEmpty(), "El nombre de la división no debería estar vacío");
    }

    @Test
    public void testContarDocentesD() {
        int total = dao.contarDocentesYCoordinadores();
        assertTrue(total >= 0, "El total de docentes debe ser mayor o igual a 0");
    }

    @Test
    public void testObtenerTodosLosPeriodos() {
        List<Periodo> periodos = dao.obtenerTodosLosPeriodos();
        assertNotNull(periodos, "La lista de periodos no debe ser nula");
    }

    @Test
    public void testContarEventos() {
        int total = dao.contarEventos();
        assertTrue(total >= 0, "El total de eventos debe ser mayor o igual a 0");
    }
}
