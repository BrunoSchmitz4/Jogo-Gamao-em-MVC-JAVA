package projetogamao.Model;

public class Ponto {
    private int quantidade;
    private CorPeca dono;

    public int getQuantidade() { return quantidade; }
    public CorPeca getDono() { return dono; }

    public void set(CorPeca dono, int quantidade) {
        this.dono = quantidade > 0 ? dono : null;
        this.quantidade = quantidade;
    }

    public void add(CorPeca j, int delta) {
        quantidade += delta;
        if (quantidade <= 0) {
            quantidade = 0;
            dono = null;
        } else {
            dono = j;
        }
    }
}
