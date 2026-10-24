/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.modelo;

/**
 *
 * @author eduar
 */
public class ErrorSemantico {
    private final String descripcion;
    private final int fila;

    public ErrorSemantico(String descripcion, int fila) {
        this.descripcion = descripcion;
        this.fila = fila;
    }

    public String getDescripcion() { return descripcion; }
    public int getFila() { return fila; }
}
