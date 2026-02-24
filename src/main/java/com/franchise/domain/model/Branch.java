package com.franchise.domain.model;

/**
 * Modelo de dominio para la Sucursal (Branch).
 * Esta clase es POJO puro, sin dependencias de frameworks.
 */
public class Branch {
    private Long id;
    private String name;
    private Long franchiseId;

    public Branch() {
    }

    public Branch(Long id, String name, Long franchiseId) {
        this.id = id;
        this.name = name;
        this.franchiseId = franchiseId;
    }

    // Getters y Setters necesarios para los Mappers y la lógica de negocio

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

    public Long getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(Long franchiseId) {
        this.franchiseId = franchiseId;
    }
}