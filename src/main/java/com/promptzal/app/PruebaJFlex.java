/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.app;

import com.promptzal.ast.Agente;
import com.promptzal.ast.Programa;
import com.promptzal.generadores.GeneradorPromptzal;
import com.promptzal.logica.AnalizadorLexicoJFlex;
import com.promptzal.logica.AnalizadorSemantico;
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
        String texto = """
            @modelo "claude-sonnet-4-6"
            @rol "analista de datos"
            @formato "markdown"
            // Agente que prepara el analisis de un archivo de ventas
            AGENTE analista {
                contexto = "Eres un analista de datos experto"
                variable ventas = CARGAR("ventas.csv")
                PREGUNTAR "Cuales son las 3 tendencias principales?" SOBRE ventas -> tendencias
                RESUMIR tendencias EN 100 palabras -> resumen
            }
            EJECUTAR analista
            EXPORTAR resumen
            """;

        try {
            //1. Lexico
            AnalizadorLexicoJFlex lexer = new AnalizadorLexicoJFlex(new StringReader(texto));
            List<Token> tokens = new ArrayList<>();
            Token t;
            do {
                t = lexer.siguienteToken();
                tokens.add(t);
            } while (!t.getTipo().equals("EOF"));

            //2. Sintactico
            Parser parser = new Parser(tokens);
            Programa programa = parser.programa();

            System.out.println("Errores lexicos: " + lexer.getErrores().size());
            System.out.println("Errores sintacticos: " + parser.getErrores().size());
            parser.getErrores().forEach(e ->
                    System.out.println("  " + e.getEncontrado() + " | esperado: " + e.getEsperado()
                            + " | " + e.getFila() + "," + e.getColumna()));

            //3. Semantico
            AnalizadorSemantico sem = new AnalizadorSemantico(programa);
            sem.analizar();
            System.out.println("Errores semanticos: " + sem.getErrores().size());
            sem.getErrores().forEach(e ->
                    System.out.println("  " + e.getDescripcion() + " | linea " + e.getFila()));
            System.out.println("Simbolos: " + sem.getTabla().getSimbolos().size());
            //4. Generacion del promptzal
            System.out.println();
            System.out.println(new GeneradorPromptzal().generar(programa));
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
}
