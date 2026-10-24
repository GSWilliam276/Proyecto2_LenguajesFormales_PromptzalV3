/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.modelo;

/**
 *
 * @author eduar
 */
public class ErrorSintactico {
    private final String encontrado;
    private final String esperado;
    private final int fila;
    private final int columna;

    public ErrorSintactico(String encontrado, String esperado, int fila, int columna) {
        this.encontrado = encontrado;
        this.esperado = esperado;
        this.fila = fila;
        this.columna = columna;
    }

    public String getEncontrado() { return encontrado; }
    public String getEsperado() { return esperado; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
}
