package unrevealing;

public class TestGeneral {
	public static void main(String[] args) throws Exception{
		// Anchored.process(args[0], args[1] );
		// Anchored.compute(args[0], args[1] );
		// Anchored.compute_M(args[0], args[1] );
		// Anchored.createMmutableGraph(args[0], args[1] );
		// Anchored.createKCore(args[0], args[1], 3);
		Anchored.findAnchors(args[0], args[1], 3);
		//Anchored.process("/Users/luqin/Documents/workspace/data/twitter-2010/twitter-2010", "/Users/luqin/Documents/workspace/data/twitter-2010/twitter-2010.txt" );
		//Anchored.process( "H:/datasets/twitter-2010/twitter-2010", "H:/datasets/twitter-2010/twitter-2010.txt" );
		//Anchored.process( "H:/datasets/uk-2007/uk-2007-05", "H:/datasets/uk-2007/uk-2007-05.txt" );
	}
}
