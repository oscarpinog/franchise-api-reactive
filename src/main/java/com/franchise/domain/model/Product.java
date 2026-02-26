package com.franchise.domain.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidad de dominio pura.
 * Incluye validaciones de Bean Validation para la capa de entrada 
 * y lógica de protección de integridad en setters.
 */
public class Product {

    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre del producto debe tener entre 2 y 100 caracteres")
    private String name;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long branchId;

    public Product() {
        // Inicialización por defecto para evitar NullPointerException internos
        this.stock = 0;
    }

    public Product(Long id, String name, Integer stock, Long branchId) {
        this.id = id;
        this.name = name;
        this.setStock(stock); // Usamos el setter para aplicar la lógica de nulidad
        this.branchId = branchId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStock() {
        return stock;
    }

    /**
     * Setea el stock. Si el valor es nulo, se inicializa en 0.
     */
    public void setStock(Integer stock) {
        this.stock = (stock == null) ? 0 : stock;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
}