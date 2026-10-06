/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.logica;

import com.promptzal.modelo.Token;
import com.promptzal.modelo.ErrorLexico;
import java.util.ArrayList;
import java.util.List;
import com.promptzal.modelo.Estado;
import com.promptzal.modelo.TipoEstado;
import com.promptzal.modelo.Transicion;
/**
 * Analizador lexico de PromptZal guiado por el AFD.
 * El AFD vive como datos en DefinicionAFD (estados q0 a q24), de ahi sale
 * tambien el codigo DOT. Este analizador solo recorre esa definicion:
 * reconocer() es el motor y analizar() representa el estado inicial q0.
 *
 * @author eduar
 */
public class AnalizadorLexico {
    //Atributos
    private String texto;
    private int posicion;
    private int fila;
    private int columna;
    private int contadorTokens;

    private List<Token> listaTokens;
    private List<ErrorLexico> listaErrores;
    private final DefinicionAFD afd = new DefinicionAFD();

    //Listas de palabras conocidas, para clasificar contra ellas
    private static final String[] PALABRAS_RESERVADAS = {"AGENTE", "contexto", "variable", "EJECUTAR", "EXPORTAR"};
    private static final String[] COMANDOS_IA = {"PREGUNTAR", "GENERAR", "RESUMIR", "ANALIZAR", "TRADUCIR", "CLASIFICAR", "EXTRAER", "CARGAR"};
    private static final String[] CONECTORES_PALABRA = {"SOBRE", "DESDE", "EN", "COMO"};

    //Constructor
    public AnalizadorLexico(String texto) {
        this.texto = texto;
        this.posicion = 0;
        this.fila = 1;
        this.columna = 1;
        this.contadorTokens = 0;
        this.listaTokens = new ArrayList<>();
        this.listaErrores = new ArrayList<>();
    }
    
    private void avanzar() {
        //Mueve el puntero una posicion hacia adelante, actualiza fila/columna 
        //correctamente segun lo que se acaba de dejar atras
        if (texto.charAt(posicion) == '\n') {
            fila++;
            columna = 1;
        } else {
            columna++;
        }
        posicion++;
    }

    private String clasificarPalabra(String palabra) {
        //Respeta el orden de prioridad
        if (contiene(PALABRAS_RESERVADAS, palabra)) {
            return "PALABRA_RESERVADA";
        }
        if (contiene(COMANDOS_IA, palabra)) {
            return "COMANDO_IA";
        }
        if (contiene(CONECTORES_PALABRA, palabra)) {
            return "CONECTOR";
        }
        return "IDENTIFICADOR";
    }

    private boolean contiene(String[] arreglo, String valor) {
        //Recorre el arreglo comparando uno por uno
        for (String s : arreglo) {
            if (s.equals(valor)) {
                return true;
            }
        }
        return false;
    }

    private boolean esDirectivaValida(String directiva) {
        String[] directivasValidas = {"modelo", "rol", "formato"};
        return contiene(directivasValidas, directiva);
    }
    
    //Despachador principal: representa el estado inicial q0 del AFD.
    //Salta los espacios en blanco; si q0 tiene transicion para el caracter actual,
    //arranca el motor (reconocer); si no la tiene, reporta caracter no reconocido.
    public void analizar() {
        while (posicion < texto.length()) {
            char actual = texto.charAt(posicion);

            if (afd.mover("q0", actual) != null) {
                reconocer();
            } else {
                //q0 no tiene transicion: caracter no reconocido
                listaErrores.add(new ErrorLexico(String.valueOf(actual), "Caracter no reconocido", fila, columna));
                avanzar();
            }
        }
    }
    
    //Motor: recorre el AFD consultando DefinicionAFD en cada caracter.
    private void reconocer() {
        int filaInicio = fila;
        int columnaInicio = columna;
        StringBuilder lexema = new StringBuilder();
        String estado = "q0";

        while (true) {
            int c = (posicion < texto.length()) ? texto.charAt(posicion) : -1;
            Transicion t = afd.mover(estado, c);
            if (t == null) {
                break; //no hay camino: el estado actual decide que pasa
            }

            //Regla de consumo: no se consume el fin de archivo, ninguna transicion
            //hacia un estado de error (el caracter que la dispara es del siguiente
            //token) y tampoco un "otro" hacia aceptacion.
            Estado destino = afd.getEstado(t.getDestino());
            boolean consume = c != -1
                    && destino.getTipo() != TipoEstado.ERROR
                    && !(t.getSimbolo().equals("otro") && destino.getTipo() != TipoEstado.TRANSITO);
            if (consume) {
                if (t.isGuardar()) {
                    lexema.append((char) c); //las comillas, por ejemplo, se consumen pero no se guardan
                }
                avanzar();
            }
            //Volver a q0 significa empezar de nuevo: lo acumulado se descarta y
            //la posicion de inicio del siguiente token es la actual
            if (t.getDestino().equals("q0")) {
                lexema.setLength(0);
                filaInicio = fila;
                columnaInicio = columna;
            }
            estado = t.getDestino();
        }
        
        //El tipo del estado final decide el resultado
        Estado fin = afd.getEstado(estado);

        //Directiva: la validacion contra {modelo, rol, formato} se hace sobre el lexema
        //completo, igual que clasificarPalabra() en q20. Si no es valida, se va a q13.
        if (fin.getEtiqueta().equals("DIRECTIVA") && !esDirectivaValida(lexema.toString())) {
            fin = afd.getEstado("q13");
            lexema.insert(0, "@"); //el error muestra la directiva completa, como antes
        }
        switch (fin.getTipo()) {
            case ACEPTACION:
                String tipo = fin.getEtiqueta();
                if (tipo.equals("ID/RESERVADA")) {
                    tipo = clasificarPalabra(lexema.toString());
                }
                contadorTokens++;
                listaTokens.add(new Token(contadorTokens, lexema.toString(), tipo, filaInicio, columnaInicio));
                break;
            case ERROR:
                listaErrores.add(new ErrorLexico(lexema.toString(), fin.getMensaje(), filaInicio, columnaInicio));
                break;
            default:
                break; //aceptacion sin token (comentario): se descarta
        }
    }
    
    //Getters
    public List<Token> getListaTokens() {
        return listaTokens;
    }

    public List<ErrorLexico> getListaErrores() {
        return listaErrores;
    }
}
