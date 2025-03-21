package fr.oiha.mealplanner.model;

public class Product {

    private final int id;
    private String name;
    double pricePerPack;
    double weightPerPack;
    String unit;

    public Product(int id, String name, double pricePerPack, double weightPerPack, String unit) {
        this.id = id;
        this.name = name;
        this.pricePerPack = pricePerPack;
        this.weightPerPack = weightPerPack;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerPack() {
        return pricePerPack;
    }

    public void setPricePerPack(double pricePerPack) {
        this.pricePerPack = pricePerPack;
    }

    public double getWeightPerPack() {
        return weightPerPack;
    }

    public void setWeightPerPack(double weightPerPack) {
        this.weightPerPack = weightPerPack;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
