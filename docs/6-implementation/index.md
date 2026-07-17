# Implementazione

Durante lo sviluppo del progetto, il team ha soddisfatto pienamente il requisito [4.3], implementando la quasi totalità del progetto seguendo il paradigma funzionale dimostrando le conoscenze apprese durante il corso.

## Baldazzi Andrea

## Monaco Andrea

Il contributo personale sul progetto ha riguardato soprattutto l'implementazione della logica legata a `MVU` e la gestione del gioco all'infuori della semplice funzione cuore del gameplay di scontro con i blind legata al `Round` quali:

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

L'implementazione di `MVU` è nata come un refactor rispetto al codice precedente, questo si proponeva l'obiettivo di mantenere, quanto più possibile, inalterato e funzionante il codice già scritto, ma permettendo di ridurre il debito tecnico e semplificando l'aggiunta di nuove schermate e feature aderendo il più possibile alle logiche di funzionamento del pattern selezionato.

#### Runtime

La classe cuore del programma è `Runtime`, questa gestisce il loop di gioco e esegue i comandi `Cmd` che derivano dai messaggi.

Il loop si basa sull'utilizzo della monade IO e una coda di `Msg` durante il quale, come si vede nel codice riportato di seguito, vengono estratti i messaggi, passati a `Update` assieme allo stato attuale (`Model`) e, per finire viene aggiornata la view e gestite le computazioni legate ai `Cmd` ricevuti.

```scala
for
  msg <- queue.take
  (next, cmd) = Update.update(model, msg)
  _ <- view.render(next)
  _ <- perform(cmd, queue, view)
  _ <- loop(next, queue, view)
yield ()
```

La gestione dei comandi avviene attraverso un mach case che a seconda del comando ricevuto lo gestisce o lo delega a altre classi e funzioni.

`Runtime`, al contrario di `Update` ha il parametro contestuale di `rng` che permette di eseguire i comandi che lo richiedono.

#### Update

`Update` è un singleton (`object`) che si occupa della gestione dei messaggi e aggiornamento del model, ricevuto un input si occupa semplicemente di eseguire un match case per definire la computazione da eseguire come conseguenza.

In output rilascia il `Model` che derivante dalla computazione e l'eventuale `Cmd` da eseguire.

#### Model, Cmd, Msg

Model, Cmd e Msg sono stati implementati come degli enum che permettono di rappresentare rispettivamente tutti gli stati della partita, tutti i comandi da eseguire a Runtime e tutti i messaggi utilizzati.

I `Cmd` sono:

- `NoOp`: che indica l'assenza di un comando generato, quindi l'operazione è stata gestita completamente in Update.
- `Deal(GameState)` e `DealFirstRound` che richiedono a `Runtime` di creare un round, in particolare `Deal` fa creare il round successivo e DealFirst il primo.
- `BuildShop` richiede a `Runtime` di creare lo shop.

### Deck

Il Deck è un tipo opaco che rappresenta una sequenza di carte. Alla sua creazione viene tipicamente creato come un normale mazzo da poker di 52 carte.
I metodi: `shuffle`, `sort` e `draw` sono stati implementati come extension method, in particolare `shuffle` usa un parametro contestuale per ottenere l'rng con cui disordinare il deck per rispettare il requirement **[2.2.27]**.

### Shop

Lo `Shop` è una semplice case class che viene tipicamente creata con il metodo `default` che prende in input una lista dei Joker e le selection policies, inoltre il parametro contestuale dell'rng per permettere di generare i pacchetti.

### Schermate di gioco

Per la creazione delle schermate di gioco si è fatto uso di `JavaFx` con `FXML`. Vengono utilizzati dei file fxml per definire gli aspetti grafici statici delle schermate e ad essi vengono associati dei `Controller` per modellare gli aspetti dinamici e le interazioni.

La classe `View` si occupa aggiornare i controller di JavaFx in base allo stato della partita. In particolare preso in input uno stato esegue un match case per aggiornare la schermata. Qui utilizza uno `ScreenRouter` che cambia la `root` dell'immagine a quella richiesta e ritorna un `IO[Controller]` su cui poi la view chiama eventualmente ulteriori funzioni per concludere l'aggiornamento.

## Mattia Ronchi

## Samorì Andrea

[Indice](../index.md) | [Indietro](../5-detailed-design/index.md) | [Avanti](../7-testing/index.md)