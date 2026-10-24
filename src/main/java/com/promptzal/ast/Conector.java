/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.ast;

/**
 *
 * @author eduar
 */
public class Conector {
    private final String palabra;      //SOBRE, DESDE, EN o COMO
    private final Expresion argumento;

    public Conector(String palabra, Expresion argumento) {
        this.palabra = palabra;
        this.argumento = argumento;
    }

    public String getPalabra() { return palabra; }
    public Expresion getArgumento() { return argumento; }
}
