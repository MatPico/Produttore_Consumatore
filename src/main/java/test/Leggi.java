
package test; 

// La classe Leggi estende Thread ed è responsabile della lettura dei dati da un InputStream
class Leggi extends Thread {
    // Variabile per il buffer condiviso (RingBuffer)
    RingBuffer buffer;
    // Stream di input da cui leggere i dati
    java.io.InputStream stream;

    // Costruttore: inizializza il buffer e lo stream di input
    public Leggi(RingBuffer buffer, java.io.InputStream stream) {
        this.buffer = buffer;
        this.stream = stream;
    }

    // Metodo eseguito quando il thread viene avviato
    @Override
    public void run() {
        int n;  // Numero di byte letti
        byte[] data = new byte[RingBuffer.Block.DIM];  // Array di byte per memorizzare i dati letti
        try {
            // Ciclo per leggere continuamente i dati dallo stream di input
            while ((n = stream.read(data)) > 0) {  // Legge fino a quando ci sono dati disponibili
                // Verifica se il thread è stato interrotto; in tal caso, esce dal ciclo
                if (Thread.interrupted())
                    break;
                // Inserisce i dati letti nel buffer condiviso
                buffer.put(data, n);
            }
            // Una volta che la lettura è completata, segnala la fine del file (EOF) nel buffer
            buffer.setEOF();
        }
        // Gestisce eventuali eccezioni di I/O o interruzioni del thread
        catch (java.io.IOException | InterruptedException e) {
            return;  // Se c'è un'eccezione, il metodo termina
        }
    }
}

