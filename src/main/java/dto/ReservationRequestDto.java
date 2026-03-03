package dto;

public class ReservationRequestDto {
    public String guestName;
    public String address;
    public String contactNumber;
    public Long roomId;
    public String checkIn;   // ISO date string: yyyy-MM-dd
    public String checkOut;  // ISO date string: yyyy-MM-dd
}

