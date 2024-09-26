
package test;

// La classe Scrivi estende Thread ed è responsabile della scrittura dei dati su un OutputStream
class Scrivi extends Thread {
    // Variabile per il buffer condiviso (RingBuffer)
    RingBuffer buffer;
    // Stream di output su cui scrivere i dati
    java.io.OutputStream stream;

    // Costruttore: inizializza il buffer e lo stream di output
    public Scrivi(RingBuffer buffer, java.io.OutputStream stream) {
        this.buffer = buffer;
        this.stream = stream;
    }

    // Metodo eseguito quando il thread viene avviato
    @Override
    public void run() {
        byte[] data;  // Array di byte per memorizzare i dati estratti dal buffer
        try {
            // Ciclo principale: continua finché il buffer non è vuoto e non è stato raggiunto EOF
            while (!buffer.end()) {
                // Controlla se il thread è stato interrotto; in tal caso, esce dal ciclo
                if (Thread.interrupted())
                    break;
                // Estrae i dati dal buffer (bloccante se non ci sono dati disponibili)
                data = buffer.get();
                // Se ci sono dati disponibili, scrive nel file di output
                if (data != null) {
                    stream.write(data, 0, data.length);  // Scrive i dati sullo stream di output
                    stream.flush();  // Garantisce che i dati vengano effettivamente scritti
                }
            }
        }
        // Gestisce eventuali eccezioni di I/O o interruzioni del thread
        catch (java.io.IOException | InterruptedException e) {
            return;  // In caso di eccezione, il metodo termina senza ulteriori azioni
        }
    }
}
