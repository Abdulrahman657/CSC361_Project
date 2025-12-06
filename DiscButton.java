import javax.swing.*;
import java.awt.*;

class DiscButton extends JButton {
    private char piece = '.'; 

    DiscButton() {
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    void setPiece(char p) {
        this.piece = p;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 20;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        if (piece == 'B' || piece == 'W') {

            int glowSize = size + 14;
            int gx = (getWidth() - glowSize) / 2;
            int gy = (getHeight() - glowSize) / 2;

            Color glowColor = new Color(255, 255, 255, 60);
            g2.setColor(glowColor);
            g2.fillOval(gx, gy, glowSize, glowSize);

            if (piece == 'B') g2.setColor(Color.BLACK);
            else g2.setColor(Color.WHITE);
            g2.fillOval(x, y, size, size);

            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(x, y, size, size);
        }

        g2.dispose();
    }
}
