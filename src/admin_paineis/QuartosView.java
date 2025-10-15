package admin_paineis;

import dao.QuartoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Quarto;

import java.io.File;

public class QuartosView extends BorderPane {

    private TableView<Quarto> tabela;
    private ObservableList<Quarto> listaQuartos;
    private QuartoDAO quartoDAO = new QuartoDAO();

    private TextField txtNumero, txtPreco, txtDescricao, txtOfertas;
    private ComboBox<String> cbTipo, cbStatus;
    private String imagemSelecionada;

    private Button btnSalvar, btnSelecionarImagem, btnEditar, btnEliminar;

    public QuartosView() {
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f0f4f7;");

        // Imagem de fundo - CORRIGIDO
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/imagens/7.jpg"));
            BackgroundSize bgSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);
            BackgroundImage backgroundImage = new BackgroundImage(bgImage, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundPosition.CENTER, 
                bgSize);
            setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem de fundo: " + e.getMessage());
        }

        criarFormulario();
        criarTabela();
        carregarQuartos();
    }

    private void criarFormulario() {
        // Definir largura padrão para todos os campos
        double larguraPadrao = 250;
        
        txtNumero = criarTextField("Número do quarto", (int) larguraPadrao);
        txtPreco = criarTextField("Preço", (int) larguraPadrao);
        txtDescricao = criarTextField("Descrição", (int) larguraPadrao);
        txtOfertas = criarTextField("Ofertas", (int) larguraPadrao);

        // ComboBox tipo - MESMA LARGURA
        cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Standard", "Casal", "Família", "Luxo", "Premium");
        cbTipo.setValue("Standard");
        cbTipo.setPrefWidth(larguraPadrao);

        // ComboBox status - MESMA LARGURA
        cbStatus = new ComboBox<>();
        cbStatus.getItems().add("Livre");
        cbStatus.setValue("Livre");
        cbStatus.setPrefWidth(larguraPadrao);

        // Botões
        btnSelecionarImagem = criarBotao("Selecionar Imagem", "#3498db", "#2980b9");
        btnSelecionarImagem.setOnAction(e -> selecionarImagem());

        btnSalvar = criarBotao("Salvar", "#2ecc71", "#27ae60");
        btnSalvar.setOnAction(e -> salvarQuarto());

        btnEditar = criarBotao("Editar Selecionado", "#f39c12", "#e67e22");
        btnEditar.setOnAction(e -> editarQuarto());

        btnEliminar = criarBotao("Eliminar", "#e74c3c", "#c0392b");
        btnEliminar.setOnAction(e -> removerQuarto());

        VBox form = new VBox(10);
        form.setPadding(new Insets(15));
        form.setAlignment(Pos.TOP_LEFT);
        form.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 10;");

        form.getChildren().addAll(
                new Label("Número:"), txtNumero,
                new Label("Tipo:"), cbTipo,
                new Label("Preço:"), txtPreco,
                new Label("Descrição:"), txtDescricao,
                new Label("Ofertas:"), txtOfertas,
                new Label("Status:"), cbStatus,
                btnSelecionarImagem,
                new HBox(10, btnSalvar, btnEditar, btnEliminar)
        );

        setTop(form);
    }

    private TextField criarTextField(String prompt, int width) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(width);
        tf.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #bdc3c7;");
        tf.setOnKeyPressed(this::validarCampo);
        tf.setOnKeyReleased(this::validarCampo);
        return tf;
    }

    private void validarCampo(KeyEvent event) {
        TextField tf = (TextField) event.getSource();
        if (tf.getText().trim().isEmpty()) {
            tf.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e74c3c;");
        } else {
            tf.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #2ecc71;");
        }
    }

    private Button criarBotao(String texto, String corNormal, String corHover) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color:" + corNormal + "; -fx-text-fill: white; -fx-background-radius: 8;");
        DropShadow shadow = new DropShadow();
        btn.setEffect(shadow);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:" + corHover + "; -fx-text-fill: white; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:" + corNormal + "; -fx-text-fill: white; -fx-background-radius: 8;"));
        return btn;
    }

    private void criarTabela() {
        tabela = new TableView<>();
        listaQuartos = FXCollections.observableArrayList();
        tabela.setPrefHeight(400);

        TableColumn<Quarto, Integer> colNumero = new TableColumn<>("Número");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));

        TableColumn<Quarto, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<Quarto, Double> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<Quarto, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Quarto, String> colOfertas = new TableColumn<>("Ofertas");
        colOfertas.setCellValueFactory(new PropertyValueFactory<>("ofertas"));

        TableColumn<Quarto, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Quarto, String> colImagem = new TableColumn<>("Imagem");
        colImagem.setCellValueFactory(new PropertyValueFactory<>("imagem"));
        colImagem.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String caminho, boolean empty) {
                super.updateItem(caminho, empty);
                
                // SEMPRE LIMPAR PRIMEIRO
                setGraphic(null);
                imageView.setImage(null);
                
                if (empty || caminho == null || caminho.isEmpty()) {
                    // Célula vazia - não mostrar nenhuma imagem
                    setGraphic(null);
                } else {
                    // Tentar carregar a imagem do quarto
                    File file = new File(caminho);
                    if (file.exists()) {
                        try {
                            Image img = new Image(file.toURI().toString(), 80, 80, true, true);
                            imageView.setImage(img);
                            setGraphic(imageView);
                        } catch (Exception e) {
                            System.out.println("Erro ao carregar imagem: " + e.getMessage());
                            setGraphic(null);
                        }
                    } else {
                        // Arquivo não existe - não mostrar imagem
                        setGraphic(null);
                    }
                }
            }
        });

        tabela.getColumns().addAll(colNumero, colTipo, colPreco, colDescricao, colOfertas, colStatus, colImagem);
        tabela.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #bdc3c7;");

        setCenter(tabela);
    }

    private void selecionarImagem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imagemSelecionada = file.getAbsolutePath();
            System.out.println("Imagem selecionada: " + imagemSelecionada);
        }
    }

    private void salvarQuarto() {
        try {
            if (txtNumero.getText().isEmpty() || cbTipo.getValue().isEmpty() || txtPreco.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Número, Tipo e Preço são obrigatórios!").showAndWait();
                return;
            }

            int numero;
            double preco;
            try {
                numero = Integer.parseInt(txtNumero.getText());
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Número inválido!").showAndWait();
                return;
            }

            try {
                preco = Double.parseDouble(txtPreco.getText());
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Preço inválido!").showAndWait();
                return;
            }

            Quarto quarto = new Quarto(numero, cbTipo.getValue(), preco,
                    txtDescricao.getText(), txtOfertas.getText(),
                    imagemSelecionada, cbStatus.getValue());

            boolean existe = listaQuartos.stream().anyMatch(q -> q.getNumero() == numero);
            if (existe) {
                quartoDAO.atualizar(quarto);
            } else {
                quartoDAO.inserir(quarto);
            }

            carregarQuartos();
            limparCampos();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar: " + e.getMessage()).showAndWait();
        }
    }

    private void editarQuarto() {
        Quarto selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            txtNumero.setText(String.valueOf(selecionado.getNumero()));
            cbTipo.setValue(selecionado.getTipo());
            txtPreco.setText(String.valueOf(selecionado.getPreco()));
            txtDescricao.setText(selecionado.getDescricao());
            txtOfertas.setText(selecionado.getOfertas());

            // Mostrar todas as opções de status ao editar
            cbStatus.getItems().clear();
            cbStatus.getItems().addAll("Livre", "Ocupado", "Manutenção");
            cbStatus.setValue(selecionado.getStatus());

            imagemSelecionada = selecionado.getImagem();
        }
    }

    private void removerQuarto() {
        Quarto selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            quartoDAO.deletar(selecionado.getNumero());
            carregarQuartos();
        }
    }

    private void limparCampos() {
        txtNumero.clear();
        cbTipo.setValue("Standard");
        txtPreco.clear();
        txtDescricao.clear();
        txtOfertas.clear();
        cbStatus.getItems().clear();
        cbStatus.getItems().add("Livre");
        cbStatus.setValue("Livre");
        imagemSelecionada = null;
    }

    private void carregarQuartos() {
        listaQuartos.clear();
        listaQuartos.addAll(quartoDAO.listarTodos());
        tabela.setItems(listaQuartos);
    }

    public void mostrarTela() {
        Stage stage = new Stage();
        stage.setTitle("Gestão de Quartos");
        stage.setScene(new Scene(this, 1100, 700));
        stage.show();
    }
    
    public BorderPane getRoot() {
        return this;
    }
}