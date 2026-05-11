package com.wallace.dokkan;

import javafx.application.Platform;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateService {
    private static final String LOCAL_VERSION = "1.1.1";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/WallacePiovani/Dokkan/releases/latest";

    public interface UpdateCallback {
        void onUpdateFound(String novaVersao, String DownloadUrl);
    }

    public static void verifyUpdate (UpdateCallback callback){
        System.out.println("debug - iniciando verifyUpdate");
        Thread threadUpdate = new Thread(() -> {
           try{
               System.out.println("debug - dentro do thread async");

               HttpClient client = HttpClient.newBuilder()
                       .connectTimeout(java.time.Duration.ofSeconds(15))
                       .build();

               HttpRequest request = HttpRequest.newBuilder()
                       .uri(URI.create(GITHUB_API_URL))
                       .header("Accept", "application/vnd.github.v3+json")
                       .header("User-agent", "Java-HttpClient-Dokkan")
                       .build();

               System.out.println("debug - enviando requisição ao github");
               HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
               System.out.println("debug - resposta recebida status: " + response.statusCode());

               if (response.statusCode() == 200){
                   String body = response.body();

                   String tagRemota = extrairValor(body, "tag_name").replace("v", "");
                   String downloadUrl = extrairUrlExe(body);

                   if (!tagRemota.equals(LOCAL_VERSION)) {
                       System.out.println("[DEBUG] Update necessário encontrado!");
                       callback.onUpdateFound(tagRemota, downloadUrl);
                   } else {
                       System.out.println("[DEBUG] Versões são iguais. Nada a fazer.");
                   }
               }
               else{
                   System.out.println("debug -> github retornou erro: " + response.statusCode());
               }
           }
           catch (Exception e){
               System.err.println("Erro ao verificar updates: " + e.getMessage());
           }
        });

        threadUpdate.setDaemon(true);
        threadUpdate.setName("Thread-AutoUpdate");
        threadUpdate.start();

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
                String fileName = "DokkanUpdate_" + System.currentTimeMillis() + ".exe";
                java.nio.file.Path pathExe = java.nio.file.Paths.get(tempDir, fileName);

                System.out.println("Iniciando download em: " + pathExe.toString());

                java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                        .followRedirects(java.net.http.HttpClient.Redirect.ALWAYS)
                        .build();

                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(urlDownload))
                        .header("User-Agent", "Mozilla/5.0")
                        .build();

                java.net.http.HttpResponse<java.nio.file.Path> response =
                        client.send(request, java.net.http.HttpResponse.BodyHandlers.ofFile(pathExe));

                if (response.statusCode() == 200) {
                    System.out.println("Download concluído: " + pathExe.toString());

                    ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "start", "/b", "", pathExe.toAbsolutePath().toString());

                    pb.directory(new File(tempDir));
                    pb.start();

                    System.out.println("Instalador lançado. Fechando em 1 segundo...");

                    Platform.runLater(() -> {
                        try {
                            Thread.sleep(500);
                            System.exit(0);
                        } catch (Exception e) {
                            System.exit(0);
                        }
                    });
                } else {
                    System.err.println("Erro no download: " + response.statusCode());
                }

            } catch (Exception e) {
                System.err.println("Erro crítico no update: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}

