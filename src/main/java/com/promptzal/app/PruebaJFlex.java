/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.app;

import com.promptzal.logica.AnalizadorLexicoJFlex;
import com.promptzal.modelo.Token;
import java.io.StringReader;
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
                + "EXPORTAR resumen\n"
                + "12. 12.5 #\n";

        try {
            AnalizadorLexicoJFlex lexer = new AnalizadorLexicoJFlex(new StringReader(texto));
            Token t;
            do {
                t = lexer.siguienteToken();
                System.out.println(t.getTipo() + " | " + t.getLexema()
                        + " | " + t.getFila() + "," + t.getColumna());
            } while (!t.getTipo().equals("EOF"));

            System.out.println("Errores: " + lexer.getErrores().size());
            lexer.getErrores().forEach(e ->
                    System.out.println(e.getLexema() + " | " + e.getDescripcion()
                            + " | " + e.getFila() + "," + e.getColumna()));
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
}
