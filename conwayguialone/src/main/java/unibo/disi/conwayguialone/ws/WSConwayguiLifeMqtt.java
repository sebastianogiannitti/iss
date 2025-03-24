package unibo.disi.conwayguialone.ws; 

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import jakarta.websocket.server.ServerEndpoint;
import unibo.basicomm23.mqtt.MqttInteraction;
import unibo.basicomm23.utils.CommUtils;

/*

 */

//@Component //Ci pensa WebSocketConfiguration
@ServerEndpoint("/wsupdates") // spring istanzia questa classe nel
//momento in cui parte
public class WSConwayguiLifeMqtt extends AbstractWebSocketHandler{   
	
	private static WSConwayguiLifeMqtt myself        = null;
	private MqttInteraction mqttConn;
	private final String name                    = "wslifeguimqtt";
	private Set<WebSocketSession> clients        = new HashSet<>();
	private boolean firstConnection              = true;
	private boolean ownerOn                      = true;
	private WebSocketSession owner;
	
	
	//creo un singleton, da riutilizzare per le chiamate successive
	public static WSConwayguiLifeMqtt getInstance() {
		//CommUtils.outblue( name + " | getInstance " + myself );
		if (myself == null) {
			myself = new WSConwayguiLifeMqtt();
		}
		return myself;
	}  
	
	public WSConwayguiLifeMqtt() {
		/*
		 * Stesso schema di connessione usato da Life
		 */
		//appena si avvia usa l'interazione mqtt
 		useMqttInteraction();
    }
	
	protected void useMqttInteraction() {
		CommUtils.outblue(name + " | useMqttInteraction  "  );
		//controlla se nelle variabili c'è una certa variabile
		String broker = System.getenv("MQTTBROKER_URL");
		//nel mio caso non c'è quindi il broker è sicuramente null
		//quindi assumo che il broker sia locale
		if( broker == null ) broker = "tcp://localhost:1883";
		else {
			//CommUtils.outgreen( "WSConwaygui | useMqttInteraction in docker to " + broker ); 	
		}					


		//capito dove si trova il broker creo l'interazione
		mqttConn = new MqttInteraction(  name, broker, "guiin", "lifein" );

		//nuovo thread
		new Thread() {
			public void run() { //activateReceive(); NECESSARIO SE Life non usa WS
				try {
					while (true) {
	 					String msg = "";
						 //si mette in attesa BLOCCANTE dei messaggi che il servizio manda alla GUI
						msg = mqttConn.receiveMsg();
//						CommUtils.outblack(name + " | WSConwayguiLifeMqtt receives via mqtt: " + msg);
					    //if( msg.startsWith("cell-") ) broadcastToWebSocket(msg);
						elabMsgMqtt( msg );
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();		
 	
	}

 	
	@Override //from AbstractWebSocketHandler
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);		
		//CommUtils.outblue(name + " | WebSocket: " + session.getId() + " connected!");
		if( firstConnection ) {
			owner           = session;
			firstConnection = false;
		}
        clients.add(session);
	}
 	 
	/*
	 * Invio comandi alle pagine HTML connesse
	 */
    //Usato da MqttConnCallback e dal receiver su MqttInteraction
	public void broadcastToWebSocket(String message) {
//		CommUtils.outyellow(name + " broadcastToWebSocket " + message);
        for (WebSocketSession client : clients) {
            if (client.isOpen()) {
                try {
                    client.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    //CommUtils.outred(name + "ERROR: COULD NOT SEND TEXT THROUGH WEBSOCKET TO " + client.getId() + "!");
                }
            }
        } 
    }	
	
	/*
	 * Gestione comandi provenienti dalla GUI
	 */ 	
	@Override //since AbstractWebSocketHandler
	//prendo il messaggio che arriva
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
// 		CommUtils.outgreen(name + " | receives: " + message + " wirh ownerOn=" + ownerOn );		
		String cmd = message.getPayload();
		elabMsg(cmd, session);				
	}
	
	protected void elabMsgMqtt( String cmd ) throws Exception {
		//se il messaggio inizia con "cell(" lo mando attraverso la websocket a tutti i client collegati
		if( cmd.startsWith("cell(") ){ //IL comando arriva dalla applicazione remota
			broadcastToWebSocket(cmd);
			return;
	    }

		//prendo i messaggi che arrivano dall'applicazione e li inoltro ai client connessi
		if( cmd.startsWith("GAME") ){ //IL comando arriva dalla applicazione remota
			broadcastToWebSocket(cmd);
			return;
	    }

		//clear significa che lato server vuole azzerare tutte le celle,
		//anche qui manda il messaggio a tutti i browser collegati
		if( cmd.startsWith("clear") ){ //IL comando arriva dalla GUI ma ottimizzo
			CommUtils.outmagenta(name + " | receives: " + cmd   );	
			broadcastToWebSocket(cmd);
			//mqttConn.forward(cmd);
			return;
	    }		
 	}

	protected void elabMsg(String cmd, WebSocketSession session) throws Exception {
		if( cmd.startsWith("cell(") ){ //IL comnado arriva dalla applicazione remota
			broadcastToWebSocket(cmd);
			return;
	    }		
		if( cmd.startsWith("GAME") ){ //IL comnado arriva dalla applicazione remota
			broadcastToWebSocket(cmd);
			return;
	    }		
		if( cmd.startsWith("clear") ){ //IL comnado arriva dalla GUI ma ottimizzo
//			CommUtils.outmagenta(name + " | receives: " + cmd   );	
			broadcastToWebSocket(cmd);
			mqttConn.forward(cmd);
			return;
	    }		
		if( cmd.contains("owneroff")) ownerOn = false;
		if( cmd.contains("owneron"))  ownerOn = true;
		if( session != null && ! session.equals(owner) && ownerOn) {
			CommUtils.outmagenta(name + " | received from a non-owner "   );
			return;
		} 	
        //Il comando va verso l'applicazione remota
		if( mqttConn != null ) {
			CommUtils.outgreen(name + " | handleTextMessage forward on mqttConn: " + cmd) ;	
			mqttConn.forward(cmd);  
			if( cmd.equals("exit")) System.exit(0);
	    } 
		
	}
}
