# Specifica dei requisiti

In questa sezione viene presentata la specifica dei requisiti.

## Requisiti di business

- **[1.1]** Presentazione di un buon progetto per sostenere l'esame di Paradigmi di Programmazione e Sviluppo.
- **[1.2]** Realizzazione di una versione semplificata del videogioco Balatro. Essa deve includere: carta pianeta, carte joker, organizzazione della partita in blind.

## Requisiti funzionali

### Requisiti funzionali utente

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
- **[2.1.13]** L'utente deve poter riordinare le carte in mano: per seme, per valore o manualmente.
- **[2.1.14]** L'utente deve poter vedere il numero di giocate e scarti rimanenti per il blind corrente.
- **[2.1.15]** L'utente deve poter vedere quali carte tra quelle giocate contribuiscono al punteggio.
- **[2.1.16]** L'utente deve poter vedere, al superamento di un blind o in caso di sconfitta, una schermata di riepilogo del blind con le seguenti informazioni: messaggio di superamento/sconfitta, punteggio richiesto dal blind, punteggio effettuato, numero di giocate rimanenti e numero di scarti rimanenti.
- **[2.1.17]** L'utente deve poter visualizzare le carte da gioco presenti nel mazzo in ordine.
- **[2.1.18]** L'utente deve poter visualizzare i livelli di ciascuna combinazione di carte.
- **[2.1.19]** L'utente deve poter selezionare al più un pacchetto dal negozio.
- **[2.1.20]** L'utente deve poter selezionare al più un oggetto da un pacchetto.
- **[2.1.21]** L'utente può specificare il seed della partita prima di avviarla (utilizzando la riga di comando).
- **[2.1.22]** L'utente deve poter avviare la ricerca di un seed (utilizzando la riga di comando) che soddisfa certi vincoli da lui espressi.

### Requisiti funzionali di sistema

- **[2.2.1]** Ciascuna partita del gioco è indipendente dalle altre.
- **[2.2.2]** Una partita è strutturata in livelli (_blind_), raggruppati a 3 per formare un'_anta_.
- **[2.2.3]** In un'anta il primo (_Small_) e il secondo (_Big_) blind non hanno effetti. Il terzo (_Boss_) blind introduce degli effetti svantaggiosi per il giocatore.
- **[2.2.4]** Un blind richiede di raggiungere un certo punteggio per essere superato.
- **[2.2.5]** Il punteggio richiesto aumenta da un blind a quello successivo.
- **[2.2.6]** Il punteggio consiste in un numero a virgola mobile, ottenuto dalla combinazione di due componenti numeriche: _Chips_ e _Mult_.
- **[2.2.7]** La partita prevede che inizialmente il giocatore abbia a disposizione un mazzo (_deck_) di carte da poker. Il deck è formato da 52 carte, 1 per ogni combinazione dei seguenti valori e semi:
  - valori: Asso, Re, Regina, Jack, Dieci, Nove, Otto, Sette, Sei, Cinque, Quattro, Tre, Due.
  - semi: Cuori, Quadri, Fiori, Picche.
- **[2.2.8]** Il deck può subire modifiche permanenti nel corso della partita.
- **[2.2.9]** Le carte del mazzo possono formare le seguenti combinazioni, ognuna delle quali fornisce la base per il calcolo del punteggio:
  - _Flush Five_: 5 carte che formano sia Five of a Kind che Flush (es. 5 assi di Cuori). Punteggio di base: 160 Chips, 16 Mult;
  - _Flush House_: 5 carte che formano sia Full House che Flush (es. 3 assi di Cuori e 2 Re di Cuori). Punteggio di base: 140 Chips, 14 Mult;
  - _Five of a Kind_: 5 carte dello stesso valore (es. 4 assi di Cuori e 1 Asso di Picche). Punteggio di base: 120 Chips, 12 Mult;
  - _Straight Flush_: 5 carte che formano sia Straight che Flush (es. Asso, Due, Tre, Quattro, Cinque di Cuori). Punteggio di base: 100 Chips, 8 Mult;
  - _Four of a Kind_: 4 carte dello stesso valore (es. 4 assi). Punteggio di base: 40 Chips, 4 Mult;
  - _Full House_: 3 carte che formano un _Three of a Kind_ e altre 2 che formano una _Pair_ (es. 3 assi, 2 Re). Punteggio di base: 40 Chips, 4 Mult;
  - _Flush_: 5 carte dello stesso seme. Punteggio di base: 35 Chips, 4 Mult;
  - _Straight_: 5 carte di valore consecutivo (es. Due,Tre,Quattro,Cinque,Sei). Punteggio di base: 30 Chips, 4 Mult. L'Asso può valere sia come carta precedente al Due che come carta successiva al Re;
  - _Three of a Kind_: 3 carte dello stesso valore (es. 3 assi). Punteggio di base: 30 Chips, 3 Mult;
  - _Two Pair_: 2 coppie (es. 2 assi e 2 Re). Punteggio di base: 20 Chips, 2 Mult;
  - _Pair_: 2 carte dello stesso valore (es. 2 assi). Punteggio di base: 10 Chips, 2 Mult;
  - _High Card_: nessuna delle altre combinazioni. Punteggio di base: 5 Chips, 1 Mult.
- **[2.2.10]** Una combinazione ha un livello che ne aumenta il punteggio di base.
- **[2.2.11]** Una carta pianeta è associata ad una combinazione di carte e può essere utilizzata per aumentarne il livello. Le carte pianeta disponibili sono le seguenti, ognuna delle quali aumenta il punteggio di base della combinazione:
  - _Eris_, associato a Flush Five, aumenta il punteggio di base di +50 Chips e +3 Mult;
  - _Ceres_, associato a Flush House, aumenta il punteggio di base di +40 Chips e +4 Mult;
  - _Planet X_, associato a Five of a Kind, aumenta il punteggio di base di +35 Chips e +3 Mult;
  - _Neptune_, associato a Straight Flush, aumenta il punteggio di base di +40 Chips e +4 Mult;
  - _Mars_, associato a Four of a Kind, aumenta il punteggio di base di +30 Chips e +3 Mult;
  - _Earth_, associato a Full House, aumenta il punteggio di base di +25 Chips e +2 Mult;
  - _Jupiter_, associato a Flush, aumenta il punteggio di base di +15 Chips e +2 Mult;
  - _Saturn_, associato a Straight, aumenta il punteggio di base di +30 Chips e +3 Mult;
  - _Venus_, associato a Three of a Kind, aumenta il punteggio di base di +20 Chips e +2 Mult;
  - _Uranus_, associato a Two Pair, aumenta il punteggio di base di +20 Chips e +1 Mult;
  - _Mercury_, associato a Pair, aumenta il punteggio di base di +15 Chips e +1 Mult;
  - _Pluto_, associato a High Card, aumenta il punteggio di base di +10 Chips e +1 Mult.
- **[2.2.12]** Un blind viene affrontato all'interno di un _round_.
- **[2.2.13]** Le carte joker e i Boss blind invocano degli effetti che modificano alcune caratteristiche della partita. Ciascun effetto viene invocato in un preciso momento della partita.
- **[2.2.14]** Gli effetti dei blind hanno priorità su quelli delle carte joker. Relativamente alla posizione delle carte joker, gli effetti sono applicati da sinistra a destra.
- **[2.2.15]** All'inizio del round il deck viene mescolato e al giocatore viene assegnata una mano.
- **[2.2.16]** Una mano è composta da 8 carte, ottenute pescandole dal deck. Qualora non sia possibile pescare dal deck il numero di carte necessarie a raggiungere la dimensione della mano (il deck ha esaurito le carte), essa avrà un numero ridotto di carte.
- **[2.2.17]** Durante il round è possibile effettuare le seguenti azioni:
  - Giocare: selezionare tra 1 e 5 carte di cui viene calcolato il punteggio, sommato a quello del blind corrente. Infine le carte giocate vengono scartate;
  - Scartare: selezionare tra 1 e 5 carte che vengono rimosse dalla mano senza essere reinserite nel mazzo. Una volta rimosse, vengono pescate un pari numero di carte dal mazzo;
  - Cambiare l'ordine delle carte joker.
- **[2.2.18]** Il calcolo del punteggio è suddiviso nelle seguenti fasi:
    1. Si considerano le carte selezionate e si determina il punteggio di base della giocata considerando la combinazione di carte e il corrispettivo livello. La combinazione di carte è scelta seguendo la proprità stabilita in **[2.2.18]**.
    2. Vengono applicati gli effetti di tipo "A mano giocata" del boss blind e delle carte joker.
    3. Ogni carta che fa parte della combinazione contribuisce al punteggio aggiungendo al numero di Chips corrente il suo valore (11 per l'Asso, 10 per Re,Regina,Jack,Dieci, 9 per Nove e così via).
    4. Dopo il contributo di ogni carta vengono invocati gli effetti di tipo "A carta a segno" di boss blind e joker.
    5. Al termine dei contributi e dei relativi effetti, vengono applicati gli effetti di tipo "Al termine di mano giocata" di boss blind e joker.
    6. Il punteggio complessivo della mano è calcolato combinando Chips e Mult accumulati fino a questo momento.
- **[2.2.19]** Una carta può venire _debuffed_. In questo caso la fase 3 e 4 del calcolo del punteggio ([2.2.19]) vengono saltate.
- **[2.2.20]** In ciascun round il giocatore ha a disposizione un numero finito di giocate e scarti.
- **[2.2.21]** Un round non viene superato, dunque la partita termina, quando si verifica una delle seguenti situazioni:
    1. il giocatore non ha raggiunto il punteggio richiesto nel numero di giocate a disposizione
    2. il giocatore ha esaurito le carte in mano e nel mazzo senza raggiungere il punteggio richiesto.
- **[2.2.22]** Al superamento di un round, il giocatore accede al negozio (_shop_), dove ha la possibilità di scegliere al più 1 tra i seguenti pacchetti:
  - pacchetto carte da gioco (_standard pack_)
  - pacchetto carte pianeta (_celestial pack_)
  - pacchetto carta joker (_buffoon pack_).
- **[2.2.23]** Ciascun pacchetto contiene fino a 3 elementi e il giocatore può selezionarne al più 1.
- **[2.2.24]** Il buffoon pack propone solamente joker di cui non si è a disposizione.
- **[2.2.25]** I boss blind sono i seguenti:
  - _The Needle_. Effetto "A inizio round": riduce il numero delle giocate a 1
  - _The Water_. Effetto "A inizio round": azzera il numero di scarti
  - _The Flint_. Effetto "A mano giocata": Chips e Mult del punteggio di base per ogni combinazione vengono dimezzati
  - _The Head_. Effetto "A carta a segno": tutte le carte di Cuori sono debuffed
  - _The Club_. Effetto "A carta a segno": tutte le carte di Fiori sono debuffed
  - _The Goad_. Effetto "A carta a segno": tutte le carte di Picche sono debuffed
  - _The Window_. Effetto "A carta a segno": tutte le carte di quadri sono debuffed
  - _The Plant_. Effetto "A carta a segno": tutte le carte "figura" (Re, Regina, Jack) sono debuffed
- **[2.2.26]** Le carte joker sono le seguenti:
  - _Clever Joker_. Effetto "Al termine di mano giocata": +80 Chips se la mano giocata contiene un Two Pair;
  - _Crafty Joker_. Effetto "Al termine di mano giocata": +80 Chips se la mano giocata contiene un Flush;
  - _Crazy Joker_. Effetto "Al termine di mano giocata": +12 Mult se la mano giocata contiene una Straight;
  - _Jolly Joker_. Effetto "Al termine di mano giocata": +8 Mult se la mano giocata contiene una Pair;
  - _Devious Joker_. Effetto "Al termine di mano giocata": +100 Chips se la mano giocata contiene una Straight;
  - _The Tribe_. Effetto "Al termine di mano giocata": X2 Mult se la mano giocata contiene un Flush;
  - _The Order_. Effetto "Al termine di mano giocata": X3 Mult se la mano giocata contiene una Straight;
  - _The Duo_. Effetto "Al termine di mano giocata": X2 Mult se la mano giocata contiene una Pair;
  - _Arrowhead_. Effetto "A carta a segno": +50 Chips se la carta è di Picche;
  - _Onyx Gate_. Effetto "A carta a segno": +7 Mult se la carta è di Fiori;
  - _Fibonacci_. Effetto "A carta a segno": +8 Mult se la carta è Asso, Due, Tre, Cinque, Otto;
  - _Scholar_. Effetto "A carta a segno": +20 Chips e +4 Mult se la carta è un Asso. Effetto "Ad acquisto": aumento della probabilità di ottenere assi e riduzione della probabilità di ottenere carte figura nei pacchetti dello shop;
  - _Juggler_. Effetto "A inizio round": +1 giocata e +1 scarto.
- **[2.2.27]** Lo svolgimento della partita è condizionato da un seme (_seed_), ovvero un numero che determina la sequenza e l'estrazione degli eventi che sfruttano casualità. Dato lo stesso seed, è possibile rigiocare la stessa partita. Gli eventi influenzati dal seed sono:
  - mescolamento del mazzo
  - contenuto dei pacchetti nello shop
  - apparizione dei boss blind
- **[2.2.28]** La ricerca di un seed avviene specificando un numero arbitrario di vincoli e il numero di round a cui essi sono associati. Durante la ricerca avvengono varie simulazioni fino a trovare il seed richiesto. I vincoli riguardano:
  - la presenza di carte nella mano a inizio di un round
  - la presenza di una combinazione di carte nella mano a inizio round
  - la presenza di una specifica carta, joker o pianeta all'interno di un pacchetto alla fine di un round
- **[2.2.29]** La ricerca di seed funziona grazie alle seguenti assunzioni:
  - Per ogni vincolo sul contenuto di un pacchetto, si assume che il giocatore scelga il contenuto specificato
  - Per ogni round in cui non è specificato nessun vincolo sul contenuto di un pacchetto, si assume che nessun pacchetto venga scelto

## Requisiti non funzionali

- **[3.1]** Il sistema deve fornire un'interfaccia grafica che aiuti il giocatore nella scelta delle azioni in maniera intuitiva e rapida.
- **[3.2]** L'interfaccia grafica deve ricordare il gioco originale.

## Requisiti di implementazione

- **[4.1]** Utilizzare Scala 3
- **[4.2]** Utilizzare costrutti di Scala avanzati
- **[4.3]** Utilizzare un approccio quanto più funzionale possibile
- **[4.4]** Utilizzare Prolog
- **[4.5]** Utilizzare JDK 25+

[Indice](../index.md) | [Indietro](../2-development-process/index.md) | [Avanti](../4-architectural-design/index.md)
