package projetogamao.Controller;

import projetogamao.Model.Dado;
import projetogamao.Model.EstadoJogo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiceController {
    private final EstadoJogo estado;
    private final Dado d = new Dado();

    public DiceController(EstadoJogo estado) {
        this.estado = estado;
    }

    public void resetDados() {
        estado.setDadosDisponiveis(Collections.emptyList());
        estado.setJaRolou(false);
    }

    public boolean podeRolar() {
        return !estado.isJaRolou() || estado.getDadosDisponiveis().isEmpty();
    }

    public void rolar() {
        int a = d.rolar();
        int b = d.rolar();
        List<Integer> passos = new ArrayList<>();
        if (a == b) {
            passos.add(a); passos.add(a); passos.add(a); passos.add(a);
        } else {
            passos.add(a); passos.add(b);
        }
        estado.setDadosDisponiveis(passos);
        estado.setJaRolou(true);
    }

    public boolean temPassosDisponiveis() {
        return !estado.getDadosDisponiveis().isEmpty();
    }

    public void consumirPasso(int passo) {
        List<Integer> ds = new ArrayList<>(estado.getDadosDisponiveis());
        int idx = ds.indexOf(passo);
        if (idx >= 0) {
            ds.remove(idx);
            estado.setDadosDisponiveis(ds);
        }
    }

    public void consumirSequencia(List<Integer> passos) {
        List<Integer> ds = new ArrayList<>(estado.getDadosDisponiveis());

        for (int p : passos) {
            if (!ds.contains(p)) return;
        }

        for (int p : passos) {
            int idx = ds.indexOf(p);
            ds.remove(idx);
        }
        estado.setDadosDisponiveis(ds);
    }

    public void consumirPassoParaBearOff(int passoExatoOuMaior) {
        List<Integer> ds = new ArrayList<>(estado.getDadosDisponiveis());
        int idx = ds.indexOf(passoExatoOuMaior);
        if (idx >= 0) {
            ds.remove(idx);
        } else if (!ds.isEmpty()) {
            int maxIdx = 0;
            for (int i = 1; i < ds.size(); i++) if (ds.get(i) > ds.get(maxIdx)) maxIdx = i;
            ds.remove(maxIdx);
        }
        estado.setDadosDisponiveis(ds);
    }
}