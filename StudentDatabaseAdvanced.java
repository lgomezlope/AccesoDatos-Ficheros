package aleatorio;

import java.io.*;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;

public class StudentDatabaseAdvanced {
	static final int RECORD_SIZE = 48;
	static final int ID_SIZE = 8;
	static final int NAME_SIZE = 32;
	static final int GRADE_SIZE = 8;
	
	// ACTUALIZAR CON NOMBRE DEL ARCHIVO
	static final String filename = "/home/desarrollo/Documentos/Leo/Lope de Vega/Acceso a Datos 2º DAM/Tema 1/Leo/random.txt";
	
	

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		int option;
		RandomAccessFile file;
		
		do {
			System.out.println("Menú:");
			System.out.println("1. Añadir estudiante");
			System.out.println("2. Buscar estudiante por ID");
			System.out.println("3. Modificar nota de estudiante");
			System.out.println("4. Eliminar estudiante");
			System.out.println("5. Listar todos los estudiantes");
			System.out.println("6. Salir");
			System.out.print("Elija una opción: ");
			option = scanner.nextInt();
			switch (option) {
			
			case 1:
				try {
					file = new RandomAccessFile(filename,"rw");					
					System.out.println("Introduce el identificador del estudiante: ");
					int id = scanner.nextInt();
					System.out.println("Introduce nombre del estudiante: ");
					String nombre = scanner.next();
					System.out.println("Introduce nota del estudiante: ");
					long nota = scanner.nextLong();
					creaAlumno(id, nombre, nota, file, file.length());
					
					file.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			case 2:
				try {
					file = new RandomAccessFile(filename,"r");
					System.out.println("Introduce el identificador del estudiante a buscar: ");
					int idBuscada = scanner.nextInt();
								
					if(buscaAlumno(idBuscada, file) == -1) {
						System.out.println("Estudiante con id " + idBuscada + " NO encontrado");
					}	
				}catch (IOException e) {
					e.printStackTrace();
				}
				
				break;

			case 3:
				try {
					file = new RandomAccessFile(filename,"rw");
					System.out.println("Introduce el identificador del estudiante a modificar: ");
					int idBuscada = scanner.nextInt();
					
					System.out.println("Introduce la nueva nota del estudiante: ");
					long nota = scanner.nextLong();
					int nItem = buscaAlumno(idBuscada, file);
					if( nItem == -1) {
						System.out.println("Estudiante con id " + idBuscada + " NO encontrado");
					}else {
						modificaNotaAlumno(nota, file, nItem);
					}
					file.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
				
				break;
			case 4:
				try {
					file = new RandomAccessFile(filename,"rw");
					System.out.println("Introduce el identificador del estudiante a eliminar: ");
					int idBuscada = scanner.nextInt();
										
					int nItem = buscaAlumno(idBuscada, file);
					if( nItem == -1) {
						System.out.println("Estudiante con id " + idBuscada + " NO encontrado");
					}else {
						eliminaAlumno(file, nItem);
					}
					file.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 5:
				try {
					file = new RandomAccessFile(filename,"r");
					mostrarAlumnos(file);

					file.close();

				} catch (IOException e) {
				}
				break;
			case 6:
				System.out.println("Saliendo...");
				break;
			default:
				System.out.println("Opción inválida.");
				break;
			}
		} while (option != 6);
		scanner.close();
	}
	
	static void creaAlumno(int id, String nombre, Long nota, RandomAccessFile file, long desplazamiento) {
		try {
			file.seek(desplazamiento);
			ByteBuffer bufferId = ByteBuffer.allocate(ID_SIZE);
			bufferId.putInt(id);
			file.write(bufferId.array());

			byte[] nameBytes = new byte[NAME_SIZE];
			byte[] name = nombre.getBytes(StandardCharsets.UTF_8);
			System.arraycopy(name, 0, nameBytes, 0, Math.min(name.length, NAME_SIZE));
			file.write(nameBytes);

			ByteBuffer buffer = ByteBuffer.allocate(GRADE_SIZE);
			buffer.putLong(nota);
			file.write(buffer.array());
	
		}catch(IOException e) {
		}		
		
	}
	
	static void eliminaAlumno(RandomAccessFile file, int nItem) {
		try {
		 long filesize = file.length();		 
		 for (long i = nItem*RECORD_SIZE + RECORD_SIZE; i < filesize; i += RECORD_SIZE) {
             file.seek(i);
             byte[] buffer = new byte[RECORD_SIZE];
             file.readFully(buffer);
             file.seek(i - RECORD_SIZE);
             file.write(buffer);
         }

         file.setLength(filesize - RECORD_SIZE);
         System.out.println("El registro ha sido eliminado.");
		}catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	static void modificaNotaAlumno(long nota, RandomAccessFile file, int nItem) {
		try {
			file.seek(nItem*RECORD_SIZE + ID_SIZE + NAME_SIZE);			
			
			ByteBuffer buffer = ByteBuffer.allocate(GRADE_SIZE);
			buffer.putLong(nota);
			file.write(buffer.array());
			
		}catch(IOException e) {
		}		
	}
	
	static int buscaAlumno(int id, RandomAccessFile file) {
		int posicionItem = -1;
		try {
			long items = file.length() / RECORD_SIZE;
			for (int i = 0; i < items; i++) {
				file.seek(i * RECORD_SIZE);
				byte[] idBytes = new byte[ID_SIZE];
				file.readFully(idBytes);
				ByteBuffer idBuffer = ByteBuffer.wrap(idBytes);
				if(id == idBuffer.getInt()) {
					posicionItem = i;
					byte[] nameBytes = new byte[NAME_SIZE];
					file.readFully(nameBytes);
					String name = new String(nameBytes, StandardCharsets.UTF_8).trim(); // Elimina espacios
		
					byte[] gradeBytes = new byte[GRADE_SIZE];
					file.readFully(gradeBytes);
					ByteBuffer gradeBuffer = ByteBuffer.wrap(gradeBytes);
					long grade = gradeBuffer.getLong(); 
					System.out.println("Estudiante con id " + id + " encontrado: " + name + " " + grade);
				}	
				
			}
		} catch (IOException e) {
		}		
		
		return posicionItem;
		
	}
	
	static void mostrarAlumnos(RandomAccessFile file) {
		try {
			
			long items = file.length() / RECORD_SIZE;
			for (int i = 0; i < items; i++) {
				file.seek(i * RECORD_SIZE);
	
				byte[] idBytes = new byte[ID_SIZE];
				file.readFully(idBytes);
				ByteBuffer idBuffer = ByteBuffer.wrap(idBytes);
				int id = idBuffer.getInt();
	
				byte[] nameBytes = new byte[NAME_SIZE];
				file.readFully(nameBytes);
				String name = new String(nameBytes, StandardCharsets.UTF_8).trim(); // Elimina espacios
	
				byte[] gradeBytes = new byte[GRADE_SIZE];
				file.readFully(gradeBytes);
				ByteBuffer gradeBuffer = ByteBuffer.wrap(gradeBytes);
				long grade = gradeBuffer.getLong(); 
				System.out.println(id + " " + name + " " + grade);
			}
		} catch (IOException e) {
		}	
	}
 
}