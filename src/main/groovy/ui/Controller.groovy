package ui

import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import service.ConnectCondition
import service.ConnectionService
import service.Result

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

public class Controller implements Initializable {

    @FXML Label messageLabel
    @FXML TextField urlField
    @FXML TextField userField
    @FXML TextField passwordField
    @FXML TextField driverField
    @FXML ProgressIndicator progressIndicator

    ConnectionService connectionService = new ConnectionService()

    @FXML
    def void testConnection(ActionEvent event) {

        progressIndicator.visible = true
        progressIndicator.indeterminate = true

        Service<Result> service = new Service<Result>() {

            Result result

            @Override
            protected Task<Result> createTask() {

                Result result
                def task = [
                    myCall: {
                        def condition = new ConnectCondition(
                                url: urlField.text,
                                user: userField.text,
                                password: passwordField.text,
                                driver: driverField.text)
                        result = connectionService.testConnection(condition)
                        return null
                    },
                    succeeded: {
                        progressIndicator.setVisible(false)
                        messageLabel.text = result.message
                        messageLabel.visible = true
                    }
                ] as MyTask
            }
        };
        service.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}

abstract class MyTask <V> extends Task<V> {

    protected V call() { myCall() }
    abstract V myCall()
}