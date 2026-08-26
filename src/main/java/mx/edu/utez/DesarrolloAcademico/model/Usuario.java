package mx.edu.utez.DesarrolloAcademico.model;

import java.sql.Timestamp;

/**
 * Clase de modelo (POJO) que representa a un usuario del sistema (docente, coordinador o desarrollador), con sus datos personales, de acceso y de estado.
 * @author Gael Itzaya Velez Reyez
 * @since 2026-07-16
 */
public class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String rol;
    private Integer idDivision; // Puede ser null
    private String numeroEmpleado;
    private String telefono;
    private String correoInstitucional;
    private String contrasena;
    private Timestamp fechaRegistro;
    private int activo;
    private Integer creadoPor; // Puede ser null
    private boolean entregado;

    /**
     * Construye una nueva instancia de Usuario.
     */
    public Usuario() {}

    /**
     * Construye una nueva instancia de Usuario.
     * @param idUsuario Identificador único del usuario.
     * @param nombre Nombre de la persona o registro.
     * @param apellidoPaterno Apellido paterno.
     * @param apellidoMaterno Apellido materno.
     * @param rol Rol del usuario (docente, coordinador o desarrollador).
     * @param idDivision Identificador de la división académica.
     * @param numeroEmpleado Número de empleado institucional.
     * @param telefono Número de teléfono de contacto.
     * @param correoInstitucional Correo electrónico institucional.
     * @param contrasena Contraseña del usuario.
     * @param fechaRegistro Fecha y hora de registro.
     * @param activo Estado de actividad del registro (1 = activo, 0 = inactivo).
     * @param creadoPor Identificador del usuario que creó el registro.
     */
    public Usuario(int idUsuario, String nombre, String apellidoPaterno, String apellidoMaterno, String rol, 
                   Integer idDivision, String numeroEmpleado, String telefono, String correoInstitucional, 
                   String contrasena, Timestamp fechaRegistro, int activo, Integer creadoPor) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.rol = rol;
        this.idDivision = idDivision;
        this.numeroEmpleado = numeroEmpleado;
        this.telefono = telefono;
        this.correoInstitucional = correoInstitucional;
        this.contrasena = contrasena;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
        this.creadoPor = creadoPor;
    }

    /**
     * Obtiene identificador único del usuario.
     * @return Valor entero resultante.
     */
    public int getIdUsuario() { return idUsuario; }
    /**
     * Establece identificador único del usuario.
     * @param idUsuario Identificador único del usuario.
     */
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    /**
     * Obtiene nombre de la persona o registro.
     * @return Cadena de texto resultante.
     */
    public String getNombre() { return nombre; }
    /**
     * Establece nombre de la persona o registro.
     * @param nombre Nombre de la persona o registro.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene apellido paterno.
     * @return Cadena de texto resultante.
     */
    public String getApellidoPaterno() { return apellidoPaterno; }
    /**
     * Establece apellido paterno.
     * @param apellidoPaterno Apellido paterno.
     */
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    /**
     * Obtiene apellido materno.
     * @return Cadena de texto resultante.
     */
    public String getApellidoMaterno() { return apellidoMaterno; }
    /**
     * Establece apellido materno.
     * @param apellidoMaterno Apellido materno.
     */
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    /**
     * Obtiene rol del usuario (docente, coordinador o desarrollador).
     * @return Cadena de texto resultante.
     */
    public String getRol() { return rol; }
    /**
     * Establece rol del usuario (docente, coordinador o desarrollador).
     * @param rol Rol del usuario (docente, coordinador o desarrollador).
     */
    public void setRol(String rol) { this.rol = rol; }

    /**
     * Obtiene identificador de la división académica.
     * @return Resultado de tipo `Integer`.
     */
    public Integer getIdDivision() { return idDivision; }
    /**
     * Establece identificador de la división académica.
     * @param idDivision Identificador de la división académica.
     */
    public void setIdDivision(Integer idDivision) { this.idDivision = idDivision; }

    /**
     * Obtiene número de empleado institucional.
     * @return Cadena de texto resultante.
     */
    public String getNumeroEmpleado() { return numeroEmpleado; }
    /**
     * Establece número de empleado institucional.
     * @param numeroEmpleado Número de empleado institucional.
     */
    public void setNumeroEmpleado(String numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }

    /**
     * Obtiene número de teléfono de contacto.
     * @return Cadena de texto resultante.
     */
    public String getTelefono() { return telefono; }
    /**
     * Establece número de teléfono de contacto.
     * @param telefono Número de teléfono de contacto.
     */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /**
     * Obtiene correo electrónico institucional.
     * @return Cadena de texto resultante.
     */
    public String getCorreoInstitucional() { return correoInstitucional; }
    /**
     * Establece correo electrónico institucional.
     * @param correoInstitucional Correo electrónico institucional.
     */
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }

    /**
     * Obtiene contraseña del usuario.
     * @return Cadena de texto resultante.
     */
    public String getContrasena() { return contrasena; }
    /**
     * Establece contraseña del usuario.
     * @param contrasena Contraseña del usuario.
     */
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    /**
     * Obtiene fecha y hora de registro.
     * @return Resultado de tipo `Timestamp`.
     */
    public Timestamp getFechaRegistro() { return fechaRegistro; }
    /**
     * Establece fecha y hora de registro.
     * @param fechaRegistro Fecha y hora de registro.
     */
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    /**
     * Obtiene estado de actividad del registro (1 = activo, 0 = inactivo).
     * @return Valor entero resultante.
     */
    public int getActivo() { return activo; }
    /**
     * Establece estado de actividad del registro (1 = activo, 0 = inactivo).
     * @param activo Estado de actividad del registro (1 = activo, 0 = inactivo).
     */
    public void setActivo(int activo) { this.activo = activo; }

    /**
     * Obtiene identificador del usuario que creó el registro.
     * @return Resultado de tipo `Integer`.
     */
    public Integer getCreadoPor() { return creadoPor; }
    /**
     * Establece identificador del usuario que creó el registro.
     * @param creadoPor Identificador del usuario que creó el registro.
     */
    public void setCreadoPor(Integer creadoPor) { this.creadoPor = creadoPor; }

    /**
     * Indica si la constancia/documento ya fue entregado.
     * @return `true` si la operación fue exitosa o la condición se cumple; `false` en caso contrario.
     */
    public boolean isEntregado() { return entregado; }
    /**
     * Establece indica si la constancia/documento ya fue entregado.
     * @param entregado Indica si la constancia/documento ya fue entregado.
     */
    public void setEntregado(boolean entregado) { this.entregado = entregado; }
}
