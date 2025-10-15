package model;

public class Funcionario extends Pessoa {
    private String cargo;
    private double salario;
    private boolean ativo; // novo campo

    public Funcionario() {}

    public Funcionario(int id, String nome, String documento, String telefone, String email,
                       String cargo, double salario, boolean ativo) {
        super(id, nome, documento, telefone, email);
        this.cargo = cargo;
        this.salario = salario;
        this.ativo = ativo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getSalario() {
        return (int) salario;
    }

    public void setSalario(int salario) {
        this.salario = salario;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
