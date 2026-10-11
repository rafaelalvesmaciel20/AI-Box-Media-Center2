# AI Box Media Center

Launcher Android nativo em modo paisagem para AI Box com Android completo e Play Store.

## Recursos
- Grade dos aplicativos instalados e iniciáveis.
- Filtros heurísticos: Todos, Vídeo, TV e Áudio.
- Atalho para Play Store quando disponível.
- Verificação de versão usando `version.json` hospedado no GitHub.
- Pressionar e segurar um aplicativo abre a confirmação de desinstalação do Android.
- Compilação do APK de teste pelo GitHub Actions.

## Como usar pelo celular
1. Crie no GitHub um repositório chamado `AI-Box-Media-Center`.
2. Envie o conteúdo deste projeto para a raiz do repositório, mantendo o caminho `.github/workflows/build-apk.yml`.
3. Em `app/src/main/java/com/aibox/mediacenter/MainActivity.kt`, substitua `SEU_USUARIO` na URL `versionJsonUrl` pelo seu usuário/organização GitHub.
4. Faça a mesma substituição em `version.json` no campo `apkUrl`.
5. Abra **Actions**, selecione **Build AI Box Media Center APK** e use **Run workflow**. Aguarde o processo terminar com sucesso.
6. Na execução concluída, baixe o artifact `AI-Box-Media-Center-debug-apk`. Extraia o ZIP para obter `app-debug.apk`.
7. Transfira o APK para a AI Box e abra-o pelo gerenciador de arquivos. O Android pode pedir autorização para instalar apps dessa origem.

## Atualizações
A checagem lê `versionCode`, `versionName` e `apkUrl` do `version.json`. Para testar o mecanismo, hospede o JSON publicamente e publique uma versão com `versionCode` maior e um APK em uma Release do GitHub. O workflow inicial produz um APK debug para testes. Para distribuição contínua, configure assinatura de release e mantenha o mesmo certificado em atualizações futuras.

## Limitações
- A classificação de Vídeo/TV/Áudio usa palavras do nome/pacote; pode classificar alguns apps incorretamente. Ajuste `classify()` em `MainActivity.kt`.
- A Play Store só funciona se estiver instalada/ativada; o fallback abre a loja web.
- A instalação de APK exige confirmação do Android; não há instalação silenciosa.
- O projeto é para Android nativo na AI Box, não transforma a projeção convencional Android Auto/CarPlay em um sistema que executa qualquer app. Compatibilidade, DRM e reprodução dependem do app e do dispositivo.
