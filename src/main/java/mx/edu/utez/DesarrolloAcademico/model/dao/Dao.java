package mx.edu.utez.DesarrolloAcademico.model.dao;
import java.util.List;

/**
 * Interfaz genérica que define las operaciones CRUD básicas (crear, consultar, actualizar, eliminar) que debe implementar cualquier DAO del sistema.
 * @param <T> Tipo de la entidad gestionada por el DAO.
 * @param <K> Tipo del identificador (llave primaria) de la entidad.
 * @author Ángel Gael Flores Ronces
 * @since 2026-07-01
 */
public interface Dao<T, K> {
    /**
     * Inserta una nueva entidad en la base de datos.
     * @param entidad Entidad a persistir.
     * @return `true` si la inserción fue exitosa; `false` en caso contrario.
     */
    boolean create(T entidad);

    /**
     * Consulta y devuelve todos los registros de la entidad en la base de datos.
     * @return Lista con todos los registros encontrados (vacía si no hay resultados).
     */
    List<T> getAll();

    /**
     * Busca y devuelve la entidad cuyo identificador coincide con el recibido.
     * @param id Identificador único de la entidad a buscar.
     * @return La entidad encontrada, o `null` si no existe.
     */
    T getById(K id);

    /**
     * Actualiza en la base de datos los datos de la entidad recibida.
     * @param entidad Entidad con los datos ya actualizados.
     * @return `true` si la actualización fue exitosa; `false` en caso contrario.
     */
    boolean update(T entidad);

    /**
     * Elimina de la base de datos la entidad cuyo identificador coincide con el recibido.
     * @param id Identificador único de la entidad a eliminar.
     * @return `true` si la eliminación fue exitosa; `false` en caso contrario.
     */
    boolean delete(K id);
}
