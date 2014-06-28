package ui

import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
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

import java.util.concurrent.TimeUnit

public class Controller implements Initializable {

    @FXML Label messageLabel
    @FXML TextField urlField
    @FXML TextField userField
    @FXML TextField passwordField
    @FXML TextField driverField
    @FXML ProgressIndicator progressIndicator

    ConnectionService service = new ConnectionService()

    @FXML
    def void testConnection(ActionEvent event) {

        progressIndicator.visible = true
        progressIndicator.indeterminate = true

        Service<Result> service = new Service<Result>() {

            Result result

            @Override
            protected Task<Result> createTask() {
                Task<Result> task = new Task<Result>() {

                    @Override
                    protected Result call() throws Exception {
                        TimeUnit.SECONDS.sleep(1);
                        def condition = new ConnectCondition(
                                url: urlField.text,
                                user: userField.text,
                                password: passwordField.text,
                                driver: driverField.text)

                        result = service.testConnection(condition)
                    }
                };

                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

                    @Override
                    public void handle(WorkerStateEvent wEvent) {
                        System.out.println("finished")
                        progressIndicator.setVisible(false);
                        messageLabel.text = result.message
                        messageLabel.visible = true
                    }
                });
                return task;
            }
        };
        service.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
