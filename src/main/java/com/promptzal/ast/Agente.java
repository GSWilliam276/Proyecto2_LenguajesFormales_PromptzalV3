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
public class Agente {
    private final String nombre;
    private final int fila;
    private String contexto;                     //texto de: contexto = "..."
    private final List<Variable> variables = new ArrayList<>();
    private final List<Comando> comandos = new ArrayList<>();

    public Agente(String nombre, int fila) {
        this.nombre = nombre;
        this.fila = fila;
    }

    public void setContexto(String contexto) { this.contexto = contexto; }
    public void agregarVariable(Variable v) { variables.add(v); }
    public void agregarComando(Comando c) { comandos.add(c); }

    public String getNombre() { return nombre; }
    public int getFila() { return fila; }
    public String getContexto() { return contexto; }
    public List<Variable> getVariables() { return variables; }
    public List<Comando> getComandos() { return comandos; }
}
