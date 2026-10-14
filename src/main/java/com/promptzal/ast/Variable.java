/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.ast;

/**
 *
 * @author eduar
 */
public class Variable {
    private final String nombre;
    private final Expresion valor;
    private final int fila;      //linea donde se declaro
    private final int columna;

    public Variable(String nombre, Expresion valor, int fila, int columna) {
        this.nombre = nombre;
        this.valor = valor;
        this.fila = fila;
        this.columna = columna;
    }

    public String getNombre() { return nombre; }
    public Expresion getValor() { return valor; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
}
