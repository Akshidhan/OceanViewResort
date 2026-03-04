package model;

public class RoomModel {
    private Long id;
    private String name;
    private double pricePerNight;
    private int capacity;

    public RoomModel() {}

    public RoomModel(Long id, String name, double pricePerNight, int capacity) {
        this.id = id;
        this.name = name;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}

