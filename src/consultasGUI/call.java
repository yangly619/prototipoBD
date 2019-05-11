package consultasGUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class call {
	
	public static void main( String args[] ){  
	    
	    final Window app = new Window();   
	      
	    app.addWindowListener( new WindowAdapter() {  
	        public void windowClosing( WindowEvent e ){  
	            app.shutDown();  
	            System.exit( 0 );   
	        }  
	    });  
	}  
}

