/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.app;

import com.promptzal.ast.Programa;
import com.promptzal.logica.AnalizadorLexicoJFlex;
import com.promptzal.logica.Parser;
import com.promptzal.modelo.Token;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author eduar
 */
public class PruebaJFlex {
    public static void main(String[] args) {
        String texto = "@modelo \"claude-sonnet-4-6\"\n"
                + "@rol \"analista de datos\"\n"
                + "// comentario de linea\n"
                + "AGENTE analista {\n"
                + "    contexto = \"Eres un analista\"\n"
                + "    variable ventas = CARGAR(\"ventas.csv\")\n"
                + "    PREGUNTAR \"Cuales son las 3 tendencias?\" SOBRE ventas -> tendencias\n"
                + "    RESUMIR tendencias EN 100 palabras -> resumen\n"
                + "}\n"
                + "EJECUTAR analista\n"
                + "EXPORTAR resumen\n";

        try {
            AnalizadorLexicoJFlex lexer = new AnalizadorLexicoJFlex(new StringReader(texto));
            Token t;
            List<Token> tokens = new ArrayList<>();
            do {
                t = lexer.siguienteToken();
                tokens.add(t);
            } while (!t.getTipo().equals("EOF"));
            Parser parser = new Parser(tokens);
            Programa programa = parser.programa();
            System.out.println("Directivas: " + programa.getDirectivas().size());
            System.out.println("Agentes: " + programa.getAgentes().size());
            System.out.println("Ejecuciones: " + programa.getEjecuciones().size());
            System.out.println("Exportaciones: " + programa.getExportaciones().size());
            System.out.println("Errores sintacticos: " + parser.getErrores().size());

            System.out.println("Errores: " + lexer.getErrores().size());
            lexer.getErrores().forEach(e ->
                    System.out.println(e.getLexema() + " | " + e.getDescripcion()
                            + " | " + e.getFila() + "," + e.getColumna()));
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
}
