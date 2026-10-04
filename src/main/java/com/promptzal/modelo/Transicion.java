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
    private final String simbolo;   //lo que compara el motor: "digito", ".", "otro"...
    private final String etiqueta;  //lo que se dibuja en el DOT

    public Transicion(String origen, String destino, String simbolo, String etiqueta) {
        this.origen = origen;
        this.destino = destino;
        this.simbolo = simbolo;
        this.etiqueta = etiqueta;
    }

    public Transicion(String origen, String destino, String simbolo) {
        this(origen, destino, simbolo, simbolo); //si no se indica, se dibuja el mismo simbolo
    }

    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public String getSimbolo() { return simbolo; }
    public String getEtiqueta() { return etiqueta; }
}
