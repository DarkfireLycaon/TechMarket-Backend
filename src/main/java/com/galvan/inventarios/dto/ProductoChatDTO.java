package com.galvan.inventarios.dto;

public class ProductoChatDTO {
    public Integer id;
    public String nombre;
    public String stockInfo;

    public ProductoChatDTO() {}
    public ProductoChatDTO(Integer id, String nombre, String stockInfo) {
        this.id = id;
        this.nombre = nombre;
        this.stockInfo = stockInfo;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getStockInfo() {
        return stockInfo;
    }
}
