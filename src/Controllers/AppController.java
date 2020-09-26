package Controllers;



import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import java.io.*;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;


public class AppController implements Initializable {
    public HashMap<String, File> songs = new HashMap<>();
    String extension;
    public int index;
    public int i = 0;
    public Media m;
    public MediaPlayer player;
    @FXML
    Button previousBtn;
    @FXML
    MenuItem closeMenu;
    @FXML
    public BorderPane frame;
    @FXML
    public Slider volumeSlider;
    @FXML
    public Text songName;
    @FXML
    public Button playBtn;
    @FXML
    public ListView list;
    @FXML
    public Slider Slider;
    @FXML
    public ImageView addBtnImage;
    @FXML
    public ImageView nextBtnImage;
    @FXML
    public ImageView previousBtnImage;

    @FXML
    public ImageView randomBtnImage;
    @FXML
    public ImageView repeatBtnImage;
    @FXML
    public ImageView muteBtnImage;
    @FXML
    public ImageView stopBtnImage;
    @FXML
    public ProgressBar pb;
    public  File file;
    public MediaPlayer.Status currentStatus = MediaPlayer.Status.PAUSED;
    private Object ime;
    Stage stage;
    Metadata metadata;
    Metadata md;
    public double d;
    public void openFile() {
        try {

            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter mp3 = new FileChooser.ExtensionFilter("music files","*.mp3", "*.mp4", "*waw");
            fc.getExtensionFilters().add(mp3);

            Window window = null;
            file = fc.showOpenDialog(window);
            int i = file.getName().lastIndexOf(".");
             extension = file.getName().substring(i+1);
        }
        catch(NullPointerException ex ){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Niste odabrali pjesmu");
            alert.show();
            return;
        }

        addToQueue(file);

    }
    @FXML
    public void random(){
        Random rand = new Random();
        currentStatus = MediaPlayer.Status.PAUSED;
        int random = rand.nextInt(songs.size());
        String ime = list.getItems().get(random).toString();
        file = songs.get(ime);
        player = new MediaPlayer(new Media(file.toURI().toString()));
        playButton();
    }

    @FXML
    public void repeat(){
        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                player.seek(Duration.ZERO);
                player.play();
            }
        });
    }

    @FXML
    public void stop(){
        currentStatus = MediaPlayer.Status.PAUSED;
        initialize(null,null);

        player.stop();

    }
    public void addToQueue(File f) {
            try {
                String data;
                if(extension.equals("mp3") || extension.equals("wav")){

                    md = getMetaData(file);
                    if(md.get("title") == null || md.get("xmpDM:artist") == null){
                        throw new NullPointerException();
                    }
                     data = md.get("xmpDM:artist") + " - " + md.get("title");
                }
                else{
                    data = file.getName();
                }

                songs.put(data, f);
                list.getItems().add(data);

            }
        catch (NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Pjesma ne sadrzi metadata, molimo odaberite drugu");
            alert.show();


        }



        }





    public void mount() {
        try {


            if (currentStatus == MediaPlayer.Status.PLAYING) {
                player.stop();
            }
            this.ime = this.list.getSelectionModel().getSelectedItem();

            index = list.getItems().indexOf(ime);
            file = songs.get(ime);
            m = new Media(songs.get(ime.toString()).toURI().toString());
            player = new MediaPlayer(m);
            player.seek(new Duration(0));
            currentStatus = MediaPlayer.Status.PAUSED;
            playButton();
        }catch(NullPointerException exp){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Niste odabrali pjesmu");
            alert.show();
            return;
        }
    }
    public void loadView() {
            i = file.getName().lastIndexOf(".");
            extension = file.getName().substring(i+1);
            System.out.println(extension);
        if(extension.equals("mp3")|| extension.equals("wav")){
            Image img = new Image("file:/C:/Users/Marko/IdeaProjects/Studio/src/anim.gif");
            ImageView iv = new ImageView();
            iv.setImage(img);
            iv.setVisible(true);
            frame.setCenter(iv);
            iv.isResizable();
            iv.setPreserveRatio(true);
            iv.setFitHeight(300);
            iv.setFitWidth(300);


        }
        else{
            MediaView mw = new MediaView(player);
            mw.setFitHeight(300);
            mw.setFitWidth(300);
            frame.setCenter(mw);

        }










    }

    public Metadata getMetaData(File file){
        try {

            InputStream input = new FileInputStream(file);
            ContentHandler handler = new DefaultHandler();
             metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();

        }

        catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return metadata;
    }
    public void playButton() {
        loadView();
        if(extension.equals("mp3"))
        songName.setText(md.get("xmpDM:artist") + " - " + md.get("title"));
        else{
            songName.setText(file.getName());
        }

        volumeSlider.setValue(player.getVolume()*100);
        volumeSlider.valueProperty().addListener(observable -> player.setVolume(volumeSlider.getValue()/100));


        if(currentStatus == MediaPlayer.Status.PLAYING) {

            currentStatus = MediaPlayer.Status.PAUSED;
            initialize(null,null);

            player.pause();
        }
        else if(currentStatus == MediaPlayer.Status.PAUSED || currentStatus == MediaPlayer.Status.STOPPED) {
            currentStatus = MediaPlayer.Status.PLAYING;
            initialize(null,null);

            System.out.println("Player will start at: " + player.getCurrentTime());
            player.play();
        }

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                 d = player.getCurrentTime().toSeconds()/player.getTotalDuration().toSeconds();
                Slider.setValue(d*100);
            }
        });
        Slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (Slider.isPressed()) {
                    double postotak = Slider.getValue()/100;

                    player.seek(new Duration(postotak*player.getTotalDuration().toMillis()));
                }
            }
        });


    player.setOnPaused(() -> System.out.println("Paused at: " + player.getCurrentTime()));
    }

    @FXML
    public void previous() {
        index--;
        if(index<0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Trenutno svira prva pjesma");
            alert.show();
            return;
        }
        currentStatus = MediaPlayer.Status.PAUSED;

        String ime = (String) list.getItems().get(index);
        file = songs.get(ime);
        player.stop();
        m = new Media(file.toURI().toString());
        player = new MediaPlayer(m);
        playButton();
    }
    @FXML
    public void next() {
        index++;
        if(index>songs.size()-1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Trenutno svira zadnja pjesma");
            alert.show();
            return;
        }
        String ime = (String) list.getItems().get(index);
        currentStatus = MediaPlayer.Status.PAUSED;
        file = songs.get(ime);
        player.stop();
        m = new Media(file.toURI().toString());
        player = new MediaPlayer(m);
        System.out.println(currentStatus);
        playButton();
    }
    public void resize(ImageView img){
            img.setFitHeight(15);
            img.setFitWidth(15);

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    closeMenu.setStyle("-fx-background-color:#C0C0C0");
    Image play = new Image("file:images\\play.png");
    ImageView playGraphic = new ImageView(play);
    Image previous = new Image("file:images\\previous.png");
    Image next = new Image("file:images\\next.png");
    Image stop = new Image("file:images\\stop.png");
    Image repeat = new Image("file:images\\repeat.png");
    Image add = new Image("file:images\\add.png");
    Image soundOff = new Image("file:images\\soundOff.png");
    Image random = new Image("file:images\\random.png");

    previousBtnImage.setImage(previous);
    resize(previousBtnImage);
    nextBtnImage.setImage(next);
    resize(nextBtnImage);
    muteBtnImage.setImage(soundOff);
    resize(muteBtnImage);
    stopBtnImage.setImage(stop);
    resize(stopBtnImage);
    repeatBtnImage.setImage(repeat);
    resize(repeatBtnImage);
    addBtnImage.setImage(add);
    resize(addBtnImage);
    muteBtnImage.setImage(soundOff);
    resize(muteBtnImage);
    randomBtnImage.setImage(random);
    resize(randomBtnImage);




        if(currentStatus == MediaPlayer.Status.PAUSED){
        playBtn.setGraphic(playGraphic);
        playGraphic.setFitWidth(20);
        playGraphic.setFitHeight(20);
        Insets inset = new Insets(2,0,2,0);
        playBtn.setPadding(inset);


    }
    if(currentStatus == MediaPlayer.Status.PLAYING){
        Image pause = new Image("file:images\\pause.png");
        ImageView pauseGraphic = new ImageView(pause);
        playBtn.setGraphic(pauseGraphic);
        pauseGraphic.setFitWidth(20);
        pauseGraphic.setFitHeight(20);
    }
    }


    @FXML
    public void emptyQueue(){
        songs.clear();
        list.getItems().remove(0,songs.size());
    }


    public void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }
    public void mute(){
        volumeSlider.setValue(0);
    }
}