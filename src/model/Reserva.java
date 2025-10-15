package model;

import java.time.LocalDate;

public class Reserva {
    private int id;
    private Cliente cliente;
    private Quarto quarto;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private double valorTotal;
    private String servicos; // serviços adicionais
    private String status;   // "Ativa", "Cancelada", "Finalizada"

    public Reserva() {
        this.status = "Ativa";
    }

    // Construtor completo
    public Reserva(int id, Cliente cliente, Quarto quarto,
                   LocalDate dataEntrada, LocalDate dataSaida,
                   double valorTotal, String servicos, String status) {
        this.id = id;
        this.cliente = cliente;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.valorTotal = valorTotal;
        this.servicos = servicos;
        this.status = status != null ? status : "Ativa";
    }

    // Construtor sem ID (para novas reservas)
    public Reserva(Cliente cliente, Quarto quarto,
                   LocalDate dataEntrada, LocalDate dataSaida,
                   double valorTotal, String servicos) {
        this.cliente = cliente;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.valorTotal = valorTotal;
        this.servicos = servicos;
        this.status = "Ativa";
    }

    // ✅ Corrigido — usado pela RoomsView
    public Reserva(Cliente cliente, Quarto quarto, LocalDate in, LocalDate out, double total) {
        this.cliente = cliente;
        this.quarto = quarto;
        this.dataEntrada = in;
        this.dataSaida = out;
        this.valorTotal = total;
        this.servicos = "";
        this.status = "Ativa";
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }

    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDate getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDate dataSaida) { this.dataSaida = dataSaida; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getServicos() { return servicos; }
    public void setServicos(String servicos) { this.servicos = servicos; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Métodos compatíveis com RoomsView
    public LocalDate getCheckin() { return dataEntrada; }
    public void setCheckin(LocalDate checkin) { this.dataEntrada = checkin; }

    public LocalDate getCheckout() { return dataSaida; }
    public void setCheckout(LocalDate checkout) { this.dataSaida = checkout; }

    // Auxiliares para integração com DAO
    public void setClienteId(int id) {
        if (this.cliente == null) this.cliente = new Cliente();
        this.cliente.setId(id);
    }

    public void setQuartoId(int id) {
        if (this.quarto == null) this.quarto = new Quarto();
        this.quarto.setId(id);
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", cliente=" + (cliente != null ? cliente.getNome() : "null") +
                ", quarto=" + (quarto != null ? quarto.getNumero() : "null") +
                ", dataEntrada=" + dataEntrada +
                ", dataSaida=" + dataSaida +
                ", valorTotal=" + valorTotal +
                ", servicos='" + servicos + '\'' +
                ", status='" + status + '\'' +
                '}';
    }


}
