package projetogamao.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstadoJogo {
    private CorPeca jogadorAtual = CorPeca.JOGADOR1;
    private boolean jaRolou = false;
    private List<Integer> dadosDisponiveis = new ArrayList<>();
    private Integer selecionado = null;

    public CorPeca getJogadorAtual() { return jogadorAtual; }
    public void setJogadorAtual(CorPeca j) { this.jogadorAtual = j; }

    public boolean isJaRolou() { return jaRolou; }
    public void setJaRolou(boolean v) { this.jaRolou = v; }

    public List<Integer> getDadosDisponiveis() { return Collections.unmodifiableList(dadosDisponiveis); }
    public void setDadosDisponiveis(List<Integer> ds) {
        this.dadosDisponiveis = new ArrayList<>(ds);
        this.jaRolou = !this.dadosDisponiveis.isEmpty();
    }

    public boolean temSelecao() { return selecionado != null; }
    public int getSelecionado() { return selecionado == null ? -1 : selecionado; }
    public void setSelecionado(int i) { this.selecionado = i; }
    public void limparSelecao() { this.selecionado = null; }
}
