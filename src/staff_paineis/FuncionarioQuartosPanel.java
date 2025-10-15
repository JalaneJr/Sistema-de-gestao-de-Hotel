package staff_paineis;

import dao.QuartoDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Quarto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class FuncionarioQuartosPanel extends VBox {

    private FlowPane gridQuartos;
    private TextField campoPesquisa;
    private List<Quarto> listaOriginal;

    public FuncionarioQuartosPanel() {
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f8f9fa;");

        Label titulo = new Label("🏨 Gestão de Quartos");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        campoPesquisa = new TextField();
        campoPesquisa.setPromptText("🔍 Pesquise por número, tipo ou status...");
        campoPesquisa.setPrefWidth(350);
        campoPesquisa.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #bdc3c7;");

        gridQuartos = new FlowPane();
        gridQuartos.setHgap(20);
        gridQuartos.setVgap(20);
        gridQuartos.setAlignment(Pos.CENTER);
        gridQuartos.setPadding(new Insets(15));

        ScrollPane scrollPane = new ScrollPane(gridQuartos);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.getChildren().addAll(titulo, campoPesquisa, scrollPane);

        carregarQuartos();

        // 🔍 Pesquisa em tempo real
        campoPesquisa.textProperty().addListener((obs, oldVal, newVal) -> filtrarQuartos(newVal));
    }

    private void carregarQuartos() {
        QuartoDAO dao = new QuartoDAO();
        listaOriginal = dao.listarTodos();
        mostrarQuartos(listaOriginal);
    }

    private void filtrarQuartos(String filtro) {
        if (listaOriginal == null) return;

        List<Quarto> filtrados = listaOriginal.stream()
                .filter(q -> String.valueOf(q.getNumero()).toLowerCase().contains(filtro.toLowerCase())
                        || q.getTipo().toLowerCase().contains(filtro.toLowerCase())
                        || q.getStatus().toLowerCase().contains(filtro.toLowerCase()))
                .collect(Collectors.toList());

        mostrarQuartos(filtrados);
    }

    private void mostrarQuartos(List<Quarto> quartos) {
        gridQuartos.getChildren().clear();

        if (quartos.isEmpty()) {
            Label vazio = new Label("Nenhum quarto encontrado com esse critério.");
            vazio.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            gridQuartos.getChildren().add(vazio);
            return;
        }

        for (Quarto quarto : quartos) {
            VBox card = criarCardQuarto(quarto);
            gridQuartos.getChildren().add(card);
        }
    }

    private VBox criarCardQuarto(Quarto quarto) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(220, 280);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
                + "-fx-border-radius: 12; -fx-border-color: #dcdde1; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 6, 0, 0, 2);");

        ImageView imgView;
        try {
            Image imagem = null;
            Object imgObj = quarto.getImagem();

            if (imgObj != null) {
                if (imgObj instanceof byte[]) {
                    imagem = new Image(new java.io.ByteArrayInputStream((byte[]) imgObj));
                } else {
                    String caminho = imgObj.toString();
                    if (new java.io.File(caminho).exists()) {
                        imagem = new Image(new FileInputStream(caminho));
                    } else {
                        java.io.InputStream is = getClass().getResourceAsStream("/imagens/" + caminho);
                        if (is != null) imagem = new Image(is);
                    }
                }
            }

            if (imagem == null)
                imagem = new Image(getClass().getResourceAsStream("/imagens/quarto 4.jpg"));

            imgView = new ImageView(imagem);
            imgView.setFitWidth(200);
            imgView.setFitHeight(130);
            imgView.setPreserveRatio(true);

        } catch (FileNotFoundException e) {
            imgView = new ImageView(new Image(getClass().getResourceAsStream("/imagens/quarto 2.jpg")));
            imgView.setFitWidth(200);
            imgView.setFitHeight(130);
        }

        Label lblNumero = new Label("Quarto Nº " + quarto.getNumero());
        lblNumero.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label lblTipo = new Label("Tipo: " + quarto.getTipo());
        Label lblPreco = new Label("Preço: " + quarto.getPreco() + " MZN / noite");
        lblPreco.setStyle("-fx-text-fill: #16A085; -fx-font-weight: bold;");

        Label lblStatus = new Label("Status: " + quarto.getStatus());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: " +
                (quarto.getStatus().equalsIgnoreCase("Em manutenção") ? "#E67E22" :
                        quarto.getStatus().equalsIgnoreCase("Reservado") ? "#E74C3C" : "#27AE60") + ";");

        Button btnDetalhes = new Button("Detalhes");
        btnDetalhes.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDetalhes.setOnAction(e -> abrirModalQuarto(quarto));

        card.getChildren().addAll(imgView, lblNumero, lblTipo, lblPreco, lblStatus, btnDetalhes);

        return card;
    }

    // 🔹 Modal com opções de manutenção
    private void abrirModalQuarto(Quarto quarto) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 12;");
        box.setMaxWidth(500);
        box.setAlignment(Pos.TOP_CENTER);

        ImageView imgView;
        try {
            String caminho = quarto.getImagem();
            Image img;
            if (caminho != null && !caminho.isEmpty()) {
                java.io.File f = new java.io.File(caminho);
                if (f.exists()) img = new Image(f.toURI().toString());
                else {
                    java.io.InputStream is = getClass().getResourceAsStream("/imagens/" + caminho);
                    img = (is != null) ? new Image(is)
                            : new Image(getClass().getResourceAsStream("/imagens/quarto 3.jpg"));
                }
            } else {
                img = new Image(getClass().getResourceAsStream("/imagens/quarto 4.jpg"));
            }
            imgView = new ImageView(img);
            imgView.setFitWidth(450);
            imgView.setFitHeight(300);
            imgView.setPreserveRatio(true);
        } catch (Exception ex) {
            imgView = new ImageView(new Image(getClass().getResourceAsStream("/imagens/quarto 6.jpg")));
            imgView.setFitWidth(450);
            imgView.setFitHeight(300);
        }

        Label lblTipo = new Label("Tipo: " + quarto.getTipo());
        lblTipo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblPreco = new Label("Preço: " + quarto.getPreco() + " MZN / noite");
        lblPreco.setStyle("-fx-font-size: 16px; -fx-text-fill: #16A085; -fx-font-weight: bold;");

        Label lblStatus = new Label("Status atual: " + quarto.getStatus());
        lblStatus.setStyle("-fx-font-weight: bold;");

        Button btnManutencao = new Button("🔧 Marcar como em manutenção");
        btnManutencao.setStyle("-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnLiberar = new Button("✅ Marcar como livre");
        btnLiberar.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold;");

        Button fechar = new Button("Fechar");
        fechar.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");

        fechar.setOnAction(ev -> modal.close());

        // 🔧 Funções de manutenção
        QuartoDAO dao = new QuartoDAO();

        btnManutencao.setOnAction(ev -> {
            dao.atualizarStatus(quarto.getId(), "Em manutenção");
            modal.close();
            carregarQuartos();
        });

        btnLiberar.setOnAction(ev -> {
            dao.atualizarStatus(quarto.getId(), "Livre");
            modal.close();
            carregarQuartos();
        });

        box.getChildren().addAll(imgView, lblTipo, lblPreco, lblStatus, btnManutencao, btnLiberar, fechar);

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        Scene scene = new Scene(root);
        scene.setFill(null);
        modal.setScene(scene);
        modal.showAndWait();
    }
}
