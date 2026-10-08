package com.library.main;

import com.library.ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Main Entry Point for the Library Management System Application.
 */
public class MainApp {
    public static void main(String[] args) {
        // Ensure GUI runs safely on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
