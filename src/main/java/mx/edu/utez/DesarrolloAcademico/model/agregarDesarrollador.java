package mx.edu.utez.DesarrolloAcademico.model;

/**
 * Clase de modelo (POJO) que representa la entidad 'agregarDesarrollador' dentro del sistema.
 * @author Carlos Apreza Gutierrez
 * @since 2026-07-31
 */
public class agregarDesarrollador {

    private String nombre;
    private String apellidoM;
    private String getApellidoP;
    private int numeroEmpleado;
    private int telefonoEmpleado;

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
     * Obtiene el valor de telefono empleado.
     * @return Valor entero resultante.
     */
    public int getTelefonoEmpleado() {
        return telefonoEmpleado;
    }

    /**
     * Establece el valor de telefono empleado.
     * @param telefonoEmpleado Parámetro `telefonoEmpleado`.
     */
    public void setTelefonoEmpleado(int telefonoEmpleado) {
        this.telefonoEmpleado = telefonoEmpleado;
    }

    /**
     * Obtiene número de empleado institucional.
     * @return Valor entero resultante.
     */
    public int getNumeroEmpleado() {
        return numeroEmpleado;
    }

    /**
     * Establece número de empleado institucional.
     * @param numeroEmpleado Número de empleado institucional.
     */
    public void setNumeroEmpleado(int numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    /**
     * Obtiene el valor de get apellido p.
     * @return Cadena de texto resultante.
     */
    public String getGetApellidoP() {
        return getApellidoP;
    }

    /**
     * Establece el valor de get apellido p.
     * @param getApellidoP Parámetro `getApellidoP`.
     */
    public void setGetApellidoP(String getApellidoP) {
        this.getApellidoP = getApellidoP;
    }

    /**
     * Obtiene el valor de apellido m.
     * @return Cadena de texto resultante.
     */
    public String getApellidoM() {
        return apellidoM;
    }

    /**
     * Establece el valor de apellido m.
     * @param apellidoM Parámetro `apellidoM`.
     */
    public void setApellidoM(String apellidoM) {
        this.apellidoM = apellidoM;
    }

    /**
     * Construye una nueva instancia de agregarDesarrollador.
     * @param nombre Nombre de la persona o registro.
     * @param apellidoM Parámetro `apellidoM`.
     * @param getApellidoP Parámetro `getApellidoP`.
     * @param numeroEmpleado Número de empleado institucional.
     * @param telefonoEmpleado Parámetro `telefonoEmpleado`.
     */
    public agregarDesarrollador(String nombre, String apellidoM, String getApellidoP, int numeroEmpleado, int telefonoEmpleado) {
        this.nombre = nombre;
        this.apellidoM = apellidoM;
        this.getApellidoP = getApellidoP;
        this.numeroEmpleado = numeroEmpleado;
        this.telefonoEmpleado = telefonoEmpleado;
    }
}
