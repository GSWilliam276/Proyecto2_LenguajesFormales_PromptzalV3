/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.vista;

import java.awt.Color;
/**
 *
 * @author eduar
 */
public class Colores {
    //--- Color Base (igual que antes) ---
    public static final Color FONDO_PRINCIPAL = new Color(0, 0, 0);        //#000000
    public static final Color FONDO_PANEL = new Color(10, 10, 10);         //#0A0A0A
    public static final Color NARANJA = new Color(217, 119, 87);           //#D97757
    public static final Color NARANJA_BORDE = new Color(153, 60, 29);      //#993C1D
    public static final Color TEXTO_SECUNDARIO = new Color(217, 195, 163); //#D9C3A3
    public static final Color ERROR = new Color(226, 75, 74);              //#E24B4A

    //--- Texto del editor y del promptzal generado ---
    public static final Color TEXTO_EDITOR = new Color(255, 255, 255);     //blanco puro

    //--- Un color por categoria de token (tabla de tokens) ---
    public static final Color TOKEN_DIRECTIVA = new Color(214, 112, 255);  //magenta
    public static final Color TOKEN_ESTRUCTURA = new Color(255, 145, 77);  //naranja vivo
    public static final Color TOKEN_COMANDO = new Color(64, 156, 255);     //azul
    public static final Color TOKEN_CARGAR = new Color(0, 220, 220);       //cian
    public static final Color TOKEN_CONECTOR = new Color(255, 214, 10);    //amarillo
    public static final Color TOKEN_SIMBOLO = new Color(190, 195, 205);    //gris claro
    public static final Color TOKEN_ID = new Color(255, 255, 255);         //blanco
    public static final Color TOKEN_CADENA = new Color(80, 250, 123);      //verde
    public static final Color TOKEN_NUMERO = new Color(255, 121, 198);     //rosa

    //--- Un color por tipo de error (panel de errores) ---
    public static final Color ERROR_LEXICO = new Color(255, 69, 58);       //rojo
    public static final Color ERROR_SINTACTICO = new Color(255, 176, 0);   //ambar
    public static final Color ERROR_SEMANTICO = new Color(170, 150, 255);  //violeta

    //Devuelve el color de un token a partir de su tipo (los nombres de TipoToken)
    public static Color paraToken(String tipo) {
        switch (tipo) {
            case "DIRECTIVA":
                return TOKEN_DIRECTIVA;
            case "AGENTE": case "CONTEXTO": case "VARIABLE": case "EJECUTAR": case "EXPORTAR":
                return TOKEN_ESTRUCTURA;
            case "COMANDO_IA":
                return TOKEN_COMANDO;
            case "CARGAR":
                return TOKEN_CARGAR;
            case "CONECTOR":
                return TOKEN_CONECTOR;
            case "CADENA":
                return TOKEN_CADENA;
            case "NUMERO":
                return TOKEN_NUMERO;
            case "ID":
                return TOKEN_ID;
            default:
                return TOKEN_SIMBOLO; //FLECHA, IGUAL, MAS, LLAVE_A/C, PAR_A/C, COMA
        }
    }

    //Devuelve el color de un error a partir de su tipo ("Lexico", "Sintactico", "Semantico")
    public static Color paraError(String tipo) {
        switch (tipo) {
            case "Sintactico": return ERROR_SINTACTICO;
            case "Semantico":  return ERROR_SEMANTICO;
            default:           return ERROR_LEXICO;
        }
    }
}
