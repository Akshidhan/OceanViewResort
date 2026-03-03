package model;

public class ReservationModel {
    private Long id;
    private String guestName;
    private String address;
    private String contactNumber;
    private Long roomId;
    private String roomName;        // populated from JOIN with rooms table
    private Double pricePerNight;   // populated from JOIN with rooms table
    private String checkIn;   // yyyy-MM-dd
    private String checkOut;  // yyyy-MM-dd

    public ReservationModel() {}

    public ReservationModel(Long id, String guestName, String address, String contactNumber,
                             Long roomId, String roomName, Double pricePerNight,
                             String checkIn, String checkOut) {
        this.id = id;
        this.guestName = guestName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.roomId = roomId;
        this.roomName = roomName;
        this.pricePerNight = pricePerNight;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public Double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(Double pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
}
