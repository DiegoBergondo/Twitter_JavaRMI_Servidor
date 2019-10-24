package twitter_javarmi_servidor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import twitter_javarmi_common.CallbackUsuarioInterface;
import twitter_javarmi_common.ServicioGestorInterface;
import twitter_javarmi_common.Trino;

public class ServicioGestorImpl extends UnicastRemoteObject implements ServicioGestorInterface {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private Vector clientList;
	private List<Trino> trinos;

	@SuppressWarnings("rawtypes")
	public ServicioGestorImpl() throws RemoteException {
		super( );
		clientList = new Vector();
}

	@SuppressWarnings("unchecked")
	//Método para añadir clientes al callback.
	public synchronized void registerForCallback(CallbackUsuarioInterface callbackClientObject) throws java.rmi.RemoteException{      
 
		if (!(clientList.contains(callbackClientObject))) {
			clientList.addElement(callbackClientObject);
			}
		}
	
	@Override
	//Método para añadir un amigo a la lista del usuario.
	public boolean agregar(String amigo, String nick) throws RemoteException {
		//Primero se verifica que el usuario que se quiere añadir esté registrado.
		if (amigo != null && !amigo.isEmpty() && Servidor.bbdd.usuarioRegistrado(nick)){
		Servidor.bbdd.añadirAmigo(amigo, nick);
		return true;
		}
		else 
			return false;
		}
	
	@Override
	//Método para eliminar a un amigo de la lista del usuario.
	public boolean eliminar(String amigo, String nick) throws RemoteException {
		//Primero se verifica que el usuario que se quiere eliminar esté registrado.
		if (amigo != null && !amigo.isEmpty() && Servidor.bbdd.usuarioRegistrado(nick)){
		Servidor.bbdd.eliminarAmigo(amigo, nick);
		return true;
		}
		else 
			return false;
		}
	
	@Override
	//Método que envía los trinos a la bbdd tras comprobar que el usuario que lo va a recibir esté en la lista del que lo envía.
	public boolean enviar(String mensage, String nickDe, String nickA) throws RemoteException{

		if (!Servidor.bbdd.usuarioRegistrado(nickDe) || !Servidor.bbdd.usuarioEnMiLista(nickA, nickDe))
			return false;
		else{
			Servidor.bbdd.enviarTrino(mensage, nickDe, nickA);			
			return true;
		}		
	}
	
	@Override
	//Método que devuelve una lista con los trinos que están en el buffer de un usuario y luego vacía dicho buffer.
	public List<Trino> recibir(String nick) throws RemoteException {
		if(Servidor.bbdd.recibir(nick)!=null){
			List<Trino> trinos = Servidor.bbdd.recibir(nick);
			Servidor.bbdd.limpiarBuffer(nick);
			return trinos;
		}
		else return null;
	}
	
	@Override
	//Método que devuelve la lista de amigos de un usuario.
	public List<String> amigos(String nick) throws RemoteException{
		return Servidor.bbdd.amigos(nick);
	}
	
	//Método que saca por pantalla la lista de usuarios que están online en ese momento en el sistema.
	public void listarUsuarios() throws RemoteException{
		if(clientList.isEmpty())//Primero se comprueba si hay alguno online.
			System.out.println("No hay ningún usuario logeado en este momento:");
		else{//Si hay alguno online se saca la lista.
			System.out.println("Usuarios logeados:");
			for (int i = 0; i < clientList.size(); i++){
				CallbackUsuarioInterface nextClient = (CallbackUsuarioInterface)clientList.elementAt(i);
				System.out.println("@ " + nextClient.getNick());
				}
			}
		}

	//Método que elimina a un cliente del callback cuando se desloga.
	public synchronized void unregisterForCallback(
		CallbackUsuarioInterface callbackClientObject) 
			throws java.rmi.RemoteException{
		clientList.removeElement(callbackClientObject);
		}

	//Método de envío de trinos al callback.
	public synchronized void nuevoTrino(String nick) throws java.rmi.RemoteException{
		
		boolean recibido = false;
		for (int i = 0; i < clientList.size(); i++){
			CallbackUsuarioInterface nextClient = (CallbackUsuarioInterface)clientList.elementAt(i);
			if(nextClient.getNick().equals(nick)){
			trinos = recibirTrinos(nick);
			recibido = true;//Se da el trino como recibido.
			for (Trino trino : trinos) {
				nextClient.notificarTrino("@ " + trino.ObtenerNickPropietario());
				Date date = new Date(trino.ObtenerTimestamp());
				nextClient.notificarTrino("\t" + date + "    " + trino.ObtenerTrino() + "\n");
				}
			}
			}
		if (recibido == true)//Si el receptor estaba online ya recibió el mensaje, por lo tanto se puede borrar su buffer.
			Servidor.bbdd.limpiarBuffer(nick);//El buffer de trinos se vacía aquí ya que de ese modo si el cliente está logado en varios terminales, le llegarán los trinos a todos.
		}
	
	//Método que devuelve una lista con los trinos que están en el buffer de un usuario.
	public List<Trino> recibirTrinos(String nick) throws RemoteException {
		if(Servidor.bbdd.recibir(nick)!=null){
			List<Trino> trinos = Servidor.bbdd.recibir(nick);
			return trinos;
		}
		else return null;
		}
	
	}