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
public class Programa {
    private final List<Directiva> directivas = new ArrayList<>();
    private final List<Agente> agentes = new ArrayList<>();
    private final List<Referencia> ejecuciones = new ArrayList<>();
    private final List<Referencia> exportaciones = new ArrayList<>();

    public void agregarDirectiva(Directiva d) { directivas.add(d); }
    public void agregarAgente(Agente a) { agentes.add(a); }
    public void agregarEjecucion(Referencia r) { ejecuciones.add(r); }
    public void agregarExportacion(Referencia r) { exportaciones.add(r); }

    public List<Directiva> getDirectivas() { return directivas; }
    public List<Agente> getAgentes() { return agentes; }
    public List<Referencia> getEjecuciones() { return ejecuciones; }
    public List<Referencia> getExportaciones() { return exportaciones; }

    //Valor de una directiva (por ejemplo "@modelo"), o null si no fue declarada.
    //El generador del promptzal la usa para el encabezado.
    public String getValorDirectiva(String nombre) {
        for (Directiva d : directivas) {
            if (d.getNombre().equals(nombre)) {
                return d.getValor();
            }
        }
        return null;
    }
}
