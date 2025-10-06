package projetogamao.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CasaView extends JPanel {
    private int numPecas;
    private Color corPeca;
    private final int index;
    private Runnable onClick;

    // Estilo
    private static final Color TRIANGLE_LIGHT = new Color(234, 215, 190);
    private static final Color TRIANGLE_DARK  = new Color(172, 137, 104);
    private static final Color BOARD_WOOD     = new Color(120, 85, 62);

    private static final Color PIECE_STROKE   = new Color(30, 30, 30);
    private static final Color HIGHLIGHT_SEL  = new Color(255, 235, 59, 110);   // amarelo
    private static final Color HIGHLIGHT_MOVE = new Color(76, 175, 80, 110);    // verde
    private static final Color HIGHLIGHT_HIT  = new Color(156, 39, 176, 110);   // roxo

    private static final Font  BADGE_FONT     = new Font("SansSerif", Font.BOLD, 12);


    private boolean hovered = false;

    public CasaView(int index) {
        this.index = index;
        this.numPecas = 0;
        this.corPeca = Color.GRAY;

        setPreferredSize(new Dimension(60, 200));
        setOpaque(true);
        setBackground(null);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true; repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered = false; repaint();
            }
        });
        setToolTipText("Casa C" + index);
    }

    public void setOnClick(Runnable onClick) { this.onClick = onClick; }

    public void setPecas(int num, Color cor) {
        this.numPecas = num;
        this.corPeca = cor;
        repaint();
    }

    public void setNumPecas(int num) {
        this.numPecas = num;
        repaint();
    }

    public void setCorPeca(Color cor) {
        this.corPeca = cor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Antialias
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(BOARD_WOOD);
        g2.fillRoundRect(1, 1, w - 2, h - 2, 10, 10);


        boolean topRow = index < 12;
        boolean dark = ((index % 2) == 0);
        g2.setColor(dark ? TRIANGLE_DARK : TRIANGLE_LIGHT);

        Polygon p = new Polygon();
        int pad = 6;
        if (topRow) {
            p.addPoint(pad, pad);
            p.addPoint(w - pad, pad);
            p.addPoint(w / 2, h - pad);
        } else {
            p.addPoint(pad, h - pad);
            p.addPoint(w - pad, h - pad);
            p.addPoint(w / 2, pad);
        }
        g2.fillPolygon(p);

        if (hovered && getBackground() == null && numPecas == 0) {
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillPolygon(p);
        }

        drawPieces(g2, w, h, topRow);

        if (getBackground() != null) {
            Color b = getBackground();
            if (b.getAlpha() == 255) b = new Color(b.getRed(), b.getGreen(), b.getBlue(), 80);
            g2.setColor(b);
            g2.fillRoundRect(1, 1, w - 2, h - 2, 10, 10);

            g2.setStroke(new BasicStroke(2.2f));
            g2.setColor(new Color(255, 255, 255, 140));
            g2.drawRoundRect(2, 2, w - 4, h - 4, 10, 10);
        }

        g2.setColor(new Color(0,0,0,120));
        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        g2.drawString("C" + index, 6, 14);

        g2.dispose();
    }

    private void drawPieces(Graphics2D g2, int w, int h, boolean topRow) {
        if (numPecas <= 0) return;

        int maxVisible = 5;
        int diametro = Math.min(40, Math.max(26, (int) (getWidth() * 0.6)));
        int spacing  = Math.max(4, (int) (diametro * 0.15));

        int startY;
        if (topRow) {
            startY = 8;
        } else {
            startY = h - diametro - 8;
        }

        int visible = Math.min(numPecas, maxVisible);
        for (int i = 0; i < visible; i++) {
            int y = topRow ? (startY + i * (diametro + spacing)) : (startY - i * (diametro + spacing));
            int x = (w - diametro) / 2;

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval(x + 2, y + 2, diametro, diametro);

            Paint old = g2.getPaint();
            Color base = corPeca != null ? corPeca : Color.GRAY;
            Color highlight = base.brighter();
            RadialGradientPaint rgp = new RadialGradientPaint(
                    new Point(x + diametro / 3, y + diametro / 3),
                    diametro,
                    new float[]{0f, 1f},
                    new Color[]{new Color(highlight.getRed(), highlight.getGreen(), highlight.getBlue(), 220),
                            new Color(base.getRed(), base.getGreen(), base.getBlue(), 220)});
            g2.setPaint(rgp);
            g2.fillOval(x, y, diametro, diametro);
            g2.setPaint(old);

            g2.setColor(PIECE_STROKE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(x, y, diametro, diametro);
        }

        if (numPecas > visible) {
            String txt = "+" + (numPecas - visible);
            FontMetrics fm = g2.getFontMetrics(BADGE_FONT);
            int bw = Math.max(22, fm.stringWidth(txt) + 10);
            int bh = Math.max(18, fm.getHeight());

            int bx = w - bw - 6;
            int by = topRow ? (6) : (h - bh - 6);

            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(bx + 1, by + 1, bw, bh, 10, 10);

            g2.setColor(new Color(255, 255, 255, 230));
            g2.fillRoundRect(bx, by, bw, bh, 10, 10);

            g2.setColor(new Color(30, 30, 30));
            g2.setFont(BADGE_FONT);
            g2.drawString(txt, bx + (bw - fm.stringWidth(txt)) / 2, by + fm.getAscent() + (bh - fm.getHeight()) / 2);
        }
    }
}
