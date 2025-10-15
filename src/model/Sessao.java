package model;

import model.Cliente;

public class Sessao {
    private static Cliente clienteLogado;

    public static void setClienteLogado(Cliente cliente) {
        clienteLogado = cliente;
    }

    public static Cliente getClienteLogado() {
        return clienteLogado;
    }

    public static boolean isLogado() {
        return clienteLogado != null;
    }
}
