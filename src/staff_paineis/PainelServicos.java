package staff_paineis;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class PainelServicos extends VBox {
    public PainelServicos() {
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setStyle("-fx-background-color: #fef9e7;");

        Label titulo = new Label("🧹 Serviços do Quarto");
        titulo.setFont(new Font(26));

        Label descricao = new Label("Gerencie a limpeza e manutenção dos quartos.");
        descricao.setFont(new Font(16));

        getChildren().addAll(titulo, descricao);
    }
}
