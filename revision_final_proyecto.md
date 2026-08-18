# Análisis Final del Proyecto: Desarrollo Académico

A continuación se detalla una revisión del proyecto actual. Dado que mencionaste que el proyecto ya está terminado, este análisis destaca los aspectos que podrían considerarse como **puntos de mejora**, **deuda técnica** o **vulnerabilidades críticas** (ideal para una sección de "Trabajo Futuro" o correcciones de último minuto).

## 1. Vulnerabilidades de Seguridad Críticas 🚨

### Filtro de Seguridad Limitado a Vistas (JSP)
El archivo `SecurityFilter.java` intercepta únicamente las peticiones a `*.jsp`. Esto significa que **ningún Servlet está protegido**. 
* **Problema:** Un usuario externo sin iniciar sesión puede usar herramientas como Postman para enviar una solicitud `POST` a URLs como `/EliminarDocente` o `/EditarDesarrollador` y el sistema ejecutará la acción porque los Servlets no validan que exista un `HttpSession` activo ni el rol.
* **Solución:** Extender el `urlPatterns` del filtro a `urlPatterns = {"*.jsp", "/Agregar*", "/Editar*", "/Eliminar*", "/Listar*"}` o añadir validación de sesión (`request.getSession(false)`) al inicio de los métodos `doPost` en los Servlets críticos.

### Contraseñas en Texto Plano
Las contraseñas de los usuarios no están cifradas.
* **Problema:** En `AgregarDesarrolladorServlet.java` y los DAOs existe el comentario `// TODO: hashear con BCrypt antes de guardar`. En `LoginServlet.java` la contraseña enviada por el usuario se compara directamente con el texto en la base de datos (`AND contrasena = ?`). Esto es una vulnerabilidad grave en caso de fuga de datos.
* **Solución:** Implementar la librería `jBCrypt` u otra similar para hashear las contraseñas al guardar (`BCrypt.hashpw`) y verificarlas al hacer login (`BCrypt.checkpw`).

---

## 2. Buenas Prácticas y Arquitectura 🏗️

### Manejo de Errores y Logs
A lo largo de los controladores y DAOs se utiliza `e.printStackTrace()` y `System.out.println()`.
* **Problema:** Esto ensucia la consola del servidor de aplicaciones (Tomcat/Glassfish) y no es óptimo para mantenimiento en producción. Además, las excepciones no manejadas podrían llegar a la interfaz del usuario.
* **Solución:** Implementar un sistema de logs como `SLF4J` o configurar una vista de error genérica (`error.jsp`) en el archivo `web.xml` utilizando las etiquetas `<error-page>`.

### Prevención de Caché (Navegador)
El sistema controla los roles (`_co`, `_de`, `_do`), pero no invalida la caché de las páginas.
* **Problema:** Si un usuario cierra sesión y hace clic en la flecha de "Atrás" en su navegador web, todavía podrá ver las páginas cargadas en caché (aunque no pueda ejecutar acciones nuevas).
* **Solución:** Agregar en los controladores o en el `SecurityFilter` las cabeceras HTTP:
  ```java
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setHeader("Pragma", "no-cache");
  response.setDateHeader("Expires", 0);
  ```

---

## 3. Funcionalidades y Experiencia de Usuario (UI/UX) 🎨

### Páginas Restantes Mencionadas
En tu historial de tareas quedó una anotada que no fue abordada del todo (dependiendo si fue decisión de diseño cancelarla):
* **Recuperación de Contraseña:** Los archivos `nueva-contra.jsp` y `recuperar-contra.jsp` requerían ajuste para coincidir exactamente con el diseño del Login. Adicionalmente, el flujo de envío de correos (JavaMail API está en el `pom.xml`) se debe revisar para asegurar que envía los tokens de seguridad y no contraseñas directamente.

### Validaciones SQL Adicionales
* **Problema con Constraints:** Si un Coordinador intenta eliminar una división o un usuario que tiene registros dependientes (ej. Eventos vinculados a ese Coordinador, o Constancias subidas por ese Docente), la base de datos de Oracle arrojará un error de Integridad Referencial (`SQLIntegrityConstraintViolationException`). 
* **Solución actual:** El sistema devuelve que "no se pudo eliminar", pero el mensaje al usuario podría no ser lo suficientemente específico para explicarle que primero debe reasignar o eliminar los registros asociados.

## Conclusión

El proyecto está funcional: se guardan los archivos correctamente como BLOB (evitando problemas de sistema de archivos), el CRUD de todas las áreas funciona con promesas AJAX y la interfaz gráfica es consistente mediante SweetAlert2 y Bootstrap. Si decides darlo por finalizado aquí, los aspectos de **seguridad (Servlets desprotegidos y contraseñas planas)** son los más importantes a considerar si alguna vez el sistema se pone en un servidor en la nube de forma pública.
