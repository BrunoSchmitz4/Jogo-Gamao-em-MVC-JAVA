package projetogamao.Controller;

import projetogamao.Model.*;

import java.util.*;


public class ScoreController {

    public static class BearOffPlan {
        public final int origem;
        public final List<Integer> passos;
        public BearOffPlan(int origem, List<Integer> passos) {
            this.origem = origem;
            this.passos = passos;
        }
        @Override public String toString() { return "origem=" + origem + ", passos=" + passos; }
    }

    private final Tabuleiro tabuleiro;
    private final EstadoJogo estado;

    public ScoreController(Tabuleiro tabuleiro, EstadoJogo estado) {
        this.tabuleiro = tabuleiro;
        this.estado = estado;
    }
    
    private boolean canBearOffFromPosWithDie(int idxAtual, CorPeca j, int passo) {
        int pos = tabuleiro.posicaoNoCaminho(idxAtual, j);
        if (pos < 0) return false;
        int destinoPos = pos + passo;
        if (destinoPos == 24) return true;
        if (destinoPos > 24) return !tabuleiro.existePecaAFrenteNoHomePos(j, pos);
        return false;
    }
    
    public boolean podePontuarAgora() {
        return planejarBearOff() != null;
    }

    public BearOffPlan planejarBearOff() {
        CorPeca j = estado.getJogadorAtual();
        List<Integer> dados = estado.getDadosDisponiveis();
        if (!estado.isJaRolou() || dados.isEmpty()) return null;
        if (tabuleiro.temNaBarra(j)) return null;
        if (!tabuleiro.todosNoHome(j)) return null;

        BearOffPlan p1 = planejarComUmDado(j, dados, true);
        if (p1 != null) return p1;
        BearOffPlan p1Maior = planejarComUmDado(j, dados, false);
        if (p1Maior != null) return p1Maior;

        return planejarComSequencia(j, dados);
    }

    public boolean executarBearOff(BearOffPlan plano) {
        return tabuleiro.removerParaBearOff(plano.origem, estado.getJogadorAtual());
    }

    public boolean houveVencedor() {
        int t1 = tabuleiro.getTotalInicial(CorPeca.JOGADOR1);
        int t2 = tabuleiro.getTotalInicial(CorPeca.JOGADOR2);

        boolean j1Venceu = (t1 > 0) && (tabuleiro.getBearOff(CorPeca.JOGADOR1) >= t1);
        boolean j2Venceu = (t2 > 0) && (tabuleiro.getBearOff(CorPeca.JOGADOR2) >= t2);

        return j1Venceu || j2Venceu;
    }

    public CorPeca getVencedor() {
        int t1 = tabuleiro.getTotalInicial(CorPeca.JOGADOR1);
        int t2 = tabuleiro.getTotalInicial(CorPeca.JOGADOR2);

        if ((t1 > 0) && (tabuleiro.getBearOff(CorPeca.JOGADOR1) >= t1)) return CorPeca.JOGADOR1;
        if ((t2 > 0) && (tabuleiro.getBearOff(CorPeca.JOGADOR2) >= t2)) return CorPeca.JOGADOR2;

        return (tabuleiro.getBearOff(CorPeca.JOGADOR1) >= tabuleiro.getBearOff(CorPeca.JOGADOR2))
                ? CorPeca.JOGADOR1 : CorPeca.JOGADOR2;
    }

    private BearOffPlan planejarComUmDado(CorPeca j, List<Integer> dados, boolean apenasExato) {

        int melhorOrigem = -1;
        int melhorPasso = -1;

        for (int i = 0; i < 24; i++) {
            if (!tabuleiro.casaDoJogador(i, j) || tabuleiro.qtdPecas(i) == 0) continue;
            for (int passo : dados) {
                if (!canBearOffFromPosWithDie(i, j, passo)) continue;
                boolean exato = (tabuleiro.posicaoNoCaminho(i, j) + passo) == 24;
                if (apenasExato && !exato) continue;

                if (melhorOrigem == -1) { melhorOrigem = i; melhorPasso = passo; }
                else if (!apenasExato && passo > melhorPasso) { melhorOrigem = i; melhorPasso = passo; }

                if (exato) return new BearOffPlan(melhorOrigem, List.of(melhorPasso));
            }
        }
        if (melhorOrigem != -1) return new BearOffPlan(melhorOrigem, List.of(melhorPasso));
        return null;
    }

    private BearOffPlan planejarComSequencia(CorPeca j, List<Integer> dados) {
        List<List<Integer>> seqs = gerarSequencias(dados);
        for (int origem = 0; origem < 24; origem++) {
            if (!tabuleiro.casaDoJogador(origem, j) || tabuleiro.qtdPecas(origem) == 0) continue;

            for (List<Integer> seq : seqs) {
                if (seq.size() < 2) continue;

                Integer posAtual = origem;
                boolean valido = true;

                for (int k = 0; k < seq.size() - 1; k++) {
                    int p = seq.get(k);
                    int prox = tabuleiro.avancarIndice(posAtual, p, j);
                    if (prox == Tabuleiro.BEAR_OFF) { valido = false; break; }
                    if (!tabuleiro.podeOcupar(prox, j)) { valido = false; break; }

                    posAtual = prox;
                }
                if (!valido) continue;

                int ultimo = seq.get(seq.size() - 1);
                if (canBearOffFromPosWithDie(posAtual, j, ultimo)) {
                    return new BearOffPlan(origem, seq);
                }
            }
        }
        return null;
    }

    private List<List<Integer>> gerarSequencias(List<Integer> dados) {
        List<List<Integer>> res = new ArrayList<>();
        boolean doubles = dados.size() >= 2 && new HashSet<>(dados).size() == 1;

        if (doubles) {
            int v = dados.get(0);
            for (int k = 1; k <= dados.size(); k++) {
                List<Integer> seq = new ArrayList<>();
                for (int i = 0; i < k; i++) seq.add(v);
                res.add(seq);
            }
            return res;
        }

        int n = Math.min(dados.size(), 4);
        for (int i = 0; i < dados.size(); i++) res.add(List.of(dados.get(i)));
        if (dados.size() >= 2) {
            for (int i = 0; i < dados.size(); i++)
                for (int j = 0; j < dados.size(); j++)
                    if (j != i) res.add(List.of(dados.get(i), dados.get(j)));
        }
        if (dados.size() >= 3) {
            for (int i = 0; i < dados.size(); i++)
                for (int j = 0; j < dados.size(); j++)
                    for (int k = 0; k < dados.size(); k++)
                        if (i != j && j != k && i != k)
                            res.add(List.of(dados.get(i), dados.get(j), dados.get(k)));
        }

        return res.size() > 300 ? res.subList(0, 300) : res;
    }
    
}
