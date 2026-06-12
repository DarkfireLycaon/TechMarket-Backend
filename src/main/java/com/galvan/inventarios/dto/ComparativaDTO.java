package com.galvan.inventarios.dto;

import com.galvan.inventarios.modelo.Producto;

public class ComparativaDTO {
    public String producto1;
    public String producto2;
    public double precio1;
    public double precio2;
    public String stock1;
    public String stock2;
    public String categoria1;
    public String categoria2;

    public ComparativaDTO() {}
    public ComparativaDTO(Producto p1, Producto p2) {
        // Datos del primer producto
        this.producto1 = p1.getNombre();
        this.precio1 = p1.getPrecioVenta();
        this.stock1 = p1.getStock() > 0 ? p1.getStock() + " uds" : "Agotado";
        this.categoria1 = p1.getCategoria();

        // Datos del segundo producto
        this.producto2 = p2.getNombre();
        this.precio2 = p2.getPrecioVenta();
        this.stock2 = p2.getStock() > 0 ? p2.getStock() + " uds" : "Agotado";
        this.categoria2 = p2.getCategoria();
    }
    public ComparativaDTO(String producto1, String producto2, double precio1, double precio2, String stock1, String stock2,  String categoria1, String categoria2 ) {
        this.producto1 = producto1;
        this.producto2 = producto2;
        this.precio1 = precio1;
        this.precio2 = precio2;
        this.stock1 = stock1;
        this.stock2 = stock2;
        this.categoria1 = categoria1;
        this.categoria2 = categoria2;
    }
    public String getProducto1() {
        return producto1;
    }
    public void setProducto1(String producto1) {
        this.producto1 = producto1;
    }
    public String getProducto2() {
        return producto2;
    }
    public void setProducto2(String producto2) {
        this.producto2 = producto2;
    }
    public double getPrecio1() {
        return precio1;
    }
    public void setPrecio1(double precio1) {
        this.precio1 = precio1;
    }
    public double getPrecio2() {
        return precio2;
    }
    public void setPrecio2(double precio2) {
        this.precio2 = precio2;
    }
    public String getStock1() {
        return stock1;
    }
    public void setStock1(String stock1) {
        this.stock1 = stock1;
    }
    public String getStock2() {
        return stock2;
    }
    public void setStock2(String stock2) {
        this.stock2 = stock2;
    }
    public String getCategoria1() {
        return categoria1;
    }
    public void setCategoria1(String categoria1) {
        this.categoria1 = categoria1;
    }
    public String getCategoria2() {
        return categoria2;
    }
    public void setCategoria2(String categoria2) {
        this.categoria2 = categoria2;
    }
}
