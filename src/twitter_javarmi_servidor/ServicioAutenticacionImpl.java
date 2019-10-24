package twitter_javarmi_servidor;

import java.rmi.RemoteException;
import twitter_javarmi_common.ServicioAutenticacionInterface;

public class ServicioAutenticacionImpl implements ServicioAutenticacionInterface {
	
	@Override
	//Método que solicita al servicio de datos la entrada de un nuvo usuario.
	public boolean registrar(String nick, String password, String nombre, String mail) throws RemoteException {
		//Primero se comprueba que no exista ya otro usuario registrado con el mismo nick.
		if (Servidor.bbdd.usuarioRegistrado(nick))
			return false;
		else{
			Servidor.bbdd.añadirUsuario(nick, password, nombre, mail);
			return true;
			}
		}
	
	@Override
	//Método que verifica si el nick y password de un usuario son correctos y por tanto autoriza su login.
	public boolean autenticar(String nick, String password) throws RemoteException {
		if (nick != null && !nick.isEmpty() && Servidor.bbdd.usuarioRegistrado(nick) && Servidor.bbdd.getPass(nick).equals(password))
			return true;
		else return false;
	}

	@Override
	//Método que indica si un usuario está en la lista de amigos de otro.
	public boolean usuarioEnMiLista(String nickAmigo, String nick) throws RemoteException{
		return Servidor.bbdd.usuarioEnMiLista(nickAmigo, nick);
	}
	
	@Override
	//Método que devuelve el nombre de un usuario.
	public String getNombre(String nick) throws RemoteException{
		return Servidor.bbdd.getNombre(nick);
	}
	
	@Override
	//Método que devuelve el email de un usuario.
	public String getMail(String nick) throws RemoteException{
		return Servidor.bbdd.getMail(nick);
	}

}