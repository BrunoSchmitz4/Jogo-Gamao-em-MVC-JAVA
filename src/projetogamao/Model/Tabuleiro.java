package projetogamao.Model;

import java.util.List;

public class Tabuleiro {
    public static final int BEAR_OFF = 100;

    private final Ponto[] casas = new Ponto[24];
    private int barraJ1 = 0, barraJ2 = 0;
    private int bearOffJ1 = 0, bearOffJ2 = 0;

    private int totalInicialJ1 = 0, totalInicialJ2 = 0;

    private static final int[] PATH_J1;
    private static final int[] PATH_J2;

    static {
        PATH_J1 = new int[24];
        int k = 0;
        for (int i = 11; i >= 0; i--) PATH_J1[k++] = i;
        for (int i = 12; i <= 23; i++) PATH_J1[k++] = i;

        PATH_J2 = new int[24];
        k = 0;
        for (int i = 23; i >= 12; i--) PATH_J2[k++] = i;
        for (int i = 0; i <= 11; i++) PATH_J2[k++] = i;
    }

    public Tabuleiro() {
        for (int i = 0; i < 24; i++) casas[i] = new Ponto();
    }

    public void resetInicial() {
        for (int i = 0; i < 24; i++) casas[i].set(null, 0);
        barraJ1 = barraJ2 = 0;
        bearOffJ1 = bearOffJ2 = 0;
        
        // Instância Player 1
         casas[0].set(CorPeca.JOGADOR1, 2);
         casas[11].set(CorPeca.JOGADOR1, 5);
         casas[19].set(CorPeca.JOGADOR1, 3);
        casas[17].set(CorPeca.JOGADOR1, 5);

        // Instância Player 2
         casas[12].set(CorPeca.JOGADOR2, 2);
         casas[23].set(CorPeca.JOGADOR2, 5);
        casas[7].set(CorPeca.JOGADOR2, 3);
         casas[5].set(CorPeca.JOGADOR2, 5);
        recalcularTotaisIniciais();
    }

    public int qtdPecas(int idx) { return casas[idx].getQuantidade(); }
    public CorPeca donoDaCasa(int idx) { return casas[idx].getDono(); }

    public boolean casaDoJogador(int idx, CorPeca j) {
        return casas[idx].getQuantidade() > 0 && j == casas[idx].getDono();
    }

    public boolean casaOcupadaPorOponente(int idx, CorPeca j) {
        return casas[idx].getQuantidade() > 0 && casas[idx].getDono() == j.oponente();
    }

    public int getBarra(CorPeca j) { return j == CorPeca.JOGADOR1 ? barraJ1 : barraJ2; }
    public int getBearOff(CorPeca j) { return j == CorPeca.JOGADOR1 ? bearOffJ1 : bearOffJ2; }
    public boolean temNaBarra(CorPeca j) { return getBarra(j) > 0; }

    private int[] path(CorPeca j) { return j == CorPeca.JOGADOR1 ? PATH_J1 : PATH_J2; }

    private int posNoCaminho(int idx, CorPeca j) {
        int[] p = path(j);
        for (int i = 0; i < 24; i++) if (p[i] == idx) return i;
        return -1;
    }

    public int avancarIndice(int origemIdx, int passos, CorPeca j) {
        int pos = posNoCaminho(origemIdx, j);
        if (pos < 0) return -1;
        int destinoPos = pos + passos;
        if (destinoPos >= 24) return BEAR_OFF;
        return path(j)[destinoPos];
    }

    public int distanciaNoCaminho(int origemIdx, int destinoIdx, CorPeca j) {
        int a = posNoCaminho(origemIdx, j);
        int b = posNoCaminho(destinoIdx, j);
        if (a < 0 || b < 0) return -1;
        return b - a;
    }

    public boolean podeOcupar(int destino, CorPeca j) {
        int qtd = casas[destino].getQuantidade();
        if (qtd == 0) return true;
        if (casas[destino].getDono() == j) return true;
        return qtd == 1;
    }

    public boolean aplicarMovimento(Movimento mov, CorPeca j) {
        if (mov.getOrigem() == Movimento.ORIGEM_BARRA) {
            if (getBarra(j) <= 0) return false;
            if (mov.getDestino() == BEAR_OFF) return false;
            if (!podeOcupar(mov.getDestino(), j)) return false;

            if (casaOcupadaPorOponente(mov.getDestino(), j) && casas[mov.getDestino()].getQuantidade() == 1) {
                if (casas[mov.getDestino()].getDono() == CorPeca.JOGADOR1) barraJ1++; else barraJ2++;
                casas[mov.getDestino()].set(null, 0);
            }

            casas[mov.getDestino()].add(j, +1);

            if (j == CorPeca.JOGADOR1) barraJ1--; else barraJ2--;
            return true;
        }

        int origem = mov.getOrigem();
        int destino = mov.getDestino();
        if (destino == BEAR_OFF) return false;

        if (!casaDoJogador(origem, j)) return false;
        if (!podeOcupar(destino, j)) return false;

        casas[origem].add(j, -1);


        if (casaOcupadaPorOponente(destino, j) && casas[destino].getQuantidade() == 1) {
            if (casas[destino].getDono() == CorPeca.JOGADOR1) barraJ1++; else barraJ2++;
            casas[destino].set(null, 0);
        }

        casas[destino].add(j, +1);
        return true;
    }

    public boolean podeEntrarDaBarra(CorPeca j, List<Integer> dados) {
        for (int p : dados) {
            int d = destinoEntradaBarra(j, p);
            if (d >= 0 && podeOcupar(d, j)) return true;
        }
        return false;
    }

    public int destinoEntradaBarra(CorPeca j, int passo) {
        if (passo < 1 || passo > 6) return -1;
        int[] p = path(j);
        return p[passo - 1];
    }

    public int passoEntradaBarra(CorPeca j, int destino) {
        int pos = posNoCaminho(destino, j);
        return (pos >= 0 && pos < 6) ? (pos + 1) : -1;
    }

    public boolean todosNoHome(CorPeca j) {
        if (temNaBarra(j)) return false;
        for (int i = 0; i < 24; i++) {
            if (casaDoJogador(i, j)) {
                int pos = posNoCaminho(i, j);
                if (pos < 18) return false;
            }
        }
        return true;
    }

    public boolean podeBearOffDe(int origem, CorPeca j, int passo) {
        if (!todosNoHome(j)) return false;
        if (!casaDoJogador(origem, j)) return false;

        int pos = posNoCaminho(origem, j);
        if (pos < 0) return false;

        int destinoPos = pos + passo;
        if (destinoPos == 24) {
            return true;
        }
        if (destinoPos > 24) {
            return !existePecaAFrenteNoHome(j, pos);
        }
        return false;
    }

    public boolean passoExatoParaBearOff(int origem, CorPeca j, int passo) {
        int pos = posNoCaminho(origem, j);
        return pos >= 0 && (pos + passo) == 24;
    }

    public boolean removerParaBearOff(int origem, CorPeca j) {
        if (!casaDoJogador(origem, j) || casas[origem].getQuantidade() == 0) return false;
        casas[origem].add(j, -1);
        if (j == CorPeca.JOGADOR1) bearOffJ1++; else bearOffJ2++;
        return true;
    }

   private boolean existePecaAFrenteNoHome(CorPeca j, int posOrigem) {

       int inicio = Math.max(posOrigem + 1, 18);
       for (int pos = inicio; pos <= 23; pos++) {
           int idx = path(j)[pos];
           if (casaDoJogador(idx, j)) return true;
       }
       return false;
   }

    private void recalcularTotaisIniciais() {
        int t1 = 0, t2 = 0;
        for (int i = 0; i < 24; i++) {
            CorPeca dono = casas[i].getDono();
            int q = casas[i].getQuantidade();
            if (dono == CorPeca.JOGADOR1) t1 += q;
            else if (dono == CorPeca.JOGADOR2) t2 += q;
        }
        totalInicialJ1 = t1;
        totalInicialJ2 = t2;
    }

    public int getTotalInicial(CorPeca j) {
        return j == CorPeca.JOGADOR1 ? totalInicialJ1 : totalInicialJ2;
    }

    public int totalEmJogo(CorPeca j) {
        int tot = getBarra(j);
        for (int i = 0; i < 24; i++) {
            if (casas[i].getDono() == j) tot += casas[i].getQuantidade();
        }
        return tot;
    }
    
    public int posicaoNoCaminho(int idx, CorPeca j) {
        return posNoCaminho(idx, j);
    }

    public boolean existePecaAFrenteNoHomePos(CorPeca j, int posOrigem) {
        int inicio = Math.max(posOrigem + 1, 18);
        for (int pos = inicio; pos <= 23; pos++) {
            int idx = path(j)[pos];
            if (casaDoJogador(idx, j)) return true;
        }
        return false;
    }
}
