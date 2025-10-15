package admin_paineis;

import dao.ReservaDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Reserva;

import java.util.List;

public class ReservasView {

    private VBox root;

    public ReservasView() {
        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titulo = new Label("📅 Gestão de Reservas");
        titulo.setFont(Font.font(22));

        root.getChildren().add(titulo);

        mostrarReservas();
    }

    private void mostrarReservas() {
        ReservaDAO dao = new ReservaDAO();
        List<Reserva> reservas = dao.getTodasReservas();

        FlowPane painelCards = new FlowPane();
        painelCards.setHgap(20);
        painelCards.setVgap(20);
        painelCards.setPadding(new Insets(10));
        painelCards.setPrefWidth(1100);
        painelCards.setAlignment(Pos.TOP_LEFT);

        for (Reserva r : reservas) {
            VBox card = criarCard(r);
            painelCards.getChildren().add(card);
        }

        ScrollPane scroll = new ScrollPane(painelCards);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:transparent;");

        root.getChildren().add(scroll);
    }

    private VBox criarCard(Reserva r) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(15));
        card.setPrefSize(250, 320);
        String corNeutra = "#34495e"; // cor neutra para o card
        card.setStyle("-fx-background-radius: 15; -fx-background-color: " + corNeutra + ";");

        // Imagem do quarto
        ImageView img = new ImageView();
        try {
            if (r.getQuarto().getImagem() != null && !r.getQuarto().getImagem().isEmpty()) {
                img.setImage(new Image("file:" + r.getQuarto().getImagem(), 220, 140, true, true));
            } else {
                img.setImage(new Image("file:imagens/default.png", 220, 140, true, true));
            }
        } catch (Exception e) {
            img.setImage(new Image("file:imagens/default.png", 220, 140, true, true));
        }
        img.setSmooth(true);

        Label lblNumero = new Label("Quarto: " + r.getQuarto().getNumero());
        lblNumero.setTextFill(Color.WHITE);
        lblNumero.setFont(Font.font(16));

        Label lblCliente = new Label("Cliente: " + r.getCliente().getNome());
        lblCliente.setTextFill(Color.WHITE);

        Label lblDescricao = new Label(r.getQuarto().getDescricao());
        lblDescricao.setTextFill(Color.WHITE);
        lblDescricao.setWrapText(true);

        // Status
        String status = r.getStatus().trim();
        Label lblStatus = new Label(status);
        lblStatus.setTextFill(Color.WHITE);
        lblStatus.setFont(Font.font(14));
        lblStatus.setStyle("-fx-background-radius: 10; -fx-padding: 3 8 3 8; -fx-background-color: " + getCorStatus(status) + ";");

        card.getChildren().addAll(img, lblNumero, lblCliente, lblDescricao, lblStatus);

        // Efeito hover: usa a cor do status
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-radius: 15; -fx-background-color: " 
                + getCorStatus(status) + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15,0,0,0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-radius: 15; -fx-background-color: " + corNeutra + ";"));

        return card;
    }

  private String getCorStatus(String status) {
    return switch (status) {
        case "ATIVA" -> "#3498db";      // azul
        case "FINALIZADA" -> "#27ae60"; // verde
        case "CANCELADA" -> "#e67e22";  // laranja
        default -> "#7f8c8d";           // cinza neutro
    };
}
  
    public VBox getRoot() {
        return root;
    }
}
