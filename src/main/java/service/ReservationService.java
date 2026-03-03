package service;

import dao.ReservationDao;
import dao.RoomDao;
import dto.ReservationPageDto;
import dto.ReservationRequestDto;
import model.ReservationModel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservationService {

    private final ReservationDao dao;
    private final RoomDao roomDao;

    public ReservationService(ReservationDao dao, RoomDao roomDao) {
        this.dao = dao;
        this.roomDao = roomDao;
    }

    public ReservationModel create(ReservationRequestDto dto) {
        validate(dto);
        validateRoomExists(dto.roomId);
        ReservationModel model = toModel(null, dto);
        return dao.create(model);
    }

    public ReservationModel getById(long id) {
        ReservationModel r = dao.findById(id);
        if (r == null) throw new IllegalArgumentException("Reservation not found: " + id);
        return r;
    }

    public ReservationModel update(long id, ReservationRequestDto dto) {
        validate(dto);
        validateRoomExists(dto.roomId);
        if (dao.findById(id) == null) throw new IllegalArgumentException("Reservation not found: " + id);
        ReservationModel model = toModel(id, dto);
        dao.update(model);
        return dao.findById(id);
    }

    public void delete(long id) {
        if (!dao.delete(id)) throw new IllegalArgumentException("Reservation not found: " + id);
    }

    /**
     * @param page      1-based; defaults to 1
     * @param pageSize  records per page; defaults to 10, max 100
     */
    public ReservationPageDto getAll(String search, Long roomId,
                                     String dateFrom, String dateTo,
                                     int page, int pageSize) {
        if (page < 1)       page = 1;
        if (pageSize < 1)   pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        // Validate date formats if provided
        validateDateParam(dateFrom, "dateFrom");
        validateDateParam(dateTo,   "dateTo");

        long total = dao.count(search, roomId, dateFrom, dateTo);
        List<ReservationModel> data = dao.findAll(search, roomId, dateFrom, dateTo, page, pageSize);
        return new ReservationPageDto(data, total, page, pageSize);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void validateRoomExists(Long roomId) {
        if (roomDao.findById(roomId) == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }
    }

    private static void validate(ReservationRequestDto dto) {
        if (dto == null) throw new IllegalArgumentException("Request body is required");
        if (blank(dto.guestName))     throw new IllegalArgumentException("guestName is required");
        if (blank(dto.address))       throw new IllegalArgumentException("address is required");
        if (blank(dto.contactNumber)) throw new IllegalArgumentException("contactNumber is required");
        if (dto.roomId == null)       throw new IllegalArgumentException("roomId is required");
        if (blank(dto.checkIn))       throw new IllegalArgumentException("checkIn is required (yyyy-MM-dd)");
        if (blank(dto.checkOut))      throw new IllegalArgumentException("checkOut is required (yyyy-MM-dd)");

        LocalDate ci = parseDate(dto.checkIn,  "checkIn");
        LocalDate co = parseDate(dto.checkOut, "checkOut");
        if (!co.isAfter(ci)) throw new IllegalArgumentException("checkOut must be after checkIn");
    }

    private static ReservationModel toModel(Long id, ReservationRequestDto dto) {
        return new ReservationModel(
                id,
                dto.guestName.trim(),
                dto.address.trim(),
                dto.contactNumber.trim(),
                dto.roomId,
                null,  // roomName populated by DAO on fetch
                null,  // pricePerNight populated by DAO on fetch
                dto.checkIn.trim(),
                dto.checkOut.trim()
        );
    }

    private static void validateDateParam(String value, String fieldName) {
        if (value == null || value.isBlank()) return;
        parseDate(value, fieldName);
    }

    private static LocalDate parseDate(String value, String fieldName) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid date in yyyy-MM-dd format");
        }
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
