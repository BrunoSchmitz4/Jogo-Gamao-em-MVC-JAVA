package projetogamao.Model;

public enum CorPeca {
    JOGADOR1, JOGADOR2;

    public CorPeca oponente() {
        return this == JOGADOR1 ? JOGADOR2 : JOGADOR1;
    }

    public int sentido() {
        return this == JOGADOR1 ? +1 : -1;
    }
}
