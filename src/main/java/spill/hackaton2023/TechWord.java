package spill.hackaton2023;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;

import static java.lang.System.out;

public class TechWord extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static BorderPane panel;
    private static StackPane stackPane;
    private static FlowPane ord, bokstaverPane;
    private static DialogPane dialogPane;

    private static Label poeng, highscoreLabel, txt;
    private static String riktigOrd;
    private static Alert alert;
    private static String tekst, brukernavn;
    private static String[] ordTab;
    private static DatabaseConnector databaseConnector;
    private static int spillerPoeng = 0;
    private static int riktige = 0;
    private static int highscore = 0;
    private static int lifes = 3;
    private static int ganger = 3;

    private static Color[] farger = {Color.TOMATO, Color.YELLOWGREEN};

    private static String[] bokstaver = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M", "SLETT"};

    private static Button[] knapper = new Button[bokstaver.length];

    private static Button[] ordBokstav = new Button[5];


    @Override
    public void start(Stage stage) throws Exception {

        databaseConnector = new DatabaseConnector();
        databaseConnector.connect();

        brukernavn = innlogging();
        brukernavn = brukernavn.toUpperCase();

        riktigOrd = nyOrd();
        ordTab = new String[riktigOrd.length()];

        for (int i = 0; i < riktigOrd.length(); i++) {
            ordTab[i] = riktigOrd.substring(i, i + 1);
        }

        panel = new BorderPane();

        FlowPane score = new FlowPane();
        poeng = new Label("Dine poeng: " + spillerPoeng);
        poeng.setFont(Font.font("Monospaced", FontWeight.EXTRA_BOLD, 26));
        poeng.setTextFill(Color.WHITE);

        highscoreLabel = new Label("HighScore: " + highscore);
        highscoreLabel.setFont(Font.font("Monospaced", FontWeight.EXTRA_BOLD, 26));
        highscoreLabel.setTextFill(Color.WHITE);

        txt = new Label(brukernavn + ", du har " + lifes + " liv");
        txt.setFont(Font.font("Monospaced", FontWeight.EXTRA_BOLD, 30));
        txt.setTextFill(Color.RED);

        score.getChildren().addAll(txt, poeng, highscoreLabel);
        score.setAlignment(Pos.CENTER);
        score.setPadding(new Insets(10));
        panel.setPadding(new Insets(10));

        panel.setTop(score);
        panel.setLeft(poeng);
        panel.setRight(highscoreLabel);


        ordBokstav = new Button[5];
        ord = new FlowPane();
        final int LUFT = 10;
        ord.setPadding(new Insets(LUFT));
        ord.setHgap(LUFT / 2);    // Avstand mellom kolonnene
        ord.setVgap(LUFT * 2);  // Avstand mellom radene
        ord.setAlignment(Pos.CENTER);

        for (int i = 0; i < ordBokstav.length; i++) {
            Button bokstavField = new Button("_");
            Label knappTxt = new Label("_");
            knappTxt.setFont(Font.font("Monospaced", FontWeight.EXTRA_BOLD, 50));
            knappTxt.setStyle("-fx-background-color: LIGHTBLUE;");
            bokstavField.setStyle("-fx-background-color: LIGHTBLUE;");
            knappTxt.setAlignment(Pos.CENTER);
            stackPane = new StackPane();
            stackPane.getChildren().addAll(bokstavField, knappTxt);
            stackPane.setAlignment(knappTxt, Pos.CENTER);
            ord.getChildren().add(stackPane);
            bokstavField.setPrefSize(100, 100);
            ordBokstav[i] = bokstavField;
        }

        panel.setCenter(ord);

        bokstaverPane = new FlowPane();
        bokstaverPane.setAlignment(Pos.CENTER);
        bokstaverPane.setHgap(LUFT);
        bokstaverPane.setVgap(LUFT);
        bokstaverPane.setPadding(new Insets(30));

        for (int i = 0; i < bokstaver.length; i++) {
            knapper[i] = new Button(bokstaver[i]);
            knapper[i].setStyle("-fx-text-fill: red;");
            knapper[i].setFont(Font.font("Monospaced", FontWeight.EXTRA_BOLD, 25));
            knapper[i].setMinSize(60, 60);
            knapper[i].setOnAction(e -> klikk(e));
            bokstaverPane.getChildren().add(knapper[i]);
        }

        panel.setBottom(bokstaverPane);

        Image bakgrunn = new Image(getClass().getResourceAsStream("/images/cover.png"));
        ImageView bakgrunnView = new ImageView(bakgrunn);
        bakgrunnView.setPreserveRatio(false);
        bakgrunnView.setSmooth(true);

        stackPane = new StackPane();
        stackPane.getChildren().addAll(bakgrunnView, panel);

        Scene scene = new Scene(stackPane, 1000, 600);

        bakgrunnView.setFitWidth(scene.getWidth());
        bakgrunnView.setFitHeight(scene.getHeight());
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            bakgrunnView.setFitWidth(newValue.doubleValue());
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            bakgrunnView.setFitHeight(newValue.doubleValue());
        });

        tekst = brukernavn + ", du må gjette ett ord som er relatert til IT. Du har tre forsøk på å trykke på knappen før du mister et liv. Når du har mistet alle tre livene, taper du. Lykke til!";

        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(tekst);
        dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 30; -fx-font-family: 'Monospaced';");
        dialogPane.setPrefSize(800, 400);
        alert.showAndWait();

        stage.setTitle("TechWord");
        stage.setScene(scene);
        stage.show();

    }

    public String innlogging() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Innlogging");
        dialog.setHeaderText(null);
        dialog.setContentText("Skriv inn brukernavn:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "Ukjent bruker";
        }
    }


    public String nyOrd() {
        Random random = new Random();
        int nr = random.nextInt(20) + 1; // Genererer et tilfeldig tall mellom 1 og 20 (inkludert)
        String nyOrd = "";
        try {
            String sql = "SELECT navn FROM Ord WHERE ordID = " + nr;

            // Utfør SQL-spørringen.
            ResultSet rs = databaseConnector.executeQuery(sql);


            while (rs.next()) {
                String navn = rs.getString("navn");
                nyOrd = navn;
                nyOrd = nyOrd.toUpperCase();
            }

        } catch (SQLException e) {
            out.println(e.getMessage());
        }
        return nyOrd;
    }


    public void klikk(ActionEvent e) {
        if (e.getSource() == knapper[knapper.length - 1]) {
            nullstill();

        } else {
            for (int i = 0; i < knapper.length; i++) {
                if (e.getSource() == knapper[i]) {
                    boolean funnet = false;
                    for (int x = 0; x < ordTab.length; x++) {
                        if (bokstaver[i].equals(ordTab[x])) {
                            String cssColor1 = farger[1].toString().replace("0x", "#");
                            knapper[i].setStyle("-fx-background-color: " + cssColor1 + ";");
                            stackPane = (StackPane) ord.getChildren().get(x);
                            Label label = (Label) stackPane.getChildren().get(1);
                            label.setText(bokstaver[i]);
                            funnet = true;
                            spillerPoeng++;
                            tekst = "Dine poeng: " + spillerPoeng;
                            poeng.setText(tekst);
                            riktige++;
                            if (riktige == 5) {
                                tekst = brukernavn + ", du vant! Riktig ord var " + riktigOrd;
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Info");
                                alert.setHeaderText(null);
                                alert.setContentText(tekst);
                                dialogPane = alert.getDialogPane();
                                dialogPane.setStyle("-fx-font-size: 40; -fx-font-family: 'Monospaced';");

                                if (spillerPoeng > highscore) {
                                    highscore = spillerPoeng;
                                    highscoreLabel.setText("HighScore: " + highscore);
                                }
                                alert.showAndWait();

                                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                                pause.setOnFinished(event -> {
                                    resetGame();
                                });
                                pause.play();

                            }
                        }
                    }

                    if (!funnet) {
                        String cssColor0 = farger[0].toString().replace("0x", "#");
                        knapper[i].setStyle("-fx-background-color: " + cssColor0 + ";");
                        ganger--;
                        if (ganger == 0) {
                            lifes--;
                            tekst = brukernavn + ", du har " + lifes + " liv igjen";
                            txt.setText(tekst);
                            if (lifes == 0) {
                                tekst = brukernavn + ", du tapte! Riktig ord var " + riktigOrd;
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Info");
                                alert.setHeaderText(null);
                                alert.setContentText(tekst);
                                dialogPane = alert.getDialogPane();
                                dialogPane.setStyle("-fx-font-size: 40; -fx-font-family: 'Monospaced';");
                                if (spillerPoeng > highscore) {
                                    highscore = spillerPoeng;
                                    highscoreLabel.setText("HighScore: " + highscore);
                                }
                                alert.showAndWait();
                                nullstill();
                            } else {
                                ganger = 3;
                            }
                        }
                    }
                }
            }
        }
    }


    private void resetGame() {
        riktigOrd = nyOrd();
        ordTab = riktigOrd.split("");

        riktige = 0;
        for (int j = 0; j < ordTab.length; j++) {
            StackPane stackPane = (StackPane) ord.getChildren().get(j);
            Label label = (Label) stackPane.getChildren().get(1);
            label.setText("_");
        }

        for (Button knapp : knapper) {
            knapp.setStyle("");
            knapp.setOnAction(e -> klikk(e));
        }

        //lifes = 3;
        ganger = 3;
        tekst = brukernavn + ", du har " + lifes + " liv igjen";
        txt.setText(tekst);
    }


    public void nullstill() {
        lifes = 3;
        tekst = brukernavn + ", du har " + lifes + " liv";
        txt.setText(tekst);
        riktige = 0;
        spillerPoeng = 0;
        tekst = "Dine poeng: " + spillerPoeng;
        poeng.setText(tekst);
        resetGame();
    }


    public void stop() throws Exception {
        databaseConnector.disconnect();
    }
}
