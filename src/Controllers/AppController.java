package Controllers;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.poi.hslf.record.Slide;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.sound.sampled.*;
import javax.swing.text.Element;
import javax.swing.text.TableView;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import static com.sun.javafx.application.PlatformImpl.exit;

public class AppController implements Initializable {
    public HashMap<String, File> songs = new HashMap<>();
    public int index;
    public int i = 0;
    public Media m;
    public MediaPlayer player;
    @FXML
    public BorderPane frame;
    @FXML
    public Slider volumeSlider;
    @FXML
    public Text songName;
    @FXML
    public ImageView iv;
    @FXML
    public Button playBtn;
    @FXML
    public ListView list;
    @FXML
    public Slider Slider;
    @FXML
    public ProgressBar pb;
    private Object ime;
    public  File file;
    public Metadata metadata;
    public MediaPlayer.Status currentStatus = MediaPlayer.Status.PAUSED;
    public void openFile(){
        FileChooser fc = new FileChooser();
        Window window = null;
         file = fc.showOpenDialog(window);
         System.out.println(file.toURI().toString());

        addToQueue(file);
    }
    public void stop(){
    player.stop();
    playBtn.setText("Play");
    }
    public void addToQueue(File f){
        Metadata md = getMetaData(f);
        String data = md.get("xmpDM:artist") + " - " + md.get("title");
        songs.put(data,f);
        list.getItems().add(data);

        i++;

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
    public void mount() throws IOException {
        if(currentStatus == MediaPlayer.Status.PLAYING){
        player.stop();
        }
        this.ime = this.list.getSelectionModel().getSelectedItem();
        index = list.getItems().indexOf(ime);
        file = songs.get(ime);
        System.out.println(index);
        m = new Media(songs.get(ime.toString()).toURI().toString());
        player = new MediaPlayer(m);
        player.seek(new Duration(0));
        currentStatus = MediaPlayer.Status.PAUSED;
        playButton();

    }
    public void loadView() {

        Image img = new Image("file:/C:/Users/Marko/IdeaProjects/Studio/src/anim.gif");
        ImageView iv = new ImageView();
        iv.setImage(img);
        iv.setVisible(true);
        frame.setCenter(iv);
        iv.isResizable();
        iv.setPreserveRatio(true);
        iv.resize(300,300);












    }
    public void playButton() {
        Metadata md = getMetaData(file);
//        loadView();
        songName.setText(md.get("xmpDM:artist") + " - " + md.get("title"));

        volumeSlider.setValue(player.getVolume()*100);
        volumeSlider.valueProperty().addListener(observable -> player.setVolume(volumeSlider.getValue()/100));


        if(currentStatus == MediaPlayer.Status.PLAYING) {
            currentStatus = MediaPlayer.Status.PAUSED;
            playBtn.setText("Play");
            player.pause();
        }
        else if(currentStatus == MediaPlayer.Status.PAUSED || currentStatus == MediaPlayer.Status.STOPPED) {
            playBtn.setText("Pause");
            currentStatus = MediaPlayer.Status.PLAYING;

            System.out.println("Player will start at: " + player.getCurrentTime());
            player.play();
        }

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                //coding...
                Duration d = player.getCurrentTime();

                Slider.setValue(d.toSeconds());
            }
        });
        Slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (Slider.isPressed()) {
                    double val = Slider.getValue();
                    player.seek(new Duration(val *1000));
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
        String ime = (String) list.getItems().get(index);
        File file = songs.get(ime);
        player.stop();
        m = new Media(file.toURI().toString());
        System.out.println(ime);
        player = new MediaPlayer(m);
        playButton();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void next() throws IOException {
        index++;

        if(index>songs.size()-1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("Trenutno svira zadnja pjesma");
            alert.show();
            return;
        }
        String ime = (String) list.getItems().get(index);
        File file = songs.get(ime);
        player.stop();
        m = new Media(file.toURI().toString());
        System.out.println(ime);
        player = new MediaPlayer(m);
        playButton();
    }

    public void addToQueue(MouseDragEvent mouseDragEvent) {
        list.getItems().get(i);
    }
}