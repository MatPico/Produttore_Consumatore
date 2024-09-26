
package test;

public class RingBuffer {

    // Classe interna per rappresentare un blocco di dati
    class Block {
        public static final int DIM = 64;  // Dimensione massima di un blocco in byte
        private byte[] block;  // Array di byte che contiene i dati del blocco
        // Costruttore: copia 'n' byte dall'array 'data' nel blocco
        public Block(byte[] data, int n) {
            put(data, n);
        }
        // Metodo per inserire i dati nel blocco, copiando i primi 'n' byte dall'array 'data'
        public void put(byte[] data, int n) {
            block = java.util.Arrays.copyOf(data, n);
        }
        // Restituisce una copia dei dati presenti nel blocco
        public byte[] get() {
            byte[] data = java.util.Arrays.copyOf(block, block.length);
            return data;
        }
    }

    // Numero massimo di blocchi nel buffer circolare
    public static final int N = 16;
    // Array di blocchi che rappresenta il buffer circolare
    private Block[] ring_buffer;
    // Indici per tenere traccia della posizione corrente di scrittura e lettura
    private int write_index;  // Indice per inserire nuovi blocchi
    private int read_index;   // Indice per leggere blocchi esistenti
    // Variabili per tracciare il numero di blocchi presenti e lo stato di fine file (EOF)
    private volatile int N_block;  // Numero di blocchi attualmente presenti nel buffer
    private volatile boolean eof;  // Indica se è stato raggiunto l'EOF (fine file)
    // Costruttore della classe RingBuffer: inizializza il buffer, gli indici e lo stato
    public RingBuffer() {
        ring_buffer = new Block[N];  // Inizializza il buffer circolare con N blocchi
        write_index = 0;  // Inizializza l'indice di scrittura
        read_index = 0;   // Inizializza l'indice di lettura
        N_block = 0;      // Nessun blocco è ancora stato inserito
        eof = false;      // EOF non è ancora stato raggiunto
    }

    // Metodo per impostare lo stato di EOF e notificare tutti i thread in attesa
    public synchronized void setEOF() {
        eof = true;
        notify();  // Notifica i thread che potrebbero essere in attesa
    }

    // Metodo che controlla se il buffer è vuoto e l'EOF è stato raggiunto
    public synchronized boolean end() {
        return eof && (N_block == 0);  // Ritorna true se non ci sono più blocchi da leggere e EOF è stato raggiunto
    }

    // Metodo per inserire un blocco di dati nel buffer circolare
    public synchronized void put(byte[] data, int n) throws InterruptedException {
        Block block = new Block(data, n);  // Crea un nuovo blocco con i dati forniti
        // Se il buffer è pieno (N_block == N), attende che si liberi spazio
        while (N_block == N)
            wait();  // Mette il thread in attesa fino a quando non c'è spazio nel buffer
        // Inserisce il blocco nel buffer all'indice di scrittura e aggiorna l'indice
        ring_buffer[write_index] = block;
        write_index = (write_index + 1) % N;  // Aggiorna l'indice di scrittura (ciclicamente)
        N_block++;  // Incrementa il conteggio dei blocchi nel buffer
        notify();  // Notifica i thread in attesa (ad esempio, il thread di lettura)
    }

    // Metodo per recuperare un blocco di dati dal buffer circolare
    public synchronized byte[] get() throws InterruptedException {
        Block block;
        // Se il buffer è vuoto (N_block == 0), attende che ci siano blocchi disponibili
        while (N_block == 0)
            wait();  // Mette il thread in attesa fino a quando non ci sono blocchi da leggere
        // Recupera il blocco all'indice di lettura e aggiorna l'indice
        block = ring_buffer[read_index];
        read_index = (read_index + 1) % N;  // Aggiorna l'indice di lettura (ciclicamente)
        N_block--;  // Decrementa il conteggio dei blocchi nel buffer
        notify();  // Notifica i thread in attesa (ad esempio, il thread di scrittura)
        // Restituisce i dati del blocco
        return block.get();
    }
}
