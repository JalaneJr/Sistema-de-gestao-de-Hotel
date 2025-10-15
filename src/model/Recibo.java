package model;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Reserva;

public class Recibo {

    private final Reserva reserva;

    public Recibo(Reserva reserva) {
        this.reserva = reserva;
    }

    // Gera o texto do recibo caso queira apenas em textArea
    public String gerarTexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("🏨 LUXORY Hotel\n");
        sb.append("Recibo de Hospedagem\n\n");
        sb.append("Cliente: ").append(reserva.getCliente() != null ? reserva.getCliente().getNome() : "—").append("\n");
        sb.append("Quarto: ").append(reserva.getQuarto() != null ? reserva.getQuarto().getNumero() : "—").append("\n");
        sb.append("Tipo: ").append(reserva.getQuarto() != null ? reserva.getQuarto().getTipo() : "—").append("\n");
        sb.append("Check-in: ").append(reserva.getDataEntrada() != null ? reserva.getDataEntrada().toString() : "—").append("\n");
        sb.append("Check-out: ").append(reserva.getDataSaida() != null ? reserva.getDataSaida().toString() : "—").append("\n");
        sb.append("Valor Total: ").append(String.format("%.2f MZN", reserva.getValorTotal())).append("\n");
        sb.append("\nObrigado pela preferência!");
        return sb.toString();
    }

    // Mostra o recibo em um modal visual
    public void mostrarReciboModal() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Recibo de Hospedagem");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: white; -fx-border-color: #27ae60; -fx-border-width: 3; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label titulo = new Label("🏨 LUXORY Hotel");
        titulo.setFont(Font.font("Arial", 22));
        Label subtitulo = new Label("Recibo de Hospedagem");
        subtitulo.setFont(Font.font("Arial", 18));

        Label cliente = new Label("Cliente: " + (reserva.getCliente() != null ? reserva.getCliente().getNome() : "—"));
        Label quarto = new Label("Quarto: " + (reserva.getQuarto() != null ? reserva.getQuarto().getNumero() : "—"));
        Label tipo = new Label("Tipo: " + (reserva.getQuarto() != null ? reserva.getQuarto().getTipo() : "—"));
        Label checkin = new Label("Check-in: " + (reserva.getDataEntrada() != null ? reserva.getDataEntrada().toString() : "—"));
        Label checkout = new Label("Check-out: " + (reserva.getDataSaida() != null ? reserva.getDataSaida().toString() : "—"));
        Label valor = new Label(String.format("Valor Total: %.2f MZN", reserva.getValorTotal()));

        VBox infoBox = new VBox(8, cliente, quarto, tipo, checkin, checkout, valor);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Button btnImprimir = new Button("🖨️ Imprimir Recibo");
        btnImprimir.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        btnImprimir.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(stage)) {
                boolean success = job.printPage(root);
                if (success) job.endJob();
            }
        });

        root.getChildren().addAll(titulo, subtitulo, infoBox, btnImprimir);

        Scene scene = new Scene(root, 400, 350);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
