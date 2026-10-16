/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.app;

import com.promptzal.ast.Agente;
import com.promptzal.ast.Programa;
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
                AGENTE a {
                    contexto = "c"
                    variable v = "x"
                    PREGUNTAR "p" SOBRE zzz -> r1
                    GENERAR codigo DESDE v -> r2
                    RESUMIR r1 EN 100 palabras -> r3
                }
                AGENTE a {
                    contexto = "c"
                    PREGUNTAR "p" -> r
                }
                EJECUTAR a
                EJECUTAR b
                EXPORTAR r3, fantasma
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
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
}
