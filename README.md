# Baralho Truco

Aplicativo Android para marcação de pontos em partidas de Truco, desenvolvido como projeto acadêmico na **Universidade Tecnológica Federal do Paraná (UTFPR)**.

---

## Sobre o projeto

O Baralho Truco é um placar digital para partidas de Truco entre dois jogadores. O app registra os pontos de cada rodada, detecta o fim da partida ao atingir 12 tentos, acumula o histórico de vitórias ao longo de múltiplas partidas e permite personalizar os nomes dos jogadores.

---

## Funcionalidades

- **Marcação de pontos** com botões rápidos: +1, +3, +6, +9 e +12 tentos por jogador
- **Detecção automática de fim de partida** ao atingir 12 tentos, com diálogo de encerramento
- **Histórico de vitórias** acumulado entre partidas (resetável separadamente da pontuação)
- **Nomes personalizados** para os dois jogadores, com persistência durante a sessão
- **Navegação por abas** (BottomNavigation): Placar · Histórico · Ajustes
- **Suporte a temas claro e escuro** (Material Design 3)
- **Acessibilidade**: descrições de conteúdo dinâmicas nos botões de pontuação para leitores de tela
- **Localização**: interface disponível em Português (pt-BR), Inglês e Espanhol

---

## Arquitetura

O projeto segue uma estrutura simples baseada em Activities, sem ViewModel ou persistência em banco de dados. O estado da partida vive em memória enquanto o app está aberto.

```
app/src/main/java/br/edu/utfpr/baralhotruco/
├── GameModels.kt        # Data class imutável com o estado completo do jogo
├── ScoreManager.kt      # Lógica de pontuação (stateless — recebe e retorna GameState)
├── MainActivity.kt      # Tela principal: placar e botões de pontuação
├── HistoryActivity.kt   # Tela de histórico: total de vitórias por jogador
└── NamesActivity.kt     # Tela de ajustes: edição dos nomes dos jogadores
```

### Camada de dados — `GameModels.kt` + `ScoreManager.kt`

`GameState` é um `data class` imutável que representa o estado completo da partida:

| Campo           | Descrição                                          |
|-----------------|----------------------------------------------------|
| `scorePlayerOne` | Pontuação atual do Jogador 1 (0–12)               |
| `scorePlayerTwo` | Pontuação atual do Jogador 2 (0–12)               |
| `winsPlayerOne`  | Total de vitórias acumuladas do Jogador 1         |
| `winsPlayerTwo`  | Total de vitórias acumuladas do Jogador 2         |
| `isGameOver`     | `true` quando algum jogador atingiu 12 tentos     |
| `winner`         | `1` ou `2` quando `isGameOver` é `true`           |

`ScoreManager` é stateless: recebe um `GameState`, aplica a lógica e retorna um novo `GameState` via `copy()`. Nunca muta estado diretamente. Métodos principais:

- `addPoints(state, player, points)` — adiciona pontos, limita em 12, detecta vencedor e incrementa vitórias
- `resetMatch(state)` — zera o placar da rodada, preserva vitórias
- `resetWins(state)` — zera o histórico de vitórias, preserva o placar atual
- `resetAll()` — retorna um `GameState` completamente zerado

### Camada de apresentação — Activities

**`MainActivity`** é o ponto de entrada do app. Mantém uma instância de `ScoreManager` e uma de `GameState`. Registra um `ActivityResultLauncher` para receber os nomes editados de `NamesActivity`. Responde a `onNewIntent` para resetar vitórias ou abrir `NamesActivity` quando a navegação vem de outra tela.

**`HistoryActivity`** exibe o total de vitórias recebido via `Intent`. O botão "Resetar Histórico" envia um `Intent` de volta à `MainActivity` com `FLAG_ACTIVITY_CLEAR_TOP` e `EXTRA_RESET_WINS=true`, evitando criar uma nova instância.

**`NamesActivity`** é aberta via `ActivityResultLauncher`. Pré-preenche os campos com os nomes atuais, valida e devolve os nomes com `setResult(RESULT_OK, intent)`.

### Navegação

A navegação entre telas usa `BottomNavigationView` com três abas. A seleção visual da aba ativa é mantida manualmente (retorna `true` apenas na aba atual, `false` nas demais) para evitar que o `BottomNav` mude o destaque ao navegar. O roteamento de `HistoryActivity → NamesActivity` passa por `MainActivity` via `EXTRA_OPEN_NAMES` para preservar o `ActivityResultLauncher`.

---

## Estrutura de recursos

```
app/src/main/res/
├── layout/
│   ├── activity_main.xml      # Placar + botões de pontuação
│   ├── activity_history.xml   # Card de vitórias + botão resetar histórico
│   └── activity_names.xml     # Campos de texto para nomes + botões confirmar/resetar
├── menu/
│   └── bottom_nav_menu.xml    # Itens da barra de navegação inferior
├── values/
│   ├── strings.xml            # Strings em inglês (padrão)
│   ├── colors.xml             # Paleta Material Design 3
│   └── themes.xml             # Tema base (Material3 DayNight NoActionBar)
├── values-pt-rBR/strings.xml  # Tradução para Português
├── values-es/strings.xml      # Tradução para Espanhol
└── drawable/
    ├── card_background.xml    # Fundo do card de histórico
    ├── ic_score.xml           # Ícone da aba Placar
    ├── ic_history.xml         # Ícone da aba Histórico
    └── ic_settings.xml        # Ícone da aba Ajustes
```

---

## Testes

```
app/src/test/
└── ScoreManagerTest.kt         # 13 testes unitários da lógica de pontuação (JUnit 4)

app/src/androidTest/
├── MainActivityTest.kt         # Testes de instrumentação da tela principal (Espresso)
├── HistoryActivityTest.kt      # Testes de instrumentação da tela de histórico
└── NamesActivityTest.kt        # Testes de instrumentação da tela de nomes
```

Os testes unitários de `ScoreManager` cobrem adição de pontos, limite em 12, fim de partida, contagem de vitórias, reset de partida e reset de histórico. Os testes de instrumentação validam a navegação entre telas, pré-preenchimento de campos e comportamento dos botões de cada Activity.

---

## Requisitos

| Item | Versão |
|------|--------|
| Android Studio | Ladybug ou superior |
| SDK mínimo | API 30 (Android 11) |
| SDK alvo | API 37 |
| Linguagem | Kotlin |
| Gradle | 8.x |

---

## Como executar

### Pelo Android Studio

1. Clone o repositório:
   ```bash
   git clone https://github.com/Luiz-Mattos14/Truco-deck-of-cards.git
   ```
2. Abra o Android Studio e selecione **File → Open**, aponte para a pasta do projeto.
3. Aguarde a sincronização do Gradle terminar.
4. Conecte um dispositivo físico com depuração USB ativada **ou** inicie um emulador (API 30+).
5. Clique em **Run ▶** ou use o atalho `Shift + F10`.

### Pela linha de comando

```bash
# Compilar e instalar no dispositivo/emulador conectado
./gradlew installDebug

# Rodar testes unitários
./gradlew test

# Rodar testes de instrumentação (requer dispositivo/emulador)
./gradlew connectedAndroidTest
```

---

## Regras do Truco (referência)

| Ação           | Tentos |
|----------------|--------|
| Tento (rodada) | +1     |
| Truco          | +3     |
| Seis           | +6     |
| Nove           | +9     |
| Doze           | +12    |

A partida termina imediatamente quando um jogador atinge **12 tentos**.

---

## Licença

Projeto acadêmico — UTFPR. Uso livre para fins educacionais.
