package com.encoder.graphics;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * A Swing JPanel that uses Java 2D to draw signal waveforms.
 * This replaces the JOGL-based SignalRenderer and SignalPlotter.
 */
public class SignalChartPanel extends JPanel {

    private List<Double> signalLevels = Collections.emptyList();
    private String dataString = ""; // Original bits for labels
    private String encodedString = ""; // Encoded bits (might have +, -)
    private String plotTitle = "";
    private boolean isAnalogDemo = false;

    // Colors
    private static final Color COLOR_BACKGROUND = Color.BLACK;
    private static final Color COLOR_AXES = new Color(100, 100, 100);
    private static final Color COLOR_GRID = new Color(70, 70, 70);
    private static final Color COLOR_SIGNAL = new Color(50, 255, 50); // Bright Green
    private static final Color COLOR_TEXT = Color.WHITE;
    private static final Color COLOR_BIT_LABEL = Color.CYAN;

    // Padding
    private static final int PADDING_TOP = 40;
    private static final int PADDING_BOTTOM = 40;
    private static final int PADDING_LEFT = 40;
    private static final int PADDING_RIGHT = 40;

    public SignalChartPanel() {
        setBackground(COLOR_BACKGROUND);
    }

    /**
     * Sets the data for a digital signal plot.
     */
    public void setDigitalSignal(List<Double> levels, String dataString, String encodedString, String title) {
        this.signalLevels = levels;
        this.dataString = dataString;
        this.encodedString = encodedString;
        this.plotTitle = title;
        this.isAnalogDemo = false;
        repaint(); // Trigger a redraw
    }

    /**
     * Sets the data for an analog demo plot.
     */
    public void setAnalogSignal(List<Double> levels, String title) {
        this.signalLevels = levels;
        this.dataString = ""; // Not used for analog
        this.encodedString = "";
        this.plotTitle = title;
        this.isAnalogDemo = true;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smooth lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isAnalogDemo) {
            drawAnalogDemo(g2d);
        } else {
            drawDigitalSignal(g2d);
        }

        drawTitle(g2d);
    }

    private void drawTitle(Graphics2D g2d) {
        if (plotTitle == null || plotTitle.isEmpty()) return;

        g2d.setColor(COLOR_TEXT);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(plotTitle);
        g2d.drawString(plotTitle, (getWidth() - titleWidth) / 2, PADDING_TOP / 2 + fm.getAscent() / 2);
    }

    private void drawAnalogDemo(Graphics2D g2d) {
        if (signalLevels == null || signalLevels.isEmpty()) return;

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int chartWidth = panelWidth - PADDING_LEFT - PADDING_RIGHT;
        int chartHeight = panelHeight - PADDING_TOP - PADDING_BOTTOM;

        if (chartWidth <= 0 || chartHeight <= 0) return;

        // --- Draw Axes ---
        int yMid = PADDING_TOP + chartHeight / 2;
        double yMax = 2.0; // Max amplitude for demo
        double yAmplitude = chartHeight / (yMax * 2.0);

        g2d.setColor(COLOR_AXES);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawLine(PADDING_LEFT, yMid, PADDING_LEFT + chartWidth, yMid); // X-Axis (0V)
        g2d.drawLine(PADDING_LEFT, PADDING_TOP, PADDING_LEFT, PADDING_TOP + chartHeight); // Y-Axis

        // --- Draw Signal ---
        g2d.setColor(COLOR_SIGNAL);
        g2d.setStroke(new BasicStroke(2.0f));

        double xStep = (double) chartWidth / (signalLevels.size() - 1);

        for (int i = 0; i < signalLevels.size() - 1; i++) {
            int x1 = PADDING_LEFT + (int) (i * xStep);
            int y1 = yMid - (int) (signalLevels.get(i) * yAmplitude);
            int x2 = PADDING_LEFT + (int) ((i + 1) * xStep);
            int y2 = yMid - (int) (signalLevels.get(i + 1) * yAmplitude);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawDigitalSignal(Graphics2D g2d) {
        if (signalLevels == null || signalLevels.isEmpty() || dataString == null) return;

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int chartWidth = panelWidth - PADDING_LEFT - PADDING_RIGHT;
        int chartHeight = panelHeight - PADDING_TOP - PADDING_BOTTOM;

        if (chartWidth <= 0 || chartHeight <= 0) return;

        // --- Draw Axes and Grid ---
        int yMid = PADDING_TOP + chartHeight / 2;
        // Y-axis: scale from -1.5 to 1.5 (to give padding around -1 to 1)
        double yMaxVoltage = 1.5;
        double yAmplitude = (chartHeight / (yMaxVoltage * 2.0));

        // Dotted stroke
        Stroke dottedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 5}, 0);

        // 0V Line (X-Axis)
        g2d.setColor(COLOR_AXES);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawLine(PADDING_LEFT, yMid, PADDING_LEFT + chartWidth, yMid);
        g2d.drawString(" 0V", PADDING_LEFT - 25, yMid + 5);

        // +1V Line
        g2d.setColor(COLOR_GRID);
        g2d.setStroke(dottedStroke);
        int yPlus1 = yMid - (int) (1.0 * yAmplitude);
        g2d.drawLine(PADDING_LEFT, yPlus1, PADDING_LEFT + chartWidth, yPlus1);
        g2d.setColor(COLOR_AXES);
        g2d.drawString("+1V", PADDING_LEFT - 25, yPlus1 + 5);

        // -1V Line
        int yMinus1 = yMid + (int) (1.0 * yAmplitude);
        g2d.setColor(COLOR_GRID);
        g2d.drawLine(PADDING_LEFT, yMinus1, PADDING_LEFT + chartWidth, yMinus1);
        g2d.setColor(COLOR_AXES);
        g2d.drawString("-1V", PADDING_LEFT - 25, yMinus1 + 5);

        // --- Draw Bit Labels and Clock Lines ---
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();

        // Use original dataString length for bit divisions
        double bitWidth = (double) chartWidth / dataString.length();

        for (int i = 0; i < dataString.length(); i++) {
            int x_start = PADDING_LEFT + (int) (i * bitWidth);
            int x_mid = x_start + (int) (bitWidth / 2);

            // Draw bit label
            String bit = String.valueOf(dataString.charAt(i));
            int bitTextWidth = fm.stringWidth(bit);
            g2d.setColor(COLOR_BIT_LABEL);
            g2d.drawString(bit, x_mid - bitTextWidth / 2, PADDING_TOP - 10);

            // Draw vertical clock line
            g2d.setColor(COLOR_GRID);
            g2d.setStroke(dottedStroke);
            if (i > 0) {
                g2d.drawLine(x_start, PADDING_TOP, x_start, PADDING_TOP + chartHeight);
            }
        }

        // --- Draw Signal ---
        g2d.setColor(COLOR_SIGNAL);
        g2d.setStroke(new BasicStroke(2.0f));

        // Use signalLevels length for signal drawing
        double xStep = (double) chartWidth / signalLevels.size();

        double lastY = signalLevels.get(0);
        int y_last_draw = yMid - (int) (lastY * yAmplitude);

        for (int i = 0; i < signalLevels.size(); i++) {
            double y = signalLevels.get(i);
            int y_draw = yMid - (int) (y * yAmplitude);
            int x_start = PADDING_LEFT + (int) (i * xStep);
            int x_end = PADDING_LEFT + (int) ((i + 1) * xStep);

            // Draw vertical line for transition
            if (y != lastY) {
                g2d.drawLine(x_start, y_last_draw, x_start, y_draw);
            }

            // Draw horizontal line for the level
            g2d.drawLine(x_start, y_draw, x_end, y_draw);

            lastY = y;
            y_last_draw = y_draw;
        }
    }
}