package ws;

import org.glassfish.tyrus.server.Server;

public class Main {
    public static void main(String[] args) {
            startWebSocketServer();
    }

    private static void startWebSocketServer() {
        // Crea un'istanza del server WebSocket
        Server server = new Server("localhost", 8081, "/ws", null, WSserver.class);

        try {
            server.start();
            System.out.println("Server WebSocket avviato su ws://localhost:8081/ws/conway");

            // Mantieni il server in esecuzione
            Thread.currentThread().join(); // Questo blocco impedisce all'app di terminare
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ferma il server quando l'applicazione termina
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
