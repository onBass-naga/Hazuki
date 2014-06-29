package ui

import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.fxml.JavaFXBuilderFactory
import javafx.scene.layout.AnchorPane

public class Controller implements Initializable {

    @FXML
    AnchorPane main;

    @FXML
    def void showConnectionPage(ActionEvent event) {
        replaceSceneContent(new ConnectionPage())
    }

    @FXML
    def void showMasterPage(ActionEvent event) {
//        System.out.println("接続設定画面を表示")
        replaceSceneContent(new MasterPage())
    }

    @FXML
    def void showTransactionPage(ActionEvent event) {
//        System.out.println("接続設定画面を表示")
        replaceSceneContent(new TransactionPage())
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    protected Initializable replaceSceneContent(Page page) throws Exception {
        FXMLLoader loader = new FXMLLoader()
        loader.setBuilderFactory(new JavaFXBuilderFactory())
        loader.setLocation(Main.class.getResource(page.fxml))
        AnchorPane pane = loader.load();
        main.getChildren().clear()
        main.getChildren().add(pane)
        return (Initializable) loader.getController()
    }
}

abstract class MyTask <V> extends Task<V> {

    protected V call() { myCall() }
    abstract V myCall()
}

abstract class MyService <V> extends Service<V> {

    protected Task<V> createTask() { createMyTask() }
    abstract Task<V> createMyTask()
}