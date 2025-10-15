package model;

public class Cliente extends Pessoa {
    private String preferencia;
    private String cadastradoPor; // "FUNCIONARIO" ou "CLIENTE"

    public Cliente() {}

    public Cliente(int id, String nome, String documento, String telefone, String email, String preferencia) {
        super(id, nome, documento, telefone, email);
        this.preferencia = preferencia;
    }

    // Preferência
    public String getPreferencia() {
        return preferencia;
    }

    public void setPreferencia(String preferencia) {
        this.preferencia = preferencia;
    }

    // 🔹 Indica quem cadastrou o cliente (FUNCIONARIO ou CLIENTE)
    public String getCadastradoPor() {
        return cadastradoPor;
    }

    public void setCadastradoPor(String cadastradoPor) {
        this.cadastradoPor = cadastradoPor;
    }
}
