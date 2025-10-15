package model;

import java.time.LocalDateTime;

public class Hospedagem {
    private int id;
    private Reserva reserva;
    private Cliente cliente;
    private Quarto quarto;
    private LocalDateTime dataCheckin;
    private LocalDateTime dataCheckout;
    private String status;

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }

    public LocalDateTime getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(LocalDateTime dataCheckin) { this.dataCheckin = dataCheckin; }

    public LocalDateTime getDataCheckout() { return dataCheckout; }
    public void setDataCheckout(LocalDateTime dataCheckout) { this.dataCheckout = dataCheckout; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
