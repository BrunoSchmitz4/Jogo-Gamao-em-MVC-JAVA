package projetogamao.Model;

import java.util.Random;

public class Dado {
    private final Random rng = new Random();
    public int rolar() { return 1 + rng.nextInt(6); }
}
