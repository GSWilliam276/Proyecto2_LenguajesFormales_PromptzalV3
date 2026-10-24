/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.ast;

/**
 *
 * @author eduar
 */
public class Termino {
    public enum Tipo { CADENA, ID, NUMERO, CARGAR }

    private final Tipo tipo;
    private final String valor;    //texto de la cadena, nombre del ID, numero o archivo de CARGAR
    private final String unidad;   //solo NUMERO: "palabras" en "100 palabras"; null si no hay
    private final int fila;
    private final int columna;

    public Termino(Tipo tipo, String valor, String unidad, int fila, int columna) {
        this.tipo = tipo;
        this.valor = valor;
        this.unidad = unidad;
        this.fila = fila;
        this.columna = columna;
    }

    public Tipo getTipo() { return tipo; }
    public String getValor() { return valor; }
    public String getUnidad() { return unidad; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }

    //Representacion legible, para el reporte del AST y el promptzal
    public String aTexto() {
        switch (tipo) {
            case CADENA: return "\"" + valor + "\"";
            case CARGAR: return "CARGAR(\"" + valor + "\")";
            case NUMERO: return unidad == null ? valor : valor + " " + unidad;
            default:     return valor;
        }
    }
}
