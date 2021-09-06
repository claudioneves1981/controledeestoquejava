package controls;

import DAO.CategoriaDAO;
import DAO.FornecedorDAO;
import DAO.ProdutoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Categoria;
import models.Fornecedor;
import models.Produto;


import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class GerenciarProduto implements Initializable {

    @FXML
    private TextField txNome;
    @FXML
    private TextField txId;
    @FXML
    private TextField txPreco;
    @FXML
    private TextField txQtd;
    @FXML
    private ComboBox<String> cbUn;
    @FXML
    private TextField txEstoqueMin;
    @FXML
    private ComboBox cbForn;
    @FXML
    private ComboBox cbCat;
    @FXML
    private Button btSalvar;
    @FXML
    private Button btCancelar;

    ProdutoDAO pdao = new ProdutoDAO();
    FornecedorDAO fdao = new FornecedorDAO();
    CategoriaDAO cdao = new CategoriaDAO();

    private boolean edit = false;
    private boolean view = false;
    private int idProd;


    public GerenciarProduto(){

    }
   public GerenciarProduto(boolean view, boolean edit, int idProd) throws ClassNotFoundException {
       this.view = view;
       this.edit = edit;
       this.idProd = idProd;
   }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            if(!view){
            iniComboBox();
            ativarBotaoSalvar();
            }

            if(!view && !edit){
                txId.setText(Integer.toString(pdao.idAutoIncrement()));
            }
            if(view){
                preencher();
                btSalvar.setVisible(false);
                btCancelar.setText("Voltar");
                txNome.setEditable(false);
                txId.setEditable(false);
                txPreco.setEditable(false);
                txQtd.setEditable(false);
                txEstoqueMin.setEditable(false);
            }
            if(edit) {
                preencher();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void preencher() throws ClassNotFoundException {
        Produto p = pdao.read(idProd);

        txNome.setText(p.getNome());
        txId.setText(Integer.toString(p.getId()));
        txPreco.setText(NumberFormat.getCurrencyInstance().format(p.getPreco()));
        txQtd.setText(Double.toString(p.getQtd()));
        cbUn.setValue(p.getTipoUn());
        txEstoqueMin.setText(Double.toString(p.getEstoqueMin()));
        cbForn.setValue(p.getForn());
        cbCat.setValue(p.getCat());
    }

    public void ativarBotaoSalvar(){
        btSalvar.setDisable(txNome.getText().isEmpty() || txPreco.getText().isEmpty() ||
                txQtd.getText().isEmpty() || cbUn.getValue().equals("<Selecione>") ||
                txEstoqueMin.getText().isEmpty() || cbForn.getValue().equals("<Selecione>") ||
                cbCat.getValue().equals("<Selecione>"));
    }

    public void txtChanged(){

        if(Character.isDigit(Integer.parseInt(txPreco.getText()))){

        }
        else{
            txPreco.deletePreviousChar();
        }
    }
    public void show(boolean view, boolean edit, int id) throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/views/GerenciarProduto.fxml"));
        root.setControllerFactory(c -> {
                    try {
                        return new GerenciarProduto(view, edit, id);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return new GerenciarProduto();
                    }
                });
        primaryStage.setTitle("ControlX - Gerenciar Produto");
        Main.stage.hide();
        Main.stage = primaryStage;
        primaryStage.setScene(new Scene(root.load(), primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.setResizable(false);
        Main.stage.getIcons().add(new Image("images/controlx.png"));
        primaryStage.show();
    }

    public void show() throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/views/GerenciarProduto.fxml"));
        root.setControllerFactory(c -> new GerenciarProduto());
        primaryStage.setTitle("ControlX - Adicionar Produto");
        Main.stage.hide();
        Main.stage = primaryStage;
        primaryStage.setScene(new Scene(root.load(), primaryStage.getWidth(), primaryStage.getHeight()));
        primaryStage.setResizable(false);
        Main.stage.getIcons().add(new Image("images/controlx.png"));
        primaryStage.show();
    }


    public void iniComboBox() throws ClassNotFoundException {
        //Categoria
        ObservableList<Categoria> categorias = FXCollections.observableArrayList();
        categorias.addAll(cdao.listAll());
        cbCat.setItems(categorias);
        cbCat.setValue("<Selecione>");

       //Fornecedor
        ObservableList<Fornecedor> fornecedores = FXCollections.observableArrayList();
        fornecedores.addAll(fdao.listAll());
        cbForn.setItems(fornecedores);
        cbForn.setValue("<Selecione>");

       //TipoUn
        ObservableList<String> tipoUn = FXCollections.observableArrayList("UN", "KG", "L", "ML", "G", "M", "CM");
        cbUn.setItems(tipoUn);
        cbUn.setValue("<Selecione>");


    }

    public void btCancel_Click() throws IOException {
        new Estoque().show();
    }

    public void btSalvar_Click() throws ClassNotFoundException, IOException {

        Produto p = pdao.read(idProd);
        p.setNome(txNome.getText());
        p.setPreco(Double.parseDouble(txPreco.getText()));
        p.setQtd(Double.parseDouble(txQtd.getText()));
        p.setTipoUn(cbUn.getValue());
        p.setEstoqueMin(Double.parseDouble(txEstoqueMin.getText()));
        p.setCat((Categoria) cbCat.getValue());
        p.setForn((Fornecedor) cbForn.getValue());

        if(edit) {
            pdao.up(p);
        } else {
            pdao.add(p);
        }

        new Estoque().show();

    }
}
