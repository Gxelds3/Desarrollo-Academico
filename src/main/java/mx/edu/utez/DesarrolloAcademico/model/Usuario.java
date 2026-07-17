package mx.edu.utez.DesarrolloAcademico.model;

public class Usuario {
    private int id;
    private String username;
    private String email;
    private String password;
    private String codigoRecuperacion;

    public Usuario() {}

    public Usuario(int id, String username, String email, String password, String codigoRecuperacion) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.codigoRecuperacion = codigoRecuperacion;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getCodigoRecuperacion() { return codigoRecuperacion; }
    public void setCodigoRecuperacion(String codigoRecuperacion) { this.codigoRecuperacion = codigoRecuperacion; }
}
