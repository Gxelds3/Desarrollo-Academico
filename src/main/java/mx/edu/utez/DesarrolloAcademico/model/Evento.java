package mx.edu.utez.DesarrolloAcademico.model;

import java.util.Date;



/**
 * Clase de modelo (POJO) que representa un evento académico registrado en el sistema.
 * @author Carlos Apreza Gutierrez
 * @since 2026-08-07
 */
public class Evento {
    private  int ID;
    private  String nombre;
    private Date Fecha_Inicio;
    private   Date Fecha_Fin;
    private String Lugar;
    private String Institucion;
    private String Tipo_Evento;
    private String Descripcion;
    private String Modalidad;


    /**
     * Obtiene nombre de la persona o registro.
     * @return Cadena de texto resultante.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece nombre de la persona o registro.
     * @param nombre Nombre de la persona o registro.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene identificador único del registro.
     * @return Valor entero resultante.
     */
    public int getID() {
        return ID;
    }

    /**
     * Establece identificador único del registro.
     * @param ID Identificador único del registro.
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Obtiene fecha de inicio.
     * @return Resultado de tipo `Date`.
     */
    public Date getFecha_Inicio() {
        return Fecha_Inicio;
    }

    /**
     * Establece fecha de inicio.
     * @param fecha_Inicio Parámetro `fecha_Inicio`.
     */
    public void setFecha_Inicio(Date fecha_Inicio) {
        Fecha_Inicio = fecha_Inicio;
    }

    /**
     * Obtiene fecha de fin.
     * @return Resultado de tipo `Date`.
     */
    public Date getFecha_Fin() {
        return Fecha_Fin;
    }

    /**
     * Establece fecha de fin.
     * @param fecha_Fin Parámetro `fecha_Fin`.
     */
    public void setFecha_Fin(Date fecha_Fin) {
        Fecha_Fin = fecha_Fin;
    }

    /**
     * Construye una nueva instancia de Evento.
     */
    public Evento() {
    }

    /**
     * Obtiene el valor de lugar.
     * @return Cadena de texto resultante.
     */
    public String getLugar() {
        return Lugar;
    }

    /**
     * Establece el valor de lugar.
     * @param lugar Parámetro `lugar`.
     */
    public void setLugar(String lugar) {
        Lugar = lugar;
    }

    /**
     * Obtiene institución relacionada con el evento.
     * @return Cadena de texto resultante.
     */
    public String getInstitucion() {
        return Institucion;
    }

    /**
     * Establece institución relacionada con el evento.
     * @param institucion Institución relacionada con el evento.
     */
    public void setInstitucion(String institucion) {
        Institucion = institucion;
    }

    /**
     * Obtiene descripción del registro.
     * @return Cadena de texto resultante.
     */
    public String getDescripcion() {
        return Descripcion;
    }

    /**
     * Establece descripción del registro.
     * @param descripcion Descripción del registro.
     */
    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    /**
     * Obtiene el valor de tipo evento.
     * @return Cadena de texto resultante.
     */
    public String getTipo_Evento() {
        return Tipo_Evento;
    }

    /**
     * Establece el valor de tipo evento.
     * @param tipo_Evento Parámetro `tipo_Evento`.
     */
    public void setTipo_Evento(String tipo_Evento) {
        Tipo_Evento = tipo_Evento;
    }

    /**
     * Obtiene modalidad del evento (presencial, en línea, etc.).
     * @return Cadena de texto resultante.
     */
    public String getModalidad() {
        return Modalidad;
    }

    /**
     * Establece modalidad del evento (presencial, en línea, etc.).
     * @param modalidad Modalidad del evento (presencial, en línea, etc.).
     */
    public void setModalidad(String modalidad) {
        Modalidad = modalidad;
    }
}
