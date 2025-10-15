package model;

public class Tarifa {
    private int id;
    private String tipoQuarto;
    private double preco;
    private boolean promocao;
    private String descricao; // Novo campo para descrição da tarifa ou promoção

    public Tarifa() {}

    public Tarifa(int id, String tipoQuarto, double preco, boolean promocao, String descricao) {
        this.id = id;
        this.tipoQuarto = tipoQuarto;
        this.preco = preco;
        this.promocao = promocao;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoQuarto() {
        return tipoQuarto;
    }

    public void setTipoQuarto(String tipoQuarto) {
        this.tipoQuarto = tipoQuarto;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public boolean isPromocao() {
        return promocao;
    }

    public void setPromocao(boolean promocao) {
        this.promocao = promocao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
