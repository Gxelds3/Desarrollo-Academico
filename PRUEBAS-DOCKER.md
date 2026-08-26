# Pruebas unitarias con contenedores Docker (Testcontainers)

## Qué se hizo

Las pruebas de los DAO ya no se conectan a la base de datos real de Oracle Cloud.
En su lugar, **Testcontainers levanta un contenedor Docker con Oracle XE**, crea el
esquema desde cero, siembra datos mínimos, y ejecuta las pruebas contra esa base
desechable. Al terminar, el contenedor se descarta.

Ventajas:

- No se ensucia ni se arriesga la base de datos real del proyecto.
- Las pruebas son reproducibles: siempre parten de un estado limpio y conocido.
- Cualquier integrante del equipo puede correrlas sin tener el wallet de Oracle Cloud.

## Requisitos

1. **Docker Desktop** (Windows/Mac) o **Docker Engine** (Linux) instalado y **corriendo**.
   Verifícalo con: `docker ps`
2. Java 17 y Maven (ya los usa el proyecto).
3. Conexión a internet la **primera vez**, para descargar la imagen de Oracle
   (`gvenzl/oracle-xe:21-slim-faststart`, ~2 GB). Las siguientes ejecuciones la reutilizan.

## Cómo ejecutar

**Desde la terminal:**

```bash
mvn test
```

**Desde IntelliJ:**

Clic derecho sobre `src/test/java` → *Run 'All Tests'*

La primera ejecución tarda varios minutos (descarga + arranque de Oracle).
Las siguientes son mucho más rápidas.

## Archivos que componen las pruebas

| Archivo | Rol |
|---|---|
| `AbstractDaoContainerTest.java` | Clase base: levanta el contenedor, crea el esquema, siembra datos y redirige el pool de la app hacia Docker |
| `schema-test.sql` | DDL de las 7 tablas del sistema, reconstruido desde las consultas de los DAO |
| `UsuarioDaoTest.java` | 14 pruebas — CRUD de usuarios, login, códigos de recuperación |
| `UsuarioListaDaoTest.java` | 13 pruebas — CRUD de periodos de carga, listados y contadores |
| `AgregarDesarrollador_DaoTest.java` | 12 pruebas — CRUD de desarrolladores, validación de duplicados y contraseñas |
| `ConstanciaDaoTest.java` (model.dao) | 12 pruebas — CRUD de constancias con archivo BLOB |
| `ConstanciaDaoTest.java` (controller) | 7 pruebas — CRUD de constancias con ruta de archivo |
| `AgregarEvento_CoTest.java` | 9 pruebas — CRUD de eventos y su transacción con participantes |
| `DaoTest.java` | 10 pruebas — contrato de la interfaz genérica `Dao<T,K>` (sin Docker: usa un doble en memoria) |

**Total: 77 pruebas.**

## Cambio hecho al código de producción

Para que los DAO pudieran apuntar al contenedor, se agregó a
`utils/DatabaseConnection.java` un `DataSource` alterno:

```java
public static void setTestDataSource(javax.sql.DataSource ds)
```

Cuando vale `null` (siempre en producción), la clase se comporta **exactamente igual
que antes**: usa el pool de HikariCP contra Oracle Cloud. Solo las pruebas lo
sobrescriben. Es el único cambio hecho fuera de `src/test`.

## Advertencia importante sobre `schema-test.sql`

El archivo `schema-test.sql` es una **reconstrucción** del esquema, inferida leyendo
las sentencias SQL que ejecutan los DAO (`INSERT INTO usuario (...)`, `SELECT ... FROM
evento`, etc.). No es el DDL oficial exportado de la base de datos real.

Esto significa que **los tipos de dato, tamaños y restricciones pueden no coincidir
exactamente** con la base de producción. Si alguna prueba falla por una restricción
inesperada, lo correcto es exportar el DDL real desde Oracle
(en SQL Developer: clic derecho sobre el esquema → *Export DDL*) y reemplazar el
contenido de `schema-test.sql` con él.

## Nota sobre las versiones

Las pruebas usan Testcontainers `1.19.8` y la imagen `gvenzl/oracle-xe:21-slim-faststart`.
Si Maven no encuentra esa versión, revisa la última disponible en
https://mvnrepository.com/artifact/org.testcontainers/testcontainers
y actualiza la propiedad `<testcontainers.version>` del `pom.xml`.
