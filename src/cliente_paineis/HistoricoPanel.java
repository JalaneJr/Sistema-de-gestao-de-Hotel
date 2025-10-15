package cliente_paineis;

import dao.ReservaDAO;
import model.Cliente;
import model.Reserva;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HistoricoPanel extends VBox {

    private Cliente clienteLogado;
    private VBox reservasList;

    public HistoricoPanel(Cliente clienteLogado) {
        this.clienteLogado = clienteLogado;
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f4f6f7;");

        Label titulo = new Label("Histórico de Reservas de " + clienteLogado.getNome());
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        reservasList = new VBox(20);
        reservasList.setPadding(new Insets(10));

        ScrollPane scroll = new ScrollPane(reservasList);
        scroll.setFitToWidth(true);

        this.getChildren().addAll(titulo, scroll);

        carregarReservas();
    }

    private void carregarReservas() {
        reservasList.getChildren().clear();
        ReservaDAO rdao = new ReservaDAO();
        List<Reserva> reservas = rdao.buscarPorCliente(clienteLogado.getId());

        if (reservas.isEmpty()) {
            reservasList.getChildren().add(new Label("Nenhuma reserva encontrada."));
            return;
        }

        // Ordena da mais recente para a mais antiga
        reservas.sort(Comparator.comparing(Reserva::getDataEntrada).reversed());

        boolean primeira = true;
        for (Reserva r : reservas) {
            HBox card = criarCardReserva(r, primeira);
            reservasList.getChildren().add(card);
            primeira = false;
        }
    }

    private HBox criarCardReserva(Reserva reserva, boolean destaque) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 2;");
        if (destaque && reserva.getStatus().equalsIgnoreCase("Ativa")) {
            card.setStyle(card.getStyle() + "-fx-border-color: goldenrod; -fx-border-width: 3;");
        }

        // Imagem do quarto
        ImageView img = new ImageView();
        try {
            File f = new File(reserva.getQuarto().getImagem());
            if (f.exists()) img.setImage(new Image(f.toURI().toString()));
        } catch (Exception ex) {
            // fallback vazio
        }
        img.setFitHeight(150);
        img.setFitWidth(200);
        img.setPreserveRatio(true);

        // Informações da reserva
        VBox info = new VBox(6);
        info.setAlignment(Pos.TOP_LEFT);

        Label tipo = new Label("Quarto: " + reserva.getQuarto().getTipo());
        tipo.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label numero = new Label("Número: " + reserva.getQuarto().getNumero());
        Label datas = new Label("Check-in: " + reserva.getDataEntrada() + " | Check-out: " + reserva.getDataSaida());
        Label valor = new Label("Valor Total: " + reserva.getValorTotal() + " MZN");
        Label servicos = new Label("Serviços: " + (reserva.getServicos() != null ? reserva.getServicos() : "Nenhum"));
        Label status = new Label("Status: " + reserva.getStatus());

        info.getChildren().addAll(tipo, numero, datas, valor, servicos, status);

        // Botões
        VBox botoes = new VBox(10);
        botoes.setAlignment(Pos.TOP_CENTER);

        if (reserva.getStatus().equalsIgnoreCase("Ativa") && destaque) {
            Button cancelar = new Button("Cancelar Reserva");
            cancelar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
            cancelar.setOnAction(e -> cancelarReserva(reserva));
            botoes.getChildren().add(cancelar);
        }

        // Botão de Recibo para reservas finalizadas
        if (reserva.getStatus().equalsIgnoreCase("Finalizada") || reserva.getStatus().equalsIgnoreCase("Cancelada")) {
            Button recibo = new Button("Visualizar Recibo");
            recibo.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold;");
            recibo.setOnAction(e -> mostrarRecibo(reserva));
            botoes.getChildren().add(recibo);
        }

        card.getChildren().addAll(img, info, botoes);
        return card;
    }

    private void cancelarReserva(Reserva reserva) {
        LocalDate hoje = LocalDate.now();
        if (reserva.getDataEntrada().minusDays(1).isBefore(hoje)) {
            showAlert("Cancelamento Não Permitido", "Não é possível cancelar a reserva a menos de 24h do check-in.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Cancelamento");
        confirm.setHeaderText(null);
        confirm.setContentText("Deseja realmente cancelar a reserva do quarto " + reserva.getQuarto().getTipo() + "?");
        Optional<ButtonType> opt = confirm.showAndWait();

        if (opt.isPresent() && opt.get() == ButtonType.OK) {
            try {
                ReservaDAO rdao = new ReservaDAO();
                reserva.setStatus("Cancelada");
                rdao.atualizar(reserva);
                showAlert("Sucesso", "Reserva cancelada com sucesso.");
                carregarReservas();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erro", "Erro ao cancelar reserva. Tente novamente.");
            }
        }
    }

    private void mostrarRecibo(Reserva reserva) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Recibo da Reserva");

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.TOP_LEFT);

        Label header = new Label("Recibo de Reserva");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label cliente = new Label("Cliente: " + clienteLogado.getNome());
        Label quarto = new Label("Quarto: " + reserva.getQuarto().getTipo() + " | Nº " + reserva.getQuarto().getNumero());
        Label datas = new Label("Check-in: " + reserva.getDataEntrada() + " | Check-out: " + reserva.getDataSaida());
        Label servicos = new Label("Serviços: " + (reserva.getServicos() != null ? reserva.getServicos() : "Nenhum"));
        Label valor = new Label("Valor Total: " + reserva.getValorTotal() + " MZN");
        Label status = new Label("Status: " + reserva.getStatus());

        Button fechar = new Button("Fechar");
        fechar.setOnAction(e -> modal.close());

        box.getChildren().addAll(header, cliente, quarto, datas, servicos, valor, status, fechar);

        Scene scene = new Scene(box, 400, 350);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
