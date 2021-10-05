package jsmahy.ups_client;

import javax.swing.*;

public class MainMenu {
    private JButton DBBoysButton;
    private JPanel panel1;
    private JButton TEST2Button;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new MainMenu().panel1);
        frame.setVisible(true);
    }
}
