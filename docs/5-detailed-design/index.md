# Design di dettaglio

## Dati di gioco

### GameState

All'interno del sistema c'è la necessità di rappresentare alcune informazioni che persistono tra un round e l'altro. Queste informazioni sono raggruppate all'intern di _GameState_ e riguardano:
- le carte di cui è composto un Deck;
- dimensione della mano;
- numero di giocate e scarti per ogni round;
- informazioni sul blind corrente;
- i joker accumulati;
- i livelli delle combinazioni di carte.

### RoundState

Il ciclo di vita di alcune informazioni invece è limitato solamente al round corrente e sono necessarie per il suo svolgimento. Queste sono raggruppate in _RoundState_ e riguardano:
- il punteggio attuale;
- le carte presenti nella mano;
- le carte rimaste all'interno del mazzo;
- il numero di giocate e scarti rimasti;
- il GameState per recuperare alcune informazioni necessarie per il corretto svolgimento del round.

Nello specifico, il mazzo iniziale utilizzato all'interno del round è ottenuto mischiando quello globale contenuto nel GameState.

## Dinamica del sistema

### Stati del sistema

In ogni momento il sistema può trovarsi o in fase di gioco (ovvero all'interno di un round) oppure all'interno dello shop. La fine della fase di gioco avviene in seguito alla conclusione di un round. In caso il round sia stato superato con successo, il giocatore accede allo shop, dove ha la possibilità di visualizzare i livelli delle combinazioni di carte e di aprire un pacchetto, selezionando eventualmente un oggetto. Conclusa la fase dello shop, il giocatore rientra nella fase di gioco iniziando il round successivo. Nel caso invece di sconfitta, può iniziare una nuova partita.

![Model Update](Model_Update.svg)

### Esecuzione del round

Il componente che si occupa di gestire il flusso di esecuzione del round è _RoundManager_, che esegue ciclicamente, fino alla terminazione del round, le seguenti azioni:
1. Aggiornamento della grafica;
2. Recupero della prossima azione di gioco (Play, Discard o Order);
3. Processamento di quest'ultima;
4. Aggiornamento del round.

![Round Flow](Round_Flow.svg)

## Calcolo del punteggio

Il calcolo del punteggio si avvale di diversi componenti:
- Mano di gioco: sequenza di carte giocate;
- Joker posseduti;
- Blind corrente;
- HandScoreCalculator: componente che fornisce la strategia per ottenere il punteggio a partire da chips e mult accumulati.

![Score Engine](Score_Engine.svg)

La computazione del punteggio si articola in diverse fasi, come descritto nel requisito [2.2.19].

[Indice](../index.md) | [Indietro](../4-architectural-design/index.md)