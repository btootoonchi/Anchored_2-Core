/**
 *
 * @author Babak Tootoonchi, babakt@uvic.ca, 2015
 */
package ca.uvic.css.unrevealing;
import java.io.File;

public class TestGeneral {
	public static void main(String[] args) throws Exception {

		/*final File folder = new File(args[0]);
    	for (final File fileEntry : folder.listFiles()) {
        	if (!fileEntry.isDirectory()) {
        		if (fileEntry.getName().contains(".graph")) {
            		System.out.println(args[0]+fileEntry.getName().substring(0, fileEntry.getName().lastIndexOf('.')));
            		//System.out.println(args[0]+fileEntry.getName().substring(0, fileEntry.getName().lastIndexOf('.'))+".txt");
            		Anchored.findAnchors(args[0]+fileEntry.getName().substring(0, fileEntry.getName().lastIndexOf('.')), 
            			args[0]+fileEntry.getName().substring(0, fileEntry.getName().lastIndexOf('.'))+".txt", 3);
            	}
        	}
    	}*/

		Anchored.findAnchors(args[0], args[1], 5);
	}
}
