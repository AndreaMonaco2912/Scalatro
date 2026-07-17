# Implementazione

Durante lo sviluppo del progetto, il team ha soddisfatto pienamente il requisito [4.3], implementando la quasi totalità
del progetto seguendo il paradigma funzionale dimostrando le conoscenze apprese durante il corso.

## Baldazzi Andrea

## Monaco Andrea

Il contributo personale sul progetto ha riguardato soprattutto l'implementazione della logica legata a `MVU` e la
gestione del gioco all'infuori della semplice funzione cuore del gameplay di scontro con i blind legata al `Round`
quali:

- `GameState`: una semplice case class contenente tutte le informazioni persistenti della partita;
- Elementi di Gameplay:
    - `Shop`
    - `Deck`
- Schermate di gioco, e con esse la loro integrazione nel GamePlay Loop:
    - Schermata di sconfitta e vittoria
    - Schermata di informazioni del `Deck`
    - Schermata di informazioni dei livelli delle mani
    - Schermata di del negozio
    - Schermata di apertura dei pacchetti

### Model View Update

L'implementazione di `MVU` è nata come un refactor rispetto al codice precedente, questo si proponeva l'obiettivo di
mantenere, quanto più possibile, inalterato e funzionante il codice già scritto non direttamente interessato, ma
permettendo di ridurre il debito tecnico e semplificando l'aggiunta di nuove schermate e feature aderendo il più
possibile alle logiche di funzionamento del pattern selezionato.

#### Runtime

La classe cuore del programma è `Runtime`, questa gestisce il loop di gioco e esegue i comandi `Cmd` che derivano dai
messaggi.

Il loop si basa sull'utilizzo della monade IO e una coda di `Msg` durante il quale, come si vede nel codice riportato di
seguito, vengono estratti i messaggi, passati a `Update` assieme allo stato attuale (`Model`) e, per finire viene
aggiornata la view e gestite le computazioni legate ai `Cmd` ricevuti.

```scala
for
  msg <- queue.take
  (next, cmd) = Update.update(model, msg)
  _ <- view.render(next)
  _ <- perform(cmd, queue, view)
  _ <- loop(next, queue, view)
yield ()
```

La gestione dei comandi avviene attraverso un mach case che a seconda del comando ricevuto lo gestisce o lo delega a
altre classi e funzioni.

`Runtime`, al contrario di `Update` ha il parametro contestuale di `rng` che permette di eseguire i comandi che lo
richiedono.

#### Update

`Update` è un singleton (`object`) che si occupa della gestione dei messaggi e aggiornamento del model, ricevuto un
input si occupa semplicemente di eseguire un match case per definire la computazione da eseguire come conseguenza.

In output rilascia il `Model` che derivante dalla computazione e l'eventuale `Cmd` da eseguire.

`Update` ha anche una funzione `init` che imposta ritorna il `Model.Playing` e il `Cmd.DealFirstRound`.

#### Model, Cmd, Msg

Model, Cmd e Msg sono stati implementati come degli enum che permettono di rappresentare rispettivamente tutti gli stati
della partita, tutti i comandi da eseguire a Runtime e tutti i messaggi utilizzati.

I `Cmd` sono:

- `NoOp`: che indica l'assenza di un comando generato, quindi l'operazione è stata gestita completamente in Update.
- `Deal(GameState)` e `DealFirstRound` che richiedono a `Runtime` di creare un round, in particolare `Deal` fa creare il
  round successivo e DealFirst il primo.
- `BuildShop` richiede a `Runtime` di creare lo shop.

Con il sono fine di approfondire tutti le funzioni che hanno i messaggi e il loro risultato sul model: indichiamo con
`Model ! Msg` un metodo che chiama `Update` su `(Model, Msg)` e ritorna solo il prossimo `Model` senza definire il
comando, viceversa `Model ? Msg` fa lo stesso ritornando solo il `Cmd` che ne deriva ignorando il model. Di seguito
l'elenco con tutte le reazioni che porta `Update`:

- `Model.Playing ! Msg.RoundWon` => `Model.RoundWon`
- `Model.RoundWon ! Msg.ShopReady` => `Model.InShop`
- `Model.RoundWon ? Msg.NextRound` => `Cmd.BuildShop`
- `Model.RoundLost ? Msg.Restart` => `Cmd.DealFirstRound`
- `Model.InShop ! Msg.OpenCardPack` => `Model.OpeningPack`
- `Model.InShop ! Msg.OpenPlanetPack` => `Model.OpeningPack`
- `Model.InShop ! Msg.OpenJokerPack` => `Model.OpeningPack`
- `Model.InShop ? Msg.SkipShop` => `Cmd.Deal`
- `Model.OpeningPack ? Msg.SelectCard` => `Cmd.Deal`
- `Model.OpeningPack ? Msg.SelectPlanet` => `Cmd.Deal`
- `Model.OpeningPack ? Msg.SelectJoker` => `Cmd.Deal`
- `Model.OpeningPack ? Msg.SkipPack` => `Cmd.Deal`
- `Model.ShowDeck ! Msg.CloseDeck` => `previousModel`
- `Model.ShowLevels ! Msg.CloseLevels` => `previousModel`
- `Model.AnyStateWithDeck ! Msg.ShowDeck` => `Model.ShowDeck`
- `Model.AnyStateWithLevel ! Msg.ShowLevels` => `Model.ShowLevels`

### Deck

Il Deck è un tipo opaco che rappresenta una sequenza di carte. Alla sua creazione viene tipicamente creato come un
normale mazzo da poker di 52 carte.
I metodi: `shuffle`, `sort` e `draw` sono stati implementati come extension method, in particolare `shuffle` usa un
parametro contestuale per ottenere l'rng con cui disordinare il deck per rispettare il requirement **[2.2.27]**.

### Shop

Lo `Shop` è una semplice case class che viene tipicamente creata con il metodo `default` che prende in input una lista
dei Joker e le selection policies, inoltre il parametro contestuale dell'rng per permettere di generare i pacchetti.

Il metodo default è presente nel `companion object` di Shop e si occupa anche della creazione dei 3 pacchetti contenuti.

### Schermate di gioco

Per la creazione delle schermate di gioco si è fatto uso di `JavaFx` con `FXML`. Vengono utilizzati dei file fxml per
definire gli aspetti grafici statici delle schermate e ad essi vengono associati dei `Controller` per modellare gli
aspetti dinamici e le interazioni, questi controlli hanno metodi pubblici per aggiornare le loro grafiche che vengono al
thread `Platform` tramite `JavaFx`.

La classe `View` si occupa aggiornare i controller di JavaFx in base allo stato della partita. In particolare preso in
input uno stato esegue un match case per aggiornare la schermata. Qui utilizza uno `ScreenRouter` che cambia la `root`
dell'immagine a quella richiesta e ritorna un `IO[Controller]` su cui poi la view chiama eventualmente ulteriori
funzioni per concludere l'aggiornamento.

## Mattia Ronchi

Il mio contributo si è concentrato sugli elementi relativi al punteggio di gioco e agli effetti:

- combinazioni di carte (`HandType`)
- carte pianeta (`Planet`)
- effetti (`Modification`)
- carte joker (`Joker`)
- punteggio (`Score`)

### Combinazioni di carte

Le combinazioni di carte sono state modellate attraverso un'enumerazione come entità statiche dotate di un punteggio di
base.

Il companion object fornisce i seguenti metodi:

- `detect`: rileva la combinazione di carte valida per il punteggio a partire dalle carte giocate
- `contains`: determina se un sottoinsieme delle carte giocate può formare la combinazione data
- `scoringCards`: restituisce le carte che contribuiscono alla combinazione valida per il punteggio

### Carte pianeta

Le carte pianeta sono modellate attraverso un'enumerazione come entità statiche contenenti il punteggio aggiunto alla
rispettiva combinazione ad ogni utilizzo.

Assieme alle carte pianeta sono state definiti altri tipi di dato relativi al loro utilizzo:

- `Level` rappresenta il livello di una combinazione
- `HandTypeLevels` rappresenta l'associazione, all'interno di una partita, tra una combinazione di carte e il suo
  livello

### Effetti

Gli effetti sono uno degli aspetti del gioco più importanti che ho realizzato. Come descritto nei capitoli precedenti,
essi rappresentano modifiche allo stato della partita che vengono invocate in momenti predefiniti da parte di joker e
blind.

`Modification` è un trait che rappresenta una funzione che prende un elemento e restituisce la versione a seguito della
modifica. All'interno del companion object sono definiti metodi di utility che semplificano l'applicazione degli effetti
all'interno del gioco:

- `applyAll` applica in sequenza una serie di effetti
- `when` applica una modifica o meno in base al valore di una condizione
- `run` applica in sequenza una serie di effetti da parte di una serie di fonti

```scala
object Modification:
  def run[A, S, I](initial: A, sources: Seq[S], input: I)(
    pf: PartialFunction[S, I => Seq[Modification[A]]]
  ): A =
    sources.collect(pf).flatMap(effect => effect(input)).applyAll(initial)

  extension [A](mods: Seq[Modification[A]])
    def applyAll(initial: A): A =
      mods.foldLeft(initial)((acc, mod) => mod.apply(acc))
```

La modellazione della Modification come trait permette di stabilire a priori le diverse tipologie di modifiche
effettuabili allo
stato della partita ed è stata pensata anche con l'intento, poi non realizzato concretamente, di arricchirne
l'applicazione con
funzionalità di logging e animazioni grafiche.

Relativamente al concetto di effetti sono inoltre definiti dei capability trait per rappresentare la presenza, da parte
di una fonte di effetti, di un effetto invocabile in uno specifico momento della partita. Durante l'applicazione degli
effetti, questi trait sono utilizzati nella partial function di `run` per selezionare le fonti il cui effetto è
invocabile in quel momento.
Come esempio mostro l'invocazione degli effetti di tipo "A inizio round" presente in `RoundManager`:

```scala
def runOnRoundStartEffects(roundState: RoundState): RoundState =
  val onRoundStartEffectSources = Seq(
    initialRoundState.gameState.blindProgression.blind
  ) ++ initialRoundState.gameState.jokers
  Modification.run(roundState, onRoundStartEffectSources, roundState) {
    case s: OnRoundStartEffect => s.onRoundStart
  }
```

### Joker

I joker sono stati implementati come trait aventi soltanto informazioni statiche e non comportamentali (nome e
descrizione). La realizzazione dei singoli tipi di joker è stata realizzata attraverso il meccanismo dei mixin, che
permette di costruire facilmente nuovi tipi di joker aggiungendo diversi tipi di effetti.

Come esempio è mostrata la costruzione di un joker avente 2 effetti applicabili in diversi momenti della partita:

```scala
case Scholar
extends JokerType(
  "Scholar",
  "Played Aces give +20 Chips and +4 Mult when scored"
)
with RanksScored(
  Seq(Rank.Ace),
  Seq(
    HandScoreModification.FlatChips(Chips(20)),
    HandScoreModification.FlatMult(Mult(4))
  )
)
with OnBuyModifier(
  Seq(GameStateModification.SetCardPolicy(PresetPolicies.scholarPolicy))
)
```

### Punteggio

All'interno di `Score` sono definiti diversi tipi di dato relativi al punteggio:

- Chips e Mult per rappresentare le componenti del punteggio
- HandScore: punteggio accumulato durante la giocata di una mano, composto da Chips e Mult
- Score: punteggio a seguito della combinazione di Chips e Mult dall'`HandScore`

Il metodo principale relativo al punteggio è `calculateScore` (e `calculateHandScore` su cui si appoggia) che si occupa
di calcolare il punteggio effettuato
dall'intera giocata di una mano. Esso contiene la logica di applicazione ordinata degli effetti, semplificata grazie ai
metodi presenti in `Modification`. Il metodo ottiene il punteggio di base della combinazione e aggrega le modifiche
degli effetti fino ad ottenere il punteggio della mano finale. La strategia di combinazione dei valori di Chips e Mult
all'interno del punteggio finale è definita da `HandScoreCalculator`.

Il calcolo del punteggio utilizza un parametro contestuale `scoreConfig` contenente gli elementi che, oltre alle carte
giocate, contribuiscono a determinare il punteggio.

## Samorì Andrea

[Indice](../index.md) | [Indietro](../5-detailed-design/index.md) | [Avanti](../7-testing/index.md)
