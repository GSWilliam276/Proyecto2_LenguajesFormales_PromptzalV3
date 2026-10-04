/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.generadores;

import com.promptzal.excepciones.ExcepcionGraphvizNoDisponible;
import com.promptzal.logica.DefinicionAFD;
import com.promptzal.modelo.Estado;
import com.promptzal.modelo.Transicion;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 *
 * @author eduar
 */
public class GeneradorAFD {
    private final DefinicionAFD afd = new DefinicionAFD();

    public String generarCodigoDOT() {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph AFD_PromptZal {\n");
        dot.append("    rankdir=LR;\n");
        dot.append("    node [fontname=\"Helvetica\", fontsize=10];\n");
        dot.append("    edge [fontname=\"Helvetica\", fontsize=9];\n\n");

        dot.append("    inicio [shape=point];\n");
        dot.append("    inicio -> q0;\n\n");

        //Los estados salen de la definicion, uno por uno
        for (Estado e : afd.getEstados()) {
            String texto = e.getEtiqueta().isEmpty() ? e.getId() : e.getId() + "\\n" + e.getEtiqueta();
            String forma;
            switch (e.getTipo()) {
                case ACEPTACION:
                    forma = "shape=doublecircle";
                    break;
                case ACEPTACION_SIN_TOKEN:
                    forma = "shape=doublecircle, style=filled, fillcolor=lightblue";
                    break;
                case ERROR:
                    forma = "shape=circle, style=filled, fillcolor=red";
                    break;
                default:
                    forma = "shape=circle";
            }
            dot.append("    " + e.getId() + " [" + forma + ", label=\"" + texto + "\"];\n");
        }
        dot.append("\n");

        //Las transiciones tambien salen de la definicion
        for (Transicion t : afd.getTransiciones()) {
            dot.append("    " + t.getOrigen() + " -> " + t.getDestino()
                    + " [label=\"" + escapar(t.getSimbolo()) + "\"];\n");
        }

        dot.append("}\n");
        return dot.toString();
    }

    //Escapa barras y comillas para que el simbolo sea valido dentro del DOT
    private String escapar(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public boolean generarImagen(String rutaDot, String rutaImagenSalida) throws ExcepcionGraphvizNoDisponible {
        File archivoDot = new File(rutaDot);
        File carpeta = archivoDot.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        try (FileWriter writer = new FileWriter(rutaDot)) {
            writer.write(generarCodigoDOT());
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", rutaDot, "-o", rutaImagenSalida);
            pb.redirectErrorStream(true);
            Process proceso = pb.start();
            int resultado = proceso.waitFor();
            return resultado == 0;
        } catch (IOException ex) {
            throw new ExcepcionGraphvizNoDisponible();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
