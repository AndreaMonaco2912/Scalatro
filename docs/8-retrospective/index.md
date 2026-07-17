# Retrospettiva

## Andamento dello sviluppo Scrum

Durante lo sviluppo del progetto si ha avuto modo di sperimentare la metodologia Agile Scrum. 
Il meeting settimanale è risultato molto utile per rimanere aggiornati sul lavoro effettuato dal resto del gruppo e la produzione di uno stato consistente del sistema alla fine di ogni sprint ha permesso di rimanere sincronizzati sul lavoro da svolgere e sulla direzione da prendere negli sprint successivi.

### Sprint 1

Il primo sprint si è concentrato sulla produzione di una versione estremamente semplificata del gioco. Essa infatti prevedeva una partita con livelli aventi punteggio incrementale e con la presenza delle sole carte e del calcolo del punteggio base, senza alcun effetto.

### Sprint 2

Il secondo print ha prodotto una versione più completa del sistema, con l'introduzione delle carte joker e pianeta nella loro versione iniziale. Anche la grafica dell'applicazione ha subito un leggero miglioramento, aggiungendo la possibilità di ordinare le carte e la creazione di qualche animazione per rendere più esplicito il calcolo del punteggio.

### Sprint 3

Nello sprint 3 è stato integrato lo shop all'interno del gioco, avvalendosi dell'architettura MVU, predisposta per questi cambiamenti. Questa aggiunta ha permesso di acquistare carte di gioco, carte joker e carte pianeta che, a questo punto dello sviluppo, avevano raggiunto lo stato finale. 

### Sprint 4

Nell'ultimo sprint il gioco ha raggiunto la sua versione finale:
- aggiunta dei Blind con possibili effetti 
- possibilità di visualizzare il mazzo corrente e i livelli delle combinazioni di carte dallo shop
- calcolo della migliore giocata possibile utilizzando le carte in mano 
- finalizzazione del DSL per configurare una partita
- aggiunta del sistema di randomizzazione e della ricerca vincolata di un seed

## Considerazioni finali

Durante lo sviluppo del progetto si sono riscontrate delle difficoltà nell'utilizzo della metodologia Scrum, forse dovute anche al fatto che questa fosse la prima esperienza per tutti i membri del gruppo. In particolare si è riscontrato un forte overhead dovuto alla produzione e all'aggiornamento dei documenti Scrum. Inoltre, abbiamo constatato, per via della scarsa esperienza, quanto possa essere difficile stimare tempi ragionevoli per ciascun task e quindi per l'intero sprint. 

La scelta di realizzare il sistema seguendo più possibile l'approccio funzionale ha introdotto notevoli difficoltà. Il dover pensare in un paradigma nuovo e con il quale si ha meno esperienza ha richiesto uno sforzo maggiore da parte dei membri del gruppo, richiedendo vari refactor per ristrutturare l'applicazione, mantenendo una certa qualità del codice. Per un dominio applicativo come quello di Balatro, sarebbe stato più facile adottare in molte parti il paradigma object-oriented. Nel mondo reale probabilmente sarebbe stato più opportuno realizzare una versione più ibrida tra funzionale e OOP, sfruttando i vantaggi di ciascuno.


[Indice](../index.md) | [Indietro](../7-testing/index.md)