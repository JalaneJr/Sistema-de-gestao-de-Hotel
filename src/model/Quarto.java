package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Quarto {
    private IntegerProperty id;
    private IntegerProperty numero;
    private StringProperty tipo;
    private DoubleProperty preco;
    private StringProperty descricao;
    private StringProperty ofertas;
    private StringProperty imagem;
    private StringProperty status; // ✅ Novo campo

    // Construtor completo (quando já temos o ID do banco)
    public Quarto(int id, int numero, String tipo, double preco, String descricao,
                  String ofertas, String imagem, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.numero = new SimpleIntegerProperty(numero);
        this.tipo = new SimpleStringProperty(tipo);
        this.preco = new SimpleDoubleProperty(preco);
        this.descricao = new SimpleStringProperty(descricao);
        this.ofertas = new SimpleStringProperty(ofertas);
        this.imagem = new SimpleStringProperty(imagem);
        this.status = new SimpleStringProperty(status);
    }

    // Construtor sem ID (para novos quartos antes de salvar no BD)
    public Quarto(int numero, String tipo, double preco, String descricao,
                  String ofertas, String imagem, String status) {
        this.id = new SimpleIntegerProperty(0); // será atualizado depois pelo BD
        this.numero = new SimpleIntegerProperty(numero);
        this.tipo = new SimpleStringProperty(tipo);
        this.preco = new SimpleDoubleProperty(preco);
        this.descricao = new SimpleStringProperty(descricao);
        this.ofertas = new SimpleStringProperty(ofertas);
        this.imagem = new SimpleStringProperty(imagem);
        this.status = new SimpleStringProperty(status);
    }

    // Construtor vazio funcional
    public Quarto() {
        this.id = new SimpleIntegerProperty(0);
        this.numero = new SimpleIntegerProperty(0);
        this.tipo = new SimpleStringProperty("");
        this.preco = new SimpleDoubleProperty(0.0);
        this.descricao = new SimpleStringProperty("");
        this.ofertas = new SimpleStringProperty("");
        this.imagem = new SimpleStringProperty("");
        this.status = new SimpleStringProperty("livre");
    }

    // Getters e setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public int getNumero() { return numero.get(); }
    public void setNumero(int numero) { this.numero.set(numero); }
    public IntegerProperty numeroProperty() { return numero; }

    public String getTipo() { return tipo.get(); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public StringProperty tipoProperty() { return tipo; }

    public double getPreco() { return preco.get(); }
    public void setPreco(double preco) { this.preco.set(preco); }
    public DoubleProperty precoProperty() { return preco; }

    public String getDescricao() { return descricao.get(); }
    public void setDescricao(String descricao) { this.descricao.set(descricao); }
    public StringProperty descricaoProperty() { return descricao; }

    public String getOfertas() { return ofertas.get(); }
    public void setOfertas(String ofertas) { this.ofertas.set(ofertas); }
    public StringProperty ofertasProperty() { return ofertas; }

    public String getImagem() { return imagem.get(); }
    public void setImagem(String imagem) { this.imagem.set(imagem); }
    public StringProperty imagemProperty() { return imagem; }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }

    @Override
    public String toString() {
        return "Quarto{" +
                "id=" + getId() +
                ", numero=" + getNumero() +
                ", tipo='" + getTipo() + '\'' +
                ", preco=" + getPreco() +
                ", descricao='" + getDescricao() + '\'' +
                ", ofertas='" + getOfertas() + '\'' +
                ", imagem='" + getImagem() + '\'' +
                ", status='" + getStatus() + '\'' +
                '}';
    }
}
