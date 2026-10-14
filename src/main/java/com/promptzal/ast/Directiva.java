/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.ast;

/**
 *
 * @author eduar
 */
public class Directiva {
    private final String nombre;   //modelo, rol o formato (sin la arroba)
    private final String valor;    //texto de la cadena
    private final int fila;

    public Directiva(String nombre, String valor, int fila) {
        this.nombre = nombre;
        this.valor = valor;
        this.fila = fila;
    }

    public String getNombre() { return nombre; }
    public String getValor() { return valor; }
    public int getFila() { return fila; }
}
