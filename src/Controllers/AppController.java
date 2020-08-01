package Controllers;

import application.WaveFormService;
import application.WaveVisualization;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    public static File selectedFile;
    public static File savedFile;

    public static String status = null;
    AudioInputStream audioInputStream;
    Clip clip;

    {
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public Long currentFrame = clip.getMicrosecondPosition();

    @FXML
    Label fileName;
    @FXML
    HBox hbox;

    @FXML
    public void uploadFile(ActionEvent e) throws IOException, UnsupportedAudioFileException {
        FileChooser fileChooser = new FileChooser();
        Window stage = null;
        selectedFile = fileChooser.showOpenDialog(stage);
        audioInputStream = AudioSystem.getAudioInputStream(new File(selectedFile.getAbsolutePath()).getAbsoluteFile());
        fileName.setText(selectedFile.getName());
        generateWaveform(selectedFile);
        //extract(selectedFile);

    }

    @FXML
    public void playButton(ActionEvent e) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        if (status.equals("played")) {
            clip.setMicrosecondPosition(currentFrame);
            clip.start();
        } else {
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            status = "played";
        }
    }

    @FXML
    public void pause() {
        if (status.equals("paused")) {
            System.out.println("audio is already paused");
            return;
        }
        currentFrame = clip.getMicrosecondPosition();
        clip.stop();
        status = "played";
    }

    public void generateWaveform(File file) throws IOException {
        WaveVisualization waveVisualization = new WaveVisualization(400, 32);
        Button btn1 = new Button();
        btn1.setText("Button1");
        waveVisualization.getWaveService().startService(file.getAbsolutePath(), WaveFormService.WaveFormJob.AMPLITUDES_AND_WAVEFORM);
        hbox.getChildren().add(btn1);

    }

    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }


    @FXML
    public void record(ActionEvent e) {
        try {

            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing

            System.out.println("Start capturing...");

            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Start recording...");
            FileChooser fc = new FileChooser();
            Window stage = null;
            fc.setTitle("Save");
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("wav", "*.wav*"));
            savedFile = fc.showSaveDialog(stage);
            // start recording
            AudioSystem.write(ais, fileType, new File(savedFile.getAbsolutePath()));

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    @FXML
    public void finish(ActionEvent e) {
        line.stop();
        line.close();
        System.out.println("Finished");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


}