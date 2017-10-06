package mazeoblig;

/**
 *Interface provided at assignment startup. No changes were made here
 */

import java.rmi.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface BoxMazeInterface extends Remote {
    Box [][] getMaze() throws RemoteException;
}
