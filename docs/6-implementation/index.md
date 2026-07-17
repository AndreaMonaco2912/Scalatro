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

Per quanto riguarda l'implementazione del sistema mi sono concentrato sulla parte relativa ai Blind. Oltre a questo mi sono concentrato sulla creazione di alcuni Domain Specific Language (DLS) per la creazione semplificata di alcuni componenti. Infine ho realizzato la funzione di suggerimento (Hint) che per determinare la miglior mano giocabile in ogni situazione. Le classi a cui ho contribuito, in totalità o per la maggior parte, sono le seguenti:

- Gioco
  - `Card`
  - `Pack`
  - `BlindProgression`

- Costruttori (DSL)
  - `CardBuilder`
  - `GameStateBuilder`
  - `RoundBuilder`
  - `CustomScenarioBuilder`

- `Hint`

## Gioco

### Card

Uno degli elementi principali del sistema. Ogni carta è composta dalle seguenti informazioni: `rank`, `suit`. Oltre a questo, ogni carta implementa un metodo `onScored` che ritorna una sequenza di `HandScoreModification`.

```scala
trait Card extends Weighable:
  def rank: Rank
  def suit: Suit
  def onScored: Seq[HandScoreModification]
```

Questo metodo viene utilizzato nel calcolo del punteggio di una giocata. Infatti quando viene richiesto `onScored` di una carta, viene ritornata una sequenza di `HandScoreModification` contenente una solo elemento, cioè la `Modification` che aggiunge al punteggio attuale la componente `Chips` della carta.

```scala
override def onScored: Seq[HandScoreModification] =
      Seq(HandScoreModification.FlatChips(this.baseChips))
```

È stato scelto di ritornare una sequenza e non una singola `HandScoreModification` per permettere in maniera semplice di aggiungere degli effetti addizzionali che vengono attivati quando avviene l'`onScored` della carta.

### Pack

L'elemento con cui l'utente può arricchire la sua partita. Alla fine di ogni round vinto viene proposto uno shop dove sono presenti tre pacchetti (un card pack, un joker pack e un planet pack). Il giocatore sceglie uno di questi pacchetti e lo apre. Al suo interno sono presenti tre elementi di cui il giocatore ne può scegliere al più uno. Per modellare queste entità, ho creato una `PackFactory` generica sul tipo di elementi del pacchetto (nel nostro caso carte, joker oppure pianeti). A livello di tipo, devono essere tutti sottotipo di `Weighable` (vedere la parte di gestione della randomicità per maggiori dettagli).

```scala
case class Pack[A](items: Seq[A])

trait PackFactory[A <: Weighable](using SelectionPolicy[A]):
    def pool: Pool[A]
    def apply(n: Int)(using rng: ScalatroRng): Pack[A] =
        require(n >= 0, s"cannot present a pack with a negative amount of cards")
        Pack(rng.draw(pool, n))
    def apply(n: Int, blackList: Seq[A])(using rng: ScalatroRng): Pack[A] =
        require(n >= 0, s"cannot present a pack with a negative amount of cards")
        Pack(rng.draw(pool - Pool(blackList), n))
```

All'interno di `PackFactory` è presente un `pool` da cui inserire gli oggetti nei vari pacchetti.

### BlindProgression

#### Blind

Ad ogni round il giocatore si trova a dover sfidare un `Blind`. Questo può avere degli effetti che vanno a svantaggio del giocatore. In questo caso, stiamo parlando di `Boss Blind`. Per accentuare la distinzione tra Boss e Non Boss blind, ho scelto di modellare i Blind come un trait e creare i sealed trait `NormalBlind` e `BossBlind` che estendono da Blind. Il fatto che sia sealed permette un pattern matching esaustivo, ricevendo un warning dal compilatore in caso ci stessimo facendo matching sui sottotipi di Blind e ci stessimo dimenticando di includere alcuni di essi.

```scala
sealed trait Blind:
  def name: String
  def description: String

sealed trait NormalBlind extends Blind
sealed trait BossBlind extends Blind, Weighable
```

`Boss Blind` estende anche da `Weighable` perchè è un entità che deve essere estratta randomicamente (vedere la parte di gestione della randomicità per maggiori dettagli).

Per ogni Blind, ho creato un object a lui associato che estende dal trait appropriato.

```scala
object SmallBlind extends NormalBlind:
  val name = "Small Blind"
  val description = "No special effect"

object BigBlind extends NormalBlind:
  val name = "Big Blind"
  val description = "No special effect"

object TheNeedle extends BossBlind with OnRoundStartEffect:
  override def onRoundStart(round: RoundState): Seq[RoundStateModification] =
    Seq(RoundStateModification.SetRemainingPlays(1))
  val name = "The Needle"
  val description = "Play only 1 hand"

object TheWater extends BossBlind with OnRoundStartEffect:
  val name = "The Water"
  val description = "Start with 0 discards"

  override def onRoundStart(round: RoundState): Seq[RoundStateModification] =
    Seq(RoundStateModification.SetRemainingDiscards(0))
```

Ogni `BossBlind` porta con se un effetto negativo per il giocatore. Questo effetto può essere di varia natura e può attivarsi in diverse fasi del round (vedere la parte di descrizione su come funzionano gli effetti per maggiori dettagli).

#### Progressione dei Blind

`BlindProgression` è l'entità dati responsabile di modellare e far avanzare correttamente la sequenza dei blind durante una partita. Ogni `ante` è composta da tre blind consecutivi Small Blind, poi Big Blind e infine Boss Blind al termine dei quali si passa all'ante successiva ripartendo da Small Blind, con target score progressivamente crescenti.

```scala
def next(using rng: ScalatroRng): BlindProgression =
    val nextBlind: Blind = blind match
      case SmallBlind => BigBlind
      case BigBlind   =>
        rng.draw(bossBlindPool, 1).headOption.getOrElse(defaultBoss)
      case _: BossBlind => SmallBlind

    BlindProgression(roundNum + 1, nextBlind)
```

Il punto interessante è passare da `BigBlind` a `BossBlind`. Qui il prossimo boss viene estratto casualmente da un pool tramite il generatore rng (context parameter).

La classe incapsula sia lo stato corrente della progressione sia la logica per passare allo stato successivo, garantendo che il susseguirsi dei blind sia sempre coerente con le regole di gioco.

Il target score (punteggio da battere per superare il round) cresce esponenzialmente con l'ante e viene ulteriormente scalato in base al tipo di blind. Il metodo targetScore dà però sempre priorità a un eventuale valore fornito dall'esterno (targetScoreFromOutside), rendendo il calcolo standard sovrascrivibile senza doverlo duplicare o modificare. Questo è utile sia per eventuali joker che possono modificarlo sia per permettere un testing semplice.

## Costruttori

Questi componenti costituiscono un insieme di Domain-Specific Language (DSL) interni pensati per costruire in modo dichiarativo e leggibile gli oggetti del dominio. Questi sono stati particolarmente prezionsi durante il testing visto che gran parte dei comportamenti di gioco vanno verificati creando scenari specifici e circoscritti.

### CardBuilder

```scala
object CardBuilder:
  val S: Suit = Suit.Spades
  val H: Suit = Suit.Hearts
  val C: Suit = Suit.Clubs
  val D: Suit = Suit.Diamonds

  extension (value: Int)
    infix def of(suit: Suit): Card = value match
      case 2  => Card(Rank.Two, suit)
      case 3  => Card(Rank.Three, suit)
      case 4  => Card(Rank.Four, suit)
      case 5  => Card(Rank.Five, suit)
      case 6  => Card(Rank.Six, suit)
      case 7  => Card(Rank.Seven, suit)
      case 8  => Card(Rank.Eight, suit)
      case 9  => Card(Rank.Nine, suit)
      case 10 => Card(Rank.Ten, suit)
      case _  =>
        throw new IllegalArgumentException(
          s"Invalid numeric rank: $value (must be 2-10)"
        )

  object J:
    infix def of(suit: Suit): Card = Card(Rank.Jack, suit)
  /*... stesso per Q, K e Asso */
```

L'utilizzo dell'extension method `of` mi ha permesso la creazione delle carte numeriche in maniera molto concisa e comprensibile. La dicitura `infix` evita di dover specificare i punti e le parentesi durante la chiamata (`7 of C` anziché `7.of(C)`). Per le carte non numeriche sono stati creati dei singleton che permettono di mantenere uniforme per tutte le carte il modo di creazione.

### RoundBuilder e GameStateBuilder

```scala
def configure(configuration: GameStateBuilder ?=> Unit): GameState =
  val builder = GameStateBuilder()
  configuration(using builder)
  builder.build
```

La parte interessante di questi due componenti è l'utilizzo di una `context function` nella funzione `configure`. Il codice passato a `configure` non riceve il builder esplicitamente, ma lo trova implicitamente in scope grazie a using.

### CustomScenarioBuilder

Questo componente è pensato specificamente per costruire scenari in un unico flusso di facile interpretazione:

```scala
Cards(A of S, K of H) withJokers Seq(Scholar, Fibonacci) onLevels(HC lv 3, TP lv 2, SF lv 5) inBlind BigBlind withTarget Score(500)
```

## Hint

`Hint` implementa la logica di suggerimento della miglior giocata possibile data una mano di carte: individua, tra tutte le combinazioni di carte giocabili, quella che produce il punteggio più alto (a parità di punteggio, quella con meno carte). L'aspetto più interessante è l'enumerazione delle combinazioni possibili la quale non viene fatta con cicli o ricorsione Scala, ma viene delegata a un motore Prolog, sfruttando la programmazione logica.

```prolog
between(Low, High, Low) :- Low =< High.
between(Low, High, X) :- Low < High, Low1 is Low + 1, between(Low1, High, X).

combos(0, _, []).
combos(N, [H|T], [H|T2]) :- N > 0, N1 is N - 1, combos(N1, T, T2).
combos(N, [_|T], T2) :- N > 0, combos(N, T, T2).
```

Qui `between/3 (+Low, +High, -Elem)` trova tutti gli interi in un determinato intervallo, mentre `combos/3 (+Size, +List, -CombinationList)` restituisce tutte le combinazioni della lista `List` di dimensione `Size`.

Prese tutte le combinazioni giocabili delle carte in mano, la funzione `rankedPlays` mappa ogni combinazione con il proprio punteggio.

```scala
def best(hand: Hand)(using ScoreConfig): Seq[Card] =
  require(hand.sizeIs > 0, "Hand must have at least one card")
  rankedPlays(hand).foldLeft(Option.empty[(Seq[Card], Score)]) { (acc, cur) =>
    (acc, cur) match
      case (None, best)                                 => Some(best)
      case (Some((bestHand, bestScore)), (hand, score)) =>
        if score > bestScore then Some(cur)
        else if score == bestScore && hand.sizeIs < bestHand.size then Some(cur)
        else acc
  } match
    case Some((bestHand, _)) => bestHand
    case None => throw new IllegalArgumentException("empty hand")
```

Tramite `best` riusciamo a ridurre tutte le `rankedPlayes` in quella che ha il maggior punteggio (a parità di punteggio, viene scelta quella con meno carte).

[Indice](../index.md) | [Indietro](../5-detailed-design/index.md) | [Avanti](../7-testing/index.md)
