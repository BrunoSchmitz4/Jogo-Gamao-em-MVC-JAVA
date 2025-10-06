package projetogamao.Controller;

import projetogamao.Model.*;

import java.util.*;

public class MovementController {

    private final Tabuleiro tabuleiro;
    private final EstadoJogo estado;

    public MovementController(Tabuleiro tabuleiro, EstadoJogo estado) {
        this.tabuleiro = tabuleiro;
        this.estado = estado;
    }
    
    public List<Integer> destinosValidos(int origem) {
        Set<Integer> res = new LinkedHashSet<>();
        if (tabuleiro.qtdPecas(origem) == 0 || !tabuleiro.casaDoJogador(origem, estado.getJogadorAtual())) {
            return List.copyOf(res);
        }

        List<Integer> dados = estado.getDadosDisponiveis();
        if (dados.isEmpty()) return List.copyOf(res);

        List<List<Integer>> sequencias = gerarSequenciasDeDados(dados);

        for (List<Integer> seq : sequencias) {
            Integer destino = simularSequencia(origem, seq, estado.getJogadorAtual());
            if (destino != null && destino >= 0 && destino < 24) {
                res.add(destino);
            }
        }
        return List.copyOf(res);
    }
    
    public List<Integer> encontrarSequenciaParaDestino(int origem, int destino) {
        List<Integer> dados = estado.getDadosDisponiveis();
        List<List<Integer>> sequencias = gerarSequenciasDeDados(dados);

        for (List<Integer> seq : sequencias) {
            Integer dest = simularSequencia(origem, seq, estado.getJogadorAtual());
            if (dest != null && dest == destino) return seq;
        }
        return List.of();
    }

    private List<List<Integer>> gerarSequenciasDeDados(List<Integer> dados) {
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

        List<Integer> base = new ArrayList<>(dados);

        for (int i = 0; i < base.size(); i++) res.add(List.of(base.get(i)));

        if (base.size() >= 2) {
            for (int i = 0; i < base.size(); i++) {
                for (int j = 0; j < base.size(); j++) {
                    if (j == i) continue;
                    res.add(List.of(base.get(i), base.get(j)));
                }
            }
        }
        if (base.size() == 3) {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    for (int k = 0; k < 3; k++)
                        if (i != j && j != k && i != k)
                            res.add(List.of(base.get(i), base.get(j), base.get(k)));
        }
        return res.size() > 200 ? res.subList(0, 200) : res;
    }

    private Integer simularSequencia(int origem, List<Integer> passos, CorPeca j) {
        int atual = origem;

        for (int p : passos) {
            int prox = tabuleiro.avancarIndice(atual, p, j);

            if (prox == Tabuleiro.BEAR_OFF) {
                return null;
            }
            if (!tabuleiro.podeOcupar(prox, j)) {
                return null;
            }

            atual = prox;
        }
        return atual;
    }

    public Movimento montarMovimento(int origem, int destino) {
        boolean hit = tabuleiro.casaOcupadaPorOponente(destino, estado.getJogadorAtual()) && tabuleiro.qtdPecas(destino) == 1;
        return new Movimento(origem, destino, hit);
    }

    public boolean tentarMover(Movimento movimento) {
        CorPeca j = estado.getJogadorAtual();
        return tabuleiro.aplicarMovimento(movimento, j);
    }

    public int distanciaPasso(int origem, int destino, CorPeca j) {
        return tabuleiro.distanciaNoCaminho(origem, destino, j);
    }

    public boolean ehCasaEntradaBarra(CorPeca j, int casaIndex) {
        for (int p : estado.getDadosDisponiveis()) {
            int d = tabuleiro.destinoEntradaBarra(j, p);
            if (d == casaIndex && tabuleiro.podeOcupar(d, j)) return true;
        }
        return false;
    }

    public Movimento montarEntradaBarra(int destino) {
        return Movimento.entradaBarra(destino);
    }

    public int passoEntradaBarra(CorPeca j, int destino) {
        return tabuleiro.passoEntradaBarra(j, destino);
    }

    public List<Integer> casasEntradaPossiveisDaBarra() {
        CorPeca j = estado.getJogadorAtual();
        Set<Integer> res = new LinkedHashSet<>();
        for (int passo : estado.getDadosDisponiveis()) {
            int dest = tabuleiro.destinoEntradaBarra(j, passo);
            if (dest >= 0 && tabuleiro.podeOcupar(dest, j)) res.add(dest);
        }
        return List.copyOf(res);
    }

    public boolean existemMovimentosPossiveis() {
        CorPeca j = estado.getJogadorAtual();
        if (tabuleiro.temNaBarra(j)) {
            return tabuleiro.podeEntrarDaBarra(j, estado.getDadosDisponiveis());
        }
        for (int i = 0; i < 24; i++) {
            if (tabuleiro.casaDoJogador(i, j) && tabuleiro.qtdPecas(i) > 0) {
                if (!destinosValidos(i).isEmpty()) return true;
                for (int passo : estado.getDadosDisponiveis()) {
                    if (tabuleiro.podeBearOffDe(i, j, passo)) return true;
                }
            }
        }
        return false;
    }
    
    public Integer encontrarPassoParaEntradaDaBarra(CorPeca j, int destino) {
        for (int passo : estado.getDadosDisponiveis()) {
            int d = tabuleiro.destinoEntradaBarra(j, passo);
            if (d == destino && tabuleiro.podeOcupar(d, j)) return passo;
        }
        return null;
    }
}