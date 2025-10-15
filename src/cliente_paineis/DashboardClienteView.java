package cliente_paineis;

import dao.Conexao;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import model.Cliente;
import model.Quarto;
import model.Tarifa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardClienteView extends VBox {

    private Cliente clienteLogado;
    private ImageView slideshowImageView = new ImageView();
    private Label slideshowLabel = new Label();
    private List<Image> quartoImages = new ArrayList<>();
    private List<String> quartoDescriptions = new ArrayList<>();
    private int currentImageIndex = 0;

    public DashboardClienteView(Cliente cliente) {
        this.clienteLogado = cliente;
        initDashboard();
    }

    public DashboardClienteView() {
        this.clienteLogado = null;
        initDashboard();
    }

    private void initDashboard() {
        setSpacing(16);
        setPadding(new Insets(16));
        setStyle("-fx-background-color: linear-gradient(to bottom, #f7fbff, #e3f2fd);");

        VBox infoHotel = createHotelInfoCard();
        HBox content = createMainContent();

        getChildren().addAll(infoHotel, content);

        startSlideshow();
    }

    private VBox createHotelInfoCard() {
        VBox infoHotel = new VBox();
        infoHotel.setSpacing(8);
        infoHotel.setStyle("-fx-background-color: white; -fx-background-radius:12; -fx-padding:16;");
        infoHotel.setEffect(new DropShadow(6, Color.gray(0.4)));

        Label infoTitle = new Label("🏨 Informações do Hotel Luxory");
        infoTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        infoTitle.setTextFill(Color.DARKBLUE);

        Label infoText = new Label(
                "📍 Av. Principal 123, Maputo\n" +
                "📞 +258 84 000 0000 | ✉️ reservas@luxoryhotel.com\n" +
                "⏰ Check-in: 14h | Check-out: 11h\n" +
                "⭐ Restaurante 🍴 | Piscina 🏊 | SPA 💆 | Ginásio 🏋 | Wi-Fi grátis"
        );
        infoText.setWrapText(true);
        infoText.setFont(Font.font("System", 14));
        infoText.setLineSpacing(2);

        infoHotel.getChildren().addAll(infoTitle, infoText);
        return infoHotel;
    }

    private HBox createMainContent() {
        HBox content = new HBox();
        content.setSpacing(18);

        VBox leftColumn = new VBox();
        leftColumn.setSpacing(12);
        leftColumn.setPrefWidth(580);

        StackPane slideshowCard = createSlideshow();

        HBox infoRow = new HBox(12,
                createInfoCard("📊 Taxa de Ocupação", calcularOcupacao() + "%"),
                createInfoCard("🔑 Check-ins Hoje", getCheckinsHoje() + "")
        );
        infoRow.setSpacing(12);
        leftColumn.getChildren().addAll(slideshowCard, infoRow);

        VBox rightColumn = createRightColumn();
        content.getChildren().addAll(leftColumn, rightColumn);

        return content;
    }

    private VBox createRightColumn() {
        VBox rightColumn = new VBox();
        rightColumn.setSpacing(20);

        if (clienteLogado != null) {
            VBox welcomeCard = createWelcomeCard();
            rightColumn.getChildren().add(welcomeCard);
        }

        // cards de promoções
        HBox promoRow = new HBox();
        promoRow.setSpacing(20);

        List<Tarifa> promocoes = getPromocoesDisponiveis();
        for (Tarifa t : promocoes) {
            VBox card = createPromoCard(t.getDescricao(), t.getPreco());
            promoRow.getChildren().add(card);
        }

        rightColumn.getChildren().add(promoRow);
        return rightColumn;
    }

    private VBox createWelcomeCard() {
        VBox welcomeCard = new VBox();
        welcomeCard.setSpacing(6);
        welcomeCard.setStyle("-fx-background-color: linear-gradient(to right, #e3f2fd, #bbdefb); -fx-background-radius:12; -fx-padding:12;");
        welcomeCard.setEffect(new DropShadow(5, Color.gray(0.3)));

        Label welcomeTitle = new Label("👋 Bem-vindo, " + clienteLogado.getNome().split(" ")[0] + "!");
        welcomeTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label statusLabel = new Label(getStatusReservaCliente());
        statusLabel.setFont(Font.font("System", 12));
        statusLabel.setTextFill(Color.DARKGREEN);

        welcomeCard.getChildren().addAll(welcomeTitle, statusLabel);
        return welcomeCard;
    }

    private StackPane createSlideshow() {
        carregarImagensSlideshow();

        slideshowImageView.setFitWidth(560);
        slideshowImageView.setFitHeight(280);
        slideshowImageView.setPreserveRatio(true);

        try {
            slideshowImageView.setImage(quartoImages.get(0));
            slideshowLabel.setText(quartoDescriptions.get(0));
        } catch (Exception e) {
            slideshowImageView.setImage(createImagePadrao());
            slideshowLabel.setText("Nenhum quarto disponível");
        }

        slideshowLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        slideshowLabel.setWrapText(true);
        slideshowLabel.setTextFill(Color.DARKSLATEGRAY);

        VBox slideshowBox = new VBox(slideshowImageView, slideshowLabel);
        slideshowBox.setSpacing(8);
        slideshowBox.setAlignment(Pos.CENTER);

        StackPane card = new StackPane(slideshowBox);
        card.setStyle("-fx-background-color: white; -fx-background-radius:12; -fx-padding:16;");
        card.setEffect(new DropShadow(8, Color.gray(0.3)));
        return card;
    }

    private void carregarImagensSlideshow() {
        quartoImages.clear();
        quartoDescriptions.clear();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT imagem, descricao FROM quartos LIMIT 5");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String imgPath = rs.getString("imagem");
                String desc = rs.getString("descricao");
                try {
                    Image imagem = new Image("file:" + imgPath);
                    quartoImages.add(imagem);
                    quartoDescriptions.add(desc);
                } catch (Exception e) {
                    quartoImages.add(createImagePadrao());
                    quartoDescriptions.add(desc + " (imagem não disponível)");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            quartoImages.add(createImagePadrao());
            quartoDescriptions.add("Erro ao carregar dados do banco");
        }
    }

    private Image createImagePadrao() {
        return new Image("file:imagens/quarto 4.jpg");
    }

    private void startSlideshow() {
        if (quartoImages.isEmpty()) return;
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            currentImageIndex = (currentImageIndex + 1) % quartoImages.size();
            slideshowImageView.setImage(quartoImages.get(currentImageIndex));
            slideshowLabel.setText(quartoDescriptions.get(currentImageIndex));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private VBox createInfoCard(String title, String value) {
        VBox box = new VBox();
        box.setSpacing(6);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white; -fx-background-radius:12; -fx-padding:12;");
        box.setEffect(new DropShadow(5, Color.gray(0.3)));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        titleLabel.setTextFill(Color.DARKSLATEBLUE);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        valueLabel.setTextFill(Color.DARKGREEN);

        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }

    private VBox createPromoCard(String descricao, double preco) {
        VBox card = new VBox();
        card.setSpacing(6);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: #fffbf0; -fx-background-radius:12; -fx-padding:12;");
        card.setEffect(new DropShadow(5, Color.gray(0.3)));

        Label descLabel = new Label(descricao);
        descLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        descLabel.setTextFill(Color.DARKRED);
        descLabel.setWrapText(true);

        Label precoLabel = new Label(String.format("Preço: %.2f MZN", preco));
        precoLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        precoLabel.setTextFill(Color.DARKBLUE);

        card.getChildren().addAll(descLabel, precoLabel);
        return card;
    }

    // =================== MÉTODOS DE BANCO ===================

    private String calcularOcupacao() {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS total, SUM(CASE WHEN status='Ocupado' THEN 1 ELSE 0 END) AS ocupados FROM quartos");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total");
                int ocupados = rs.getInt("ocupados");
                return total > 0 ? String.valueOf((ocupados * 100) / total) : "0";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "0";
    }

    private int getCheckinsHoje() {
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) AS qtd FROM reservas WHERE dataEntrada = CURDATE()"
             );
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt("qtd");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private List<Quarto> getQuartosEmDestaque() {
        List<Quarto> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM quartos ORDER BY preco DESC LIMIT 2"
             );
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Quarto q = new Quarto();
                q.setTipo(rs.getString("tipo"));
                q.setDescricao(rs.getString("descricao"));
                q.setImagem(rs.getString("imagem"));
                lista.add(q);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private String getStatusReservaCliente() {
        if (clienteLogado == null) return "Faça login para ver suas reservas.";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT status FROM reservas WHERE cliente_id = ? ORDER BY id DESC LIMIT 1"
             )) {
            stmt.setInt(1, clienteLogado.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return "Sua reserva atual: " + rs.getString("status");
        } catch (SQLException e) { e.printStackTrace(); }
        return "Nenhuma reserva encontrada.";
    }

    private List<Tarifa> getPromocoesDisponiveis() {
        List<Tarifa> lista = new ArrayList<>();
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM tarifas WHERE disponibilidade = 1"
             );
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tarifa t = new Tarifa();
                t.setDescricao(rs.getString("descricao"));
                t.setPreco(rs.getDouble("preco"));
                lista.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
