package projetogamao.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ViewSwing extends JFrame implements View {
    private CasaView[] casas;
    private JTextArea log;
    private JButton botaoRolarDados;

    private JPanel painelBar1Container, painelBar2Container;
    private JPanel painelBar1, painelBar2;

    private JLabel placarValor;
    private JButton botaoPontuar;

    // Cores para o botão Pontuar
    private static final Color GREEN_WEAK   = new Color(171, 222, 179);
    private static final Color GREEN_STRONG = new Color(76, 175, 80);

    public ViewSwing(int numCasas) {
        setTitle("Gamão — MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);

        Color bg = new Color(245, 241, 236);
        Color panel = new Color(252, 249, 245);
        Color border = new Color(210, 185, 160);
        Color headerBg = new Color(92, 64, 51);
        Color headerFg = new Color(255, 248, 240);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(bg);
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(headerBg);
        header.setBorder(new EmptyBorder(10, 14, 10, 14));
        JLabel titulo = new JLabel("Gamão");
        titulo.setForeground(headerFg);
        titulo.setFont(new Font("Serif", Font.BOLD, 26));

        botaoRolarDados = new JButton("🎲 Rolar dados");
        stylePrimaryButton(botaoRolarDados);

        header.add(titulo, BorderLayout.WEST);
        header.add(botaoRolarDados, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel centroWrap = new JPanel(new BorderLayout(10, 10));
        centroWrap.setOpaque(false);

        casas = new CasaView[numCasas];
        JPanel painelTabuleiro = new JPanel(new GridLayout(2, numCasas / 2, 6, 6)) {
            @Override public boolean isOpaque() { return false; }
        };

        for (int i = 0; i < numCasas; i++) {
            casas[i] = new CasaView(i);
            casas[i].setBorder(new LineBorder(new Color(0,0,0,40), 1, true));
            painelTabuleiro.add(casas[i]);
        }

        JPanel tabuleiroContainer = new JPanel(new BorderLayout());
        tabuleiroContainer.setBackground(new Color(216, 191, 170)); // moldura de madeira
        tabuleiroContainer.setBorder(new LineBorder(new Color(120, 85, 62), 4, true));
        tabuleiroContainer.add(painelTabuleiro, BorderLayout.CENTER);

        painelBar1 = boardBarPanel();
        painelBar2 = boardBarPanel();

        painelBar1Container = sideContainer("Barra Jogador 1", panel, border);
        painelBar2Container = sideContainer("Barra Jogador 2", panel, border);

        JScrollPane sc1 = new JScrollPane(painelBar1, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane sc2 = new JScrollPane(painelBar2, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sc1.setBorder(null); sc2.setBorder(null);
        painelBar1Container.add(sc1, BorderLayout.CENTER);
        painelBar2Container.add(sc2, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setOpaque(false);
        centro.add(painelBar1Container, BorderLayout.WEST);
        centro.add(tabuleiroContainer, BorderLayout.CENTER);
        centro.add(painelBar2Container, BorderLayout.EAST);

        centroWrap.add(centro, BorderLayout.CENTER);
        root.add(centroWrap, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new GridLayout(1, 2, 10, 10));
        rodape.setOpaque(false);

        JPanel painelLog = new JPanel(new BorderLayout());
        painelLog.setBackground(panel);
        painelLog.setBorder(BorderFactory.createTitledBorder(new LineBorder(border, 1, true), "Jogadas"));
        log = new JTextArea(8, 20);
        log.setEditable(false);
        log.setWrapStyleWord(true);
        log.setLineWrap(true);
        JScrollPane spLog = new JScrollPane(log);
        spLog.setBorder(new EmptyBorder(6, 6, 6, 6));
        painelLog.add(spLog, BorderLayout.CENTER);

        JPanel painelPlacar = new JPanel(new GridBagLayout());
        painelPlacar.setBackground(panel);
        painelPlacar.setBorder(BorderFactory.createTitledBorder(new LineBorder(border, 1, true), "Placar (Peças Pontuadas)"));
        JLabel placarTitulo = new JLabel("Bear-off");
        placarTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));

        placarValor = new JLabel("0  x  0");
        placarValor.setFont(new Font("SansSerif", Font.BOLD, 36));
        placarValor.setForeground(new Color(60, 60, 60));

        botaoPontuar = new JButton("🏁 Pontuar");
        styleSecondaryButton(botaoPontuar);
        botaoPontuar.setEnabled(false);
        botaoPontuar.setForeground(new Color(22, 22, 22));
        botaoPontuar.setIconTextGap(8);

        UIManager.put("Button.disabledText", new Color(33, 33, 33));
        botaoPontuar.setDisabledIcon(botaoPontuar.getIcon());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.gridx = 0; gbc.gridy = 0; painelPlacar.add(placarTitulo, gbc);
        gbc.gridy = 1; painelPlacar.add(placarValor, gbc);
        gbc.gridy = 2; painelPlacar.add(botaoPontuar, gbc);

        rodape.add(painelLog);
        rodape.add(painelPlacar);

        root.add(rodape, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private void stylePrimaryButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(52, 152, 219));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(GREEN_WEAK);
        b.setForeground(new Color(33, 33, 33));
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JPanel sideContainer(String title, Color panel, Color border) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(panel);
        container.setBorder(BorderFactory.createTitledBorder(new LineBorder(border, 1, true), title));
        container.setPreferredSize(new Dimension(180, 620));
        return container;
    }

    private JPanel boardBarPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        return p;
    }

    @Override
    public void mostrarMensagem(String mensagem) {
        log.append(mensagem + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    @Override
    public void mostrarTabuleiro(String t) { mostrarMensagem("Tabuleiro:\n" + t); }

    @Override
    public String lerEntrada() { return null; }

    public void addAcaoCasa(int index, Runnable acao) { casas[index].setOnClick(acao); }

    public void addAcaoRolarDados(ActionListener l) { botaoRolarDados.addActionListener(l); }

    public void addAcaoPontuar(ActionListener l) { botaoPontuar.addActionListener(l); }

    public void setBotaoAtivo(boolean ativo) { botaoRolarDados.setEnabled(ativo); }

    public void setPontuarAtivo(boolean ativo) {
        botaoPontuar.setEnabled(ativo);
        botaoPontuar.setBackground(ativo ? GREEN_STRONG : GREEN_WEAK);
    }

    public void atualizarCasa(int index, int numPecas, boolean jogador1) {
        Color j1 = new Color(220, 71, 71);
        Color j2 = new Color(66, 133, 244);
        casas[index].setPecas(numPecas, jogador1 ? j1 : j2);
        casas[index].setBackground(null);
        casas[index].repaint();
    }

    public void atualizarCorFundoCasa(int index, Color cor) {
        casas[index].setBackground(cor);
        casas[index].repaint();
    }

    public void destacarCasa(int index, boolean destaque) {
        Color amarelo = new Color(255, 235, 59, 110);
        casas[index].setBackground(destaque ? amarelo : null);
        casas[index].repaint();
    }

    public void atualizarBarra(int jogador, int quantidade) {
        JPanel painel = (jogador == 1) ? painelBar1 : painelBar2;
        painel.removeAll();
        CasaView casaBar = new CasaView(-1);
        casaBar.setPreferredSize(new Dimension(120, 260));
        Color jColor = (jogador == 1) ? new Color(220, 71, 71) : new Color(66, 133, 244);
        casaBar.setPecas(quantidade, jColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        painel.add(casaBar, gbc);
        painel.revalidate();
        painel.repaint();
    }

    public void atualizarPlacar(int j1, int j2) {
        placarValor.setText(j1 + "  x  " + j2);
        placarValor.repaint();
    }

    public void mostrarTelaVencedor(String vencedor, int pontJ1, int pontJ2, Runnable onJogarNovamente) {
        JDialog dialog = new JDialog(this, "Fim de Jogo", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(460, 260);
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(252, 249, 245));
        JLabel titulo = new JLabel("🏆 Vencedor: " + vencedor);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel placar = new JLabel("Placar final: " + pontJ1 + "  x  " + pontJ2); // corrigido
        placar.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JButton jogarNovamente = new JButton("🔁 Jogar novamente");
        stylePrimaryButton(jogarNovamente);
        jogarNovamente.addActionListener(e -> {
            dialog.dispose();
            if (onJogarNovamente != null) onJogarNovamente.run();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = 0; root.add(titulo, gbc);
        gbc.gridy = 1; root.add(placar, gbc);
        gbc.gridy = 2; root.add(jogarNovamente, gbc);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    public void limparLog() { log.setText(""); }
}
