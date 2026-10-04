/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.generadores;

import com.promptzal.excepciones.ExcepcionGraphvizNoDisponible;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
/**
 *
 * @author eduar
 */
public class GeneradorAFD {
    public String generarCodigoDOT() {
    StringBuilder dot = new StringBuilder();
    dot.append("digraph AFD_PromptZal {\n");
    dot.append("    rankdir=LR;\n");
    dot.append("    node [fontname=\"Helvetica\", fontsize=10];\n");
    dot.append("    edge [fontname=\"Helvetica\", fontsize=9];\n\n");

    dot.append("    inicio [shape=point];\n");
    dot.append("    inicio -> q0;\n\n");
    dot.append("    q0 [shape=circle];\n\n");

    //Rama: simbolo suelto 
    dot.append("    q1 [shape=doublecircle, label=\"q1\\nSIMBOLO\"];\n");
    dot.append("    q0 -> q1 [label=\"=\"];\n");
    dot.append("    q0 -> q1 [label=\"+\"];\n");
    dot.append("    q0 -> q1 [label=\"{\"];\n");
    dot.append("    q0 -> q1 [label=\"}\"];\n");
    dot.append("    q0 -> q1 [label=\"(\"];\n");
    dot.append("    q0 -> q1 [label=\")\"];\n");
    dot.append("    q0 -> q1 [label=\",\"];\n\n");

    //Rama: conector flecha 
    dot.append("    q2 [shape=circle, label=\"q2\\ndecidiendo\"];\n");
    dot.append("    q3 [shape=doublecircle, label=\"q3\\nCONECTOR\"];\n");
    dot.append("    q4 [shape=circle, style=filled, fillcolor=red, label=\"q4\\nERROR\"];\n");
    dot.append("    q0 -> q2 [label=\"-\"];\n");
    dot.append("    q2 -> q3 [label=\">\"];\n");
    dot.append("    q2 -> q4 [label=\"otro\"];\n\n");

    //Rama: numero (entero / decimal) 
    dot.append("    q5 [shape=doublecircle, label=\"q5\\nENTERO\"];\n");
    dot.append("    q6 [shape=circle, label=\"q6\\ntransito\"];\n");
    dot.append("    q7 [shape=doublecircle, label=\"q7\\nDECIMAL\"];\n");
    dot.append("    q21 [shape=circle, style=filled, fillcolor=red, label=\"q21\\nERROR\"];\n");
    dot.append("    q0 -> q5 [label=\"digito\"];\n");
    dot.append("    q5 -> q5 [label=\"digito\"];\n");
    dot.append("    q5 -> q6 [label=\".\"];\n");
    dot.append("    q6 -> q7 [label=\"digito\"];\n");
    dot.append("    q6 -> q21 [label=\"otro (no digito)\"];\n");
    dot.append("    q7 -> q7 [label=\"digito\"];\n\n");

    //Rama: cadena 
    dot.append("    q8 [shape=circle, label=\"q8\\nacumulando\"];\n");
    dot.append("    q9 [shape=doublecircle, label=\"q9\\nCADENA\"];\n");
    dot.append("    q10 [shape=circle, style=filled, fillcolor=red, label=\"q10\\nERROR\"];\n");
    dot.append("    q0 -> q8 [label=\"\\\"\"];\n");
    dot.append("    q8 -> q8 [label=\"otro\"];\n");
    dot.append("    q8 -> q9 [label=\"\\\"\"];\n");
    dot.append("    q8 -> q10 [label=\"salto de linea\"];\n\n");

    //Rama: directiva 
    dot.append("    q11 [shape=circle, label=\"q11\\nacumulando\"];\n");
    dot.append("    q12 [shape=doublecircle, label=\"q12\\nDIRECTIVA\"];\n");
    dot.append("    q13 [shape=circle, style=filled, fillcolor=red, label=\"q13\\nERROR\"];\n");
    dot.append("    q0 -> q11 [label=\"@\"];\n");
    dot.append("    q11 -> q11 [label=\"letra/digito\"];\n");
    dot.append("    q11 -> q12 [label=\"valida\"];\n");
    dot.append("    q11 -> q13 [label=\"no valida\"];\n\n");

    //Rama: comentario (linea y bloque comparten el punto de decision) 
    dot.append("    q14 [shape=circle, label=\"q14\\ndecidiendo\"];\n");
    dot.append("    q15 [shape=circle, label=\"q15\\nmodo linea\"];\n");
    dot.append("    q16 [shape=doublecircle, style=filled, fillcolor=lightblue, label=\"q16\\nsin token\"];\n");
    dot.append("    q17 [shape=circle, label=\"q17\\nmodo bloque\"];\n");
    dot.append("    q18 [shape=doublecircle, style=filled, fillcolor=lightblue, label=\"q18\\nsin token\"];\n");
    dot.append("    q19 [shape=circle, style=filled, fillcolor=red, label=\"q19\\nERROR\"];\n");
    dot.append("    q22 [shape=circle, label=\"q22\\nposible cierre\"];\n");
    dot.append("    q23 [shape=circle, style=filled, fillcolor=red, label=\"q23\\nERROR\"];\n");
    dot.append("    q0 -> q14 [label=\"/\"];\n");
    dot.append("    q14 -> q15 [label=\"/\"];\n");
    dot.append("    q14 -> q17 [label=\"*\"];\n");
    dot.append("    q14 -> q23 [label=\"otro\"];\n");
    dot.append("    q15 -> q15 [label=\"otro\"];\n");
    dot.append("    q15 -> q16 [label=\"salto de linea\"];\n");
    dot.append("    q15 -> q16 [label=\"fin archivo\"];\n");
    dot.append("    q17 -> q17 [label=\"otro (no *)\"];\n");
    dot.append("    q17 -> q22 [label=\"*\"];\n");
    dot.append("    q17 -> q19 [label=\"fin archivo\"];\n");
    dot.append("    q22 -> q18 [label=\"/\"];\n");
    dot.append("    q22 -> q22 [label=\"*\"];\n");
    dot.append("    q22 -> q17 [label=\"otro\"];\n");
    dot.append("    q22 -> q19 [label=\"fin archivo\"];\n\n");

    //Rama: identificador / palabra reservada / comando / conector 
    dot.append("    q20 [shape=doublecircle, label=\"q20\\nID/RESERVADA\"];\n");
    dot.append("    q0 -> q20 [label=\"letra\"];\n");
    dot.append("    q20 -> q20 [label=\"letra/digito\"];\n\n");

    dot.append("}\n");
    return dot.toString();
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
