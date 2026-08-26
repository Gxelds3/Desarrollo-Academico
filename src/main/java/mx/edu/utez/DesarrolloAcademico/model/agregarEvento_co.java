package mx.edu.utez.DesarrolloAcademico.model;

import java.util.List;

/**
 * Clase de modelo (POJO) que representa la entidad 'agregarEvento_co' dentro del sistema.
 * @author Carlos Apreza Gutierrez
 * @since 2026-07-29
 */
public class agregarEvento_co {



    private int id;
    private String nombre;
    private String lugar;
    private String institucion;
    private String tipo;
    private String descripcion;
    private String fechaInicio; // Puedes usar java.sql.Date si prefieres
    private String fechaFin;
    private String modalidad;
    private List<Integer> docentesAsignados; // Lista de IDs de los docentes

    // Agrega constructores, getters y setters

    /**
     * Construye una nueva instancia de agregarEvento_co.
     */
    public agregarEvento_co() {
    }

    /**
     * Obtiene identificador único del registro.
     * @return Valor entero resultante.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece identificador único del registro.
     * @param id Identificador único del registro.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el valor de docentes asignados.
     * @return Lista con los registros obtenidos (vacía si no hay resultados).
     */
    public List<Integer> getDocentesAsignados() {
        return docentesAsignados;
    }

    /**
     * Establece el valor de docentes asignados.
     * @param docentesAsignados Parámetro `docentesAsignados`.
     */
    public void setDocentesAsignados(List<Integer> docentesAsignados) {
        this.docentesAsignados = docentesAsignados;
    }

    /**
     * Obtiene modalidad del evento (presencial, en línea, etc.).
     * @return Cadena de texto resultante.
     */
    public String getModalidad() {
        return modalidad;
    }

    /**
     * Establece modalidad del evento (presencial, en línea, etc.).
     * @param modalidad Modalidad del evento (presencial, en línea, etc.).
     */
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    /**
     * Obtiene fecha de fin.
     * @return Cadena de texto resultante.
     */
    public String getFechaFin() {
        return fechaFin;
    }

    /**
     * Establece fecha de fin.
     * @param fechaFin Fecha de fin.
     */
    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Obtiene tipo o categoría del registro.
     * @return Cadena de texto resultante.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Establece tipo o categoría del registro.
     * @param tipo Tipo o categoría del registro.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene descripción del registro.
     * @return Cadena de texto resultante.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece descripción del registro.
     * @param descripcion Descripción del registro.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene fecha de inicio.
     * @return Cadena de texto resultante.
     */
    public String getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establece fecha de inicio.
     * @param fechaInicio Fecha de inicio.
     */
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Obtiene institución relacionada con el evento.
     * @return Cadena de texto resultante.
     */
    public String getInstitucion() {
        return institucion;
    }

    /**
     * Establece institución relacionada con el evento.
     * @param institucion Institución relacionada con el evento.
     */
    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    /**
     * Obtiene el valor de lugar.
     * @return Cadena de texto resultante.
     */
    public String getLugar() {
        return lugar;
    }

    /**
     * Establece el valor de lugar.
     * @param lugar Parámetro `lugar`.
     */
    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    /**
     * Obtiene nombre de la persona o registro.
     * @return Cadena de texto resultante.
     */
    public String getNombre() {
        return nombre;
    }
    private Integer idDivision;
    private String nombreDivision;
    private Integer creadoPor;

    /**
     * Establece nombre de la persona o registro.
     * @param nombre Nombre de la persona o registro.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Construye una nueva instancia de agregarEvento_co.
     * @param id Identificador único del registro.
     * @param nombre Nombre de la persona o registro.
     * @param lugar Parámetro `lugar`.
     * @param institucion Institución relacionada con el evento.
     * @param tipo Tipo o categoría del registro.
     * @param descripcion Descripción del registro.
     * @param fechaInicio Fecha de inicio.
     * @param fechaFin Fecha de fin.
     * @param modalidad Modalidad del evento (presencial, en línea, etc.).
     * @param docentesAsignados Parámetro `docentesAsignados`.
     */
    public agregarEvento_co (int id, String nombre, String lugar, String institucion, String tipo, String descripcion, String fechaInicio, String fechaFin, String modalidad, List<Integer> docentesAsignados) {
        this.id = id;
        this.nombre = nombre;
        this.lugar = lugar;
        this.institucion = institucion;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.modalidad = modalidad;
        this.docentesAsignados = docentesAsignados;
    }

    /**
     * Obtiene identificador de la división académica.
     * @return Resultado de tipo `Integer`.
     */
    public Integer getIdDivision() {
        return idDivision;
    }

    /**
     * Establece identificador de la división académica.
     * @param idDivision Identificador de la división académica.
     */
    public void setIdDivision(Integer idDivision) {
        this.idDivision = idDivision;
    }

    /**
     * Obtiene el valor de nombre division.
     * @return Cadena de texto resultante.
     */
    public String getNombreDivision() {
        return nombreDivision;
    }

    /**
     * Establece el valor de nombre division.
     * @param nombreDivision Parámetro `nombreDivision`.
     */
    public void setNombreDivision(String nombreDivision) {
        this.nombreDivision = nombreDivision;
    }

    /**
     * Obtiene identificador del usuario que creó el registro.
     * @return Resultado de tipo `Integer`.
     */
    public Integer getCreadoPor() {
        return creadoPor;
    }

    /**
     * Establece identificador del usuario que creó el registro.
     * @param creadoPor Identificador del usuario que creó el registro.
     */
    public void setCreadoPor(Integer creadoPor) {
        this.creadoPor = creadoPor;
    }
}
