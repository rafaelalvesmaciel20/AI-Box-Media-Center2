# AI Box Media Center

Aplicativo Android para organizar e abrir apps instalados no celular. O projeto compila um APK de teste pelo GitHub Actions.

## Situação para o Geely EX2 + Flyme Auto + Android Auto

**Este APK não transmite Netflix, YouTube ou outros vídeos para a tela do carro.** O Android Auto convencional projeta uma experiência de apps permitidos; instalar um APK no celular não instala o app no Flyme Auto e não transforma o app em um player de vídeo para a central.

O app continua sendo um launcher para o dispositivo Android onde está instalado:
- Lista apps iniciáveis e aplica filtros heurísticos de Vídeo, TV e Áudio.
- Abre apps no próprio dispositivo.
- Oferece atalho para a Play Store quando disponível.
- Consulta `version.json` para verificar uma versão mais recente.

## Caminho tecnicamente correto para vídeo

1. **Não declarar o app como app de mídia/Android Auto sem implementar a categoria correta.** Um `MediaBrowserService` vazio não habilita vídeo; por isso ele não faz parte desta versão.
2. O Google anunciou que vídeo em apps adaptativos poderá chegar ao Android Auto quando estacionado, começando a ser distribuído para veículos compatíveis em 2026 e celulares com Android 17 ou superior. A disponibilidade depende do rollout do Google, do veículo e dos requisitos de distribuição. Consulte a documentação oficial antes de iniciar essa implementação.
3. Para uma implementação real, o app precisará de uma experiência de vídeo adaptativa, controles de segurança que pausem a reprodução quando exigido, tratamento de ciclo de vida, testes no Android Auto Desktop Head Unit e aprovação/distribuição conforme as regras do Google. Isso não dá acesso automático ao conteúdo protegido de Netflix ou de outros provedores.
4. Android Automotive OS é um sistema operacional instalado no veículo. Não é a mesma coisa que Android Auto. O proprietário não consegue adicionar AAOS ao Flyme Auto apenas instalando um APK no telefone.

## Build de teste

1. Abra **Actions** no GitHub e selecione **Build AI Box Media Center APK**.
2. Aguarde a execução terminar com sucesso.
3. Baixe o artifact `AI-Box-Media-Center-debug-apk` e extraia `app-debug.apk`.
4. Instale o APK no dispositivo Android para o qual ele foi criado.

O artifact é um APK debug, destinado a testes. Uma compilação bem-sucedida confirma apenas que o código compila; não confirma compatibilidade com a central do Geely EX2.

## Atualizações

A checagem lê `versionCode`, `versionName` e `apkUrl` de `version.json`. Para distribuição contínua, configure assinatura de release e mantenha o mesmo certificado nas atualizações futuras.

## Referências oficiais

- [Apps estacionados para Android Auto e Android Automotive OS](https://developer.android.com/agents/skills/cars/android-for-cars-parked-apps/skill)
- [Novidades do Android for Cars — Google, maio de 2026](https://android-developers.googleblog.com/2026/05/android-for-cars-unifying-platforms-premium-experiences.html)
- [Testar apps para carros](https://developer.android.com/training/cars/testing)
