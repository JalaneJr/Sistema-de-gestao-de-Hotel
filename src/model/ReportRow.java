package model;

public class ReportRow {
    private String col1;    // Nome ou descrição
    private double val1;    // Valor principal
    private double val2;    // Valor secundário (opcional, como receita)
    private String val2Str; // Valor secundário como string (para datas ou textos)

    public ReportRow() {}

    public ReportRow(String col1, double val1) {
        this.col1 = col1;
        this.val1 = val1;
    }

    public ReportRow(String col1, double val1, double val2) {
        this.col1 = col1;
        this.val1 = val1;
        this.val2 = val2;
    }

    // Getters e Setters
    public String getCol1() { return col1; }
    public void setCol1(String col1) { this.col1 = col1; }

    public double getVal1() { return val1; }
    public void setVal1(double val1) { this.val1 = val1; }

    public double getVal2() { return val2; }
    public void setVal2(double val2) { this.val2 = val2; }

    public String getVal2Str() { return val2Str; }
    public void setVal2Str(String val2Str) { this.val2Str = val2Str; }

    // 🔹 Retorna o objeto formatado como linha de CSV
    public String toCSV() {
        return String.format("%s,%.2f,%s",
                col1 != null ? col1 : "",
                val1,
                val2Str != null ? val2Str : String.format("%.2f", val2));
    }

    // 🔹 Representação legível para debug/logs
    @Override
    public String toString() {
        return String.format("ReportRow[col1='%s', val1=%.2f, val2=%.2f, val2Str='%s']",
                col1, val1, val2, val2Str);
    }
}
