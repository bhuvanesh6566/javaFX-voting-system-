package miniproject;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class hi extends Application {

    private final ObservableList<Candidate> candidates = FXCollections.observableArrayList();
    private final TableView<Candidate> table = new TableView<>();
    private final String ADMIN_PASSWORD = "admin"; // change this in production

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simple JavaFX Voting System");

        // seed candidates
        candidates.addAll(
                new Candidate("Dinesh"),
                new Candidate("Priya"),
                new Candidate("Vetri")
        );

        // Top-level layout: split pane with Voter area on left and Admin area on right (tabbed)
        TabPane tabPane = new TabPane();

        Tab voterTab = new Tab("Voter");
        voterTab.setContent(createVoterPane());
        voterTab.setClosable(false);

        Tab adminTab = new Tab("Admin");
        adminTab.setContent(createAdminAuthPane(primaryStage));
        adminTab.setClosable(false);

        tabPane.getTabs().addAll(voterTab, adminTab);

        Scene scene = new Scene(tabPane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private BorderPane createVoterPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(12));

        Label title = new Label("Cast your vote");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Candidate selection list
        ListView<Candidate> listView = new ListView<>(candidates);
        listView.setPrefWidth(250);

        Button voteBtn = new Button("Vote");
        voteBtn.setOnAction(e -> {
            Candidate selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No selection", "Please select a candidate before voting.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm vote");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to vote for: " + selected.getName() + "?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selected.incrementVotes();
                listView.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Thank you!", "Your vote has been recorded.");
            }
        });

        VBox left = new VBox(10, title, listView, voteBtn);
        left.setAlignment(Pos.TOP_CENTER);
        left.setPadding(new Insets(4));

        // Results chart
        VBox right = new VBox(10);
        right.setPadding(new Insets(4));
        right.setAlignment(Pos.TOP_CENTER);

        Label resultsLabel = new Label("Live results");
        resultsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        BarChart<String, Number> chart = createBarChart();
        updateChart(chart);

        // refresh button
        Button refreshBtn = new Button("Refresh results");
        refreshBtn.setOnAction(ev -> updateChart(chart));

        right.getChildren().addAll(resultsLabel, chart, refreshBtn);

        pane.setLeft(left);
        pane.setCenter(right);

        return pane;
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Candidate");
        yAxis.setLabel("Votes");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Vote count");
        chart.setLegendVisible(false);
        chart.setPrefSize(500, 400);
        return chart;
    }

    private void updateChart(BarChart<String, Number> chart) {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Candidate c : candidates) {
            series.getData().add(new XYChart.Data<>(c.getName(), c.getVotes()));
        }
        chart.getData().add(series);
    }

    private VBox createAdminAuthPane(Stage owner) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(16));
        box.setAlignment(Pos.TOP_CENTER);

        Label prompt = new Label("Enter admin password to access admin panel");
        PasswordField pf = new PasswordField();
        pf.setPromptText("Password");

        Button login = new Button("Login");
        login.setOnAction(e -> {
            if (ADMIN_PASSWORD.equals(pf.getText())) {
                // replace with admin panel
                TabPane parent = (TabPane) owner.getScene().getRoot();
                Tab adminTab = parent.getTabs().get(1);
                adminTab.setContent(createAdminPanel(owner));
            } else {
                showAlert(Alert.AlertType.ERROR, "Access denied", "Incorrect admin password.");
            }
        });

        box.getChildren().addAll(prompt, pf, login);
        return box;
    }

    private BorderPane createAdminPanel(Stage owner) {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(12));

        // Table of candidates
        table.setItems(candidates);
        table.setEditable(false);

        TableColumn<Candidate, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Candidate, Integer> votesCol = new TableColumn<>("Votes");
        votesCol.setCellValueFactory(new PropertyValueFactory<>("votes"));
        votesCol.setPrefWidth(80);

        table.getColumns().setAll(nameCol, votesCol);

        // Controls to add/remove/modify
        TextField nameField = new TextField();
        nameField.setPromptText("Candidate name");

        Button addBtn = new Button("Add Candidate");
        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty name", "Enter a candidate name.");
                return;
            }
            candidates.add(new Candidate(name));
            nameField.clear();
        });

        Button removeBtn = new Button("Remove Selected");
        removeBtn.setOnAction(e -> {
            Candidate sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert(Alert.AlertType.WARNING, "No selection", "Select a candidate to remove.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Remove candidate: " + sel.getName() + "?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.YES) {
                candidates.remove(sel);
            }
        });

        Button resetVotesBtn = new Button("Reset All Votes");
        resetVotesBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "This will set all votes to zero. Continue?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.YES) {
                for (Candidate c : candidates) c.setVotes(0);
                table.refresh();
            }
        });

        Button exportBtn = new Button("Export CSV");
        exportBtn.setOnAction(evt -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export results as CSV");
            chooser.setInitialFileName("results.csv");
            File f = chooser.showSaveDialog(owner);
            if (f != null) {
                try (FileWriter fw = new FileWriter(f)) {
                    fw.append("Name,Votes\n");
                    for (Candidate c : candidates) fw.append(c.getName()).append(",").append(String.valueOf(c.getVotes())).append("\n");
                    showAlert(Alert.AlertType.INFORMATION, "Exported", "Results exported to: " + f.getAbsolutePath());
                } catch (IOException ex) {
                    showAlert(Alert.AlertType.ERROR, "Export failed", ex.getMessage());
                }
            }
        });

        HBox controls = new HBox(8, nameField, addBtn, removeBtn, resetVotesBtn, exportBtn);
        controls.setPadding(new Insets(8));
        controls.setAlignment(Pos.CENTER_LEFT);

        pane.setTop(controls);
        pane.setCenter(table);

        // Admin quick view: chart
        BarChart<String, Number> chart = createBarChart();
        updateChart(chart);
        Button refreshChart = new Button("Refresh Chart");
        refreshChart.setOnAction(e -> updateChart(chart));

        VBox right = new VBox(10, new Label("Admin results"), chart, refreshChart);
        right.setPadding(new Insets(8));
        right.setAlignment(Pos.TOP_CENTER);
        right.setPrefWidth(350);

        pane.setRight(right);

        return pane;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static class Candidate {
        private final String name;
        private int votes;

        public Candidate(String name) {
            this.name = name;
            this.votes = 0;
        }

        public String getName() {
            return name;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }

        public void incrementVotes() {
            votes++;
        }

        @Override
        public String toString() {
            return name + " (" + votes + ")";
        }
    }
}
