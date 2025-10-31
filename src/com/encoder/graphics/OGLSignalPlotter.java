package com.encoder.graphics;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.JFrame;
import java.util.List;

public class OGLSignalPlotter {

    public static void plot(List<Double> levels, String title) {
        // Get the default OpenGL profile (e.g., GL2)
        GLProfile profile = GLProfile.get(GLProfile.GL2ES2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        
        // Create the OpenGL canvas
        GLCanvas glcanvas = new GLCanvas(capabilities);
        
        // Create our custom renderer and add it
        OGLSignalRenderer renderer = new OGLSignalRenderer(levels);
        glcanvas.addGLEventListener(renderer);
        glcanvas.setSize(800, 400);

        // Create the JFrame
        final JFrame frame = new JFrame(title);
        frame.getContentPane().add(glcanvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start the animator
        // Use FPSAnimator for continuous rendering (good practice)
        final FPSAnimator animator = new FPSAnimator(glcanvas, 60);
        animator.start();
        
        // Add a shutdown hook
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                animator.stop();
            }
        });
    }
}