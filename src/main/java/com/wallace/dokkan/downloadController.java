package com.wallace.dokkan;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class downloadController {
    @FXML
    private TextField txtUrl;

    @FXML
    private Label lblStatus;

    @FXML
    private Button btnDownload;

    @FXML
    private ProgressBar progressDownload;

    @FXML
    protected void onDownloadButtonClick(){
        String url = txtUrl.getText();
        String pasta = selecionarPasta();

        if(url.isEmpty()){
            System.out.println("Por favor, insira um URL valido!");
            return;
        }

        if (pasta != null){
            executarDownload(url, pasta);
        }

        //executarDownload(url,pasta);

    };
    private String selecionarPasta() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecionar Pasta de Destino");
        File selectedDirectory = directoryChooser.showDialog(txtUrl.getScene().getWindow());

        return (selectedDirectory != null) ? selectedDirectory.getAbsolutePath() : null;
    }

    private void executarDownload(String url, String pasta){
        lblStatus.setText("Iniciando download...");
        progressDownload.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        new Thread(() -> {
            try{
                ProcessBuilder pb = new ProcessBuilder(
                        "yt-dlp",
                        "-x",
                        "--audio-format", "mp3",
                        "-o", pasta + "//%(title)s.%(ext)s",
                        url
                );

                Process p = pb.start();
                int exitCode = p.waitFor();

                Platform.runLater(() ->{
                    if (exitCode == 0) {
                        lblStatus.setText("Download finalizado com sucesso!");
                        progressDownload.setProgress(1.0);
                    } else {
                        lblStatus.setText("Erro ao baixar o audio.");
                        progressDownload.setProgress(0);
                    }
                });

                System.out.println("Download finalizado!");
            }
        catch(Exception e){
                e.printStackTrace();
            }
        }).start();

    }

}
