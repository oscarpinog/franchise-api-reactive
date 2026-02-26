package com.franchise.domain.util;

public final class DomainConstants {
    private DomainConstants() {}

    /*
     * SUCURSAL
     */
    // Errores
    public static final String ERROR_BRANCH_NOT_FOUND = "No se encontró la sucursal con ID: %d";
    public static final String ERROR_PRODUCT_BRANCH_NOT_FOUND = "No se puede agregar el producto. La sucursal con ID %d no existe.";
    public static final String ERROR_NULL_ARGUMENT = "El argumento '%s' no puede ser nulo";

    // Logs de Sucursal
    public static final String LOG_BRANCH_ADD_START = "Agregando nueva sucursal '{}' a la franquicia ID: {}";
    public static final String LOG_BRANCH_ADD_SUCCESS = "Sucursal guardada exitosamente con ID: {}";
    public static final String LOG_BRANCH_UPDATE_START = "Buscando sucursal ID: {} para actualizar nombre a '{}'";
    public static final String LOG_BRANCH_UPDATE_SUCCESS = "Nombre de sucursal actualizado con éxito";

    // Logs de Producto
    public static final String LOG_PRODUCT_ADD_VALIDATING = "Validando sucursal ID: {} para añadir producto '{}'";
    public static final String LOG_PRODUCT_ADD_SUCCESS = "Producto '{}' vinculado a la sucursal {}";
    public static final String LOG_PRODUCT_ADD_ERROR = "Error al agregar producto: {}";
    
    
    /*
     * FRANQUICIA
     */
 // Errores
    public static final String ERROR_FRANCHISE_NOT_FOUND = "Error: Franquicia no encontrada con ID: %d";
    public static final String ERROR_FRANCHISE_ADD_BRANCH_NOT_FOUND = "No se puede agregar sucursal. La franquicia %d no existe.";

    // Logs Franquicia
    public static final String LOG_FRANCHISE_CREATE_START = "Registrando nueva franquicia: {}";
    public static final String LOG_FRANCHISE_CREATE_SUCCESS = "Franquicia '{}' creada exitosamente con ID: {}";
    public static final String LOG_FRANCHISE_UPDATE_START = "Intentando actualizar nombre de franquicia ID: {} a '{}'";
    public static final String LOG_FRANCHISE_UPDATE_SUCCESS = "Nombre de franquicia actualizado correctamente";
    
    // Logs Reportes y Sucursales vinculadas
    public static final String LOG_FRANCHISE_TOP_PRODUCTS_START = "Generando reporte de productos top para franquicia ID: {}";
    public static final String LOG_FRANCHISE_TOP_PRODUCTS_DEBUG = "Buscando producto con mayor stock en sucursal: {}";
    public static final String LOG_FRANCHISE_TOP_PRODUCTS_COMPLETE = "Reporte de top productos para franquicia {} finalizado con éxito";
    public static final String LOG_FRANCHISE_ADD_BRANCH_VALIDATING = "Validando franquicia ID: {} para agregar sucursal '{}'";
    public static final String LOG_FRANCHISE_ADD_BRANCH_SUCCESS = "Sucursal '{}' vinculada correctamente a la franquicia {}";
    
    /*
     * PRODUCTOS
     */
    
 // Errores
    public static final String ERROR_PRODUCT_NOT_FOUND = "No se encontró el producto con ID: %d";
    public static final String ERROR_PRODUCT_DELETE_NOT_FOUND = "No se puede eliminar: Producto con ID %d no existe.";


    // Logs Producto
    public static final String LOG_PRODUCT_ADD_START = "Registrando producto '{}' para la sucursal ID: {}";
    public static final String LOG_PRODUCT_ADD_SUCC = "Producto guardado exitosamente con ID: {}";
    public static final String LOG_PRODUCT_STOCK_UPDATE_START = "Solicitud para actualizar stock de producto ID: {} a {}";
    public static final String LOG_PRODUCT_STOCK_UPDATE_SUCCESS = "Stock actualizado para '{}'";
    public static final String LOG_PRODUCT_NAME_UPDATE_START = "Solicitud para cambiar nombre de producto ID: {} a '{}'";
    public static final String LOG_PRODUCT_NAME_UPDATE_SUCCESS = "Nombre actualizado correctamente para ID: {}";
    public static final String LOG_PRODUCT_DELETE_START = "Iniciando proceso de eliminación para producto ID: {}";
    public static final String LOG_PRODUCT_DELETE_SUCCESS = "<< Producto ID: {} eliminado exitosamente del repositorio";
    
 // Mensajes específicos para Sucursal-controller
    public static final String VALIDATION_ID_POSITIVE = "El ID debe ser un valor positivo";
    public static final String VALIDATION_NAME_NOT_BLANK = "El nombre no puede estar vacío o ser nulo";
    public static final String VALIDATION_BRANCH_ID_POSITIVE = "El ID de la sucursal debe ser positivo";
    
 // Mensajes específicos para Franquicias-controller
    public static final String VALIDATION_FRANCHISE_ID_POSITIVE = "El ID de la franquicia debe ser positivo";
    public static final String VALIDATION_FRANCHISE_NAME_NOT_BLANK = "El nombre de la franquicia no puede estar vacío";
    
 // Mensajes específicos para Productos-controller
    public static final String VALIDATION_PRODUCT_ID_POSITIVE = "El ID del producto debe ser positivo";
    public static final String VALIDATION_PRODUCT_STOCK_MIN = "El stock no puede ser negativo";
    public static final String VALIDATION_PRODUCT_NAME_NOT_BLANK = "El nombre del producto no puede estar vacío";
}