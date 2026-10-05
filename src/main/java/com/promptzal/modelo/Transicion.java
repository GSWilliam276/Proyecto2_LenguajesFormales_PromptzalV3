/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.modelo;

/**
 *
 * @author eduar
 */
public class Transicion {
    private final String origen, destino;
    private final String simbolo;    //Lo que compara el motor: "digito", ".", "otro"...
    private final String etiqueta;   //Lo que se dibuja en el DOT
    private final boolean guardar;   //false: se consume el caracter pero no entra al lexema

    //Constructor principal: el unico que asigna los campos
    public Transicion(String origen, String destino, String simbolo, String etiqueta, boolean guardar) {
        this.origen = origen;
        this.destino = destino;
        this.simbolo = simbolo;
        this.etiqueta = etiqueta;
        this.guardar = guardar;
    }

    //Con etiqueta propia; el caracter si se guarda en el lexema
    public Transicion(String origen, String destino, String simbolo, String etiqueta) {
        this(origen, destino, simbolo, etiqueta, true);
    }

    //Sin etiqueta: se dibuja el mismo simbolo
    public Transicion(String origen, String destino, String simbolo) {
        this(origen, destino, simbolo, simbolo, true);
    }

    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public String getSimbolo() { return simbolo; }
    public String getEtiqueta() { return etiqueta; }
    public boolean isGuardar() { return guardar; }
}
