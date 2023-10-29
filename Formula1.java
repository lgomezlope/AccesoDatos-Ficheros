package formula1;
import java.util.*;
import java.io.*;

public class Formula1 {

	public static void main(String[] args) {
		Scanner escaner = new Scanner(System.in);
		String nombre = "", escuderia = "";
		int puntos = 0;
		File fichero = new File("C:/copias/formula1.txt");
		Piloto p1 = null;
		int opcion = 0;
		
		do{
			System.out.println("Introduzca opción: ");
			
			opcion = escaner.nextInt();
			escaner.nextLine();
			
			switch(opcion) {
				case 1:				
					System.out.println("Introduzca nombre del piloto: ");
					nombre = escaner.nextLine();		
					System.out.println("Introduzca escudería del piloto: ");
					escuderia = escaner.nextLine();
					System.out.println("Introduzca puntos del piloto: ");
					puntos = escaner.nextInt();
					
					p1 = new Piloto(nombre, escuderia, puntos);
					try {
						FileWriter streamEscritor = new FileWriter(fichero, true);
						streamEscritor.write(p1.formato());
						streamEscritor.close();
					}catch(IOException exception) {
						System.out.println("Error al acceder al fichero");
					}
					
					escaner.nextLine();
				
					break;
					
				case 2:
					try {
						FileReader streamLector = new FileReader(fichero);
						BufferedReader buffer = new BufferedReader(streamLector);
						String linea = buffer.readLine();
						while(linea != null) {
							System.out.println(linea);
							linea = buffer.readLine();
						}
						
						// Cierra tanto Stream como Buffer
						buffer.close();
						
					}catch(IOException exception) {
						System.out.println("Error al acceder al fichero");
					}
					break;
				case 3:
					System.out.println("Elemento a buscar: ");
					String pilotobusqueda = escaner.nextLine();
					
					try {
						
						FileReader streamLector = new FileReader(fichero);
						BufferedReader buffer = new BufferedReader(streamLector);
						String linea = buffer.readLine();
						ArrayList<String> ficheroaux = new ArrayList<String>();
						while(linea != null) {
							if(!linea.contains(pilotobusqueda)) {
								ficheroaux.add(linea);
							}
							linea = buffer.readLine();
						}
						
						// Cierra tanto Stream como Buffer
						buffer.close();
						
						new FileWriter(fichero, false).close();
						
						for (String item : ficheroaux) {
							FileWriter nuevoEscritor = new FileWriter(fichero, true);
							nuevoEscritor.write(item);
							nuevoEscritor.close();
							
						}
						
						System.out.println("Registro borrado");
						
						
					}catch(IOException exception) {
						System.out.println("Error al acceder al fichero");
					}	
					
					break;
			}	
		}while(opcion != 0);
		System.out.println("Salgo");
	}
}