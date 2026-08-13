package one.austral.lpa1.model;

/**
 * Producto de las vistas Thymeleaf (U1/U2). Es el record que antes se llamaba
 * Producto; las plantillas usan slug/precioCop/unidad/stock y el i18n
 * (messages_*.properties) provee nombre y descripcion.
 */
public record ProductoVista(String slug, int precioCop, String unidad, int stock) {
}
