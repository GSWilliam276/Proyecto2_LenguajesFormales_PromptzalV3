/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.modelo;

/**
 *
 * @author eduar
 */
public class Simbolo {
    public enum Tipo { AGENTE, VARIABLE, RESULTADO }

    private final String nombre;
    private final Tipo tipo;
    private final String ambito;   //agente al que pertenece; "global" para los agentes
    private final int linea;       //linea donde se declaro

    public Simbolo(String nombre, Tipo tipo, String ambito, int linea) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.ambito = ambito;
        this.linea = linea;
    }

    public String getNombre() { return nombre; }
    public Tipo getTipo() { return tipo; }
    public String getAmbito() { return ambito; }
    public int getLinea() { return linea; }

    //Texto para el reporte de la tabla de simbolos
    public String getTipoTexto() {
        switch (tipo) {
            case AGENTE:    return "Agente";
            case VARIABLE:  return "Variable declarada";
            default:        return "Resultado de comando";
        }
    }
}
