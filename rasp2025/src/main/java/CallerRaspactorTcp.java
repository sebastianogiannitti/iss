package main.java;

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;

public class CallerRaspactorTcp {
	
	protected IApplMessage turnOn   = CommUtils.buildDispatch("callertcp", "turnOn",  "turnOn(ok)",  "raspactor");
	protected IApplMessage turnOff  = CommUtils.buildDispatch("callertcp", "turnOff", "turnOff(ok)", "raspactor");
	
	public void doJob() {
        String hostAddr       = "192.168.137.2"; 
        int port              = 8080;
        ProtocolType protocol = ProtocolType.tcp;

        Interaction conn = ConnectionFactory.createClientSupport(protocol, hostAddr, ""+port);
        
        try {
        	CommUtils.outblue("callertcp  :" + turnOn);
        	 conn.forward(turnOn);
        	 CommUtils.delay(3000);
         	CommUtils.outblue("callertcp  :" + turnOff);
       	    conn.forward(turnOff);
         	
       	    CommUtils.delay(3000);
       	    CommUtils.outmagenta("callertcp | ENDS"   );
        	System.exit(0);
		} catch (Exception e) {
 			CommUtils.outred("callertcp ERROR:" + e.getMessage() );
		}
	}

	 public static void main( String[] args ){
		 new CallerRaspactorTcp().doJob();
	 }
}
