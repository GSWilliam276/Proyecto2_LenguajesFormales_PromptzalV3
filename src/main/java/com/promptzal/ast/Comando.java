/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.ast;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author eduar
 */
public class Comando {
    private final String tipo;               //PREGUNTAR, GENERAR, RESUMIR...
    private final Expresion principal;       //argumento principal del comando
    private final List<Conector> conectores = new ArrayList<>();
    private final String destino;            //variable despues de ->
    private final int fila;                  //linea del comando
    private final int filaDestino;           //linea donde se declara el destino

    public Comando(String tipo, Expresion principal, String destino, int fila, int filaDestino) {
        this.tipo = tipo;
        this.principal = principal;
        this.destino = destino;
        this.fila = fila;
        this.filaDestino = filaDestino;
    }

    public void agregarConector(Conector c) {
        conectores.add(c);
    }

    public String getTipo() { return tipo; }
    public Expresion getPrincipal() { return principal; }
    public List<Conector> getConectores() { return conectores; }
    public String getDestino() { return destino; }
    public int getFila() { return fila; }
    public int getFilaDestino() { return filaDestino; }
}

