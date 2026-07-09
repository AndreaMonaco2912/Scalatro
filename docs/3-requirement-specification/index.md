# Specifica dei requisiti

In questa sezione viene presentata la specifica dei requisiti.

<!-- Business Requirements (why?):
▶ answering to: why is this SW strategic?
▶ high-level: project goals, customers’ hopes, developers’ goals. . .
▶ should necessarily clarify: how do we judge if the project is successful?
2. Functional Requirements (what functionalities in the system run?)
▶ what are the functions/features the system provides?
2.1 User Requirements: how the project’s result is used by end users
how users specifically interact with the SW?
which I/O constraints exist?
2.2 System Requirements: how the system internally works/operates
what are its rules of behaviour, norms, constraints?
3. Non-functional Requirements (what qualities of the system run?)
▶ what are the general properties and quality attributes of the solution?
▶ (performance, scalability, reliability, friendliness, . . . )
4. Implementation Requirements (what constraints on “production”?)
▶ they anticipate details of architecture/design/implementation/process
▶ what are constraints for the implementor? (and why)
▶ (technologies, training, material, internal quality)
▶ do not anticipate design decisions -->

## Requisiti di business

- **[1.1]** Presentazione di un buon progetto per sostenere l'esame di Paradigmi di Programmazione e Sviluppo.
- **[1.2]** Realizzazione di una versione semplificata del videogioco Balatro. Essa deve includere: carta pianeta, carte joker, organizzazione della partita in blind.

## Requisiti funzionali

### Requisiti funzionali utente

// TODO: mettere il tooltip degli effetti dei joker

- **[2.1.1]** L'utente deve poter utilizzare una GUI per interagire con il gioco.
- **[2.1.2]** L'utente deve poter vedere le carte da gioco in mano.
- **[2.1.3]** L'utente deve poter vedere il numero di carte da gioco rimanenti nel mazzo.
- **[2.1.4]** L'utente deve poter vedere le carte joker possedute e i loro effetti.
- **[2.1.5]** L'utente deve poter vedere il nome e gli effetti del blind corrente.
- **[2.1.6]** L'utente deve poter vedere il numero del round e dell'ante raggiunti.
- **[2.1.7]** L'utente deve poter vedere il punteggio da superare nel blind corrente.
- **[2.1.8]** L'utente deve poter vedere il punteggio da lui ottenuto nel blind corrente.
- **[2.1.9]** L'utente deve poter selezionare delle carte dalla mano.
- **[2.1.10]** L'utente deve poter vedere la combinazione delle carte selezionate.
- **[2.1.11]** L'utente deve poter vedere il punteggio di base e il livello della combinazione delle carte selezionate.
- **[2.1.12]** L'utente deve poter giocare o scartare da 1 a 5 carte.
- **[2.1.13]** L'utente deve poter visualizzare la combinazione di carte, tra quelle in mano, che fornirà il punteggio più alto.
- **[2.1.13]** L'utente deve poter riordinare le carte in mano:
  - per seme
  - per valore
  - manualmente
- **[2.1.14]** L'utente deve poter vedere il numero di giocate e scarti rimanenti per il blind corrente.
- **[2.1.15]** L'utente deve poter vedere quali carte tra quelle giocate contribuiscono al punteggio.
- **[2.1.16]** L'utente deve poter vedere, al superamento di un blind o in caso di sconfitta, una schermata di riepilogo del blind con le seguenti informazioni:
  - messaggio di superamento/sconfitta
  - punteggio richiesto dal blind
  - punteggio effettuato
  - numero di giocate rimanenti
  - numero di scarti rimanenti
- **[2.1.17]** L'utente deve poter visualizzare le carte da gioco presenti nel mazzo.
- **[2.1.18]** L'utente deve poter visualizzare i livelli di ciascuna combinazione di carte.
- **[2.1.19]** L'utente deve poter selezionare al più un pacchetto dal negozio.
- **[2.1.20]** L'utente deve poter selezionare al più un oggetto da un pacchetto.

### Requisiti funzionali di sistema

- struttura della partita
- struttura dell'ante
- struttura di un round
- definizione di deck
- definizione di carte da gioco
- definizione di combinazioni di carte
- definizione di carte joker
- definizione di carte pianeta
- shop
- pakketti

- **[2.2.1]** Ciascuna partita del gioco è indipendente dalle altre.
- **[2.2.2]** Una partita è strutturata in livelli (_blind_), raggruppati a 3 per formare un'_anta_.
- **[2.2.3]** In un'anta il primo (_Small_) e il secondo (_Big_) blind non hanno effetti. Il terzo (_Boss_) blind introduce degli effetti svantaggiosi per il giocatore.
- **[2.2.4]** Un blind richiede di raggiungere un certo punteggio per essere superato.
- **[2.2.5]** Il punteggio richiesto aumenta da un blind a quello successivo.
- **[2.2.5]** Il punteggio consiste in un numero a virgola mobile, ottenuto dalla combinazione di due componenti numeriche: _Chips_ e _Mult_.
- **[2.2.6]** La partita prevede che inizialmente il giocatore abbia a disposizione un mazzo (_deck_) di carte da poker. Il deck è formato da 52 carte, 1 per ogni combinazione dei seguenti valori e semi:
  - valori:
    - _Ace_ (Asso)
    - _King_ (Re)
    - _Queen_ (Regina)
    - _Jack_ (Jack)
    - _Ten_ (Dieci)
    - _Nine_ (Nove)
    - _Eight_ (Otto)
    - _Seven_ (Sette)
    - _Six_ (Sei)
    - _Five_ (Cinque)
    - _Four_ (Quattro)
    - _Three_ (Tre)
    - _Two_ (Due)
  - semi:
    - _Hearts_ (Cuori)
    - _Diamonds_ (Quadri)
    - _Clubs_ (Fiori)
    - _Spades_ (Picche)
- **[2.2.7]** Il deck può subire modifiche permanenti nel corso della partita.
- **[2.2.7]** Le carte del mazzo possono formare le seguenti combinazioni, ognuna delle quali fornisce la base per il calcolo del punteggio:
  - _Flush Five_: 5 carte che formano sia Five of a Kind che Flush (es. 5 assi di cuori). Punteggio di base: 160 Chips, 16 Mult.
  - _Flush House_: 5 carte che formano sia Full House che Flush (es. 3 assi di cuori e 2 re di cuori). Punteggio di base: 140 Chips, 14 Mult.
  - _Five of a Kind_: 5 carte dello stesso valore (es. 4 assi di cuori e 1 asso di picche). Punteggio di base: 120 Chips, 12 Mult.
  - _Straight Flush_: 5 carte che formano sia Straight che Flush (es. Asso,Due,Tre,Quattro,Cinque di cuori). Punteggio di base: 100 Chips, 8 Mult.
  - _Four of a Kind_: 4 carte dello stesso valore (es. 4 assi). Punteggio di base: 40 Chips, 4 Mult.
  - _Full House_: 3 carte che formano un _Three of a Kind_ e altre 2 che formano una _Pair_ (es. 3 assi, 2 re). Punteggio di base: 40 Chips, 4 Mult.
  - _Flush_: 5 carte dello stesso seme. Punteggio di base: 35 Chips, 4 Mult.
  - _Straight_: 5 carte di valore consecutivo (es. Due,Tre,Quattro,Cinque,Sei). Punteggio di base: 30 Chips, 4 Mult. L'asso può valere sia come carta precedente al Due che come carta successiva al Re.
  - _Three of a Kind_: 3 carte dello stesso valore (es. 3 assi). Punteggio di base: 30 Chips, 3 Mult.
  - _Two Pair_: 2 coppie (es. 2 assi e 2 re). Punteggio di base: 20 Chips, 2 Mult.
  - _Pair_: 2 carte dello stesso valore (es. 2 assi). Punteggio di base: 10 Chips, 2 Mult.
  - _High Card_: nessuna delle altre combinazioni. Punteggio di base: 5 Chips, 1 Mult.
- **[2.2.8]** Una combinazione ha un livello che ne aumenta il punteggio di base.
- **[2.2.8]** Una carta pianeta è associata ad una combinazione di carte e può essere utilizzata per aumentarne il livello. Le carte pianeta disponibili sono le seguenti, ognuna delle quali aumenta il punteggio di base della combinazione:
  - _Eris_, associato a Flush Five, aumenta il punteggio di base di +50 Chips e +3 Mult
  - _Ceres_, associato a Flush House, aumenta il punteggio di base di +40 Chips e +4 Mult
  - _Planet X_, associato a Five of a Kind, aumenta il punteggio di base di +35 Chips e +3 Mult
  - _Neptune_, associato a Straight Flush, aumenta il punteggio di base di +40 Chips e +4 Mult
  - _Mars_, associato a Four of a Kind, aumenta il punteggio di base di +30 Chips e +3 Mult
  - _Earth_, associato a Full House, aumenta il punteggio di base di +25 Chips e +2 Mult
  - _Jupiter_, associato a Flush, aumenta il punteggio di base di +15 Chips e +2 Mult
  - _Saturn_, associato a Straight, aumenta il punteggio di base di +30 Chips e +3 Mult
  - _Venus_, associato a Three of a Kind, aumenta il punteggio di base di +20 Chips e +2 Mult
  - _Uranus_, associato a Two Pair, aumenta il punteggio di base di +20 Chips e +1 Mult
  - _Mercury_, associato a Pair, aumenta il punteggio di base di +15 Chips e +1 Mult
  - _Pluto_, associato a High Card, aumenta il punteggio di base di +10 Chips e +1 Mult

- **[2.2.8]** Un blind viene affrontato all'interno di un _round_.
- **[2.2.9]** Le carte joker e i Boss blind invocano degli effetti che modificano alcune caratteristiche della partita. Un effetto ha un preciso momento della partita in cui viene invocato.
- **[2.2.10]** Gli effetti dei blind hanno priorità su quelli delle carte joker. Relativamente alla posizione delle carte joker, gli effetti sono applicati da sinistra a destra.
- **[2.2.9]** All'inizio del round il deck viene mescolato e al giocatore viene assegnata una mano.
- **[2.2.7]** Una mano è composta da 8 carte, ottenute pescandole dal deck. Qualora non sia possibile pescare dal deck il numero di carte necessarie a raggiungere la dimensione della mano (il deck ha esaurito le carte), essa avrà un numero ridotto di carte.
- **[2.2.7]** Durante il round è possibile effettuare le seguenti azioni:
  - giocare: selezionare tra 1 e 5 carte di cui viene calcolato il punteggio, sommato a quello del blind corrente. Infine le carte giocate vengono scartate
  - scartare: selezionare tra 1 e 5 carte che vengono rimosse dalla mano senza essere reinserite nel mazzo. Una volta rimosse, vengono pescate un pari numero di carte dal mazzo
  - cambiare l'ordine delle carte joker
- **[2.2.8]** Il calcolo del punteggio è suddiviso nelle seguenti fasi:
    1. Si considerano le carte selezionate e si determina il punteggio di base della giocata considerando la combinazione di carte e il corrispettivo livello. La combinazione di carte è scelta seguendo la proprità stabilita in **[2.2.9]**.
    2. Vengono applicati gli effetti di tipo "A mano giocata" del boss blind e delle carte joker.
    3. Ogni carta contribuisce al punteggio aggiungendo al numero di Chips corrente il suo valore (11 per l'asso, 10 per Re,Regina,Jack,Dieci, 9 per Nove e così via).
    4. Dopo il contributo di una singola carta vengono invocati gli effetti di tipo "A carta giocata" di boss blind e joker.
    5. Al termine dei contributi e dei relativi effetti, vengono applicati gli effetti di tipo "Al termine di mano giocata" di boss blind e joker.
    6. Il punteggio complessivo della mano è calcolato combinando Chips e Mult accumulati fino a questo momento.

## Requisiti non funzionali

## Requisiti di implementazione

[Indice](../index.md) | [Indietro](../2-development-process/index.md)
