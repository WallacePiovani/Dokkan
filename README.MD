# 🎵 Dokkan - YouTube to MP3 Converter

O **Dokkan** é uma ferramenta desktop desenvolvida em **JavaFX** projetada para converter e baixar o audio de videos do Youtube. Ele permite converter vídeos do YouTube em arquivos MP3 de alta qualidade de forma rápida, utilizando o poder do `yt-dlp` e `ffmpeg`.

## Funcionalidades

*   **Interface Intuitiva**: Interface gráfica limpa construída com JavaFX.
*   **Alta Performance**: Otimizado para processadores multi-core, podendo realizar downloads paralelos.
*   **Conversão Rápida**: Extração direta de áudio sem re-processamento desnecessário.

## Tecnologias Utilizadas

*   **Java 21**: Versão LTS mais recente para máxima performance.
*   **JavaFX 21**: Framework para a interface gráfica moderna.
*   **Maven**: Gerenciamento de dependências e automação de build.
*   **yt-dlp**: Motor de download potente e atualizado.
*   **FFmpeg**: O padrão da indústria para manipulação de áudio e vídeo.

## Como Instalar

Para facilitar o uso, você pode baixar a versão mais recente na aba [Releases](https://github.com/WallacePiovani/Dokkan/releases).

1. Baixe o instalador `.exe`.
2. Siga as instruções de instalação do Windows.
3. O programa criará um atalho na sua área de trabalho.

> **Nota**: Os motores `yt-dlp` e `ffmpeg` já estão inclusos no pacote de instalação, não sendo necessário baixar nada adicional.

## Para Desenvolvedores (Build Local)

Se você deseja clonar o repositório e compilar por conta própria:

1. Certifique-se de ter o **JDK 21** instalado.
2. Clone o repositório:
   ```bash
   git clone https://github.com/WallacePiovani/Dokkan.git
    ```
3. Compile o projeto com Maven:
    ```
    ./mvnw clean package
   ```
4. Para gerar o instalador (Windows), utilize o jpackage conforme descrito na documentação interna.


## Próximos Passos (To-Do)

Esta seção detalha o roadmap de desenvolvimento do Dokkan, focando em aprimorar a experiência do usuário e a eficiência técnica do software:

### Progresso e Feedback em Tempo Real
- [ ] **Visão de Tempo Real**: Implementar a captura de dados via `BufferedReader` para converter o log do terminal em uma barra de progresso real (0% a 100%).
- [ ] **Estimativa de Tempo (ETA)**: Calcular e exibir o tempo restante para a conclusão do download e conversão.
- [ ] **Status por Etapa**: Criar labels dinâmicos que informem se o app está "Baixando", "Convertendo" ou "Finalizando" o arquivo.

### Otimização e Performance
- [ ] **Otimização de Finalização**: Refinar os parâmetros do FFmpeg para reduzir o tempo de encodamento após o término do download.
- [ ] **Multi-threading Adaptativo**: Ajustar automaticamente o número de threads (`-N`) com base nos núcleos disponíveis no sistema (otimização para processadores high-end).
### Gestão de Downloads
- [ ] **Fila de Downloads (Queue)**: Adicionar suporte para enfileirar múltiplas URLs e processá-las em sequência ou em lote.
- [ ] **Download de Playlists**: Implementar a lógica necessária para baixar álbuns ou listas de reprodução completas de uma só vez.

### Melhorias de UI/UX
- [ ] **Dark Mode**: Adicionar um tema escuro para facilitar o uso durante sessões de RPG em ambientes com pouca luz.
- [ ] **Seletor de Diretórios**: Implementar um `DirectoryChooser` nativo para que o usuário escolha a pasta de destino de forma visual.
- [ ] **Notificações do Sistema**: Adicionar alertas sonoros ou pop-ups de sistema ao concluir downloads em segundo plano.
