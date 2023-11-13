package sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;

public class saxParser extends DefaultHandler {
    private static String tituloCreado;
    private boolean inTituloElement;
    private static int idBuscado;
    private boolean modifyTitle; // Variable para rastrear si debemos modificar el título

    public static void main(String[] args) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser handler = new saxParser();

            File xmlFile = new File("/home/desarrollo/Documentos/Leo/Lope de Vega/Acceso a Datos 2º DAM/Tema 1/Leo/catalogo_peliculas_sax.xml");
            String fileURL = "file://" + xmlFile.getAbsolutePath();
            URL url = new URL(fileURL);
            InputStream inputStream = url.openStream();

            // Solicitar al usuario el ID de la película a modificar
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Ingrese el ID de la película que desea modificar: ");
            int id = Integer.parseInt(reader.readLine());
            
            // Solicitar al usuario el nuevo título
            System.out.print("Ingrese el nuevo título: ");
            String nuevoTitulo = reader.readLine();

            // Setear el ID y el nuevo título para la modificación
            setModificationParameters(id, nuevoTitulo);
            saxParser.parse(new InputSource(inputStream), handler);

            // Guardar los cambios en el archivo XML
            guardarCambiosEnArchivo("/home/desarrollo/Documentos/Leo/Lope de Vega/Acceso a Datos 2º DAM/Tema 1/Leo/catalogo_peliculas_sax.xml");

            System.out.println("Título modificado con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startDocument() throws SAXException {
        System.out.println("Comienzo del Documento XML");
    }

    @Override
    public void endDocument() throws SAXException {
        System.out.println("Fin del Documento XML");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        System.out.println("Inicio de Elemento: " + qName);

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        System.out.println("Fin de Elemento: " + qName);

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (inTituloElement && modifyTitle) {
        	//nuevoTitulo.append(new String(ch, start, length));
        }
    }

    // Método para configurar el ID y el nuevo título para la modificación
    public static void setModificationParameters(int id, String nuevoTitulo) {
        idBuscado = id;
        if (!nuevoTitulo.isEmpty()) {
            // Establecer el nuevo título solo si no está vacío
        	tituloCreado = new String(nuevoTitulo);
        }
    }

    // Método para guardar los cambios en el archivo XML
 // Método para guardar los cambios en el archivo XML
    public static void guardarCambiosEnArchivo(String filePath) throws IOException {
        try {
            File inputFile = new File(filePath);
            File tempFile = new File("tempfile.xml");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            boolean inPelicula = false;
            boolean updatedTitle = false;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().startsWith("<pelicula id=\"" + idBuscado + "\">")) {
                    inPelicula = true;
                }
            	System.out.println(inPelicula + " " + tituloCreado);
                if (inPelicula && currentLine.trim().startsWith("<titulo>")) {
                    if (!tituloCreado.toString().isEmpty()) {
                        // Reemplazar el título solo si se proporciona un nuevo título no vacío
                        currentLine = "        <titulo>" + tituloCreado.toString() + "</titulo>";
                        updatedTitle = true;
                    }
                    inPelicula = false; // Dejar de buscar después de reemplazar
                }

                writer.write(currentLine + System.lineSeparator());
            }

            writer.close();
            reader.close();

            // Reemplazar el archivo original con el archivo temporal solo si el título se ha actualizado
            if (updatedTitle) {
                if (!inputFile.delete()) {
                    System.out.println("No se pudo eliminar el archivo original.");
                    return;
                }

                if (!tempFile.renameTo(inputFile)) {
                    System.out.println("No se pudo renombrar el archivo temporal.");
                }
            } else {
                // No se actualizó el título, no es necesario reemplazar el archivo original
                tempFile.delete(); // Eliminar el archivo temporal
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
