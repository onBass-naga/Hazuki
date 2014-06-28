package ui

import javafx.concurrent.Task
import javafx.event.ActionEvent
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

    ExecutorService service = Executors.newSingleThreadExecutor();
    ConnectionService connectionService = new ConnectionService()

    @FXML
    def void testConnection(ActionEvent event) {

        progressIndicator.visible = true
        progressIndicator.indeterminate = true

//        Result result
//        def task = [
//            call: { ->
//                def condition = new ConnectCondition(
//                        url: urlField.text,
//                        user: userField.text,
//                        password: passwordField.text,
//                        driver: driverField.text)
//                result = connectionService.testConnection(condition)
//                return null
//            },
//            succeeded: { ->
//                progressIndicator.setVisible(false)
//                messageLabel.text = result.message
//                messageLabel.visible = true
//                service.shutdown()
//            }
//        ] as Task

        Task<Void> task = new Task<Void>() {

            Result result

            @Override
            public Void call() {
                def condition = new ConnectCondition(
                        url: urlField.text,
                        user: userField.text,
                        password: passwordField.text,
                        driver: driverField.text)

                result = connectionService.testConnection(condition)
                return null
            }

            void succeeded() {
                progressIndicator.setVisible(false)
                messageLabel.text = result.message
                messageLabel.visible = true
                service.shutdown()
            }
        };
        service.submit(task);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
