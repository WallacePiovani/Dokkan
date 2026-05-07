package com.wallace.dokkan;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.OutputStream;

public class downloadController {

    @FXML
    public void initialize() {
        System.out.println("[DEBUG] 0. Controller inicializado!");

        try {
            System.out.println("[DEBUG] 0.1 Tentando chamar UpdateService...");

            // Chamada direta sem lambda primeiro para testar o acesso à classe
            UpdateService.verifyUpdate((novaVersao, downloadURL) -> {
                Platform.runLater(() -> {
                    System.out.println("[DEBUG] Callback recebido: " + novaVersao);
                    mostrarAlertaNovoUpdate(novaVersao, downloadURL);
                });
            });

            System.out.println("[DEBUG] 0.2 Chamada ao UpdateService enviada.");

        } catch (Throwable t) {
            // Usamos Throwable para pegar inclusive erros de carregamento de classe (Error)
            System.out.println("[DEBUG] ERRO FATAL AO CHAMAR SERVICE: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private void mostrarAlertaNovoUpdate(String versao, String url){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Atualização Disponível");
        alert.setHeaderText("Uma nova versão (" + versao + ") foi encontrada!");
        alert.setContentText("Clique em OK para iniciar o download e instalação automática.");

        javafx.stage.Stage stage = (javafx.stage.Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        alert.initOwner(txtUrl.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                System.out.println("Usuário aceitou o update para: " + url);
                UpdateService.baixarEInstalar(url);
            }
        });
    }


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

    };
    private String selecionarPasta() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecionar Pasta de Destino");
        File selectedDirectory = directoryChooser.showDialog(txtUrl.getScene().getWindow());

        return (selectedDirectory != null) ? selectedDirectory.getAbsolutePath() : null;
    }

    private void executarDownload(String url, String pasta){
        lblStatus.setText("Baixando ...");
        progressDownload.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        new Thread(() -> {
            try{

                String pathYtDlp = new File("app/bin/yt-dlp.exe").getAbsolutePath();
                String pathFfmpeg = new File("app/bin/ffmpeg.exe").getAbsolutePath();

                // para testes
                /*ProcessBuilder pb = new ProcessBuilder(
                        "yt-dlp",
                        "-x",
                        "--audio-format", "mp3",
                        "--audio-quality", "0",
                        "-N", "8",
                        "--newline",
                        "--progress",
                        "-o", pasta + "/%(title)s.%(ext)s",
                        url
                ); */

                ProcessBuilder pb = new ProcessBuilder(
                        pathYtDlp,
                        "-x",
                        "--audio-format", "mp3",
                        "--audio-quality", "0",
                        "-N", "8",
                        "--ffmpeg-location", pathFfmpeg,
                        "--newline",
                        "--progress",
                        "--retries","infinite",
                        "--fragment-retries","infinite",
                        "-o", pasta + "/%(title)s.%(ext)s",
                        url
                );
                pb.redirectErrorStream(true); //Caso haja, redireciona o erro para a saida padrão e consome o buffer.
                Process p = pb.start();
                //p.getInputStream().transferTo(OutputStream.nullOutputStream());


                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        System.out.println(linha);
                    }
                }


                int exitCode = p.waitFor();

                Platform.runLater(() ->{
                    if (exitCode == 0) {
                        lblStatus.setText("Download finalizado com sucesso!");
                        progressDownload.setProgress(1.0);
                    } else {
                        lblStatus.setText("Erro ao baixar o audio.");
                        System.out.checkError();
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
