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
    private StringBuilder currentData;
    private boolean inTituloElement;
    private int currentId;
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
            ((saxParser) handler).setModificationParameters(id, nuevoTitulo);
            
            saxParser.parse(new InputSource(inputStream), handler);

            // Guardar los cambios en el archivo XML
            ((saxParser) handler).guardarCambiosEnArchivo("/home/desarrollo/Documentos/Leo/Lope de Vega/Acceso a Datos 2º DAM/Tema 1/Leo/catalogo_peliculas_sax.xml");

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
        if (qName.equalsIgnoreCase("titulo")) {
            inTituloElement = true;
            currentData = new StringBuilder();
        }
        if (qName.equalsIgnoreCase("pelicula")) {
            int id = Integer.parseInt(attributes.getValue("id"));
            if (id == currentId) {
                modifyTitle = true;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        System.out.println("Fin de Elemento: " + qName);
        if (qName.equalsIgnoreCase("titulo")) {
            inTituloElement = false;
            if (modifyTitle) {
                currentData = new StringBuilder();
            }
        }
        if (qName.equalsIgnoreCase("pelicula")) {
            modifyTitle = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (inTituloElement && modifyTitle) {
            currentData.append(new String(ch, start, length));
        }
    }

    // Método para configurar el ID y el nuevo título para la modificación
    public void setModificationParameters(int id, String nuevoTitulo) {
        currentId = id;
        // Actualizamos el título si es diferente del título actual
        if (!nuevoTitulo.isEmpty()) {
            currentData = new StringBuilder(nuevoTitulo);
        }
    }

    // Método para guardar los cambios en el archivo XML
 // Método para guardar los cambios en el archivo XML
    public void guardarCambiosEnArchivo(String filePath) throws IOException {
        try {
            // Crear un lector para leer el archivo XML original
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder fileContent = new StringBuilder();
            String line;

            // Leer el archivo y reemplazar el título si es necesario
            boolean inPelicula = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("<pelicula id=\"" + currentId + "\">")) {
                    inPelicula = true;
                }

                if (inPelicula && line.trim().startsWith("<titulo>")) {
                    // Reemplazar el título antiguo con el nuevo título
                    line = "        <titulo>" + currentData.toString() + "</titulo>";
                    inPelicula = false; // Dejar de buscar después de reemplazar
                }

                fileContent.append(line).append(System.lineSeparator());
            }

            reader.close();

            // Escribir los cambios en el archivo XML
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(fileContent.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
