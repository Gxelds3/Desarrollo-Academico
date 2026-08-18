package mx.edu.utez.DesarrolloAcademico.model.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConstanciaDaoTest {

    private ConstanciaDao dao;

    @BeforeEach
    public void setUp() {
        dao = new ConstanciaDao();
    }

    @AfterEach
    public void tearDown() {
        dao = null;
    }

    @Test
    public void testVerificarConstanciaExistente_NoExiste() {
        // Con un ID de participante que muy probablemente no exista
        boolean existe = dao.verificarConstanciaExistente(-999);
        assertFalse(existe, "No debería existir una constancia para un ID de participante inválido");
    }

    @Test
    public void testObtenerConstancia_NoExiste() {
        Map<String, Object> constancia = dao.obtenerConstancia(-999);
        assertNull(constancia, "La constancia devuelta debería ser nula para un ID de participante inválido");
    }

    @Test
    public void testEsPeriodoActivo() {
        // Ejecutamos el método para asegurar que la consulta SQL no tenga errores de sintaxis
        // El resultado puede ser true o false dependiendo de la BD, pero no debe fallar.
        boolean esActivo = false;
        try {
            esActivo = dao.esPeriodoActivo(1);
        } catch (Exception e) {
            fail("La ejecución de esPeriodoActivo no debería lanzar excepciones");
        }
        // Solo verificamos que corre sin excepciones
    }
}
