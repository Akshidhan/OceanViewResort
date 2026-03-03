package model;

public class ReservationModel {
    private Long id;
    private String guestName;
    private String address;
    private String contactNumber;
    private String roomType;
    private String checkIn;   // yyyy-MM-dd
    private String checkOut;  // yyyy-MM-dd

    public ReservationModel() {}

    public ReservationModel(Long id, String guestName, String address, String contactNumber,
                             String roomType, String checkIn, String checkOut) {
        this.id = id;
        this.guestName = guestName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.roomType = roomType;
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

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
}
