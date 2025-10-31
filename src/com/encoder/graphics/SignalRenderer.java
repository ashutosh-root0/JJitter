package com.encoder.graphics;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import java.util.List;

public class SignalRenderer implements GLEventListener {

    private List<Double> signalLevels;
    private GLU glu = new GLU();

    public SignalRenderer(List<Double> levels) {
        this.signalLevels = levels;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Set the background color to black
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Clear the color buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity(); // Reset the model-view matrix

        drawAxes(gl);
        drawSignal(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Set up the coordinate system
        // X-axis: 0 to number of levels
        // Y-axis: -2.0 to +2.0 (to give padding)
        glu.gluOrtho2D(0.0, signalLevels.size(), -2.0, 2.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private void drawAxes(GL2 gl) {
        // Draw X-axis (0V)
        gl.glColor3f(1.0f, 1.0f, 1.0f); // White
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2d(0, 0);
        gl.glVertex2d(signalLevels.size(), 0);
        gl.glEnd();

        // Draw dotted helper lines for +1V and -1V
        gl.glEnable(GL2.GL_LINE_STIPPLE);
        gl.glLineStipple(1, (short) 0xAAAA); // Dotted pattern
        gl.glColor3f(0.5f, 0.5f, 0.5f); // Gray
        
        gl.glBegin(GL2.GL_LINES);
        // +1V line
        gl.glVertex2d(0, 1.0);
        gl.glVertex2d(signalLevels.size(), 1.0);
        // -1V line
        gl.glVertex2d(0, -1.0);
        gl.glVertex2d(signalLevels.size(), -1.0);
        gl.glEnd();
        
        gl.glDisable(GL2.GL_LINE_STIPPLE);
    }

    private void drawSignal(GL2 gl) {
        gl.glColor3f(0.0f, 1.0f, 0.0f); // Bright green (oscilloscope)
        gl.glLineWidth(2.0f);
        
        gl.glBegin(GL2.GL_LINES);
        
        double lastY = 0.0;
        if (!signalLevels.isEmpty()) {
            lastY = signalLevels.get(0);
        }

        for (int x = 0; x < signalLevels.size(); x++) {
            double y = signalLevels.get(x);

            // Draw vertical line for transition
            if (x > 0 && y != lastY) {
                gl.glVertex2d(x, lastY);
                gl.glVertex2d(x, y);
            }
            
            // Draw horizontal line for the level
            gl.glVertex2d(x, y);
            gl.glVertex2d(x + 1, y);
            
            lastY = y;
        }
        
        gl.glEnd();
    }
}