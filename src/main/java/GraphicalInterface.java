/*
 * Name: discord-bot
 * Date: 15/1/2019
 * Author(s): Jacob Dixon (RoyalSlothKing) @RoyalSlothKing.com
 * Repo: https://github.com/JacobDixon0/discord-bot
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class GraphicalInterface extends Application {

    private static boolean keyPressed = false;
    private static boolean inMenu = false;

    private static int[] windowSize = {250, 140};
    private static int[] popupSize = {400, 400};

    private static String popupDiologPromptText = "";

    @Override
    public void start(Stage primaryStage) throws Exception {

        FileChooser fileChooser = new FileChooser();

        Stage stage = new Stage();
        Pane root = new Pane();
        Scene scene = new Scene(root, windowSize[0], windowSize[1]);
        Timer timer = new Timer();

        TextField tf = new TextField();
        tf.setMinWidth(180);
        tf.setLayoutX(10);
        tf.setLayoutY(10);
        tf.setPromptText("Send message to all guilds");

        Button btn = new Button("send");
        btn.setLayoutX(10);
        btn.setLayoutY(50);
        btn.setOnAction(e -> {
            if (!tf.getText().equals("")) {
                Main.sendAnnouncement(tf.getText());
                tf.setText("");
            }
        });

        Button btn2 = new Button("close gui");
        btn2.setLayoutX(10);
        btn2.setLayoutY(80);
        btn2.setOnAction(e -> {
            stage.close();
        });

        Button btn3 = new Button("stop");
        btn3.setLayoutX(78);
        btn3.setLayoutY(80);
        btn3.setId("stop-button");
        btn3.setOnAction(e -> {
            System.out.println("stopping SlothBot...");
            Main.jda.shutdown();
            System.exit(0);
        });

        Button btn4 = new Button("restart");
        btn4.setLayoutY(80);
        btn4.setLayoutX(121);
        btn4.setOnAction(e ->
                Main.restartJDA()
        );

        Button btn5 = new Button("servers");
        btn5.setLayoutX(176);
        btn5.setLayoutY(80);
        btn5.setOnAction(e ->
                openServerListPopup()
        );

        Label label = new Label("uptime: 0");
        label.setLayoutX(10);
        label.setLayoutY(115);
        label.setTextFill(Color.WHITE);

        RadioButton radioButton = new RadioButton("maintenance mode");
        radioButton.setLayoutY(54);
        radioButton.setLayoutX(58);
        radioButton.setOnAction(e -> {
            if (radioButton.isSelected()) {
                Main.jda.getPresence().setGame(Game.playing("undergoing maintenance"));
            } else Main.jda.getPresence().setGame(Game.playing(Main.gamePlaying));
        });

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> label.setText("uptime: " + Main.getUptime() + "s"));
            }
        }, 0, 1000);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !keyPressed) {
                if (!tf.getText().equals("")) {
                    Main.sendAnnouncement(tf.getText());
                    tf.setText("");
                }
                keyPressed = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER && keyPressed) {
                keyPressed = false;
            }
        });

        root.getChildren().addAll(btn, btn2, btn3, btn4, btn5, label, tf, radioButton);

        loadStageAssets(stage, scene);

        stage.setTitle("SlothBot - GUI");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            System.out.println("stopping SlothBot...");
            Main.jda.shutdown();
            System.exit(0);
        });
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setX(Toolkit.getDefaultToolkit().getScreenSize().width - windowSize[0] - 10);
        stage.setY(Toolkit.getDefaultToolkit().getScreenSize().height - windowSize[1] - 75);
        stage.setAlwaysOnTop(true);
        stage.show();
    }

    private static void openServerListPopup() {
        Stage popupStage = new Stage();
        Pane root = new Pane();
        VBox serverList = new VBox(10);
        Scene scene = new Scene(root, popupSize[0], popupSize[1]);

        inMenu = true;

        for (Guild guild : Main.jda.getGuilds()) {
            Button btn = new Button(guild.getName());
            btn.setOnAction(e -> {
                popupStage.setScene(serverSelectedPopup(popupStage, scene, guild));
            });
            serverList.getChildren().add(btn);
        }

        serverList.setLayoutX(10);
        serverList.setLayoutY(10);
        root.getChildren().add(serverList);

        loadStageAssets(popupStage, scene);
        popupStage.setScene(scene);
        popupStage.setTitle("SlothBot - Server List");
        popupStage.setAlwaysOnTop(true);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setOnCloseRequest(e -> {
            inMenu = false;
            popupStage.close();
        });
        popupStage.show();
    }

    private static Scene serverSelectedPopup(Stage popupStage, Scene serverList, Guild guild) {

        Pane root = new Pane();
        VBox channelList = new VBox();
        Scene scene = new Scene(root, popupSize[0], popupSize[1]);
        int longestServerName = 0;

        channelList.setLayoutX(20);
        channelList.setLayoutY(45);

        loadStageAssets(scene);

        Button btn0 = new Button("back");
        btn0.setLayoutX(10);
        btn0.setLayoutY(10);
        btn0.setOnAction(e ->
                popupStage.setScene(serverList)
        );

        Button btn1 = new Button("Members");
        btn1.setLayoutX(60);
        btn1.setLayoutY(10);
        btn1.setOnAction(e -> {
            popupStage.setScene(serverMembersPopup(popupStage, scene, guild));
        });

        Rectangle r0 = new Rectangle(10, 45, 140, guild.getTextChannels().size() * 23);
        r0.setFill(Color.web("#42464d"));
        r0.setArcHeight(10);
        r0.setArcWidth(10);

        root.getChildren().addAll(btn0, btn1, r0);

        for (TextChannel channel : guild.getTextChannels()) {
            if(channel.getName().length() > longestServerName){
                longestServerName = channel.getName().length();
            }
            Text txt = new Text(channel.getName());
            txt.setX(20);
            txt.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
            if(channel.canTalk()) {
                txt.setFill(Color.WHITE);
                txt.setOnMouseEntered(e -> txt.setFill(Color.GREY));
                txt.setOnMouseExited(e -> txt.setFill(Color.WHITE));
                txt.setOnMouseClicked(e ->
                        popupStage.setScene(messageGuildChannel(channel, popupStage, scene))
                );
            } else {
                txt.setFill(Color.GREY);
            }

            channelList.getChildren().add(txt);
        }

        r0.setWidth(longestServerName * 9);

        root.getChildren().add(channelList);

        return scene;
    }

    private static Scene serverMembersPopup(Stage popupStage, Scene prevScene, Guild guild){

        Pane root = new Pane();
        VBox memberList = new VBox();
        Scene scene = new Scene(root, popupSize[0], popupSize[1]);

        scene.getStylesheets().add("file:./style.css");

        memberList.setLayoutX(20);
        memberList.setLayoutY(45);

        Rectangle r0 = new Rectangle(10, 45, 140, guild.getMembers().size() * 24);
        r0.setFill(Color.web("#42464d"));
        r0.setArcHeight(10);
        r0.setArcWidth(10);

        root.getChildren().add(backButton(10, 10, popupStage, prevScene));

        for(Member m : guild.getMembers()){
            Text txt = new Text(m.getEffectiveName());
            txt.setFill(Color.WHITE);
            txt.setStyle("-fx-font-size: 16px;");
            txt.setX(10);
            txt.setOnMouseEntered(e -> txt.setFill(Color.GREY));
            txt.setOnMouseExited(e -> txt.setFill(Color.WHITE));
            txt.setOnMouseClicked(e -> popupStage.setScene(memberSelectedPopup(popupStage, scene, m)));
            memberList.getChildren().add(txt);
        }

        root.getChildren().addAll(r0, memberList);

        return scene;
    }

    private static Scene messageGuildChannel(TextChannel channel, Stage stage, Scene prevScene) {

        Pane root = new Pane();
        Scene scene = new Scene(root, 200, 80);

        loadStageAssets(scene);

        TextField tf = new TextField();
        tf.setPromptText("send message to " + channel.getName());
        tf.setLayoutX(10);
        tf.setLayoutY(10);
        tf.setPrefWidth(180);

        Button btnBack = new Button("back");
        btnBack.setLayoutY(40);
        btnBack.setLayoutX(10);
        btnBack.setOnAction(e ->
            stage.setScene(prevScene)
        );

        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                if(channel.canTalk()) {
                    if(Main.checkMessageValidity(tf.getText())) {
                        channel.sendMessage(tf.getText()).queue();
                        stage.setScene(prevScene);
                    } else {
                        System.err.println("ERROR: Invalid Message - no content ");
                        tf.clear();
                        root.requestFocus();
                    }
                } else {
                    Main.consoleOut("could not send message to channel: " + channel.getGuild().getName() + "::" + channel.getName());
                    stage.setScene(prevScene);
                }
            }
        });

        root.getChildren().addAll(tf, btnBack);
        root.requestFocus();

        return scene;
    }

    private static Scene memberSelectedPopup(Stage popupStage, Scene prevScene, Member member){

        Pane root = new Pane();
        Scene scene = new Scene(root, 200, 100);

        loadStageAssets(scene);

        root.getChildren().add(backButton(10, 10, popupStage, prevScene));

        Button btn0 = new Button("Ban");
        btn0.setLayoutX(10);
        btn0.setLayoutY(50);
        btn0.setId("stop-button");
        btn0.setOnAction(e -> {
            if(member.getGuild().getSelfMember().canInteract(member)) {
                member.getGuild().getController().ban(member, 0).queue();
            } else {
                System.err.println("ERROR: Could not ban member: " + member.getEffectiveName());
            }
        });

        Button btn1 = new Button("Message User");
        btn1.setLayoutX(50);
        btn1.setLayoutY(50);

        root.getChildren().addAll(btn0, btn1);

        return scene;
    }

    private static Button backButton(int x, int y, Stage stage, Scene prevScene){
        Button btn = new Button("Back");
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setOnAction(e -> stage.setScene(prevScene));
        return btn;
    }

    private static void loadStageAssets(Stage stage, Scene scene) {

        try {
            scene.getStylesheets().add("file:./style.css");
            stage.getIcons().add(new Image("file:./favicon.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: Failed to load application style/favicon, using default style.");
        }

    }

    private static void loadStageAssets(Scene scene) {

        try {
            scene.getStylesheets().add("file:./style.css");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: Failed to load application style, using default style.");
        }

    }

    static void initialize(String[] args) {
        launch(args);
    }

}
