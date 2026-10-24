/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.generadores;

import com.promptzal.ast.Agente;
import com.promptzal.ast.Comando;
import com.promptzal.ast.Conector;
import com.promptzal.ast.Directiva;
import com.promptzal.ast.Programa;
import com.promptzal.ast.Referencia;
import com.promptzal.ast.Variable;
import com.promptzal.excepciones.ExcepcionGraphvizNoDisponible;
import com.promptzal.logica.TablaSimbolos;
import com.promptzal.modelo.ErrorLexico;
import com.promptzal.modelo.ErrorSemantico;
import com.promptzal.modelo.ErrorSintactico;
import com.promptzal.modelo.Simbolo;
import com.promptzal.modelo.Token;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import com.promptzal.modelo.Token;
import com.promptzal.vista.Colores;
import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;
/**
 *
 * @author eduar
 */
public class GeneradorReporteCompilador {
    //Errores
    public boolean generarReporteErrores(List<ErrorLexico> lexicos, List<ErrorSintactico> sintacticos,
            List<ErrorSemantico> semanticos, String ruta) {
        StringBuilder cuerpo = new StringBuilder();
        int total = lexicos.size() + sintacticos.size() + semanticos.size();

        if (total == 0) {
            cuerpo.append("<p class=\"ok\">La compilación terminó sin errores.</p>");
        } else {
            cuerpo.append("<p>Total de errores: ").append(total).append("</p>");
            cuerpo.append("<table><tr><th>Tipo</th><th>Descripción</th><th>Token encontrado</th>"
                    + "<th>Token esperado</th><th>Fila</th><th>Columna</th></tr>");
            for (ErrorLexico e : lexicos) {
                fila(cuerpo, "lexico", "Léxico", e.getDescripcion() + " ('" + e.getLexema() + "')",
                        "-", "-", e.getFila(), String.valueOf(e.getColumna()));
            }
            for (ErrorSintactico e : sintacticos) {
                fila(cuerpo, "sintactico", "Sintáctico", "Estructura inválida",
                        e.getEncontrado(), e.getEsperado(), e.getFila(), String.valueOf(e.getColumna()));
            }
            for (ErrorSemantico e : semanticos) {
                fila(cuerpo, "semantico", "Semántico", e.getDescripcion(),
                        "-", "-", e.getFila(), "-");
            }
            cuerpo.append("</table>");
        }
        return escribir(ruta, pagina("Reporte de errores", cuerpo.toString()));
    }

    private void fila(StringBuilder sb, String clase, String tipo, String descripcion,
            String encontrado, String esperado, int fila, String columna) {
        sb.append("<tr class=\"").append(clase).append("\"><td>").append(tipo).append("</td><td>")
          .append(esc(descripcion)).append("</td><td>").append(esc(encontrado)).append("</td><td>")
          .append(esc(esperado)).append("</td><td>").append(fila).append("</td><td>")
          .append(columna).append("</td></tr>");
    }

    //Tabla de simbolos: cada fila con el color de su tipo, igual que en el IDE
    public boolean generarReporteSimbolos(TablaSimbolos tabla, String ruta) {
        StringBuilder cuerpo = new StringBuilder();
        if (tabla.getSimbolos().isEmpty()) {
            cuerpo.append("<p>No hay símbolos declarados.</p>");
        } else {
            cuerpo.append("<table><tr><th>Nombre</th><th>Tipo</th><th>Ámbito</th><th>Línea</th></tr>");
            for (Simbolo s : tabla.getSimbolos()) {
                cuerpo.append("<tr style=\"color:").append(hex(colorSimbolo(s))).append("\"><td>")
                      .append(esc(s.getNombre())).append("</td><td>")
                      .append(s.getTipoTexto()).append("</td><td>").append(esc(s.getAmbito()))
                      .append("</td><td>").append(s.getLinea()).append("</td></tr>");
            }
            cuerpo.append("</table>");
        }
        return escribir(ruta, pagina("Tabla de símbolos", cuerpo.toString()));
    }

    //AST: diagrama (ast.png, generado aparte) y texto con sangrias
    public boolean generarReporteAST(Programa p, String ruta) {
        StringBuilder sb = new StringBuilder();
        linea(sb, 0, "Programa");
        for (Directiva d : p.getDirectivas()) {
            linea(sb, 1, "Directiva " + d.getNombre() + " = \"" + d.getValor() + "\"");
        }
        for (Agente a : p.getAgentes()) {
            linea(sb, 1, "Agente " + a.getNombre() + " (línea " + a.getFila() + ")");
            if (a.getContexto() != null) {
                linea(sb, 2, "Contexto: \"" + a.getContexto() + "\"");
            }
            for (Variable v : a.getVariables()) {
                linea(sb, 2, "Variable " + v.getNombre() + " (línea " + v.getFila() + ")");
                linea(sb, 3, "Valor: " + v.getValor().aTexto());
            }
            for (Comando c : a.getComandos()) {
                linea(sb, 2, "Comando " + c.getTipo() + " (línea " + c.getFila() + ")");
                linea(sb, 3, "Argumento: " + c.getPrincipal().aTexto());
                for (Conector con : c.getConectores()) {
                    linea(sb, 3, "Conector " + con.getPalabra() + ": " + con.getArgumento().aTexto());
                }
                linea(sb, 3, "Destino: " + c.getDestino());
            }
        }
        for (Referencia r : p.getEjecuciones()) {
            linea(sb, 1, "Ejecutar " + r.getNombre() + " (línea " + r.getFila() + ")");
        }
        for (Referencia r : p.getExportaciones()) {
            linea(sb, 1, "Exportar " + r.getNombre() + " (línea " + r.getFila() + ")");
        }
        String cuerpo = "<h2>Diagrama</h2>"
                + "<div style=\"background:#fff;padding:15px;display:inline-block\">"
                + "<img src=\"ast.png\" alt=\"AST\" onerror=\"this.parentNode.style.display='none'\"></div>"
                + "<h2>Texto con sangrías</h2><pre>" + esc(sb.toString()) + "</pre>";
        return escribir(ruta, pagina("Árbol de sintaxis abstracta (AST)", cuerpo));
    }

    private void linea(StringBuilder sb, int nivel, String texto) {
        sb.append("    ".repeat(nivel)).append(nivel == 0 ? "" : "- ").append(texto).append("\n");
    }

    //Utilidades
    private String pagina(String titulo, String cuerpo) {
        return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"><title>" + titulo + "</title>"
                + "<style>"
                + "body{background:#000;color:#eee;font-family:Consolas,monospace;margin:30px}"
                + "h1,h2{color:#D97757}table{border-collapse:collapse;width:100%}"
                + "th{background:#D97757;color:#000;padding:8px;text-align:left}"
                + "td{border-bottom:1px solid #333;padding:6px 8px}"
                + "tr.lexico td{color:#ff453a}tr.sintactico td{color:#ffb000}tr.semantico td{color:#aa96ff}"
                + ".ok{color:#50fa7b;font-size:1.2em}pre{font-size:15px;line-height:1.5}"
                + "</style></head><body><h1>" + titulo + "</h1>" + cuerpo + "</body></html>";
    }

    private String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private boolean escribir(String ruta, String contenido) {
        try {
            Path destino = Path.of(ruta);
            if (destino.getParent() != null) {
                Files.createDirectories(destino.getParent());
            }
            Files.writeString(destino, contenido);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    //Tokens: cada fila con el color de su categoria, igual que en el IDE
    public boolean generarReporteTokens(List<Token> tokens, String ruta) {
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append("<p>Total de tokens: ").append(tokens.size()).append("</p>");
        cuerpo.append("<table><tr><th>No.</th><th>Lexema</th><th>Tipo</th><th>Fila</th><th>Columna</th></tr>");
        for (Token t : tokens) {
            cuerpo.append("<tr style=\"color:").append(hex(Colores.paraToken(t.getTipo()))).append("\"><td>")
                  .append(t.getNumero()).append("</td><td>").append(esc(t.getLexema())).append("</td><td>")
                  .append(t.getTipo()).append("</td><td>").append(t.getFila()).append("</td><td>")
                  .append(t.getColumna()).append("</td></tr>");
        }
        cuerpo.append("</table>");
        return escribir(ruta, pagina("Reporte de tokens", cuerpo.toString()));
    }

    //AST como diagrama (Graphviz)
    private int contador = 0;

    public String generarDotAST(Programa p) {
        contador = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("digraph AST {\n");
        sb.append("    node [shape=box, style=\"rounded,filled\", fontname=\"Helvetica\", fontsize=11];\n");
        sb.append("    edge [color=\"#666666\"];\n\n");

        int raiz = nodo(sb, "Programa", "#F4B393");
        for (Directiva d : p.getDirectivas()) {
            arista(sb, raiz, nodo(sb, d.getNombre() + "\n" + d.getValor(), "#E0B3F5"));
        }
        for (Agente a : p.getAgentes()) {
            int ag = nodo(sb, "Agente\n" + a.getNombre(), "#FFCBA4");
            arista(sb, raiz, ag);
            if (a.getContexto() != null) {
                arista(sb, ag, nodo(sb, "contexto\n\"" + a.getContexto() + "\"", "#E8E8E8"));
            }
            for (Variable v : a.getVariables()) {
                int nv = nodo(sb, "variable\n" + v.getNombre(), "#E8E8E8");
                arista(sb, ag, nv);
                arista(sb, nv, nodo(sb, v.getValor().aTexto(), "#FFFFFF"));
            }
            for (Comando c : a.getComandos()) {
                int nc = nodo(sb, c.getTipo(), "#A8D0FF");
                arista(sb, ag, nc);
                arista(sb, nc, nodo(sb, c.getPrincipal().aTexto(), "#FFFFFF"));
                for (Conector con : c.getConectores()) {
                    int ncon = nodo(sb, con.getPalabra(), "#FFE9A0");
                    arista(sb, nc, ncon);
                    arista(sb, ncon, nodo(sb, con.getArgumento().aTexto(), "#FFFFFF"));
                }
                arista(sb, nc, nodo(sb, "-> " + c.getDestino(), "#B8F0C0"));
            }
        }
        for (Referencia r : p.getEjecuciones()) {
            arista(sb, raiz, nodo(sb, "EJECUTAR\n" + r.getNombre(), "#FFCBA4"));
        }
        for (Referencia r : p.getExportaciones()) {
            arista(sb, raiz, nodo(sb, "EXPORTAR\n" + r.getNombre(), "#B8F0C0"));
        }
        sb.append("}\n");
        return sb.toString();
    }

    //Escribe un nodo y devuelve su numero para poder conectarlo
    private int nodo(StringBuilder sb, String etiqueta, String color) {
        int id = contador++;
        sb.append("    n").append(id).append(" [label=\"").append(escDot(etiqueta))
          .append("\", fillcolor=\"").append(color).append("\"];\n");
        return id;
    }

    private void arista(StringBuilder sb, int padre, int hijo) {
        sb.append("    n").append(padre).append(" -> n").append(hijo).append(";\n");
    }

    private String escDot(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    public boolean generarImagenAST(Programa p, String rutaDot, String rutaPng)
            throws ExcepcionGraphvizNoDisponible {
        if (!escribir(rutaDot, generarDotAST(p))) {
            return false;
        }
        try {
            Process proceso = new ProcessBuilder("dot", "-Tpng", rutaDot, "-o", rutaPng)
                    .redirectErrorStream(true).start();
            return proceso.waitFor() == 0;
        } catch (IOException ex) {
            throw new ExcepcionGraphvizNoDisponible();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
        private String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    //Mismos colores que la tabla de simbolos del IDE
    private Color colorSimbolo(Simbolo s) {
        switch (s.getTipo()) {
            case AGENTE:   return Colores.TOKEN_ESTRUCTURA;
            case VARIABLE: return Colores.TOKEN_ID;
            default:       return Colores.TOKEN_CONECTOR;
        }
    }

    //----------------------------------------------------------- Estadisticas
    public boolean generarReporteEstadisticas(List<Token> tokens, List<ErrorLexico> lexicos,
            List<ErrorSintactico> sintacticos, List<ErrorSemantico> semanticos,
            Programa programa, TablaSimbolos tabla, String ruta) {
        StringBuilder cuerpo = new StringBuilder();

        //Tokens por tipo
        Map<String, Integer> porTipo = new TreeMap<>();
        for (Token t : tokens) {
            porTipo.merge(t.getTipo(), 1, Integer::sum);
        }
        cuerpo.append("<h2>Tokens por tipo</h2><table><tr><th>Tipo</th><th>Cantidad</th></tr>");
        for (Map.Entry<String, Integer> e : porTipo.entrySet()) {
            dato(cuerpo, e.getKey(), e.getValue(), Colores.paraToken(e.getKey()));
        }
        dato(cuerpo, "TOTAL", tokens.size(), Colores.TEXTO_EDITOR);
        cuerpo.append("</table>");

        //Errores por tipo
        cuerpo.append("<h2>Errores por tipo</h2><table><tr><th>Tipo</th><th>Cantidad</th></tr>");
        dato(cuerpo, "Léxicos", lexicos.size(), Colores.ERROR_LEXICO);
        dato(cuerpo, "Sintácticos", sintacticos.size(), Colores.ERROR_SINTACTICO);
        dato(cuerpo, "Semánticos", semanticos.size(), Colores.ERROR_SEMANTICO);
        cuerpo.append("</table>");

        //Contenido del programa
        int variables = 0;
        int comandos = 0;
        for (Agente a : programa.getAgentes()) {
            variables += a.getVariables().size();
            comandos += a.getComandos().size();
        }
        cuerpo.append("<h2>Programa</h2><table><tr><th>Elemento</th><th>Cantidad</th></tr>");
        dato(cuerpo, "Directivas", programa.getDirectivas().size(), Colores.TOKEN_DIRECTIVA);
        dato(cuerpo, "Agentes", programa.getAgentes().size(), Colores.TOKEN_ESTRUCTURA);
        dato(cuerpo, "Variables declaradas", variables, Colores.TOKEN_ID);
        dato(cuerpo, "Comandos", comandos, Colores.TOKEN_COMANDO);
        dato(cuerpo, "Ejecuciones", programa.getEjecuciones().size(), Colores.TOKEN_ESTRUCTURA);
        dato(cuerpo, "Exportaciones", programa.getExportaciones().size(), Colores.TOKEN_ESTRUCTURA);
        dato(cuerpo, "Símbolos en la tabla", tabla.getSimbolos().size(), Colores.TOKEN_CONECTOR);
        cuerpo.append("</table>");

        return escribir(ruta, pagina("Reporte de estadísticas", cuerpo.toString()));
    }

    private void dato(StringBuilder sb, String nombre, int valor, Color color) {
        sb.append("<tr style=\"color:").append(hex(color)).append("\"><td>").append(esc(nombre))
          .append("</td><td>").append(valor).append("</td></tr>");
    }
}
