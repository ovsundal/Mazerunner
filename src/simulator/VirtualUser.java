package simulator;

import mazeoblig.Box;
import mazeoblig.InformationObject;
import mazeoblig.ServerInterface;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;
import java.util.Arrays;
/**
 * Instansen av denne klassen tilbyr i praksis tre metoder til programmereren. Disse er:
 * <p>
 * a. Konstrukt�ren (som tar imot en Maze som parameter<br>
 * b. getFirstIterationLoop() som returnerer en rekke med posisjoner i Maze som finner veien<br>
 *    ut av Maze og reposisjonerer "spilleren" ved starten av Maze basert p� en tilfeldig <br>
 *    posisjonering av spilleren i Maze<br>
 * c. getIterationLoop() som returnerer en rekke med posisjoner i Maze som finner veien<br>
 *    ut av Maze (fra inngangen) og reposisjonerer "spileren" ved starten av Maze p� nytt<br>
 * <p>
 * Ideen er at programmereren skal kunne benytte disse ferdig definerte posisjonene til � simulere
 * hvordan en bruker forflytter seg i en labyrint.
 *     
 * @author asd
 *
 */
public class VirtualUser extends UnicastRemoteObject implements ClientCallbackInterface {

	/**
	 * BugFix: 07.10.2013 - Fikset problem med at VirtualUser i enkelt tilfeller posisjonerer seg selv 
	 *                      utenfor selve labyrinten
	 * 
	 */
	private Box[][] maze;
	private int dim;

	static int xp;
	static int yp;
	static boolean found = false;

	
	private Stack <PositionInMaze> myWay = new Stack<PositionInMaze>();
	private PositionInMaze [] firstIteration;
	private PositionInMaze [] nextIteration;
	private Integer clientId;
	private ServerInterface serverInterface;
	private PositionInMaze[] itinerary;
	private int totalPositionsMoved = 0;
	private HashMap<Integer, PositionInMaze> listOfAllPosition;
//	private Color color;
	private InformationObject informationObject = null;
	private HashMap<Integer, InformationObject> infoFromAllClients = null;

	/**
	 * Konstrukt�r
	 * @param maze
	 */
	public VirtualUser(Box[][] maze, ServerInterface serverInterface) throws RemoteException {
		super();
		this.maze = maze;
		this.serverInterface = serverInterface;
		dim = maze[0].length;
//		this.color = new Color(new Random().nextInt(0xFFFFFF));
		Color randomColor =new Color(new Random().nextInt(0xFFFFFF));

		//create information object with callbackinterface and random color
		this.informationObject = new InformationObject(this, randomColor);
		init();


	}
	/**
	 * Initsierer en tilfeldig posisjon i labyrint
	 */
	private void init() {
		/*
		 * Setter en tifeldig posisjon i maze (xp og yp)
		 */
		Random rand = new Random();
		xp = rand.nextInt(dim - 2) + 1;
		yp = rand.nextInt(dim - 2) + 1;

		//get id from server
		try {
			//if clientId exists, client has finished traversing the maze and this is called during
			// creation of a new itinerary. No need to change id
			if(informationObject.getClientId() == null) {
				informationObject.setClientId(serverInterface.setClientId(this));
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		// L�ser veien ut av labyrinten basert p� tilfeldig inngang ...
		makeFirstIteration();
		// og deretter l�ses labyrinten basert p� inngang fra starten 
		makeNextIteration();

		//merge both iterations
		itinerary = VirtualUser.concat(firstIteration, nextIteration);
	}
	
	/**
	 * L�ser maze ut fra en tilfeldig posisjon i maze
	 */
	private void solveMaze() {
		found = false;
		// Siden posisjonen er tilfeldig valgt risikerer man at man kj�rer i en br�nn
		// Av denne grunn .... det er noe galt med kallet under
		myWay.push(new PositionInMaze(xp, yp));
		backtrack(maze[xp][yp], maze[1][0]);
	}

	/**
	 * Selve backtracking-algoritmen som brukes for � finne l�sningen
	 * @param b Box
	 * @param from Box
	 */
	private void backtrack(Box b, Box from) {
		// Aller f�rst - basistilfellet, slik at vi kan returnere
		// Under returen skrives det med R�dt
		if ((xp == dim - 2) && (yp == dim - 2)) {
			found = true;
			// Siden vi tegner den "riktige" veien under returen opp gjennom
			// Java's runtime-stack, s� legger vi utgang inn sist ...
			return;
		}
		// Henter boksene som det finnes veier til fra den boksen jeg st�r i
		Box [] adj = b.getAdjecent();
		// Og sjekker om jeg kan g� de veiene
		for (int i = 0; i < adj.length; i++) {
			// Hvis boksen har en utganger som ikke er lik den jeg kom fra ...
			if (!(adj[i].equals(from))) {
				adjustXYBeforeBacktrack(b, adj[i]);
				myWay.push(new PositionInMaze(xp, yp));
				backtrack(adj[i], b);
				// Hvis algoritmen har funnet veien ut av labyrinten, s� inneholder stacken (myWay) 
				// veien fra det tilfeldige startpunktet og ut av labyrinten
				if (!found) myWay.pop();
				adjustXYAfterBacktrack(b, adj[i]);
			}
			// Hvis veien er funnet, er det ingen grunn til � fortsette
			if (found) {
				break;
			}
		}
	}

	/**
	 * Oppdatere x og y i labyrinten f�r backtracking kalles
	 * @param from Box
	 * @param to Box
	 */
	private void adjustXYBeforeBacktrack(Box from, Box to) {
		if ((from.getUp() != null) && (to.equals(from.getUp()))) yp--;
		if ((from.getDown() != null) && (to.equals(from.getDown()))) yp++;
		if ((from.getLeft() != null) && (to.equals(from.getLeft()))) xp--;
		if ((from.getRight() != null) && (to.equals(from.getRight()))) xp++;
	}

	/**
	 * Oppdatere x og y i labyrinten etter at backtracking er kalt
	 * @param from Box
	 * @param to Box
	 */
	private void adjustXYAfterBacktrack(Box from, Box to) {
		if ((from.getUp() != null) && (to.equals(from.getUp()))) yp++;
		if ((from.getDown() != null) && (to.equals(from.getDown()))) yp--;
		if ((from.getLeft() != null) && (to.equals(from.getLeft()))) xp++;
		if ((from.getRight() != null) && (to.equals(from.getRight()))) xp--;
	}

	/**
	 * Returnerer hele veien, fra tilfeldig startpunkt og ut av Maze som en array
	 * @return [] PositionInMaze 
	 */
	private PositionInMaze [] solve() {
		solveMaze();
		PositionInMaze [] pos = new PositionInMaze[myWay.size()];
		for (int i = 0; i < myWay.size(); i++)
			pos[i] = myWay.get(i);
		return pos;
	}

	/**
	 * Returnerer posisjonene som gir en vei rundt maze, tilfeldig valgt - mot h�yre eller mot venstre
	 * @return [] PositionInMaze;
	 */
	private PositionInMaze [] roundAbout() {
		PositionInMaze [] pos = new PositionInMaze[dim * 2];
		int j = 0;
		pos[j++] = new PositionInMaze(dim - 2, dim - 1);
		// Vi skal enten g� veien rundt mot h�yre ( % 2 == 0)
		// eller mot venstre
		if (System.currentTimeMillis() % 2 == 0) { 
			for (int i = dim - 1; i >= 0; i--)
				pos[j++] = new PositionInMaze(dim - 1, i);
			for (int i = dim - 1; i >= 1; i--)
				pos[j++] = new PositionInMaze(i, 0);
		}
		else {
			for (int i = dim - 1; i >= 1; i--)
				pos[j++] = new PositionInMaze(i, dim - 1);
			for (int i = dim - 1; i >= 0; i--)
				pos[j++] = new PositionInMaze(0, i);
		}
		// Uansett, s� returneres resultatet
		return pos;
	}

	/**
	 * L�ser hele maze, fra startposisjonen
	 * @return
	 */
	@SuppressWarnings("unused")
	private PositionInMaze [] solveFull() {
		solveMaze();
		PositionInMaze [] pos = new PositionInMaze[myWay.size()];
		for (int i = 0; i < myWay.size(); i++)
			pos[i] = myWay.get(i);
		return pos;
	}

	/**
	 * Genererer opp veien ut av labyrinten fra en tilfeldig posisjon, samt veien 
	 * rundt og frem til inngangen av labyrinten 
	 */
	private void makeFirstIteration() {
		PositionInMaze [] outOfMaze = solve();
		PositionInMaze [] backToStart = roundAbout();
		firstIteration = VirtualUser.concat(outOfMaze, backToStart);
	}

	/**
	 * Genererer opp veien ut av labyrinten fra inngangsposisjonen i labyrinten, samt veien 
	 * rundt og frem til inngangen av labyrinten igjen
	 */
	private void makeNextIteration() {
		// Tvinger posisjonen til � v�re ved inngang av Maze
		xp = 1; yp = 1;
		myWay = new Stack<PositionInMaze>();
		PositionInMaze [] outOfMaze = solve();
		PositionInMaze [] backToStart = roundAbout();
		nextIteration = VirtualUser.concat(outOfMaze, backToStart);
	}

	/**
	 * Generisk metode som sl�r sammen to arrayer av samme type
	 * @param <T>
	 * @param first
	 * @param second
	 * @return
	 */
	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	@Override
	public Integer getClientId() throws RemoteException {
		return this.informationObject.getClientId();
	}

	/**
	 * Receives a list of all client positions from callback interface
	 * @param listOfAllPosition - All current client positions
	 * @throws RemoteException
	 */
	@Override
	public void updateMap(HashMap<Integer, PositionInMaze> listOfAllPosition) throws RemoteException {
		this.listOfAllPosition = listOfAllPosition;
	}

	/**
	 * When client receives an updated information object with info from all clients from server, store it
	 * @param objectHashMap
	 * @throws RemoteException
	 */
	@Override
	public void receiveInformationObjectFromServer(HashMap<Integer, InformationObject> objectHashMap) throws RemoteException {
		setInfoFromAllClients(objectHashMap);
	}

	/**
	 * Client receives the information object back from server with updated info

	 * @throws RemoteException
	 */


	public HashMap<Integer, PositionInMaze> getListOfAllPosition() {
		return listOfAllPosition;
	}


	/**
	 * Moves the client to next position in itinerary and informs server
	 */
	public void sendInfoToServer() throws RemoteException {

		//move to next position in maze and update information object
		informationObject.setPosition(itinerary[totalPositionsMoved]);
		totalPositionsMoved++;

		//send information object to server
		try {
			serverInterface.sendInformationObjectToServer(informationObject);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		//if client has traversed the entire maze, reset travel path
		if(totalPositionsMoved >= itinerary.length ) {
			totalPositionsMoved = 0;
			init();
		}
	}

	public HashMap<Integer, InformationObject> getInfoFromAllClients() {
		return infoFromAllClients;
	}

	public void setInfoFromAllClients(HashMap<Integer, InformationObject> infoFromAllClients) {
		this.infoFromAllClients = infoFromAllClients;
	}

	public InformationObject getInformationObject() {
		return informationObject;
	}

	//	public void sendClientColor() {
//		try {
//			serverInterface.sendClientColors(clientId, color);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}


}
