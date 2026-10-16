/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.app;

import com.promptzal.ast.Agente;
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
        String texto = """
                @modelo "x"
                AGENTE a {
                    contexto = "c"
                    
                    variable w = "ok"
                    
                    RESUMIR w EN 100 palabras -> r
                }
                EJECUTAR a
                EXPORTAR r
                """;

        try {
            AnalizadorLexicoJFlex lexer = new AnalizadorLexicoJFlex(new StringReader(texto));
            List<Token> tokens = new ArrayList<>();
            Token t;
            do {
                t = lexer.siguienteToken();
                tokens.add(t);
            } while (!t.getTipo().equals("EOF"));

            Parser parser = new Parser(tokens);
            Programa programa = parser.programa();

            System.out.println("Errores lexicos: " + lexer.getErrores().size());
            System.out.println("Errores sintacticos: " + parser.getErrores().size());
            parser.getErrores().forEach(e ->
                    System.out.println("  " + e.getEncontrado() + " | esperado: " + e.getEsperado()
                            + " | " + e.getFila() + "," + e.getColumna()));

            System.out.println("Directivas: " + programa.getDirectivas().size());
            System.out.println("Agentes: " + programa.getAgentes().size());
            if (!programa.getAgentes().isEmpty()) {
                Agente ag = programa.getAgentes().get(0);
                System.out.println("Variables: " + ag.getVariables().size());
                System.out.println("Comandos: " + ag.getComandos().size());
            }
            System.out.println("Ejecuciones: " + programa.getEjecuciones().size());
            System.out.println("Exportaciones: " + programa.getExportaciones().size());
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
}
