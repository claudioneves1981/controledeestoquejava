package controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Produto;
import models.Venda;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class VisualizarVenda implements Initializable{

    @FXML
    private AnchorPane apPane;

    @FXML
    private Label lbCompraNum;

    @FXML
    private TableView<Produto> tbProdutos;

    @FXML
    private Button btVoltar;

    @FXML
    private TextField txId;

    @FXML
    private TextField txUsuario;

    @FXML
    private TextField txDataVenda;

    @FXML
    private TextField txTotal;

    Venda venda = new Venda();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fillFields();
    }

    VisualizarVenda(){

    }

    VisualizarVenda(Venda venda){
        this.venda = venda;
    }

    public void show(Venda venda) throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/views/VisualizarVenda.fxml"));
        root.setControllerFactory(c -> new VisualizarVenda(venda));
        primaryStage.setTitle("ControlX - Visualizar Venda");
        Main.stage.hide();
        Main.stage = primaryStage;
        primaryStage.setScene(new Scene(root.load(), primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.setResizable(false);
        Main.stage.getIcons().add(new Image("images/controlx.png"));
        primaryStage.show();
    }

    public void botaoVoltar() throws IOException {
            new Historico().showVenda(true);
    }

    public void fillFields(){
        DecimalFormat df = new DecimalFormat("#0.00");
        txDataVenda.setText(venda.getData().toString());
        txId.setText(String.valueOf(venda.getId()));
        txTotal.setText(NumberFormat.getCurrencyInstance().format(venda.getValor()));
        txUsuario.setText(venda.getUsuario().getNome());


        tbProdutos.getItems().clear();
        tbProdutos.getColumns().clear();

        ObservableList<Produto> lista = FXCollections.observableArrayList();

        for (Produto p : venda.getProdutos()) {
            lista.add(new Produto(p.getId(), p.getNome(), p.getPreco(), p.getQtd(), p.getTipoUn(), p.getCat()));
        }

        TableColumn<Produto, String> idColumn = new TableColumn<>("ID");
        idColumn.setMinWidth(50);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Produto, String> nomeColumn = new TableColumn<>("Nome");
        nomeColumn.setMinWidth(220);
        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Produto, String> precoColumn = new TableColumn<>("Preço (R$)");
        precoColumn.setMinWidth(60);
        precoColumn.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<Produto, String> qtdColumn = new TableColumn<>("Qtd");
        qtdColumn.setMinWidth(60);
        qtdColumn.setCellValueFactory(new PropertyValueFactory<>("qtd"));

        TableColumn<Produto, String> unColumn = new TableColumn<>("Un");
        unColumn.setMinWidth(60);
        unColumn.setCellValueFactory(new PropertyValueFactory<>("tipoUn"));

        tbProdutos.setItems(lista);
        tbProdutos.getColumns().addAll(idColumn, nomeColumn, precoColumn, qtdColumn, unColumn);
    }

}
