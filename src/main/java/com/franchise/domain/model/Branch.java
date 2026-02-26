package com.franchise.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Modelo de dominio para la Sucursal (Branch).
 * Esta clase es POJO puro, sin dependencias de frameworks.
 */
public class Branch {
	@NotNull(message = "El ID no puede ser nulo")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotNull(message = "El ID de la franquicia es obligatorio")
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
    
    
    public void rename(String name) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }
}