package ficheroxml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import javax.xml.transform.OutputKeys;
import java.util.Scanner;


public class parseadorXML {
	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File("c:/copias/catalogo_peliculas.xml"));
		
		// Normalizar el documento XML
		document.getDocumentElement().normalize();
		// Obtener todos los nodos de tipo 'pelicula'
		NodeList peliculas = document.getElementsByTagName("pelicula");
		
		Element catalogo = (Element) document.getElementsByTagName("catalogo").item(0);

		
		catalogo.appendChild(creaPelicula(document, "tt0468569", "E.T.", "Steven Spielberg","1982", "Drama", "5.0"));
		catalogo.appendChild(creaPelicula(document, "tt0468579", "Origen", "Christopher Nolan","2010", "Ciencia Ficción", "4.2"));
		catalogo.appendChild(creaPelicula(document, "tt0468589", "Blade Runner", "Ridley Scott","1982", "Ciencia Ficción", "4.1"));
		catalogo.appendChild(creaPelicula(document, "tt0468599", "Django", "Quentin Tarantino","2012", "Western", "4.4"));
		catalogo.appendChild(creaPelicula(document, "tt0468509", "Malditos Bastardos", "Quentin Tarantino","2007", "Drama", "4.2"));
		muestraPeliculas(peliculas);	
		
		System.out.println("Introduce el género buscado: ");
		Scanner scanner = new Scanner(System.in);
		String genero_buscado = scanner.next();
	
		buscaPeliculaPorGenero(peliculas, genero_buscado);
		
		eliminaPeliculaBajaValoracion(document, peliculas, 4);
		
		System.out.println("Introduce el id buscado: ");
		String id_buscado = scanner.next();
		
		System.out.println("Introduce el nuevo año: ");
		String nuevo_anio = scanner.next();
		
		actualizaAnio(peliculas, id_buscado, nuevo_anio);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // Establece la cantidad de espacios para la indentación
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File("c:/copias/catalogo_peliculas_dom.xml"));
		transformer.transform(source, result);
		
	}
	
	private static void muestraPeliculas(NodeList lista) {
		for (int i = 0; i < lista.getLength(); i++) {
			Element pelicula = (Element) lista.item(i);
			NodeList titulos = pelicula.getElementsByTagName("titulo");
			NodeList directores = pelicula.getElementsByTagName("director");
			NodeList valoraciones = pelicula.getElementsByTagName("valoracion");
			
			if (titulos.getLength() > 0 && directores.getLength() > 0 && valoraciones.getLength() > 0) {
				Element titulo = (Element) titulos.item(0);
				Element director = (Element) directores.item(0);
				Element valoracion = (Element) valoraciones.item(0);
				if(Float.parseFloat(valoracion.getTextContent()) < 5) {
					double nuevaValoracion = Double.parseDouble(valoracion.getTextContent()) + 0.1;
					valoracion.setTextContent(String.format("%.2f", nuevaValoracion).replace(",", "."));	
				}
				System.out.println("Pelicula " + (i+1) + ": " + titulo.getTextContent() + " dirigida por " + director.getTextContent() + " con valoración de " + valoracion.getTextContent());
			}
					
		}
	}
	
	private static Element creaPelicula(Document document, String id, String titulo_nuevo, String director_nuevo, String anio_nuevo, String genero_nuevo, String valoracion_nuevo) {
		Element nuevaPelicula = (Element) document.createElement("pelicula");
		nuevaPelicula.setAttribute("id", id);	
		
		Element titulo = (Element) document.createElement("titulo");
		titulo.appendChild(document.createTextNode(titulo_nuevo));
		nuevaPelicula.appendChild(titulo);
		
		Element director = (Element) document.createElement("director");
		director.appendChild(document.createTextNode(director_nuevo));
		nuevaPelicula.appendChild(director);	
		
		Element anio = (Element) document.createElement("anio");
		anio.appendChild(document.createTextNode(anio_nuevo));
		nuevaPelicula.appendChild(anio);
		
		Element genero = (Element) document.createElement("genero");
		genero.appendChild(document.createTextNode(genero_nuevo));
		nuevaPelicula.appendChild(genero);
		
		Element valoracion = (Element) document.createElement("valoracion");
		valoracion.appendChild(document.createTextNode(valoracion_nuevo));
		nuevaPelicula.appendChild(valoracion);
		
		return nuevaPelicula;
	
	}
	
	private static void buscaPeliculaPorGenero(NodeList peliculas, String genero_buscado) {
		System.out.println("Listado de películas con el género '" + genero_buscado + "': ");
		for (int i = 0; i < peliculas.getLength(); i++) {
			Element pelicula = (Element) peliculas.item(i);
			NodeList generos = pelicula.getElementsByTagName("genero");
			if(generos.getLength() > 0) {
				if(generos.item(0).getTextContent().equals(genero_buscado)) {
					NodeList titulos = pelicula.getElementsByTagName("titulo");
					if (titulos.getLength() > 0) {
						System.out.println(titulos.item(0).getTextContent());						
					}
				}
			}
		}
		
	}
	
	private static void eliminaPeliculaBajaValoracion(Document document, NodeList peliculas, int valoracion_minima) {
		NodeList catalogos = document.getElementsByTagName("catalogo");
		Element catalogo = (Element) catalogos.item(0);
		
		for (int i = 0; i < peliculas.getLength(); i++) {
			Element pelicula = (Element) peliculas.item(i);
			NodeList valoracion = pelicula.getElementsByTagName("valoracion");
			
			if(valoracion.getLength() > 0) {
				if(Float.parseFloat(valoracion.item(0).getTextContent()) < valoracion_minima) {
					catalogo.removeChild(pelicula);
				}
			}
		}
	}
	
	private static void actualizaAnio(NodeList peliculas, String id_buscado, String nuevo_anio) {
		for (int i = 0; i < peliculas.getLength(); i++) {
			Element pelicula = (Element) peliculas.item(i);
			if(pelicula.getAttribute("id").equals(id_buscado)) {
				NodeList anios = pelicula.getElementsByTagName("anio");
				anios.item(0).setTextContent(nuevo_anio);
			}
		}
	}
}
