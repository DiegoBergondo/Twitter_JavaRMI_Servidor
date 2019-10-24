package twitter_javarmi_servidor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

import twitter_javarmi_common.Gui;
import twitter_javarmi_common.ServicioAutenticacionInterface;
import twitter_javarmi_common.ServicioDatosInterface;

public class Servidor {
	
	public static ServicioDatosInterface bbdd;
	private static long timestamp;
	private static long timestamp2;
	Date date = new Date();

	//Método que levanta el servicio y saca el menú del servidor por pantalla.
	public static void main(String[] args) throws Exception {
			
		try{
	
			Registry RServer;
			ServicioAutenticacionImpl servicioAuten = new ServicioAutenticacionImpl();
			RServer = LocateRegistry.createRegistry(8889);
			ServicioAutenticacionInterface remote = (ServicioAutenticacionInterface)UnicastRemoteObject.exportObject(servicioAuten, 8889);
			RServer.rebind("Servidor", remote);
				
			Registry registrybbdd = LocateRegistry.getRegistry(8888);
			bbdd = (ServicioDatosInterface)registrybbdd.lookup("BBDD");
		
			ServicioGestorImpl servicioGestor = new ServicioGestorImpl();
			RServer.rebind("callback", servicioGestor);		
			
			int opt = 0;
			timestamp=time();
		
			do {
				opt = Gui.menu("---SERVIDOR---", 
						new String[]{ "Información del Servidor", 
								  	"Listar Usuarios Logeados", 
								  	"Salir" });
			
				switch (opt) {
					case 0: info(); break;
					case 1: servicioGestor.listarUsuarios(); break;
				}
			}
			while (opt != 2);
		
			RServer.unbind("callback");
			RServer.unbind("Servidor");
			ServicioGestorImpl.unexportObject(servicioGestor, true);
			UnicastRemoteObject.unexportObject(servicioAuten, true);
		
			System.out.println("Servidor Terminado");
		}
		catch (Exception excr) {
			System.out.println("No fue posible lanzar el servidor");
		}
	}
	
	private static long time(){
		Date date = new Date();
		return date.getTime();
	}
	
	//Muestra por pantalla el tiempo que lleva el servidor online en segundos, el nombre de los objetos remotos y el puerto.
	private static void info(){
		Date date = new Date();
		timestamp2=date.getTime();
		long tiempofinal = timestamp2 - timestamp;
		System.out.println("Objetos remotos Servidor y Callback en puerto 8889 y lleva online "+tiempofinal/1000+" segundos");
		System.out.println();
	}
}
