# Testing

## Tecnologie utilizzate

### ScalaTest

Durante il meeting iniziale, si è valutato di utilizzare il framework ScalaTest al posto di JUnit per effettuare il test di unità. Questo ci ha permesso di sfruttare una tecnologia nativa Scala 3, utilizzando meccanismi avanzati del linguaggio quale un DSL apposito.

### ScalaMock

Durante lo sviluppo è emersa la necessità di effettuare testing di unità di componenti con dipendenze da altre componenti. A tal fine è risultato utile adottare la tecnica del mocking. Sono stati messi a confronto i framework Mockito e ScalaMock e si è deciso di utilizzare quest'ultimo per la sintassi (DSL) più chiara e per la possibilità di esprimere delle _expectations_ rispetto a delle verifiche a posteriori sulle condizioni volute.

## Metodologia utilizzata

Seguendo le linee guida dello sviluppo agile si è cercato di seguire l'approccio Test Driven Development (TDD) per garantire una maggior qualità al sistema. Nella parte iniziale del progetto ci siamo attenuti rigorosamente a questa metodologia, mentre nelle fasi successive si è utilizzato un approccio al testing più classico, testando una funzionalità dopo averla sviluppata.

## Esempi rilevanti

### Utilizzo di ScalaMock

Per agevolare il testing del RoundManager si è pensato di fare mocking della funzione _updateView_. In particolare, grazie al meccanismo di expectation di ScalaMock si è testato che la funzione sia chiamata una e una sola volta ad ogni iterazione del manager e con parametro il round corretto da fornire alla view.

```scala
private def mockUpdateViewSequence(
      rounds: RoundState*
  ): RoundState => IO[Unit] =
    val updateView = mockFunction[RoundState, IO[Unit]]
    inSequence:
      rounds.foreach(round => updateView expects round returning IO.unit)
    updateView
```

### DSL per i test

Per aumentare la comprensione del test di Update, è risultato comodo creare un piccolo DSL apposito per simulare l'invio di messaggi a un Model. Sono state create le seguenti azioni:
- `!` (send): simula l'invio di un messaggio al Model, ricevendo il prossimo Model che ne deriva;
- `?` (ask): simula l'invio di un messaggio al Model, ricevendo il Cmd che ne deriva.

```scala
object UpdateDSL:
  extension (model: Model)
    infix def on(msg: Msg)(using ScalatroRng): (Model, Cmd) = Update.update(model, msg)

    infix def !(msg: Msg)(using ScalatroRng): Model = model on msg match
      case (m, _) => m

    infix def ?(msg: Msg)(using ScalatroRng): Cmd = model on msg match
      case (_, c) => c
```

[Indice](../index.md) | [Indietro](../6-implementation/index.md) | [Avanti](../8-retrospective/index.md)