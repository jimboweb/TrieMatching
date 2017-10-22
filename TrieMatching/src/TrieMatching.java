import java.io.*;
import java.util.*;

public class TrieMatching implements Runnable {
	int letterToIndex (char letter)
	{
		switch (letter)
		{
			case 'A': return 0;
			case 'C': return 1;
			case 'G': return 2;
			case 'T': return 3;
			default: assert (false); return -1;
		}
	}
        
        public void specificUnitTest(){
            int n = 1;
            String text = "";
            for(int i=0;i<9998;i++){
                text+="A";
            }
            text+="B";
            List<String> patterns = new ArrayList<String>();
            //patterns.add("AB");
            patterns.add("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB");
            final long startTime = System.currentTimeMillis();
            List<Integer> efficientAnswer = solve(text, n, patterns);
            final long endTime = System.currentTimeMillis();
            long timeDiff = endTime - startTime;
            System.out.printf("Program took %d milliseconds", timeDiff);
 
            
        }
        
        public void unitTest(){
            System.out.printf("Trial: ");
            int nMax = 10;
            int textMax = 100;
            int patternMax = 20;
            Random rnd = new Random();
            for(int i=0;i<10000; i++){
                String text = "";
                List<String> patterns = new ArrayList<>();
                int n = rnd.nextInt(nMax);
                int textLength = rnd.nextInt(textMax);
                for(int j=0;j<textLength;j++){
                    int nextChar =rnd.nextInt(4);
                    switch(nextChar){
                        case 0: text += "A";
                        break;
                        case 1: text += "T";
                        break;
                        case 2: text += "C";
                        break;
                        case 3: text += "G";
                        break;
                        default: break;
                    }
                }
                for(int j=0;j<n;j++){
                    String nextPattern;
                        nextPattern = "";
                        for(int k=0; k<rnd.nextInt(patternMax)+1;k++){
                            int nextChar =rnd.nextInt(4);
                            switch(nextChar){
                                case 0: nextPattern += "A";
                                break;
                                case 1: nextPattern += "T";
                                break;
                                case 2: nextPattern += "C";
                                break;
                                case 3: nextPattern += "G";
                                break;
                                default: break;
                            }                        
                        }
                    patterns.add(nextPattern);
                }
                List<Integer> naiveAnswer = solveNaive(text, n, patterns);
                Collections.sort(naiveAnswer);
                List<Integer> efficientAnswer = solve(text, n, patterns);
                if(!naiveAnswer.equals(efficientAnswer)){
                    System.out.println();
                    System.out.println("Efficient algorithm fails on:");
                    System.out.println(text);
                    System.out.println(n);
                    for(int j=0;j<patterns.size();j++){
                        System.out.println(patterns.get(j));
                    }
                    System.out.println("Naive answer is:");
                    for(int j=0;j<naiveAnswer.size();j++){
                        System.out.printf("%d ", naiveAnswer.get(j));
                    }
                    System.out.println();
                    System.out.println("Efficient answer is:");
                    for(int j=0;j<efficientAnswer.size();j++){
                        System.out.printf("%d ", efficientAnswer.get(j));
                    }
                    System.out.println();
                    
                }
                System.out.printf("%d, ", i);
            }
        }
        
        List<Integer> solveNaive(String text, int n, List<String> patterns){
            ArrayList <Integer> result = new ArrayList <> ();            
            for(String pattern:patterns){
                for(int i=0; i < (text.length() - pattern.length()+1); i++){
                    String compString = text.substring(i, i+pattern.length());
                    if(pattern.equals(compString)){
                        result.add(i);
                    }
                }
            }
            Set<Integer> rset = new HashSet<>();
            rset.addAll(result);
            result.clear();
            result.addAll(rset);
            return result;
        }
        

	List <Integer> solve (String text, int n, List <String> patterns) {
		ArrayList <Integer> result = new ArrayList <> ();
                TrieNode rootNode = buildTrie(patterns);
                //printTree(rootNode, 0);
		for(int i=0; i < text.length(); i++){
                    rootNode.matchText(text.substring(i), result, i);
                }
		return result;
	}

	public void run () {
            unitTest();
//                specificUnitTest();
//		try {
//			BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
//			String text = in.readLine ();
//		 	int n = Integer.parseInt (in.readLine ());
//		 	List <String> patterns = new ArrayList <> ();
//			for (int i = 0; i < n; i++) {
//				patterns.add (in.readLine ());
//			}
//
//                        List <Integer> ans = solve (text, n, patterns);
//                        Collections.sort(ans);
//			//List <Integer> ans = solve (text, n, patterns);
//                        //[tk] so we'll be adding integers of locations as we make a match
//			for (int j = 0; j < ans.size (); j++) {
//				System.out.print ("" + ans.get (j));
//				System.out.print (j + 1 < ans.size () ? " " : "\n");
//			}
//		}
//		catch (Throwable e) {
//			e.printStackTrace ();
//			System.exit (1);
//		}
//        
	}
        
        private TrieNode buildTrie(List<String> patterns){
            TrieNode tr = new TrieNode(-1, false);
                for(String pattern:patterns){
                    tr.buildBranches(pattern);
                }

            return tr;
            
        }
        
        private class TrieNode{
             public int edgeChar;
             public boolean endOfPattern;
             private final List<TrieNode> edges;
             public TrieNode(int edgeChar, boolean endOfPattern){  
                 this.edges = new ArrayList<>();
                 this.edgeChar = edgeChar;  
                 this.endOfPattern = endOfPattern;
             }
             public void addEdge(int edgeLetter, boolean endOfPattern){
                 edges.add(new TrieNode(edgeLetter, endOfPattern));
             }
             public void buildBranches(String pattern){
                 if(pattern.length()>0){
                     TrieNode match = matchEdge(pattern.charAt(0));
                     if(match!=null){
                         match.buildBranches(pattern.substring(1));
                         return;
                     }
                    boolean eop = pattern.length() == 1;
                    addEdge(letterToIndex(pattern.charAt(0)), eop);
                    this.edges.get(edges.size()-1).buildBranches(pattern.substring(1));
                     
                 }
             }
             
             public void matchText(String text, ArrayList<Integer> result, int patternLocation){
                 int index = 0;
                 TrieNode match = this;
                 do{
                    match = match.matchEdge(text.charAt(index));
                    if(match!=null && match.endOfPattern){
                        result.add(patternLocation);
                    }
                    index++;
//                        match.matchText(text.substring(1), result, patternLocation);
                   
                } while (index<text.length() && match!=null && !match.endOfPattern);
             }

             
             public TrieNode matchEdge(char testChar){
                for(TrieNode edge : edges){
                     int patternChar = letterToIndex(testChar);
                     if(edge.edgeChar == patternChar){
                         return edge;
                     }
                 }
                return null;
             }
         }
	public static void main (String [] args) {
		new Thread (new TrieMatching ()).start ();
	}
        
        
        private void printTree(TrieNode n, int depth){
            if(depth>0)
                System.out.print("|");
            for(int i=0; i<depth; i++)
                System.out.print("-");
            char printChar;
            switch(n.edgeChar){
                case 0: printChar = 'A';
                break;
                case 1: printChar = 'C';
                break;
                case 2: printChar = 'G';
                break;
                case 3: printChar = 'T';
                break;
                default: printChar ='!';
                break;
            }
            
            System.out.println(printChar);
            for(TrieNode node: n.edges){
                printTree(node, depth+1);
            }
        }
}
