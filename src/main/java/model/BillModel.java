package model;

public class BillModel {
    private Long id;
    private Long reservationId;

    // Snapshot fields (frozen at billing time)
    private String guestName;
    private String roomName;
    private double pricePerNight;
    private String checkIn;   // yyyy-MM-dd
    private String checkOut;  // yyyy-MM-dd
    private long numberOfNights;
    private double totalCost;

    private String createdAt; // ISO timestamp string

    public BillModel() {}

    public BillModel(Long id, Long reservationId,
                     String guestName, String roomName,
                     double pricePerNight, String checkIn, String checkOut,
                     long numberOfNights, double totalCost,
                     String createdAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomName = roomName;
        this.pricePerNight = pricePerNight;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfNights = numberOfNights;
        this.totalCost = totalCost;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public long getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(long numberOfNights) { this.numberOfNights = numberOfNights; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

