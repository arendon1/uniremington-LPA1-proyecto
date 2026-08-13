package one.austral.lpa1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Producto persistido en PostgreSQL (U3). Reemplaza al record en memoria de
 * U1/U2 (que ahora vive como {@link ProductoVista} para las vistas Thymeleaf).
 */
@Entity
@Table(name = "productos")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String slug;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false, length = 500)
	private String descripcion;

	@Column(nullable = false)
	private int precioCop;

	@Column(nullable = false)
	private String unidad;

	@Column(nullable = false)
	private int stock;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private Categoria categoria;

	@Column(nullable = false)
	private String imagenUrl;

	protected Producto() {
		// para JPA
	}

	public Producto(String slug, String nombre, String descripcion, int precioCop,
			String unidad, int stock, Categoria categoria, String imagenUrl) {
		this.slug = slug;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.precioCop = precioCop;
		this.unidad = unidad;
		this.stock = stock;
		this.categoria = categoria;
		this.imagenUrl = imagenUrl;
	}

	public Long getId() {
		return id;
	}

	public String getSlug() {
		return slug;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public int getPrecioCop() {
		return precioCop;
	}

	public String getUnidad() {
		return unidad;
	}

	public int getStock() {
		return stock;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public String getImagenUrl() {
		return imagenUrl;
	}
}
