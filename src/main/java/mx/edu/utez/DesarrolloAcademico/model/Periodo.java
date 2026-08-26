package mx.edu.utez.DesarrolloAcademico.model;

import java.sql.Date;

/**
 * Clase de modelo (POJO) que representa un periodo de carga de constancias.
 * @author Carlos Apreza Gutierrez
 * @since 2026-08-07
 */
public class Periodo {
    private int id;
    private int idDivision; // NUEVO CAMPO
    private String division;
    private Date fechaInicio;
    private Date fechaFin;
    private boolean activo;

    /**
     * Construye una nueva instancia de Periodo.
     */
    public Periodo() {}

    /**
     * Obtiene identificador de la división académica.
     * @return Valor entero resultante.
     */
    public int getIdDivision() { return idDivision; }
    /**
     * Establece identificador de la división académica.
     * @param idDivision Identificador de la división académica.
     */
    public void setIdDivision(int idDivision) { this.idDivision = idDivision; }

    /**
     * Construye una nueva instancia de Periodo.
     * @param division Parámetro `division`.
     * @param fechaInicio Fecha de inicio.
     * @param fechaFin Fecha de fin.
     * @param activo Estado de actividad del registro (1 = activo, 0 = inactivo).
     */
    public Periodo(String division, Date fechaInicio, Date fechaFin, boolean activo) {
        this.division = division;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
    }

    /**
     * Construye una nueva instancia de Periodo.
     * @param id Identificador único del registro.
     * @param division Parámetro `division`.
     * @param fechaInicio Fecha de inicio.
     * @param fechaFin Fecha de fin.
     * @param activo Estado de actividad del registro (1 = activo, 0 = inactivo).
     */
    public Periodo(int id, String division, Date fechaInicio, Date fechaFin, boolean activo) {
        this.id = id;
        this.division = division;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
    }

    // Getters y Setters
    /**
     * Obtiene identificador único del registro.
     * @return Valor entero resultante.
     */
    public int getId() { return id; }
    /**
     * Establece identificador único del registro.
     * @param id Identificador único del registro.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Obtiene el valor de division.
     * @return Cadena de texto resultante.
     */
    public String getDivision() { return division; }
    /**
     * Establece el valor de division.
     * @param division Parámetro `division`.
     */
    public void setDivision(String division) { this.division = division; }

    /**
     * Obtiene fecha de inicio.
     * @return Resultado de tipo `Date`.
     */
    public Date getFechaInicio() { return fechaInicio; }
    /**
     * Establece fecha de inicio.
     * @param fechaInicio Fecha de inicio.
     */
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    /**
     * Obtiene fecha de fin.
     * @return Resultado de tipo `Date`.
     */
    public Date getFechaFin() { return fechaFin; }
    /**
     * Establece fecha de fin.
     * @param fechaFin Fecha de fin.
     */
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    /**
     * Indica si se cumple la condición correspondiente.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean isActivo() { return activo; }
    /**
     * Establece estado de actividad del registro (1 = activo, 0 = inactivo).
     * @param activo Estado de actividad del registro (1 = activo, 0 = inactivo).
     */
    public void setActivo(boolean activo) { this.activo = activo; }
}