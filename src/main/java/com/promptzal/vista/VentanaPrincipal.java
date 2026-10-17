/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.promptzal.vista;

import com.promptzal.controlador.ControladorAnalisis;
import com.promptzal.excepciones.ExcepcionArchivoNoEncontrado;
import com.promptzal.excepciones.ExcepcionGraphvizNoDisponible;
import com.promptzal.modelo.Simbolo;
import com.promptzal.modelo.Token;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.function.BiFunction;
/**
 *
 * @author eduar
 */
public class VentanaPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel panelContenedor;
    private JTextArea areaEditor;
    private JTextArea areaPromptzal;
    private JTabbedPane pestanas;
    private JLabel etiquetaEstado;
    private DefaultTableModel modeloTokens;
    private DefaultTableModel modeloErrores;
    private DefaultTableModel modeloSimbolos;

    private final ControladorAnalisis controlador = new ControladorAnalisis();

    public static final String PANTALLA_BIENVENIDA = "bienvenida";
    public static final String PANTALLA_IDE = "ide";

    public VentanaPrincipal() {
        setTitle("PromptZal - Compilador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 780);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setBackground(Colores.FONDO_PRINCIPAL);

        panelContenedor.add(crearPanelBienvenida(), PANTALLA_BIENVENIDA);
        panelContenedor.add(crearPanelIDE(), PANTALLA_IDE);

        add(panelContenedor);
        cardLayout.show(panelContenedor, PANTALLA_BIENVENIDA);
    }

    //----------------------------------------------------------------- Bienvenida
    private JPanel crearPanelBienvenida() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colores.FONDO_PRINCIPAL);

        JPanel panelQuetzal = new JPanel(new BorderLayout());
        panelQuetzal.setBackground(Colores.FONDO_PRINCIPAL);
        panelQuetzal.setPreferredSize(new Dimension(700, 0));

        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/quetzal.png"));
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(690, -1, Image.SCALE_SMOOTH);
        JLabel labelQuetzal = new JLabel(new ImageIcon(imagenEscalada));
        labelQuetzal.setHorizontalAlignment(SwingConstants.CENTER);
        labelQuetzal.setVerticalAlignment(SwingConstants.CENTER);
        panelQuetzal.add(labelQuetzal, BorderLayout.CENTER);
        panel.add(panelQuetzal, BorderLayout.WEST);

        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(Colores.FONDO_PRINCIPAL);
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel titulo = new JLabel("[ PROMPTZAL ]");
        titulo.setFont(new Font("Consolas", Font.BOLD, 50));
        titulo.setForeground(Colores.NARANJA);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("-- Compilador --");
        subtitulo.setFont(new Font("Consolas", Font.PLAIN, 30));
        subtitulo.setForeground(Colores.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonIniciar = new JButton("[ INICIAR SISTEMA ]");
        estilizarBoton(botonIniciar);
        botonIniciar.setFont(new Font("Consolas", Font.BOLD, 25));
        botonIniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonIniciar.addActionListener(e -> cardLayout.show(panelContenedor, PANTALLA_IDE));

        JButton botonSalir = new JButton("[ SALIR ]");
        botonSalir.setFont(new Font("Consolas", Font.BOLD, 20));
        botonSalir.setForeground(Colores.ERROR);
        botonSalir.setBackground(Colores.FONDO_PANEL);
        botonSalir.setBorder(BorderFactory.createLineBorder(Colores.ERROR, 2));
        botonSalir.setFocusPainted(false);
        botonSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        botonSalir.addActionListener(e -> System.exit(0));

        panelDerecho.add(Box.createVerticalGlue());
        panelDerecho.add(titulo);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 14)));
        panelDerecho.add(subtitulo);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 40)));
        panelDerecho.add(botonIniciar);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 16)));
        panelDerecho.add(botonSalir);
        panelDerecho.add(Box.createVerticalGlue());

        panel.add(panelDerecho, BorderLayout.CENTER);
        return panel;
    }

    //----------------------------------------------------------------- IDE
    private JPanel crearPanelIDE() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colores.FONDO_PRINCIPAL);

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                crearPanelEditor(), crearPanelResultados());
        division.setResizeWeight(0.5);
        division.setBackground(Colores.FONDO_PRINCIPAL);
        division.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panel.add(division, BorderLayout.CENTER);

        etiquetaEstado = new JLabel("Listo. Escribe un programa .pz y presiona COMPILAR.");
        etiquetaEstado.setFont(new Font("Consolas", Font.PLAIN, 13));
        etiquetaEstado.setForeground(Colores.TEXTO_SECUNDARIO);
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(0, 12, 6, 12));

        //Abajo: primero los botones y debajo la barra de estado
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(Colores.FONDO_PRINCIPAL);
        panelSur.add(crearBarraHerramientas(), BorderLayout.CENTER);
        panelSur.add(etiquetaEstado, BorderLayout.SOUTH);
        panel.add(panelSur, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearBarraHerramientas() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        barra.setBackground(Colores.FONDO_PRINCIPAL);

        JButton abrir = new JButton("[ ABRIR .PZ ]");
        estilizarBoton(abrir);
        abrir.addActionListener(e -> seleccionarArchivo());

        JButton guardar = new JButton("[ GUARDAR ]");
        estilizarBoton(guardar);
        guardar.addActionListener(e -> guardarArchivo());

        //El boton principal va en verde, como el "run" de un IDE
        JButton compilar = new JButton("[ COMPILAR ]");
        estilizarBoton(compilar);
        compilar.setForeground(Colores.TOKEN_CADENA);
        compilar.setBorder(BorderFactory.createLineBorder(Colores.TOKEN_CADENA, 2));
        compilar.addActionListener(e -> compilar());

        JButton guardarPrompt = new JButton("[ GUARDAR PROMPTZAL ]");
        estilizarBoton(guardarPrompt);
        guardarPrompt.addActionListener(e -> guardarPromptzal());

        JButton reportes = new JButton("[ REPORTES HTML ]");
        estilizarBoton(reportes);
        reportes.addActionListener(e -> generarReportes());

        JButton afd = new JButton("[ AFD ]");
        estilizarBoton(afd);
        afd.addActionListener(e -> generarAFD());

        JButton inicio = new JButton("[ INICIO ]");
        estilizarBoton(inicio);
        inicio.addActionListener(e -> cardLayout.show(panelContenedor, PANTALLA_BIENVENIDA));

        barra.add(abrir);
        barra.add(guardar);
        barra.add(compilar);
        barra.add(guardarPrompt);
        barra.add(reportes);
        barra.add(afd);
        barra.add(inicio);
        return barra;
    }

    private JPanel crearPanelEditor() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colores.FONDO_PRINCIPAL);

        JLabel titulo = new JLabel("Editor (.pz)");
        titulo.setFont(new Font("Consolas", Font.BOLD, 14));
        titulo.setForeground(Colores.NARANJA);
        titulo.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panel.add(titulo, BorderLayout.NORTH);

        areaEditor = new JTextArea();
        areaEditor.setBackground(Colores.FONDO_PANEL);
        areaEditor.setForeground(Colores.TEXTO_EDITOR);
        areaEditor.setCaretColor(Colores.NARANJA);
        areaEditor.setFont(new Font("Consolas", Font.PLAIN, 15));
        areaEditor.setTabSize(4);
        JScrollPane scroll = new JScrollPane(areaEditor);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.NARANJA_BORDE));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JTabbedPane crearPanelResultados() {
        //Promptzal generado (solo lectura)
        areaPromptzal = new JTextArea();
        areaPromptzal.setEditable(false);
        areaPromptzal.setBackground(Colores.FONDO_PANEL);
        areaPromptzal.setForeground(Colores.TEXTO_EDITOR);
        areaPromptzal.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaPromptzal.setLineWrap(true);
        areaPromptzal.setWrapStyleWord(true);
        JScrollPane scrollPrompt = new JScrollPane(areaPromptzal);
        scrollPrompt.setBorder(BorderFactory.createEmptyBorder());

        //Tokens: cada categoria con su color
        modeloTokens = modeloSoloLectura("No.", "Lexema", "Tipo", "Fila", "Columna");
        JTable tablaTokens = new JTable(modeloTokens);
        estilizarTabla(tablaTokens);
        tablaTokens.setDefaultRenderer(Object.class, new RenderizadorColor(
                (t, fila) -> Colores.paraToken(String.valueOf(t.getValueAt(fila, 2)))));

        //Errores: un color por tipo (lexico, sintactico, semantico)
        modeloErrores = modeloSoloLectura("Tipo", "Descripción", "Fila", "Columna");
        JTable tablaErrores = new JTable(modeloErrores);
        estilizarTabla(tablaErrores);
        tablaErrores.setDefaultRenderer(Object.class, new RenderizadorColor(
                (t, fila) -> Colores.paraError(String.valueOf(t.getValueAt(fila, 0)))));

        //Tabla de simbolos
        modeloSimbolos = modeloSoloLectura("Nombre", "Tipo", "Ámbito", "Línea");
        JTable tablaSimbolos = new JTable(modeloSimbolos);
        estilizarTabla(tablaSimbolos);
        tablaSimbolos.setDefaultRenderer(Object.class, new RenderizadorColor((t, fila) -> {
            String tipo = String.valueOf(t.getValueAt(fila, 1));
            if (tipo.equals("Agente")) {
                return Colores.TOKEN_ESTRUCTURA;
            }
            return tipo.startsWith("Variable") ? Colores.TOKEN_ID : Colores.TOKEN_CONECTOR;
        }));

        pestanas = new JTabbedPane();
        pestanas.setBackground(Colores.FONDO_PRINCIPAL);
        pestanas.setForeground(Colores.NARANJA);
        pestanas.setFont(new Font("Consolas", Font.BOLD, 13));
        pestanas.addTab("Promptzal", scrollPrompt);
        pestanas.addTab("Tokens", envolver(tablaTokens));
        pestanas.addTab("Errores", envolver(tablaErrores));
        pestanas.addTab("Tabla de símbolos", envolver(tablaSimbolos));
        return pestanas;
    }

    //----------------------------------------------------------------- Estilo
    private JScrollPane envolver(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Colores.FONDO_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private DefaultTableModel modeloSoloLectura(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setBackground(Colores.FONDO_PANEL);
        tabla.setForeground(Colores.NARANJA);
        tabla.setGridColor(Colores.FONDO_PRINCIPAL);
        tabla.getTableHeader().setBackground(Colores.NARANJA);
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.getTableHeader().setFont(new Font("Consolas", Font.BOLD, 13));
        tabla.setFont(new Font("Consolas", Font.PLAIN, 13));
        tabla.setRowHeight(22);
    }

    private void estilizarBoton(JButton boton) {
        boton.setFont(new Font("Consolas", Font.BOLD, 14));
        boton.setForeground(Colores.NARANJA);
        boton.setBackground(Colores.FONDO_PANEL);
        boton.setBorder(BorderFactory.createLineBorder(Colores.NARANJA_BORDE, 2));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    //Pinta cada fila con el color que decida la funcion recibida
    private static class RenderizadorColor extends DefaultTableCellRenderer {
        private final BiFunction<JTable, Integer, Color> colorDeFila;

        RenderizadorColor(BiFunction<JTable, Integer, Color> colorDeFila) {
            this.colorDeFila = colorDeFila;
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor,
                boolean seleccionada, boolean foco, int fila, int columna) {
            Component c = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            c.setBackground(seleccionada ? Colores.NARANJA_BORDE : Colores.FONDO_PANEL);
            c.setForeground(colorDeFila.apply(tabla, fila));
            return c;
        }
    }

    //----------------------------------------------------------------- Acciones
    private void seleccionarArchivo() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Selecciona un archivo .pz");
        selector.setFileFilter(new FileNameExtensionFilter("Archivos PromptZal (*.pz)", "pz"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = selector.getSelectedFile().getAbsolutePath();
            try {
                areaEditor.setText(controlador.abrirArchivo(ruta));
                areaEditor.setCaretPosition(0);
            } catch (ExcepcionArchivoNoEncontrado ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar archivo .pz");
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = selector.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith(".pz")) {
                ruta += ".pz";
            }
            if (controlador.guardarArchivo(ruta, areaEditor.getText())) {
                JOptionPane.showMessageDialog(this, "Archivo guardado correctamente en:\n" + ruta,
                        "Guardado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el archivo.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Ejecuta todas las fases y muestra los resultados
    private void compilar() {
        controlador.compilar(areaEditor.getText());

        modeloTokens.setRowCount(0);
        for (Token t : controlador.getTokens()) {
            modeloTokens.addRow(new Object[]{String.valueOf(t.getNumero()), t.getLexema(), t.getTipo(),
                String.valueOf(t.getFila()), String.valueOf(t.getColumna())});
        }

        modeloErrores.setRowCount(0);
        for (String[] fila : controlador.getTodosLosErrores()) {
            modeloErrores.addRow(fila);
        }

        modeloSimbolos.setRowCount(0);
        for (Simbolo s : controlador.getTabla().getSimbolos()) {
            modeloSimbolos.addRow(new Object[]{s.getNombre(), s.getTipoTexto(), s.getAmbito(),
                String.valueOf(s.getLinea())});
        }

        areaPromptzal.setText(controlador.getPromptzal());
        areaPromptzal.setCaretPosition(0);

        int lexicos = controlador.getErroresLexicos().size();
        int sintacticos = controlador.getErroresSintacticos().size();
        int semanticos = controlador.getErroresSemanticos().size();
        int total = lexicos + sintacticos + semanticos;

        pestanas.setTitleAt(2, total == 0 ? "Errores" : "Errores (" + total + ")");
        pestanas.setSelectedIndex(total == 0 ? 0 : 2); //sin errores: el prompt; con errores: la lista

        etiquetaEstado.setText("Compilación terminada | Tokens: " + controlador.getTokens().size()
                + " | Léxicos: " + lexicos + " | Sintácticos: " + sintacticos
                + " | Semánticos: " + semanticos);
        etiquetaEstado.setForeground(total == 0 ? Colores.TOKEN_CADENA : Colores.ERROR);
    }

    private void guardarPromptzal() {
        if (areaPromptzal.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero compila un programa para generar el promptzal.",
                    "Sin promptzal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar promptzal");
        selector.setAcceptAllFileFilterUsed(false);
        selector.addChoosableFileFilter(new FileNameExtensionFilter("Markdown (*.md)", "md"));
        selector.addChoosableFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));
        if (selector.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = selector.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith(".md") && !ruta.endsWith(".txt")) {
                String extension = "md";
                if (selector.getFileFilter() instanceof FileNameExtensionFilter) {
                    extension = ((FileNameExtensionFilter) selector.getFileFilter()).getExtensions()[0];
                }
                ruta += "." + extension;
            }
            if (controlador.guardarArchivo(ruta, areaPromptzal.getText())) {
                JOptionPane.showMessageDialog(this, "Promptzal guardado en:\n" + ruta,
                        "Guardado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el promptzal.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generarReportes() {
        controlador.generarReportes("recursos/reportes");
        JOptionPane.showMessageDialog(this, "Reportes generados en recursos/reportes/",
                "Listo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generarAFD() {
        try {
            controlador.generarAFD("recursos/afd");
            JOptionPane.showMessageDialog(this, "Imagen del AFD generada en recursos/afd/",
                    "Listo", JOptionPane.INFORMATION_MESSAGE);
        } catch (ExcepcionGraphvizNoDisponible ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
