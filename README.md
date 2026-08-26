# Documentación del repositorio:
# Desarrollo academico
Desarrollo academico web para la gestion de eventos y portafolio docente, dirigido para docentes, coordinadores, desarrollo academico el personal administrativo del programa, desarrollado como proyecto integrador con **Java (Jakarta Servlets + JSP)** y base de datos **Oracle**.

## Equipo de desarrollo

| Nombre de usuario (Git)   | Matrícula   | Rama principal de trabajo  |
|---------------------------|-------------|----------------------------|
| `IXAYA`                   | 20253ds067  | `Master`                   |
| `EliassHurtado`           | 20253ds078  | `Master`                   |
| `Gxelds3`                 | 20253ds080  | `Master`                   |
| `GerardoOrgg`             | 20253ds064  | `Master`                   |
| `Charlie099402993`        | 20253ds088  | `Master`                   |

Desarrollo Académico es una plataforma web diseñada para gestionar eventos académicos y centralizar la carga de evidencias correspondientes (constancias, certificaciones y reconocimientos). El sistema opera bajo una estructura de permisos segmentada en tres roles principales (Desarrollador, Coordinador y Docente) e integra módulos transversales para la consulta de actividades.

El sistema permite:

Panel de administración global (Rol Desarrollador): Acceso total a la plataforma con vista general (contadores de eventos y docentes), creación de otros desarrolladores, visualización de todos los usuarios del sistema, gestión de eventos y convocatoria a cualquier docente, además de la facultad de subir evidencias para cualquier usuario sin restricciones.

Gestión de periodos de carga (Rol Desarrollador): Administración de las fechas límite globales en las que los usuarios tienen permitido subir sus evidencias al sistema.

Gestión académica por división (Rol Coordinador): Módulo para que los coordinadores administren únicamente a los docentes, creen eventos y convoquen a participantes pertenecientes a su misma División Académica.

Gestión y carga de evidencias segmentada: Carga de evidencias habilitada para el Desarrollador (global), Coordinador (propia y de docentes de su división) y Docente (únicamente su propia evidencia dentro de los periodos de carga vigentes).

Módulo "Mis Eventos": Vista transversal para que todos los usuarios convocados consulten las actividades académicas en las que participan activamente y suban sus constancias o certificaciones.

Historial de Eventos: Módulo transversal de consulta general que almacena y categoriza el registro de todos los eventos finalizados, en curso y próximos.

Panel de participación docente (Rol Docente): Vista orientada al usuario participante con un contador de eventos asignados y acceso directo a sus actividades convocadas.

- ## Estructura del código

```
Desarrollo_Academico/
├── pom.xml                        # Configuración Maven (Java 21, dependencias, empaquetado WAR)
├── src/
│   ├── main/
│   │   ├── java/com/example/DesarrolloAcademico/
│   │   │   ├── controller/        # Servlets: reciben peticiones HTTP y orquestan la lógica
│   │   │   ├── filter/            # AdminFilter: protege las rutas de administración
│   │   │   ├── model/             # Entidades (POJOs) del dominio
│   │   │   │   └── dao/           # Acceso a datos (JDBC) por entidad
│   │   │   └── utils/             # Utilidades: conexión a BD, hashing, envío de correos
│   │   ├── resources/
│   │   │   └── credentials.properties  # Credenciales de BD y SMTP (variables de entorno/config)
│   │   └── webapp/
│   │       ├── *.jsp              # Vistas de la aplicación
│   │       ├── Error/           # Configuración de páginas de error (404/500)
│   │       ├── assets/            # CSS, JS (jQuery, slick.js) e íconos (Bootstrap Icons)
│   └── test/java/.../model/dao/   # Pruebas unitarias (JUnit)
```

### Capa `model/`
Contiene las clases de dominio (POJOs) que representan las entidades del negocio: `usuario`, `evento`, `ParticipanteEvento`, `PerodoDeCarga`, `TokenRecuperacion`, `constancia` y `divison`,`Rol`.
  Cada una expone únicamente atributos, constructores y getters/setters.

### Capa `model/dao/`
Un DAO (*Data Access Object*) por entidad (`UserDao`, `ProductoDao`, `OfertaDao`, `TransaccionDao`, `CategoriaDao`, `CalificacionDao`, `ReporteDao`), todos implementando una interfaz común `Dao`.
  Encapsulan las consultas SQL contra Oracle usando JDBC, obteniendo la conexión a través de `SQLConnector`.

### Capa `controller/`
Cada funcionalidad del sistema tiene su propio **Servlet** (anotado con `@WebServlet`), que recibe la petición, invoca al DAO correspondiente y reenvía (`forward`) a la vista JSP adecuada.

### Capa `filter/`
`AdminFilter` intercepta las rutas y vistas de administración y solo permite el paso si en la sesión existe un `User` con `rol = "ADMIN"`; en caso contrario redirige al login.

### Capa `utils/`
- `SQLConnector`: gestiona el pool de conexiones a Oracle mediante **HikariCP**.
- `HashUtil`: hashing de contraseñas.
- `EmailSender`: envío de correos (verificación de cuenta, restablecimiento de contraseña) usando Jakarta Mail.

### `webapp/`
Vistas JSP (una por pantalla), fragmentos reutilizables en `layout/` (header/footer, variantes según usuario normal o administrador) y los recursos estáticos en `assets/` (CSS propios, Bootstrap Icons, jQuery y el plugin `slick.js` para carruseles).
