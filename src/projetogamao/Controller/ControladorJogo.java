package projetogamao.Controller;

import projetogamao.Model.*;
import projetogamao.View.ViewSwing;

import java.awt.*;
import java.util.List;

public class ControladorJogo {

    private final ViewSwing view;

    private final Tabuleiro tabuleiro;
    private final EstadoJogo estado;
    private final DiceController diceController;
    private final MovementController movementController;
    private final ScoreController scoreController;

    // Cores de destaque
    private static final Color HIGHLIGHT_SEL  = new Color(255, 235, 59, 110);   // amarelo
    private static final Color HIGHLIGHT_MOVE = new Color(76, 175, 80, 110);    // verde
    private static final Color HIGHLIGHT_HIT  = new Color(156, 39, 176, 110);   // roxo

    public ControladorJogo(ViewSwing view) {
        this.view = view;
        this.tabuleiro = new Tabuleiro();
        this.estado = new EstadoJogo();
        this.diceController = new DiceController(estado);
        this.movementController = new MovementController(tabuleiro, estado);
        this.scoreController = new ScoreController(tabuleiro, estado);
    }

    public void iniciar() {
        tabuleiro.resetInicial();
        estado.setJogadorAtual(CorPeca.JOGADOR1);
        estado.limparSelecao();
        diceController.resetDados();
        atualizarTelaCompleta("Jogo iniciado. Jogador 1 começa.");

        view.addAcaoRolarDados(e -> {
            if (!diceController.podeRolar()) {
                view.mostrarMensagem("Você já rolou os dados e ainda possui movimentos.");
                return;
            }
            diceController.rolar();
            view.mostrarMensagem("Dados: " + estado.getDadosDisponiveis());
            limparHighlights();

            if (tabuleiro.temNaBarra(estado.getJogadorAtual())) {
                List<Integer> entradas = movementController.casasEntradaPossiveisDaBarra();
                if (entradas.isEmpty()) {
                    view.mostrarMensagem("Você tem peça(s) na barra, mas todas as entradas estão bloqueadas.");
                } else {
                    view.mostrarMensagem("Entradas possíveis da barra: " + entradas);
                    for (int d : entradas) view.atualizarCorFundoCasa(d, HIGHLIGHT_MOVE);
                }
            }

            atualizarBotoes();
        });

        view.addAcaoPontuar(e -> {
            ScoreController.BearOffPlan plano = scoreController.planejarBearOff();
            if (plano == null) return;

            boolean fez = scoreController.executarBearOff(plano);
            if (!fez) {
                view.mostrarMensagem("Não foi possível pontuar agora.");
                return;
            }

            diceController.consumirSequencia(plano.passos);

            posJogada("Peça pontuada (origem C" + plano.origem + ", dados " + plano.passos + ").");
        });

        for (int i = 0; i < 24; i++) {
            final int idx = i;
            view.addAcaoCasa(i, () -> onCliqueCasa(idx));
        }

        atualizarBotoes();
    }

    private void onCliqueCasa(int index) {
        CorPeca jogador = estado.getJogadorAtual();

        if (estado.temSelecao()) {
            int origem = estado.getSelecionado();

            if (origem == index) {
                estado.limparSelecao();
                limparHighlights();
                atualizarBotoes();
                return;
            }

            List<Integer> destinos = movementController.destinosValidos(origem);
            if (!destinos.contains(index)) {
                view.mostrarMensagem("Destino inválido para os dados atuais.");
                return;
            }

            List<Integer> seq = movementController.encontrarSequenciaParaDestino(origem, index);
            if (seq.isEmpty()) {
                view.mostrarMensagem("Não encontrei sequência de dados para esse destino.");
                return;
            }

            Movimento mov = movementController.montarMovimento(origem, index);
            boolean ok = movementController.tentarMover(mov);
            if (!ok) {
                view.mostrarMensagem("Não foi possível mover.");
                return;
            }

            diceController.consumirSequencia(seq);

            estado.limparSelecao();
            posJogada("Movimento realizado: " + origem + " -> " + index + " (passos " + seq + ")");
            return;
        }

        if (tabuleiro.temNaBarra(jogador)) {
                if (!movementController.ehCasaEntradaBarra(jogador, index)) {
                    view.mostrarMensagem("Você tem peças na barra. Clique numa casa de entrada válida (iluminada).");
                    return;
                }

                Integer passoUsado = movementController.encontrarPassoParaEntradaDaBarra(jogador, index);
                if (passoUsado == null) {
                    view.mostrarMensagem("Entrada inválida para os dados atuais.");
                    return;
                }

                Movimento entrada = movementController.montarEntradaBarra(index);
                if (movementController.tentarMover(entrada)) {
                    diceController.consumirPasso(passoUsado);
                    posJogada("Entrou peça da barra em C" + index + " (usou dado " + passoUsado + ")");
                } else {
                    view.mostrarMensagem("Não foi possível entrar da barra.");
                }
                return;
        }

        if (!tabuleiro.casaDoJogador(index, jogador) || tabuleiro.qtdPecas(index) == 0) {
            view.mostrarMensagem("Selecione uma casa com suas peças.");
            return;
        }
        estado.setSelecionado(index);
        desenharSelecaoEDestinos(index);
        atualizarBotoes();
    }

    private void posJogada(String msg) {
        view.mostrarMensagem(msg);
        atualizarTelaCompleta(null);
        limparHighlights();

        if (scoreController.houveVencedor()) {
            CorPeca vencedor = scoreController.getVencedor();
            int p1 = tabuleiro.getBearOff(CorPeca.JOGADOR1);
            int p2 = tabuleiro.getBearOff(CorPeca.JOGADOR2);
            view.mostrarTelaVencedor(
                vencedor == CorPeca.JOGADOR1 ? "Jogador 1" : "Jogador 2",
                p1, p2,
                this::reiniciarPartida
            );
            return;
        }

        if (!diceController.temPassosDisponiveis() || !movementController.existemMovimentosPossiveis()) {
            trocarJogador();
        } else {
            if (tabuleiro.temNaBarra(estado.getJogadorAtual())) {
                List<Integer> entradas = movementController.casasEntradaPossiveisDaBarra();
                if (!entradas.isEmpty()) {
                    view.mostrarMensagem("Ainda na barra. Entradas possíveis: " + entradas);
                    for (int d : entradas) view.atualizarCorFundoCasa(d, HIGHLIGHT_MOVE);
                }
            }
        }

        atualizarBotoes();
    }

    private void trocarJogador() {
        estado.limparSelecao();
        limparHighlights();
        estado.setJogadorAtual(estado.getJogadorAtual().oponente());
        diceController.resetDados();
        view.mostrarMensagem("Vez do " + (estado.getJogadorAtual() == CorPeca.JOGADOR1 ? "Jogador 1" : "Jogador 2") + ". Role os dados.");
        atualizarBotoes();
        atualizarTelaCompleta(null);
    }

    private void reiniciarPartida() {
        tabuleiro.resetInicial();
        estado.setJogadorAtual(CorPeca.JOGADOR1);
        estado.limparSelecao();
        diceController.resetDados();
        atualizarTelaCompleta("Nova partida iniciada. Jogador 1 começa.");
        limparHighlights();
        atualizarBotoes();
    }

    private void atualizarTelaCompleta(String extraMsg) {
        for (int i = 0; i < 24; i++) {
            int qtd = tabuleiro.qtdPecas(i);
            boolean j1 = tabuleiro.donoDaCasa(i) == CorPeca.JOGADOR1;
            view.atualizarCasa(i, qtd, j1);
        }
        view.atualizarBarra(1, tabuleiro.getBarra(CorPeca.JOGADOR1));
        view.atualizarBarra(2, tabuleiro.getBarra(CorPeca.JOGADOR2));
        view.atualizarPlacar(tabuleiro.getBearOff(CorPeca.JOGADOR1), tabuleiro.getBearOff(CorPeca.JOGADOR2));
        if (extraMsg != null) view.mostrarMensagem(extraMsg);
    }

    private void limparHighlights() {
        for (int i = 0; i < 24; i++) view.atualizarCorFundoCasa(i, null);
    }

    private void desenharSelecaoEDestinos(int origem) {
        limparHighlights();
        view.atualizarCorFundoCasa(origem, HIGHLIGHT_SEL);
        for (int d : movementController.destinosValidos(origem)) {
            Color cor = tabuleiro.casaOcupadaPorOponente(d, estado.getJogadorAtual()) && tabuleiro.qtdPecas(d) == 1
                    ? HIGHLIGHT_HIT : HIGHLIGHT_MOVE;
            view.atualizarCorFundoCasa(d, cor);
        }
    }

    private void atualizarBotoes() {
        boolean podeRolar = diceController.podeRolar();
        view.setBotaoAtivo(podeRolar);
        boolean podePontuar = scoreController.podePontuarAgora();
        view.setPontuarAtivo(podePontuar);
    }
}