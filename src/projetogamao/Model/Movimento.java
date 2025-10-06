package projetogamao.Model;

public class Movimento {
    public static final int ORIGEM_BARRA = -1;

    private final int origem;
    private final int destino;
    private final boolean hit;

    public Movimento(int origem, int destino, boolean hit) {
        this.origem = origem;
        this.destino = destino;
        this.hit = hit;
    }

    public static Movimento entradaBarra(int destino) {
        return new Movimento(ORIGEM_BARRA, destino, false);
    }

    public int getOrigem() { return origem; }
    public int getDestino() { return destino; }
    public boolean isHit() { return hit; }
}