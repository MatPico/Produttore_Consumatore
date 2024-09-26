
package test;

public class FileCopy {
    public static void main(String args[]) throws java.io.FileNotFoundException, java.io.IOException,
            InterruptedException {

        // Creazione di uno stream di input per leggere dal file "commedia.txt"
        java.io.InputStream in = new java.io.FileInputStream("commedia.txt");
        // Creazione di uno stream di output per scrivere sul file "copia.txt"
        java.io.OutputStream out = new java.io.FileOutputStream("copia.txt");
        // Creazione di un buffer circolare condiviso tra i thread di lettura e scrittura
        RingBuffer buffer = new RingBuffer();
        // Creazione del thread di lettura, che legge dal file di input e inserisce i dati nel buffer
        Leggi leggi = new Leggi(buffer, in);
        // Creazione del thread di scrittura, che prende i dati dal buffer e li scrive sul file di output
        Scrivi scrivi = new Scrivi(buffer, out);
        // Avvio del thread di lettura
        leggi.start();
        // Avvio del thread di scrittura
        scrivi.start();
        // Attende che il thread di lettura termini prima di procedere
        leggi.join();        
        // Chiude lo stream di input dopo che la lettura è completata
        in.close();
        // Attende che il thread di scrittura termini prima di procedere
        scrivi.join();
        // Chiude lo stream di output dopo che la scrittura è completata
        out.close();
    }
}
