/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.generadores;

import com.promptzal.ast.Agente;
import com.promptzal.ast.Comando;
import com.promptzal.ast.Conector;
import com.promptzal.ast.Expresion;
import com.promptzal.ast.Programa;
import com.promptzal.ast.Referencia;
import com.promptzal.ast.Termino;
import com.promptzal.ast.Variable;
import java.util.List;
/**
 *
 * @author eduar
 */
public class GeneradorPromptzal {
    public String generar(Programa programa) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Prompt generado con PromptZal\n");
        encabezado(sb, programa);

        //Solo los agentes que aparecen en EJECUTAR, en ese orden.
        //Si la referencia no existe se salta: el error ya lo reporto la semantica.
        for (Referencia ejecucion : programa.getEjecuciones()) {
            Agente agente = buscarAgente(programa, ejecucion.getNombre());
            if (agente != null) {
                seccionAgente(sb, agente);
            }
        }

        sb.append("\n## Resultado solicitado\n");
        for (Referencia r : programa.getExportaciones()) {
            sb.append("- ").append(r.getNombre()).append("\n");
        }
        return sb.toString();
    }

    //Solo se escribe lo que fue declarado con @modelo, @rol y @formato
    private void encabezado(StringBuilder sb, Programa p) {
        String modelo = p.getValorDirectiva("@modelo");
        String rol = p.getValorDirectiva("@rol");
        String formato = p.getValorDirectiva("@formato");
        if (modelo != null) {
            sb.append("Modelo destino: ").append(modelo).append("\n");
        }
        if (rol != null) {
            sb.append("Rol general: ").append(rol).append("\n");
        }
        if (formato != null) {
            sb.append("Formato de respuesta: ").append(formato).append("\n");
        }
    }

    private Agente buscarAgente(Programa p, String nombre) {
        for (Agente a : p.getAgentes()) {
            if (a.getNombre().equals(nombre)) {
                return a;
            }
        }
        return null;
    }

    private void seccionAgente(StringBuilder sb, Agente a) {
        sb.append("\n## Agente: ").append(a.getNombre()).append("\n");
        if (a.getContexto() != null) {
            sb.append("Contexto: ").append(a.getContexto()).append("\n");
        }

        if (!a.getVariables().isEmpty()) {
            sb.append("Variables disponibles:\n");
            for (Variable v : a.getVariables()) {
                sb.append("- ").append(v.getNombre()).append(": ")
                  .append(origen(v.getValor())).append("\n");
            }
        }

        sb.append("Instrucciones:\n");
        int numero = 1;
        for (Comando c : a.getComandos()) {
            sb.append(numero++).append(". ").append(instruccion(c)).append("\n");
        }
    }

    //Origen del valor de una variable: un literal o el archivo indicado en CARGAR
    private String origen(Expresion e) {
        List<Termino> ts = e.getTerminos();
        if (ts.size() == 1 && ts.get(0).getTipo() == Termino.Tipo.CARGAR) {
            return "contenido del archivo \"" + ts.get(0).getValor() + "\" (adjuntar el archivo)";
        }
        if (ts.size() == 1 && ts.get(0).getTipo() == Termino.Tipo.CADENA) {
            return "\"" + ts.get(0).getValor() + "\"";
        }
        return texto(e);
    }

    //Traduce un comando a una instruccion en lenguaje natural
    private String instruccion(Comando c) {
        String p = texto(c.getPrincipal());
        Expresion sobre = conector(c, "SOBRE");
        Expresion desde = conector(c, "DESDE");
        Expresion en = conector(c, "EN");
        Expresion como = conector(c, "COMO");

        String base;
        String cierre = " Guarda el resultado como " + c.getDestino() + ".";

        switch (c.getTipo()) {
            case "PREGUNTAR":
                base = sobre != null ? "Basandote en " + texto(sobre) + ", responde: " + fin(p)
                                        : "Responde: " + fin(p);
                cierre = " Guarda la respuesta como " + c.getDestino() + ".";
                break;
            case "GENERAR":
                base = desde != null ? "Genera " + p + " a partir de: " + texto(desde) + "."
                                     : "Genera " + p + ".";
                break;
            case "RESUMIR":
                base = en != null ? "Resume " + p + " en " + limite(en) + "."
                                  : "Resume " + p + ".";
                break;
            case "ANALIZAR":
                base = sobre != null ? "Analiza " + p + " considerando: " + texto(sobre) + "."
                                     : "Analiza " + p + ".";
                break;
            case "TRADUCIR":
                base = en != null ? "Traduce " + p + " al idioma " + texto(en) + "."
                                  : "Traduce " + p + ".";
                break;
            case "CLASIFICAR":
                base = en != null ? "Clasifica " + p + " en las categorias: " + texto(en) + "."
                                  : "Clasifica " + p + ".";
                break;
            case "EXTRAER":
                base = desde != null ? "Extrae " + p + " a partir de " + texto(desde) + "."
                                     : "Extrae " + p + ".";
                break;
            default:
                //Comando agregado en clase: se redacta de forma generica
                StringBuilder generico = new StringBuilder(c.getTipo() + " " + p);
                for (Conector con : c.getConectores()) {
                    generico.append(" ").append(con.getPalabra().toLowerCase())
                            .append(" ").append(texto(con.getArgumento()));
                }
                base = generico + ".";
        }

        String formato = como != null ? " Entrega el resultado en formato " + texto(como) + "." : "";
        return base + cierre + formato;
    }

    //"EN 100 palabras" -> "un maximo de 100 palabras"
    private String limite(Expresion e) {
        Termino primero = e.getTerminos().get(0);
        if (primero.getTipo() == Termino.Tipo.NUMERO) {
            String unidad = primero.getUnidad() != null ? primero.getUnidad() : "palabras";
            return "un maximo de " + primero.getValor() + " " + unidad;
        }
        return texto(e);
    }

    private Expresion conector(Comando c, String palabra) {
        for (Conector con : c.getConectores()) {
            if (con.getPalabra().equals(palabra)) {
                return con.getArgumento();
            }
        }
        return null;
    }

    //Una expresion como texto natural (los terminos unidos con " + ")
    private String texto(Expresion e) {
        StringBuilder sb = new StringBuilder();
        List<Termino> ts = e.getTerminos();
        for (int i = 0; i < ts.size(); i++) {
            if (i > 0) {
                sb.append(" + ");
            }
            sb.append(termino(ts.get(i)));
        }
        return sb.toString();
    }

    private String termino(Termino t) {
        switch (t.getTipo()) {
            case NUMERO:
                return t.getUnidad() == null ? t.getValor() : t.getValor() + " " + t.getUnidad();
            case CARGAR:
                return "el contenido del archivo \"" + t.getValor() + "\" (adjuntar el archivo)";
            default:
                return t.getValor();
        }
    }
    
    //Agrega el punto final solo si la frase no termina ya en ?, ! o .
    private String fin(String s) {
        return (s.endsWith("?") || s.endsWith("!") || s.endsWith(".")) ? s : s + ".";
    }
}
