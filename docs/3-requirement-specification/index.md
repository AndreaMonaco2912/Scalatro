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
- **[2.1.2]** L'utente deve poter vedere le carte da gioco in mano
- **[2.1.3]** L'utente deve poter vedere il numero di carte da gioco rimanenti nel mazzo
- **[2.1.4]** L'utente deve poter vedere le carte joker possedute e i loro effetti
- **[2.1.5]** L'utente deve poter vedere il nome e gli effetti del blind corrente
- **[2.1.6]** L'utente deve poter vedere il numero del round e dell'ante raggiunti
- **[2.1.7]** L'utente deve poter vedere il punteggio da superare nel blind corrente
- **[2.1.8]** L'utente deve poter vedere il punteggio da lui ottenuto nel blind corrente
- **[2.1.9]** L'utente deve poter selezionare delle carte dalla mano
- **[2.1.10]** L'utente deve poter vedere la combinazione delle carte selezionate
- **[2.1.11]** L'utente deve poter vedere il punteggio di base e il livello della combinazione delle carte selezionate
- **[2.1.12]** L'utente deve poter giocare o scartare da 1 a 5 carte
- **[2.1.13]** L'utente deve poter riordinare le carte in mano:
  - per seme
  - per valore
  - manualmente
- **[2.1.14]** L'utente deve poter vedere il numero di giocate e scarti rimanenti per il blind corrente
- **[2.1.15]** L'utente deve poter vedere quali carte tra quelle giocate contribuiscono al punteggio
- **[2.1.16]** L'utente deve poter vedere, al superamento di un blind o in caso di sconfitta, una schermata di riepilogo del blind con le seguenti informazioni:
  - messaggio di superamento/sconfitta
  - punteggio richiesto dal blind
  - punteggio effettuato
  - numero di giocate rimanenti
  - numero di scarti rimanenti
- **[2.1.17]** L'utente deve poter visualizzare le carte da gioco presenti nel mazzo
- **[2.1.18]** L'utente deve poter visualizzare i livelli di ciascuna combinazione di carte
- **[2.1.19]** L'utente deve poter selezionare al più un pacchetto dal negozio
- **[2.1.20]** L'utente deve poter selezionare al più un oggetto da un pacchetto

### Requisiti funzionali di sistema

## Requisiti non funzionali

## Requisiti di implementazione

[Indice](../index.md) | [Indietro](../2-development-process/index.md)
