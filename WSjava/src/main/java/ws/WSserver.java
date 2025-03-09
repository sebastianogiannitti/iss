package ws;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import conway.Life; // Importiamo la logica del gioco

@ServerEndpoint("/conway")
public class WSserver {
    private static final CopyOnWriteArraySet<WSserver> clients = new CopyOnWriteArraySet<>();
    private static Life life = new Life(10, 10); // Creiamo una griglia 10x10
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        clients.add(this);
        System.out.println("Connessione aperta: " + session.getId());
        sendGridState();
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Messaggio ricevuto: " + message);

        if (message.equals("next")) {
            life.computeNextGen();
            sendGridState();
        } else if (message.startsWith("toggle:")) {
            String[] parts = message.split(":");
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            life.switchCellState(x, y);
            sendGridState();
        }
    }

    @OnClose
    public void onClose() {
        clients.remove(this);
        System.out.println("Connessione chiusa: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Errore WebSocket: " + throwable.getMessage());
    }

    private void sendGridState() {
        StringBuilder gridState = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                gridState.append(life.getCellState(i, j) ? "1" : "0");
            }
            gridState.append("\n");
        }

        broadcast(gridState.toString());
    }

    private void broadcast(String message) {
        for (WSserver client : clients) {
            try {
                client.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
