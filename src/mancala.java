import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.text.html.MinimalHTMLWriter;

/*import mancala.Board;
import mancala.Player;*/

public class mancala {

	String ipFile;
	public PrintWriter nextState;
	public PrintWriter traverseLog;
	
	//Input
	int task;
	int myPlayer;
	int otherPlayer;
	int cutOffDepth;
	String states2;
	String states1;
	int mancala2;
	int mancala1;
	int totalPits;
	Board board;
	ArrayList<utility> utilityArray = new ArrayList<utility>();
	int finalIndex=1;
	
	boolean playerChanged=false;
	int evalFinal=Integer.MIN_VALUE;

	Board tempBoard;
	Board returnBoard;
	
	static int alpha=Integer.MIN_VALUE;
	static int beta=Integer.MAX_VALUE;
	
	Stack<minMaxNode> stackMinB = new Stack<minMaxNode>(); 
	
	utility u=new utility();
	
	//indicates maximum at evaluation function 
	int max=Integer.MIN_VALUE;
	int index=0;int count=0;

	class utility{
		int index;
		int value;
	}
	
	class minMaxNode implements Cloneable{
		Board b;
		String name;
		int level;
		boolean haveChance;
		Player p;
		int playerNo;
		int minOrMax; //max = 1, min = 0
		minMaxNode parent;
		int value;
		minMaxNode bestState;
		int alpha=Integer.MIN_VALUE;
		int beta=Integer.MAX_VALUE;
		boolean prune;
			
		 public minMaxNode clone() {
		        
				try {
					return (minMaxNode)super.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
		    }
	
	}
	
	class Board{
		int totalPits;
		int playingPits=(totalPits/2)-1;
		Pit[] pits;
		
		Board(int total){
			totalPits=total;
			pits = new Pit[totalPits+1];
			playingPits=(totalPits/2)-1;
			for(int pitNum=0;pitNum<=totalPits;pitNum++)
				pits[pitNum] = new Pit();
		}
		
		public Board(int total, int playingPits, Pit[] pits){
			this.totalPits = total;
			this.playingPits = playingPits;
			this.pits = pits;
		}
		
	    private int getMancala(int playerNum)  {
	        return playerNum * (playingPits+1);
	    }
	    
	    public boolean gameOver(){
			int stones = 0;
			
		    for (int pitNum=1;pitNum<=playingPits;pitNum++){
		    	stones+=pits[pitNum].stones;
	        }
		    
		    for(int pitNum=playingPits+2;pitNum<=totalPits-1;pitNum++){
		    	stones+=pits[pitNum].stones;
		    }
		    
		    if(stones==0)
		    	return true;
		    else
		    	return false;
	    }
	    
	    public Board makeACopy()  {
	    		
		    	Board tempBoard =  new Board(this.totalPits);
		        	    	
		    	for (int i=0;i<=totalPits;i++){
		    		tempBoard.pits[i].addStones(this.pits[i].stones);
		        	tempBoard.pits[i].name=this.pits[i].name;
		        	tempBoard.pits[i].index=this.pits[i].index;
		        	
		    	}
		    	
		    	return tempBoard;
		    }
	    
		public boolean doTheMove(Player p,int pit){
			
			int pitNum = pit;
			int  stones  =  this.pits[pitNum].stones;
			this.pits[pitNum].stones=0;
			
			while (stones != 0){
				++pitNum;
				
				if (pitNum > totalPits)
					pitNum = 1;
					
				if (pitNum  != this.getMancala(otherPlayerNum(p.num))) {
					this.pits[pitNum].addStones(1);
					stones--;
				}
			}

			if(pitNum == this.getMancala(p.num))
			{
				int allStones=0;
				
				if(this.player1AllEmpty()){
					for(int i=playingPits+2;i<=totalPits-1;i++){
						allStones = allStones + this.pits[i].removeStones();
					}
					this.pits[totalPits].addStones(allStones);
				}
				
				allStones=0;
				
				if(this.player2AllEmpty()){
					for(int i=1;i<=playingPits;i++){
						allStones = allStones + this.pits[i].removeStones();
					}
					
					this.pits[playingPits+1].addStones(allStones);
				}
				
				return  true;
			}
			else if(ownerOf(pitNum) == p.num && this.pits[pitNum].stones == 1)  {
				
				int stonesMySide = this.pits[pitNum].removeStones();
				int stonesOtherSide = this.pits[correspondingPitNo(pitNum)].removeStones();
				
				int total = stonesMySide + stonesOtherSide;
				
				this.pits[getMancala(p.num)].addStones(total);
				
				int allStones=0;
				
				if(this.player1AllEmpty()){
					for(int i=playingPits+2;i<=totalPits-1;i++){
						allStones = allStones + this.pits[i].removeStones();
					}
					this.pits[totalPits].addStones(allStones);
				}
				
				allStones=0;
				
				if(this.player2AllEmpty()){
					for(int i=1;i<=playingPits;i++){
						allStones = allStones + this.pits[i].removeStones();
					}
					
					this.pits[playingPits+1].addStones(allStones);
					
				}
				evalFinal=this.pits[getMancala(p.num)].stones-this.pits[getMancala(otherPlayerNum(p.num))].stones;
				
			}else if(this.pits[pitNum].stones !=0){
				
				int allStones=0;
				
				if(this.player1AllEmpty()){
					for(int i=playingPits+2;i<=totalPits-1;i++){
						allStones = allStones + this.pits[i].removeStones();
					}
					this.pits[totalPits].addStones(allStones);
				}
				
				allStones=0;
				
				if(this.player2AllEmpty()){
					for(int i=1;i<=playingPits;i++){
						allStones = allStones + this.pits[i].removeStones();
					}
					this.pits[playingPits+1].addStones(allStones);
					
				}
				
				evalFinal=this.pits[getMancala(p.num)].stones-this.pits[getMancala(otherPlayerNum(p.num))].stones;
			}
			
			return false;
		}

		
public boolean doTheMoveMiniMax(Player p,int chosenPit,minMaxNode parentNode,minMaxNode childNode){
			
			int  pitNum;  
			pitNum=chosenPit;
			int stones = this.pits[pitNum].stones;
			
			this.pits[pitNum].stones=0;
			
			while  (stones != 0)  {
				++pitNum;
				
				if (pitNum > totalPits)
					pitNum = 1;
					
				if (pitNum  != childNode.b.getMancala(otherPlayerNum(p.num))) {
					this.pits[pitNum].addStones(1);
					stones--;
				}
			}

			if(pitNum == this.getMancala(p.num))
			{
				int stoneDiff;
			
				int allStones=0;
				boolean empty=false;
				
				if(this.player1AllEmpty()){
					empty=true;
					for(int i=playingPits+2;i<=totalPits-1;i++){
						allStones = allStones + childNode.b.pits[i].removeStones();
					}
					this.pits[totalPits].addStones(allStones);
					
				}
				
				allStones=0;
				
				if(this.player2AllEmpty()){
					empty=true;
					for(int i=1;i<=playingPits;i++){
						allStones = allStones + childNode.b.pits[i].removeStones();
					}
					
					this.pits[playingPits+1].addStones(allStones);
					
				}
				
				if(parentNode.minOrMax==1){
					childNode.minOrMax=1;
					childNode.value=Integer.MIN_VALUE;
					
					if(childNode.playerNo==1)
						stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
					else
						stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
					
				}else{
					childNode.minOrMax=0;
					childNode.value=Integer.MAX_VALUE;
					if(childNode.playerNo==1)
						stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
					else
						stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
					
				}
				
				if(childNode.level==cutOffDepth)
					childNode.value=stoneDiff;
				if(empty){
					if(childNode.level<=cutOffDepth){
						printChildNode(childNode, childNode.level, true,childNode.minOrMax);
					}
					childNode.value=stoneDiff;
				}
				
				childNode.parent=parentNode;
				
			   return true;
			}
			else if(this.ownerOf(pitNum) == p.num && this.pits[pitNum].stones == 1)  {
				int stonesMySide = this.pits[pitNum].removeStones();
				int stonesOtherSide = this.pits[this.correspondingPitNo(pitNum)].removeStones();
				int total = stonesMySide + stonesOtherSide;
				
				childNode.b.pits[this.getMancala(p.num)].addStones(total);
				
				int allStones=0;
				boolean empty=false;
				
				if(this.player1AllEmpty()){
					empty=true;
					for(int i=playingPits+2;i<=totalPits-1;i++){
						allStones = allStones + childNode.b.pits[i].removeStones();
					}
					
					this.pits[totalPits].addStones(allStones);
				}
				
				allStones=0;
				
				if(this.player2AllEmpty()){
					empty=true;
					
					for(int i=1;i<=playingPits;i++){
						allStones = allStones + childNode.b.pits[i].removeStones();
					}
					
					this.pits[playingPits+1].addStones(allStones);
					
				}
								
				int stoneDiff; 
				
				if(parentNode.minOrMax==1){
					childNode.minOrMax=0;
					childNode.value=Integer.MAX_VALUE;
					if(childNode.playerNo==1)
						stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
					else
						stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
					
				}else{
					childNode.minOrMax=1;
					childNode.value=Integer.MIN_VALUE;
					if(childNode.playerNo==1)
						stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
					else
						stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
					
				}
				
				if(childNode.level==cutOffDepth)
					childNode.value=stoneDiff;
				
				if(empty){
					if(childNode.level<cutOffDepth){
						printChildNode(childNode, childNode.level, true,childNode.minOrMax);
					}
					childNode.value=stoneDiff;
				}
				
				
				childNode.parent=parentNode;
				
			}else if(this.pits[pitNum].stones !=0){
				
				int allStones=0;
				boolean empty=false;
				
				
				if(this.player1AllEmpty()){
					empty=true;
					for(int i=playingPits+2;i<=totalPits-1;i++){
						allStones = allStones + childNode.b.pits[i].removeStones();
					}
					this.pits[totalPits].addStones(allStones);
				}
				
				allStones=0;
				
				if(this.player2AllEmpty()){
					empty=true;
					for(int i=1;i<=playingPits;i++){
						allStones = allStones + childNode.b.pits[i].removeStones();
					}
					
					this.pits[playingPits+1].addStones(allStones);
					
				}
				
				
				int stoneDiff;
				if(parentNode.minOrMax==1){
					childNode.minOrMax=0;
					childNode.value=Integer.MAX_VALUE;
					if(childNode.playerNo==1)
						stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
					else
						stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
					
				}else{
					childNode.minOrMax=1;
					childNode.value=Integer.MIN_VALUE;
					if(childNode.playerNo==1)
						stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
					else
						stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
					
				}
				
				
				if(childNode.level==cutOffDepth)
					childNode.value=stoneDiff;
				
				if(empty){
					if(childNode.level<cutOffDepth){
						printChildNode(childNode, childNode.level, true,childNode.minOrMax);
					}
					childNode.value=stoneDiff;
				}
				
				
				childNode.parent=parentNode;
				
			}
			
			return false;
		}


public boolean doTheMoveAlphaBeta(Player p,int chosenPit,minMaxNode parentNode,minMaxNode childNode){
	
	int  pitNum;  
	pitNum=chosenPit;
	int stones = this.pits[pitNum].stones;
	
	this.pits[pitNum].stones=0;
	
	while  (stones != 0)  {
		++pitNum;
		
		if (pitNum > totalPits)
			pitNum = 1;
			
		if (pitNum  != childNode.b.getMancala(otherPlayerNum(p.num))) {
			this.pits[pitNum].addStones(1);
			stones--;
		}
	}

	if(pitNum == this.getMancala(p.num))
	{
		int stoneDiff;
	
		int allStones=0;
		boolean empty=false;
		
		if(this.player1AllEmpty()){
			empty=true;
			for(int i=playingPits+2;i<=totalPits-1;i++){
				allStones = allStones + childNode.b.pits[i].removeStones();
			}
			this.pits[totalPits].addStones(allStones);
			
		}
		
		allStones=0;
		
		if(this.player2AllEmpty()){
			empty=true;
			
			for(int i=1;i<=playingPits;i++){
				allStones = allStones + childNode.b.pits[i].removeStones();
			}
			
			this.pits[playingPits+1].addStones(allStones);
			
		}
		
		if(parentNode.minOrMax==1){
			childNode.minOrMax=1;
			childNode.value=Integer.MIN_VALUE;
			
			if(childNode.playerNo==1)
				stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
			else
				stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
			
		}else{
			childNode.minOrMax=0;
			childNode.value=Integer.MAX_VALUE;
			if(childNode.playerNo==1)
				stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
			else
				stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
			
		}
		
		childNode.parent=parentNode;
		
		if(childNode.level==cutOffDepth){
			childNode.value=stoneDiff;
			
		}
			
		if(empty){
			if(childNode.level<=cutOffDepth){
				printChildNodeAlphaBeta(childNode, childNode.level, true,childNode.minOrMax);
			}
			childNode.value=stoneDiff;
			
		}
		
	   return true;
	}
	else if(this.ownerOf(pitNum) == p.num && this.pits[pitNum].stones == 1)  {
		int stonesMySide = this.pits[pitNum].removeStones();
		int stonesOtherSide = this.pits[this.correspondingPitNo(pitNum)].removeStones();
		int total = stonesMySide + stonesOtherSide;
		
		childNode.b.pits[this.getMancala(p.num)].addStones(total);
		
		int allStones=0;
		boolean empty=false;
		
		if(this.player1AllEmpty()){
			empty=true;
			for(int i=playingPits+2;i<=totalPits-1;i++){
				allStones = allStones + childNode.b.pits[i].removeStones();
			}
			
			this.pits[totalPits].addStones(allStones);
		}
		
		allStones=0;
		
		if(this.player2AllEmpty()){
			empty=true;
			
			for(int i=1;i<=playingPits;i++){
				allStones = allStones + childNode.b.pits[i].removeStones();
			}
			
			this.pits[playingPits+1].addStones(allStones);
			
		}
						
		int stoneDiff; 
		
		if(parentNode.minOrMax==1){
			childNode.minOrMax=0;
			childNode.value=Integer.MAX_VALUE;
			if(childNode.playerNo==1)
				stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
			else
				stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
			
		}else{
			childNode.minOrMax=1;
			childNode.value=Integer.MIN_VALUE;
			if(childNode.playerNo==1)
				stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
			else
				stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
			
		}
		
		childNode.parent=parentNode;
		
		if(childNode.level==cutOffDepth){
			childNode.value=stoneDiff;
			
		}
			
		
		if(empty){
			if(childNode.level<cutOffDepth){
				printChildNodeAlphaBeta(childNode, childNode.level, true,childNode.minOrMax);
			}
			childNode.value=stoneDiff;
			
		}
		
	}else if(this.pits[pitNum].stones !=0){
		
		int allStones=0;
		boolean empty=false;
		
		
		if(this.player1AllEmpty()){
			empty=true;
			for(int i=playingPits+2;i<=totalPits-1;i++){
				allStones = allStones + childNode.b.pits[i].removeStones();
			}
			this.pits[totalPits].addStones(allStones);
		
		}
		
		allStones=0;
		
		if(this.player2AllEmpty()){
			empty=true;
			
			for(int i=1;i<=playingPits;i++){
				allStones = allStones + childNode.b.pits[i].removeStones();
			}
			
			this.pits[playingPits+1].addStones(allStones);
			
			
		}
		
		
		int stoneDiff;
		if(parentNode.minOrMax==1){
			childNode.minOrMax=0;
			childNode.value=Integer.MAX_VALUE;
			if(childNode.playerNo==1)
				stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
			else
				stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
			
		}else{
			childNode.minOrMax=1;
			childNode.value=Integer.MIN_VALUE;
			if(childNode.playerNo==1)
				stoneDiff=this.pits[playingPits+1].stones-this.pits[totalPits].stones;
			else
				stoneDiff=this.pits[totalPits].stones-this.pits[playingPits+1].stones;
			
		}
		
		childNode.parent=parentNode;
		
		if(childNode.level==cutOffDepth){
			childNode.value=stoneDiff;
			
		}
		
		if(empty){
			if(childNode.level<cutOffDepth){
				printChildNodeAlphaBeta(childNode, childNode.level, true,childNode.minOrMax);
				childNode.value=stoneDiff;
				
			}
		}
		
	}
	
	return false;
}

public void alphaBetaVal(minMaxNode childNode,minMaxNode parent)
{
	if(childNode.minOrMax==1){
		if(childNode.value>alpha){
			if(parent.minOrMax==1)
				parent.beta=childNode.value;
			else
				parent.alpha=childNode.value;
		}
	}else{
		if(parent.minOrMax==1)
			parent.beta=childNode.value;
		else
			parent.alpha=childNode.value;
	}
}
		
		private boolean player1AllEmpty(){
			boolean empty=false;
			int count =0;
			for(int i=1;i<=playingPits;i++){
				
				if(this.pits[i].stones ==0){
					count++;
				}
				if(count == playingPits){
					empty = true;
				}
			}
			return empty;
		}
		
		private boolean player2AllEmpty(){
			boolean empty=false;
			
			int count =0;
			for(int i=playingPits+2;i<=totalPits-1;i++){
				
				if(this.pits[i].stones==0){
					count ++;
				}
				if(count == playingPits)
				{
					empty = true;
				}
			}
			return empty;
		}
		
		private int ownerOf(int pitNum)  {
	        return (pitNum/(playingPits+1))+1;
		}

		
		private int otherPlayerNum(int playerNum){
			if  (playerNum == 1)
				return  2;
			else
				return  1;
		}
		
		private int correspondingPitNo(int pitNum)  {
			return (totalPits-pitNum);
		}
		
	}

	
	class Player{
		int num;
		
		public Player(int num){
			this.num=num;
		}
		
	}
	

	class Pit{
		int stones;
		int index;
		Player p;
		String name;
		
				
		public int removeStones() {
	        int stones  =  this.stones;
	        this.stones  =  0;
	        return stones;
	    }
		
		public void addStones(int stones){
			this.stones+=stones;
		}
	}
	
	public static void main(String[] args)throws Exception{
    	
		mancala m = new mancala ();
    	
		//m.ipFile="input.txt"; 
    	m.ipFile = args[1];
    	
    	if(m.ipFile.contains(".txt")){
    		//do nothing
    	}else{
    		m.ipFile=m.ipFile.concat(".txt");
    	}
    	
    	m.nextState=new PrintWriter("output.txt");
    	m.openFile();
    	m.nextState.close();
        
    }

   
   public void openFile() throws Exception{
		
			Scanner scan = new Scanner(new File(ipFile));
			
			this.task= Integer.parseInt(scan.nextLine().trim());
			
			if(this.task==2 || this.task==3){
				this.traverseLog = new PrintWriter("traverse_log.txt");
			}
			
			this.myPlayer= Integer.parseInt(scan.nextLine().trim());
			
			if(this.myPlayer==1){
				this.otherPlayer=2;
			}else{
				this.otherPlayer=1;
			}
							
			this.cutOffDepth= Integer.parseInt(scan.nextLine().trim());
			this.states2= scan.nextLine().trim();
			this.states1= scan.nextLine().trim();
			this.mancala2= Integer.parseInt(scan.nextLine().trim());
			this.mancala1= Integer.parseInt(scan.nextLine().trim());
			
			scan.close();
			
			Player me = new Player(this.myPlayer);
			
			Player bot = new Player(this.otherPlayer);
			
			String[] s1Temp = states1.split(" ");
			String[] s2Temp = states2.split(" ");
			
			//2 additional spaces for mancala of players
			this.totalPits = 2+s1Temp.length+s2Temp.length;
			
			Pit temp = new Pit();temp.stones=0;	//Just to load the class
			
			Pit[] pits=new Pit[totalPits+1]; //Not using 0th index
			
			for(int a=0;a<pits.length;a++){
				pits[a]=new Pit();
			}
			
			int i,m=0;
							
			for(i=1;i<=s1Temp.length;i++){
				pits[i].stones=Integer.parseInt(s1Temp[m++]);
				pits[i].index=i;
				pits[i].name="B"+(i+1);
				pits[i].p = new Player(1);
				
			}
			
			pits[i].stones=mancala1;
						
			i++;
			
			m=s2Temp.length;
				
			for(m=s2Temp.length-1;m>=0;m--){
				int k=i++;
				pits[k].index=k;
				pits[k].stones=Integer.parseInt(s2Temp[m]);
				pits[k].p = new Player(2);
				pits[k].name="A"+((totalPits-k)+1);
				
			}
			
			pits[i].stones=mancala2;
			
			
			board = new Board(totalPits,s1Temp.length,pits);
			
						
			if(this.task==1){
				this.greedy(me,board);
			}else if(this.task==2){
				this.miniMax(me,bot, board, cutOffDepth,this.myPlayer);
			}else if(this.task==3){
				this.alphaBeta(me,bot, board, cutOffDepth,this.myPlayer);
			}
			
	}
   
   public void greedy(Player me,Board board){
	   Board evalBoard; 
		if(me.num==1){
			evalBoard = evaluateA(board,me);
		}else{
			evalBoard = evaluateB(board,me);
		}
		
		if(evalBoard==null){
			evalBoard=board;
		 }
		
		outputFile(evalBoard);
   }
   
   public void miniMax(Player me,Player bot,Board board,int cutOffDepth,int myPlayer){
	   
	   minMaxNode m = new minMaxNode();
	   m.b=board.makeACopy();
	   m.level=0;
	   m.minOrMax=1;
	   //Parent of himself
	   m.parent=null;
	   m.haveChance=true;
	   m.p=me;
	   m.playerNo=myPlayer;
	   m.name="root";
	   m.value=Integer.MIN_VALUE;
	   
	   this.traverseLog.println("Node,Depth,Value");
	   
	   printParent(m);
	   
	   if(me.num==1){
			stackMinB.push(m);
			minPlayerB(me,bot,m.level+1,m);
		}else{
			stackMinB.push(m);
			minPlayerA(me,bot,m.level+1,m);
		}
	   
	   if(returnBoard==null){
		   returnBoard=board;
		   if(m.playerNo==1)
			   m.value=(m.b.pits[m.b.playingPits+1].stones-m.b.pits[m.b.totalPits].stones);
		   else
			   m.value=(m.b.pits[m.b.totalPits].stones-m.b.pits[m.b.playingPits+1].stones);
		   printChildNode(m, 0, false, 1);
	   }
		outputFile(returnBoard);
    	this.traverseLog.close();
		
   }
   
   public void alphaBeta(Player me,Player bot,Board board,int cutOffDepth,int myPlayer){
	   
	   minMaxNode m = new minMaxNode();
	   m.b=board.makeACopy();
	   m.level=0;
	   m.minOrMax=1;
	   m.parent=null;
	   m.haveChance=true;
	   m.p=me;
	   m.playerNo=myPlayer;
	   m.name="root";
	   m.value=Integer.MIN_VALUE;
	   
	   m.alpha=Integer.MIN_VALUE;
	   m.beta=Integer.MAX_VALUE;
	   
	   this.traverseLog.println("Node,Depth,Value,Alpha,Beta");
	   
	   printParentAlphaBeta(m);
	   
	   if(me.num==1){
			stackMinB.push(m);
			alphaMinPlayerB(me,bot,m.level+1,m,alpha,beta);
		}else{
			stackMinB.push(m);
			alphaMinPlayerA(me,bot,m.level+1,m,alpha,beta);
		}
	   
	   if(returnBoard==null){
		   returnBoard=board;
		   if(m.playerNo==1)
			   m.value=(m.b.pits[m.b.playingPits+1].stones-m.b.pits[m.b.totalPits].stones);
		   else
			   m.value=(m.b.pits[m.b.totalPits].stones-m.b.pits[m.b.playingPits+1].stones);
		   printChildNodeAlphaBeta(m, 0, false, 1);
	   }
	   
		outputFile(returnBoard);
    	this.traverseLog.close();
		
   }
   
public Board minPlayerB(Player p,Player bot,int depth,minMaxNode node){
	
	   for(int i=1;i<=board.playingPits;i++){
		   if(node.b.pits[i].stones!=0){
		   
		   //parent is node
		   minMaxNode childNode=new minMaxNode();
		   
		   childNode.b=node.b.makeACopy();
		   
		   childNode.name=childNode.b.pits[i].name;
		   childNode.level=depth;
		   childNode.playerNo=node.playerNo;
		   		   
		   boolean goAgain = childNode.b.doTheMoveMiniMax(p, i,node,childNode);
		   
		  if(goAgain){
			  if(childNode.b.gameOver())
				  printChildNode(childNode, depth, false,childNode.minOrMax);
			  else
				  printChildNode(childNode, depth, true,childNode.minOrMax);
		  }else{
			   printChildNode(childNode, depth, false,childNode.minOrMax);
		   }
		  
		   stackMinB.push(childNode);
		   
		   if(goAgain){
			   minPlayerB(p,bot, depth, childNode);
			   
			   minMaxNode child=stackMinB.pop();
   			   minMaxNode parent=stackMinB.pop();
   			   
   			   if(parent.minOrMax==1){
				   max(child,parent,depth);
			   }else{
				   min(child,parent,depth);
			   }
   			   
   			   printParent(parent);
   			   stackMinB.push(parent);
   			   continue;
		   }
		   
		   if(childNode.level<cutOffDepth){
			   maxPlayerA(bot,p,childNode.level+1,childNode);
			   
   			   minMaxNode child=stackMinB.pop();
   			   minMaxNode parent=stackMinB.pop();
   			   
   			   if(parent.minOrMax==1){
				   max(child,parent,depth);
			   }else{
				   min(child,parent,depth);
			   }
   			
   			   printParent(parent);
   			   stackMinB.push(parent);
   		   	}else{
   			   minMaxNode child=stackMinB.pop();
   			   minMaxNode parent=stackMinB.pop();
   			   
   			   if(parent.minOrMax==1){
				   max(child,parent,depth);
			   }else{
				   min(child,parent,depth);
			   }
   			   
   			   printParent(parent);
   			   stackMinB.push(parent);
   			   
   			   
   		   }
		 }
	   }
	   
	   return returnBoard;
   }

public Board minPlayerA(Player p,Player bot,int depth,minMaxNode node){
	
	   for(int i=totalPits-1;i>=(board.playingPits+2);i--){
		   if(node.b.pits[i].stones!=0){
		   minMaxNode childNode=new minMaxNode();
		   childNode.b=node.b.makeACopy();
		   
		   childNode.name=childNode.b.pits[i].name;
		   childNode.level=depth;
		   childNode.playerNo=node.playerNo;
		   		   
		   boolean goAgain = childNode.b.doTheMoveMiniMax(p, i,node,childNode);

		   if(goAgain){
				  if(childNode.b.gameOver())
					  printChildNode(childNode, depth, false,childNode.minOrMax);
				  else
					  printChildNode(childNode, depth, true,childNode.minOrMax);
			  }else{
				   printChildNode(childNode, depth, false,childNode.minOrMax);
			   }
		   stackMinB.push(childNode);
		   
		   if(goAgain){
			   minPlayerA(p,bot, depth, childNode);
			   
			   minMaxNode child=stackMinB.pop();
			   minMaxNode parent=stackMinB.pop();
			   
			   if(parent.minOrMax==1){
				   max(child,parent,depth);
			   }else{
				   min(child,parent,depth);
			   }
			   
			   printParent(parent);
			   stackMinB.push(parent);
			   continue;
		   }
		   
		   if(childNode.level<cutOffDepth){
			   maxPlayerB(bot,p,childNode.level+1,childNode);
			   
			   
			   minMaxNode child=stackMinB.pop();
			   minMaxNode parent=stackMinB.pop();
	     			  
			   if(parent.minOrMax==1){
				   max(child,parent,depth);
			   }else{
				   min(child,parent,depth);
			   }
				      			   
			   printParent(parent);
			   stackMinB.push(parent);
			   			   
		   	}else{
			   minMaxNode child=stackMinB.pop();
			   minMaxNode parent=stackMinB.pop();
			   
			   if(parent.minOrMax==1){
				   max(child,parent,depth);
			   }else{
				   min(child,parent,depth);
			   }
			   
			   printParent(parent);
			   stackMinB.push(parent);
			   
			   
		   }
		 }
	   }
	   
	   return returnBoard;
}




public Board maxPlayerA(Player p,Player bot,int depth,minMaxNode node){
	   
	   for(int i=totalPits-1;i>=(board.playingPits+2);i--){
		   if(node.b.pits[i].stones!=0){
			   
			   //parent is node
			   minMaxNode childNode = new minMaxNode();
			   childNode.b=node.b.makeACopy();
			   childNode.name=childNode.b.pits[i].name;
			   childNode.level=depth;
			   childNode.playerNo=node.playerNo;
			   
			   		   
			   boolean goAgain = childNode.b.doTheMoveMiniMax(p, i,node,childNode);
			   
			   if(goAgain){
					  if(childNode.b.gameOver())
						  printChildNode(childNode, depth, false,childNode.minOrMax);
					  else
						  printChildNode(childNode, depth, true,childNode.minOrMax);
				  }else{
					   printChildNode(childNode, depth, false,childNode.minOrMax);
				   }
			   
			   stackMinB.push(childNode);
			   
			   if(goAgain){
				   maxPlayerA(p,bot, depth, childNode);
				   
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   if(parent.minOrMax==1){
					   max(child,parent,depth);
				   }else{
					   min(child,parent,depth);
				   }
	   			   printParent(parent);
	   			   stackMinB.push(parent);
	   			   continue;
			   }
			   if(childNode.level<cutOffDepth){
				   minPlayerB(bot,p,childNode.level+1,childNode);
				   
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   if(parent.minOrMax==1){
					   max(child,parent,depth);
				   }else{
					   min(child,parent,depth);
				   }
	   			  	
	   			   printParent(parent);
	   			   stackMinB.push(parent);
	   			   
	   		   }else{
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			if(parent.minOrMax==1){
					   max(child,parent,depth);
				   }else{
					   min(child,parent,depth);
				   }
	   			   
	   			printParent(parent);
	   			stackMinB.push(parent);
	   			   
	   		   }
		   }
	   }
	   
	   return returnBoard;
}

public Board maxPlayerB(Player p,Player bot,int depth,minMaxNode node){
	   
	   for(int i=1;i<=board.playingPits;i++){
		   if(node.b.pits[i].stones!=0){
			   
			   minMaxNode childNode = new minMaxNode();
			   childNode.b=node.b.makeACopy();
			   childNode.name=childNode.b.pits[i].name;
			   childNode.level=depth;
			   childNode.playerNo=node.playerNo;
			   		   
			   boolean goAgain = childNode.b.doTheMoveMiniMax(p, i,node,childNode);
			   
			   
			   if(goAgain){
					  if(childNode.b.gameOver())
						  printChildNode(childNode, depth, false,childNode.minOrMax);
					  else
						  printChildNode(childNode, depth, true,childNode.minOrMax);
				  }else{
					   printChildNode(childNode, depth, false,childNode.minOrMax);
				   }
			   
			   stackMinB.push(childNode);
			   			   
			   if(goAgain){
				   maxPlayerB(p,bot, depth, childNode);
				   
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   if(parent.minOrMax==1){
					   max(child,parent,depth);
				   }else{
					   min(child,parent,depth);
				   }
	   			   printParent(parent);
	   			   stackMinB.push(parent);
	   			   continue;
			   }
			   if(childNode.level<cutOffDepth){
				   minPlayerA(bot,p,childNode.level+1,childNode);
				   
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   if(parent.minOrMax==1){
					   max(child,parent,depth);
				   }else{
					   min(child,parent,depth);
				   }
	   			  	
	   			   printParent(parent);
	   			   stackMinB.push(parent);
	   			   
	   		   }else{
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			if(parent.minOrMax==1){
					   max(child,parent,depth);
				   }else{
					   min(child,parent,depth);
				   }
	   			   	   			   
	   			printParent(parent);   
	   			stackMinB.push(parent);
	   			   
	   		   }
		   }
	   }
	   
	   return returnBoard;
}

public Board alphaMinPlayerB(Player p,Player bot,int depth,minMaxNode node,int alpha, int beta){
	
		boolean prune=false;
	   for(int i=1;i<=board.playingPits;i++){
		   if(node.b.pits[i].stones!=0){
		   
		   //parent is node
		   minMaxNode childNode=new minMaxNode();
		   
		   childNode.b=node.b.makeACopy();
		   
		   childNode.name=childNode.b.pits[i].name;
		   childNode.level=depth;
		   childNode.playerNo=node.playerNo;
		   
		 //Popping here for parent
		   minMaxNode parentA = stackMinB.pop();
		   childNode.beta=parentA.beta;
		   childNode.alpha=parentA.alpha;
		   
		   stackMinB.push(parentA);
		   
		   boolean goAgain = childNode.b.doTheMoveAlphaBeta(p, i,node,childNode);
		   
		  if(goAgain){
			  if(childNode.b.gameOver())
				  printChildNodeAlphaBeta(childNode, depth, false,childNode.minOrMax);
			  else
				  printChildNodeAlphaBeta(childNode, depth, true,childNode.minOrMax);
		  }else{
			  printChildNodeAlphaBeta(childNode, depth, false,childNode.minOrMax);
		   }
		  
		   stackMinB.push(childNode);
		   
		   if(goAgain){
			   alphaMinPlayerB(p,bot, depth, childNode,alpha,beta);
			   
			   minMaxNode child=stackMinB.pop();
			   minMaxNode parent=stackMinB.pop();
			   
			   if(parent.minOrMax==1){
				   prune=maxAlphaBeta(child, parent, depth);
			   }else{
				   prune=minAlphaBeta(child, parent, depth);
			   }
			   
			   printParentAlphaBeta(parent);
			   stackMinB.push(parent);
			   if(prune)
				   return childNode.b;
			   continue;
		   }
		   
		   if(childNode.level<cutOffDepth){

				   alphaMaxPlayerA(bot,p, childNode.level+1, childNode,alpha,beta);
			     
				   minMaxNode child=stackMinB.pop();
				   minMaxNode parent=stackMinB.pop();

				   //Root gets printed at this level

				   if(parent.minOrMax==1){
					   prune=maxAlphaBeta(child, parent, depth);
				   }else{
					   prune=minAlphaBeta(child, parent, depth);
				   }

				   printParentAlphaBeta(parent);
				   stackMinB.push(parent);
				   
				   if(prune)
					   return childNode.b;
			   
		   	}else{
			   minMaxNode child=stackMinB.pop();
			   minMaxNode parent=stackMinB.pop();
			   
			   if(parent.minOrMax==1){
				   prune=maxAlphaBeta(child, parent, depth);
			   }else{
				   prune=minAlphaBeta(child, parent, depth);
			   }
			   
			   printParentAlphaBeta(parent);
			   stackMinB.push(parent);
			   
			   if(prune)
				   return childNode.b;
			   
		   }
		 }
	   }
	   
	   return returnBoard;
}


public Board alphaMaxPlayerB(Player p,Player bot,int depth,minMaxNode node,int alpha, int beta){
	
	boolean prune=false;
   for(int i=1;i<=board.playingPits;i++){
	   if(node.b.pits[i].stones!=0){
	   
	   //parent is node
	   minMaxNode childNode=new minMaxNode();
	   
	   childNode.b=node.b.makeACopy();
	   
	   childNode.name=childNode.b.pits[i].name;
	   childNode.level=depth;
	   childNode.playerNo=node.playerNo;
	   
	 //Popping here for parent
	   minMaxNode parentA = stackMinB.pop();
	   childNode.beta=parentA.beta;
	   childNode.alpha=parentA.alpha;
	   
	   stackMinB.push(parentA);
	   
	   boolean goAgain = childNode.b.doTheMoveAlphaBeta(p, i,node,childNode);
	   
	  if(goAgain){
		  if(childNode.b.gameOver())
			  printChildNodeAlphaBeta(childNode, depth, false,childNode.minOrMax);
		  else
			  printChildNodeAlphaBeta(childNode, depth, true,childNode.minOrMax);
	  }else{
		  printChildNodeAlphaBeta(childNode, depth, false,childNode.minOrMax);
	   }
	  
	   stackMinB.push(childNode);
	   
	   if(goAgain){
		   alphaMinPlayerB(p,bot, depth, childNode,alpha,beta);
		   
		   minMaxNode child=stackMinB.pop();
		   minMaxNode parent=stackMinB.pop();
		   
		   if(parent.minOrMax==1){
			   prune=maxAlphaBeta(child, parent, depth);
		   }else{
			   prune=minAlphaBeta(child, parent, depth);
		   }
		   
		   printParentAlphaBeta(parent);
		   stackMinB.push(parent);
		   if(prune)
			   return childNode.b;
		   continue;
	   }
	   
	   if(childNode.level<cutOffDepth){

			   alphaMaxPlayerA(bot,p, childNode.level+1, childNode,alpha,beta);
		      
			   minMaxNode child=stackMinB.pop();
			   minMaxNode parent=stackMinB.pop();

			   //Root gets printed at this level

			   if(parent.minOrMax==1){
				   prune=maxAlphaBeta(child, parent, depth);
			   }else{
				   prune=minAlphaBeta(child, parent, depth);
			   }

			   printParentAlphaBeta(parent);
			   stackMinB.push(parent);
			   
			   if(prune)
				   return childNode.b;
		   
	   	}else{
		   minMaxNode child=stackMinB.pop();
		   minMaxNode parent=stackMinB.pop();
		   
		   if(parent.minOrMax==1){
			   prune=maxAlphaBeta(child, parent, depth);
		   }else{
			   prune=minAlphaBeta(child, parent, depth);
		   }
		   
		   printParentAlphaBeta(parent);
		   stackMinB.push(parent);
		   
		   if(prune)
			   return childNode.b;
		   
	   }
	 }
   }
   
   return returnBoard;
}



public Board alphaMinPlayerA(Player p,Player bot,int depth,minMaxNode node,int alpha, int beta){
	   Boolean prune=false;
	   for(int i=totalPits-1;i>=(board.playingPits+2);i--){
		   if(node.b.pits[i].stones!=0){
			   
			   //parent is node
			   minMaxNode childNode = new minMaxNode();
			   childNode.b=node.b.makeACopy();
			   childNode.name=childNode.b.pits[i].name;
			   childNode.level=depth;
			   childNode.playerNo=node.playerNo;
			   
			   //Popping here for parent
			   minMaxNode parentA = stackMinB.pop();
			   
			   childNode.beta=parentA.beta;
			   childNode.alpha=parentA.alpha;
			   
			   stackMinB.push(parentA);
			   		   
			   boolean goAgain = childNode.b.doTheMoveAlphaBeta(p, i,node,childNode);
			   
			   if(goAgain){
					  if(childNode.b.gameOver())
						  printChildNodeAlphaBeta(childNode, depth, false,childNode.minOrMax);
					  else
						  printChildNodeAlphaBeta(childNode, depth, true,childNode.minOrMax);
				  }else{
					  printChildNodeAlphaBeta(childNode, depth, false,childNode.minOrMax);
				   }
			   
			   stackMinB.push(childNode);
			   
			   if(goAgain){
				   alphaMinPlayerA(p,bot, depth, childNode,alpha,beta);
				    
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   if(parent.minOrMax==1){
					   prune=maxAlphaBeta(child, parent, depth);
				   }else{
					   prune=minAlphaBeta(child, parent, depth);
				   }
	   			   
	   			   printParentAlphaBeta(parent);
	   			   stackMinB.push(parent);
	   			   if(prune)
					   return childNode.b;
	   			   continue;
			   }
			   if(childNode.level<cutOffDepth){
				   alphaMaxPlayerB(bot,p,childNode.level+1,childNode,alpha,beta);
				   
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   if(parent.minOrMax==1){
					   prune=maxAlphaBeta(child, parent, depth);
				   }else{
					   prune=minAlphaBeta(child, parent, depth);
				   }
	   			  	
	   			   printParentAlphaBeta(parent);
	   			   stackMinB.push(parent);
	   			   if(prune)
					   return childNode.b;
	   			   
	   		   }else{
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   //Pruning is happening here
	   			   
	   			if(parent.minOrMax==1){
					   prune=maxAlphaBeta(child, parent, depth);
				   }else{
					   prune=minAlphaBeta(child, parent, depth);
				   }
	   			   
	   			printParentAlphaBeta(parent);
	   			stackMinB.push(parent);
	   			if(prune)
					   return childNode.b;
	   			   
	   		   }
		   }
	   }
	   
	   return returnBoard;
}


public Board alphaMaxPlayerA(Player p,Player bot,int depth,minMaxNode node,int alpha, int beta){
	   Boolean prune=false;
	   for(int i=totalPits-1;i>=(board.playingPits+2);i--){
		   if(node.b.pits[i].stones!=0){
			   
			   //parent is node
			   minMaxNode childNode = new minMaxNode();
			   childNode.b=node.b.makeACopy();
			   childNode.name=childNode.b.pits[i].name;
			   childNode.level=depth;
			   childNode.playerNo=node.playerNo;
			   
			   //Popping here for parent
			   minMaxNode parentA = stackMinB.pop();
			   
			   childNode.beta=parentA.beta;
			   childNode.alpha=parentA.alpha;
			   
			   stackMinB.push(parentA);
			   		   
			   boolean goAgain = childNode.b.doTheMoveAlphaBeta(p, i,node,childNode);
			   
			   if(goAgain){
					  if(childNode.b.gameOver())
						  printChildNodeAlphaBeta(childNode, depth, false,childNode.minOrMax);
					  else
						  printChildNodeAlphaBeta(childNode, depth, true,childNode.minOrMax);
				  }else{
					  printChildNodeAlphaBeta(childNode, depth, false,childNode.minOrMax);
				   }
			   
			   stackMinB.push(childNode);
			   
			   if(goAgain){
				   alphaMaxPlayerA(p,bot, depth, childNode,alpha,beta);
				    
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   if(parent.minOrMax==1){
					   prune=maxAlphaBeta(child, parent, depth);
				   }else{
					   prune=minAlphaBeta(child, parent, depth);
				   }
	   			   
	   			   printParentAlphaBeta(parent);
	   			   stackMinB.push(parent);
	   			   if(prune)
					   return childNode.b;
	   			   continue;
			   }
			   if(childNode.level<cutOffDepth){
				   alphaMinPlayerB(bot,p,childNode.level+1,childNode,alpha,beta);
				   
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   if(parent.minOrMax==1){
					   prune=maxAlphaBeta(child, parent, depth);
				   }else{
					   prune=minAlphaBeta(child, parent, depth);
				   }
	   			  	
	   			   printParentAlphaBeta(parent);
	   			   stackMinB.push(parent);
	   			   if(prune)
					   return childNode.b;
	   			   
	   		   }else{
	   			   minMaxNode child=stackMinB.pop();
	   			   minMaxNode parent=stackMinB.pop();
	   			   
	   			   //Pruning is happening here
	   			   
	   			if(parent.minOrMax==1){
					   prune=maxAlphaBeta(child, parent, depth);
				   }else{
					   prune=minAlphaBeta(child, parent, depth);
				   }
	   			   
	   			printParentAlphaBeta(parent);
	   			stackMinB.push(parent);
	   			if(prune)
					   return childNode.b;
	   			   
	   		   }
		   }
	   }
	   
	   return returnBoard;
}


public void printParent(minMaxNode parent){
	
	if(parent.value==-2147483648){
	   this.traverseLog.println(parent.name+","+parent.level+","+"-Infinity");
   }else if(parent.value==2147483647){
	   this.traverseLog.println(parent.name+","+parent.level+","+"Infinity");
   }else{
	   this.traverseLog.println(parent.name+","+parent.level+","+parent.value);
   }
}

public void printParentAlphaBeta(minMaxNode parent){
	
	String alphaString="";
	String betaString="";
	
	if(parent.alpha == Integer.MIN_VALUE){
		alphaString="-Infinity";
	}else{
		alphaString=parent.alpha+"";
	}
	if(parent.beta == Integer.MAX_VALUE){
		betaString="Infinity";
	}else{
		betaString=parent.beta+"";
	}
	if(parent.value==-2147483648){
	   this.traverseLog.println(parent.name+","+parent.level+","+"-Infinity"+","+alphaString+","+betaString);
   }else if(parent.value==2147483647){
	   this.traverseLog.println(parent.name+","+parent.level+","+"Infinity"+","+alphaString+","+betaString);
   }else{
	   this.traverseLog.println(parent.name+","+parent.level+","+parent.value+","+alphaString+","+betaString);
   }
}

public void printChildNode(minMaxNode childNode,int depth,boolean infinity,int minOrMax){
	
	if(infinity){
		if(childNode.minOrMax==1){
			childNode.value=-2147483648;
		}else if(childNode.minOrMax==0){
			childNode.value=2147483647;
		}
	}
	if(childNode.value==-2147483648){
		this.traverseLog.println(childNode.name+","+depth+","+"-Infinity");
	}else if(childNode.value==2147483647){
		this.traverseLog.println(childNode.name+","+depth+","+"Infinity");
	}else{
		this.traverseLog.println(childNode.name+","+depth+","+childNode.value);
	}
	
}

public void printChildNodeAlphaBeta(minMaxNode childNode,int depth,boolean infinity,int minOrMax){
	
	if(infinity){
		if(childNode.minOrMax==1){
			childNode.value=-2147483648;
		}else if(childNode.minOrMax==0){
			childNode.value=2147483647;
		}
	}
	String alphaString="";
	String betaString="";
	
	if(childNode.alpha == Integer.MIN_VALUE){
		alphaString="-Infinity";
	}else{
		alphaString=childNode.alpha+"";
	}
	if(childNode.beta == Integer.MAX_VALUE){
		betaString="Infinity";
	}else{
		betaString=childNode.beta+"";
	}
	
	if(childNode.value==-2147483648){
		this.traverseLog.println(childNode.name+","+depth+","+"-Infinity"+","+alphaString+","+betaString);
	}else if(childNode.value==2147483647){
		this.traverseLog.println(childNode.name+","+depth+","+"Infinity"+","+alphaString+","+betaString);
	}else{
		this.traverseLog.println(childNode.name+","+depth+","+childNode.value+","+alphaString+","+betaString);
	}
	
}


public void max(minMaxNode child,minMaxNode parent,int depth){
	if(child.value>parent.value){
		   parent.value=child.value;
		   
		   if(parent.level==1){
			   if(child.bestState==null && child.level==1){
				   parent.bestState=child;
				   parent.bestState.b=child.b.makeACopy();
			   }else{
				   parent.bestState=child.bestState;
				   parent.bestState.b=child.bestState.b.makeACopy();
			   }
			   
		   }
			   
		   if(parent.level==0){
			   if(child.bestState!=null){
				   	parent.bestState=child.bestState;
			   		parent.bestState.b=child.bestState.b.makeACopy();
			   		returnBoard=parent.bestState.b;
			   }
		   }
		   
		   if(parent.level==0){
			   if(child.minOrMax==0){
				   parent.bestState=child;
				   parent.bestState.b=child.b.makeACopy();
				   returnBoard=parent.bestState.b;
			   }
			   
			   else if(parent.bestState==null){
				   parent.bestState=child;
				   parent.bestState.b=child.b.makeACopy();
				   returnBoard=parent.bestState.b;
			   }
		   }
		   
	   }
}

public boolean maxAlphaBeta(minMaxNode child,minMaxNode parent,int depth){
	
	boolean prune=false;
	int prevAlpha = parent.alpha;
	
	if(child.value>parent.value){
		   parent.value=child.value;
		   
		   if(child.value>=parent.beta){
			   prune=true;
		   }else{
				parent.alpha=max(parent.alpha,child.value);
			}
		   		   
		   if(parent.level==1){
			   if(child.bestState==null && child.level==1){
				   parent.bestState=child;
				   parent.bestState.b=child.b.makeACopy();
			   }else{
				   parent.bestState=child.bestState;
				   parent.bestState.b=child.bestState.b.makeACopy();
			   }
			   
		   }
			   
		   if(parent.level==0){
			   if(child.bestState!=null){
				   	parent.bestState=child.bestState;
			   		parent.bestState.b=child.bestState.b.makeACopy();
			   		returnBoard=parent.bestState.b;
			   }
		   }
		   
		   if(parent.level==0){
			   if(child.minOrMax==0){
				   parent.bestState=child;
				   parent.bestState.b=child.b.makeACopy();
				   returnBoard=parent.bestState.b;
			   }
			   
			   else if(parent.bestState==null){
				   parent.bestState=child;
				   parent.bestState.b=child.b.makeACopy();
				   returnBoard=parent.bestState.b;
			   }
		   }
		   
	   }
		
	return prune;
	
}

public void min(minMaxNode child,minMaxNode parent,int depth){
	if(child.value<parent.value){
		   parent.value=child.value;
		   
	}
		   
}

public boolean minAlphaBeta(minMaxNode child,minMaxNode parent,int depth){
	
	boolean prune=false;
	
	int prevBeta = parent.beta;
	
	if(child.value<parent.value){
		   parent.value=child.value;
		   
		   if(child.value<=parent.alpha){
			   prune=true;
			}else{
				parent.beta=min(parent.beta,child.value);
			}
	}
	
	
	return prune;
		   
}

public int min(int beta,int value){
	return beta<=value?beta:value;
}

public int max(int alpha,int value){
	return alpha>=value?alpha:value;
}

   public Board evaluateA(Board board,Player p){
	   for(int i=1;i<=board.playingPits;i++){
		   tempBoard = board.makeACopy();
		  
		   boolean goAgain = tempBoard.doTheMove(p, i);
		   if(goAgain){
			  tempBoard=evaluateA(tempBoard,p);
			
		   }else{
			   if(evalFinal>max || (evalFinal<0&&tempBoard.player1AllEmpty())){
				   
				   max=evalFinal;
				   returnBoard=tempBoard.makeACopy();
			   }
		   }
	   }
	   return returnBoard;
   }
   
   
   
   
   public Board evaluateB(Board board,Player p){
	   for(int i=totalPits-1;i>=(board.playingPits+2);i--){
		   
		   tempBoard = board.makeACopy();
		   boolean goAgain = tempBoard.doTheMove(p, i);
		   if(goAgain){
			  tempBoard=evaluateB(tempBoard,p);
		   }else{
			   if(evalFinal>max || (evalFinal<0&&tempBoard.player1AllEmpty())){
				   
				   max=evalFinal;
				   returnBoard=tempBoard.makeACopy();
			   }
		   }
	   }
	   return returnBoard;
   }

   
   public void printBpard(Board board){
	   for(int p=1;p<board.pits.length;p++){
			if(p==0){
				System.out.println("mancala ["+p+"] : "+board.pits[p].stones);
			}
			else if(p<=board.playingPits){
				
				System.out.println("Pit ["+p+"] : "+board.pits[p].stones);
			}
			else if(p==board.playingPits+1){
				
				System.out.println("mancala [1] : "+board.pits[p].stones);
			}else if(p==(2*(board.playingPits+1))){
				
				System.out.println("mancala [2] : "+board.pits[p].stones);
			}
			else{
				
				System.out.println("pit ["+p+"] : "+board.pits[p].stones);
			}
			
		}
	   
   }
   
   public void outputFile(Board board){
	   String player2="";
	   String player1="";
	   String mancala2="";
	   String mancala1="";
	   
	   int p;
	   for(p=1;p<=board.playingPits;p++){
			player1=player1+board.pits[p].stones+" ";
		}
			
	   	mancala1=""+board.pits[p].stones;
		
		for(int q=board.totalPits-1;q>=board.playingPits+2;q--){
   			player2=player2+board.pits[q].stones+" ";
   		}

   		mancala2=""+board.pits[board.totalPits].stones;
   		
	   this.nextState.println(player2.trim()); 
	   this.nextState.println(player1.trim());
	   this.nextState.println(mancala2.trim());
	   this.nextState.println(mancala1.trim());
	   
	   
	   
	   
   }

	
}
