package com.franchise.domain.model;

/**
 * Entidad de dominio pura.
 */
public class Product {
    private Long id;
    private String name;
    private Integer stock;
    private Long branchId;

    public Product() {}

    public Product(Long id, String name, Integer stock, Long branchId) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.branchId = branchId;
    }

    // Estos son los métodos que el Mapper está buscando:
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

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
}