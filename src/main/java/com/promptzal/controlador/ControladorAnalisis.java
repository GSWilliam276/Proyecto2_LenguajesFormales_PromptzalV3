/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.controlador;

import com.promptzal.archivos.GestorArchivos;
import com.promptzal.ast.Programa;
import com.promptzal.excepciones.ExcepcionArchivoNoEncontrado;
import com.promptzal.excepciones.ExcepcionGraphvizNoDisponible;
import com.promptzal.generadores.GeneradorAFD;
import com.promptzal.generadores.GeneradorPromptzal;
import com.promptzal.generadores.GeneradorReporteCompilador;
import com.promptzal.logica.AnalizadorLexicoJFlex;
import com.promptzal.logica.AnalizadorSemantico;
import com.promptzal.logica.Parser;
import com.promptzal.logica.TablaSimbolos;
import com.promptzal.modelo.ErrorLexico;
import com.promptzal.modelo.ErrorSemantico;
import com.promptzal.modelo.ErrorSintactico;
import com.promptzal.modelo.Token;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author eduar
 */
public class ControladorAnalisis {
    //Resultados de la ultima compilacion
    private List<Token> tokens = new ArrayList<>();
    private List<ErrorLexico> erroresLexicos = new ArrayList<>();
    private List<ErrorSintactico> erroresSintacticos = new ArrayList<>();
    private List<ErrorSemantico> erroresSemanticos = new ArrayList<>();
    private TablaSimbolos tabla = new TablaSimbolos();
    private Programa programa = new Programa();
    private String promptzal = "";

    private final GeneradorPromptzal generadorPromptzal = new GeneradorPromptzal();
    private final GeneradorReporteCompilador reportes = new GeneradorReporteCompilador();
    private final GeneradorAFD generadorAFD = new GeneradorAFD();
    private final GestorArchivos gestorArchivos = new GestorArchivos();

    //Ejecuta todas las fases: lexico, sintactico, semantico y generacion
    public void compilar(String contenido) {
        tokens = new ArrayList<>();
        erroresLexicos = new ArrayList<>();
        erroresSintacticos = new ArrayList<>();
        erroresSemanticos = new ArrayList<>();
        tabla = new TablaSimbolos();
        programa = new Programa();
        promptzal = "";

        //1. Lexico (JFlex): termina siempre con el token EOF
        try {
            AnalizadorLexicoJFlex lexer = new AnalizadorLexicoJFlex(new StringReader(contenido));
            Token t;
            do {
                t = lexer.siguienteToken();
                tokens.add(t);
            } while (!t.getTipo().equals("EOF"));
            erroresLexicos = lexer.getErrores();
        } catch (IOException ex) {
            erroresLexicos.add(new ErrorLexico("", "Error al leer el contenido", 1, 1));
            return;
        }

        //2. Sintactico: recibe los tokens validos (el lexico ya descarto los caracteres invalidos)
        Parser parser = new Parser(tokens);
        programa = parser.programa();
        erroresSintacticos = parser.getErrores();

        //3. Semantico
        AnalizadorSemantico semantico = new AnalizadorSemantico(programa);
        semantico.analizar();
        erroresSemanticos = semantico.getErrores();
        tabla = semantico.getTabla();

        //4. Generacion: siempre se genera con lo que el parser pudo armar
        promptzal = generadorPromptzal.generar(programa);
    }

    //--- Resultados para la vista ---

    //Los tokens sin el EOF, que no tiene sentido mostrar en la tabla
    public List<Token> getTokens() {
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getTipo().equals("EOF")) {
            return tokens.subList(0, tokens.size() - 1);
        }
        return tokens;
    }

    public String getPromptzal() { return promptzal; }
    public Programa getPrograma() { return programa; }
    public TablaSimbolos getTabla() { return tabla; }
    public List<ErrorLexico> getErroresLexicos() { return erroresLexicos; }
    public List<ErrorSintactico> getErroresSintacticos() { return erroresSintacticos; }
    public List<ErrorSemantico> getErroresSemanticos() { return erroresSemanticos; }

    //Los tres tipos de error en filas iguales: {tipo, descripcion, fila, columna}
    public List<String[]> getTodosLosErrores() {
        List<String[]> filas = new ArrayList<>();
        for (ErrorLexico e : erroresLexicos) {
            filas.add(new String[]{"Lexico",
                e.getDescripcion() + " ('" + e.getLexema() + "')",
                String.valueOf(e.getFila()), String.valueOf(e.getColumna())});
        }
        for (ErrorSintactico e : erroresSintacticos) {
            filas.add(new String[]{"Sintactico",
                "Token encontrado: " + e.getEncontrado() + ". Se esperaba: " + e.getEsperado(),
                String.valueOf(e.getFila()), String.valueOf(e.getColumna())});
        }
        for (ErrorSemantico e : erroresSemanticos) {
            filas.add(new String[]{"Semantico", e.getDescripcion(),
                String.valueOf(e.getFila()), "-"});
        }
        return filas;
    }

    //--- Reportes y archivos ---

    //Los tres reportes salen de los mismos datos de la ultima compilacion
    public void generarReportes(String carpetaSalida) {
        //La imagen va primero porque el HTML del AST la incluye
        try {
            reportes.generarImagenAST(programa, carpetaSalida + "/ast.dot", carpetaSalida + "/ast.png");
        } catch (ExcepcionGraphvizNoDisponible ex) {
            //Sin Graphviz el reporte del AST sale solo con el texto
        }
        reportes.generarReporteErrores(erroresLexicos, erroresSintacticos, erroresSemanticos,
                carpetaSalida + "/reporte_errores.html");
        reportes.generarReporteSimbolos(tabla, carpetaSalida + "/reporte_simbolos.html");
        reportes.generarReporteAST(programa, carpetaSalida + "/reporte_ast.html");
        reportes.generarReporteTokens(getTokens(), carpetaSalida + "/reporte_tokens.html");
        reportes.generarReporteEstadisticas(getTokens(), erroresLexicos, erroresSintacticos,
                erroresSemanticos, programa, tabla, carpetaSalida + "/reporte_estadisticas.html");
    }

    public void generarAFD(String carpetaSalida) throws ExcepcionGraphvizNoDisponible {
        generadorAFD.generarImagen(carpetaSalida + "/afd.dot", carpetaSalida + "/afd.png");
    }

    public String abrirArchivo(String ruta) throws ExcepcionArchivoNoEncontrado {
        return gestorArchivos.leerArchivo(ruta);
    }

    public boolean guardarArchivo(String ruta, String contenido) {
        return gestorArchivos.guardarArchivo(ruta, contenido);
    }
}
