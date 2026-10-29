# PromptZal – Compilador

Compilador para el lenguaje **PromptZal**, desarrollado como Proyecto 2 del Laboratorio de Lenguajes Formales y de Programación (CUNOC, USAC). Convierte un programa `.pz` en el **promptzal**: un prompt en texto, listo para pegar en una herramienta de inteligencia artificial.

## Fases

1. **Léxico:** generado con JFlex (`src/main/jflex/promptzal.flex`).
2. **Sintáctico:** parser descendente recursivo escrito a mano, con recuperación en modo pánico.
3. **Semántico:** AST, tabla de símbolos y validaciones.
4. **Generación** del promptzal a partir del AST.

Los errores léxicos, sintácticos y semánticos se reportan juntos, con su fila y columna.

## Tecnologías

Java 17, Maven, JFlex, Java Swing y Graphviz (opcional, para las imágenes del AFD y del AST).

## Cómo ejecutar

**NetBeans:** *Clean and Build* y ejecutar.

**Consola:**
```
mvn clean package
java -jar target/Promptzal-1.0-SNAPSHOT.jar
```

Requiere JDK 17 o superior. En la primera compilación Maven descarga el plugin de JFlex, por lo que necesita conexión a internet.
