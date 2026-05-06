package com.wallace.dokkan;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class UpdateService {
    private static final String LOCAL_VERSION = "0.9.0";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/WallacePiovani/Dokkan/releases/latest";

    public interface UpdateCallback {
        void onUpdateFound(String novaVersao, String DownloadUrl);
    }

    public static void verifyUpdate (UpdateCallback callback){
        CompletableFuture.runAsync(() -> {
           try{
               HttpClient client = HttpClient.newHttpClient();
               HttpRequest request = HttpRequest.newBuilder()
                       .uri(URI.create(GITHUB_API_URL))
                       .header("Accept", "application/vnd.github.v3+json")
                       .build();
               HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

               if (response.statusCode() == 200){
                   String body = response.body();

                   String tagRemota = extrairValor(body, "tag_name").replace("v", "");
                   String downloadUrl = extrairUrlExe(body);

                   if (!tagRemota.equals(LOCAL_VERSION)) {
                       callback.onUpdateFound(tagRemota, downloadUrl);
                   }
               }
           }
           catch (Exception e){
               System.err.println("Erro ao verificar updates: " + e.getMessage());
           }
        });
    }

    private static String extrairValor(String json, String chave) {
        String padrao = "\"" + chave + "\":\"";
        int inicio = json.indexOf(padrao) + padrao.length();
        int fim = json.indexOf("\"", inicio);
        return json.substring(inicio, fim);
    }

    private static String extrairUrlExe(String json) {
        try {
            String chaveDownload = "\"browser_download_url\":\"";

            int indexUltimoDownload = json.lastIndexOf(chaveDownload);

            while (indexUltimoDownload != -1) {
                int inicioUrl = indexUltimoDownload + chaveDownload.length();
                int fimUrl = json.indexOf("\"", inicioUrl);
                String urlEncontrada = json.substring(inicioUrl, fimUrl);

                if (urlEncontrada.toLowerCase().endsWith(".exe")) {
                    return urlEncontrada;
                }

                indexUltimoDownload = json.lastIndexOf(chaveDownload, indexUltimoDownload - 1);
            }
        } catch (Exception e) {
            System.err.println("Erro ao parsear URL: " + e.getMessage());
        }
        return "";
    }

    public static void baixarEInstalar(String urlDownload) {
        new Thread(() -> {
            try {
                String tempDir = System.getProperty("java.io.tmpdir");
                java.nio.file.Path pathExe = java.nio.file.Paths.get(tempDir, "DokkanUpdate.exe");

                System.out.println("Iniciando download da atualização...");
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(urlDownload))
                        .build();

                client.send(request, java.net.http.HttpResponse.BodyHandlers.ofFile(pathExe));

                System.out.println("Download concluído. Iniciando instalador...");


                new ProcessBuilder(pathExe.toString(), "/SILENT").start();

                javafx.application.Platform.runLater(() -> {
                    System.exit(0);
                });

            } catch (Exception e) {
                System.err.println("Erro no processo de update: " + e.getMessage());
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(urlDownload));
                } catch (Exception ignored) {}
            }
        }).start();
    }
}

