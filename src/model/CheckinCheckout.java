package model;

import java.time.LocalDateTime;

public class CheckinCheckout {

    private int id;
    private Reserva reserva;
    private LocalDateTime dataCheckin;
    private LocalDateTime dataCheckout;
    private String status; // "Em andamento", "Finalizada", etc.

    public CheckinCheckout() {
    }

    public CheckinCheckout(int id, Reserva reserva, LocalDateTime dataCheckin, LocalDateTime dataCheckout, String status) {
        this.id = id;
        this.reserva = reserva;
        this.dataCheckin = dataCheckin;
        this.dataCheckout = dataCheckout;
        this.status = status;
    }

    // ✅ GETTERS e SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public LocalDateTime getDataCheckin() {
        return dataCheckin;
    }

    public void setDataCheckin(LocalDateTime dataCheckin) {
        this.dataCheckin = dataCheckin;
    }

    public LocalDateTime getDataCheckout() {
        return dataCheckout;
    }

    public void setDataCheckout(LocalDateTime dataCheckout) {
        this.dataCheckout = dataCheckout;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 🔹 Métodos auxiliares
    public boolean isEmAndamento() {
        return "Em andamento".equalsIgnoreCase(status);
    }

    public boolean isFinalizada() {
        return "Finalizada".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "CheckinCheckout{" +
                "id=" + id +
                ", reserva=" + (reserva != null ? reserva.getId() : "—") +
                ", checkin=" + dataCheckin +
                ", checkout=" + dataCheckout +
                ", status='" + status + '\'' +
                '}';
    }
}
