package formula1;

public class Piloto {
	private String nombre;
	private String escuderia;
	private int puntos;
	
	public Piloto(String nombre, String escuderia, int puntos) {
		this.nombre = nombre;
		this.escuderia = escuderia;
		this.puntos = puntos;
	}
	
	public Piloto(String nombre, String escuderia) {
		this.nombre = nombre;
		this.escuderia = escuderia;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEscuderia() {
		return escuderia;
	}

	public void setEscuderia(String escuderia) {
		this.escuderia = escuderia;
	}

	public int getPuntos() {
		return puntos;
	}

	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}
	
	public String formato() {
		return this.nombre+ " " + this.escuderia+ " " + this.puntos + "\n";
	}
}
