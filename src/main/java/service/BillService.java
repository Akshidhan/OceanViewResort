package service;

import dao.BillDao;
import dao.ReservationDao;
import dto.BillPageDto;
import dto.BillRequestDto;
import model.BillModel;
import model.ReservationModel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BillService {

    private final BillDao billDao;
    private final ReservationDao reservationDao;

    public BillService(BillDao billDao, ReservationDao reservationDao) {
        this.billDao = billDao;
        this.reservationDao = reservationDao;
    }

    /**
     * Creates a bill for the given reservation.
     * Each reservation can only have one bill.
     */
    public BillModel create(BillRequestDto dto) {
        if (dto == null || dto.reservationId == null) {
            throw new IllegalArgumentException("reservationId is required");
        }

        // Ensure the reservation exists
        ReservationModel reservation = reservationDao.findById(dto.reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + dto.reservationId);
        }

        // Prevent duplicate bills for the same reservation
        if (billDao.existsByReservationId(dto.reservationId)) {
            throw new IllegalArgumentException("A bill already exists for reservation: " + dto.reservationId);
        }

        LocalDate checkIn  = LocalDate.parse(reservation.getCheckIn());
        LocalDate checkOut = LocalDate.parse(reservation.getCheckOut());
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        if (nights <= 0) {
            throw new IllegalArgumentException("Invalid reservation dates: checkOut must be after checkIn");
        }

        double pricePerNight = reservation.getPricePerNight() != null ? reservation.getPricePerNight() : 0.0;
        double totalCost = nights * pricePerNight;

        BillModel bill = new BillModel(
                null,
                reservation.getId(),
                reservation.getGuestName(),
                reservation.getRoomName(),
                pricePerNight,
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                nights,
                totalCost,
                null  // set by DB
        );

        return billDao.create(bill);
    }

    public BillModel getById(long id) {
        BillModel bill = billDao.findById(id);
        if (bill == null) throw new IllegalArgumentException("Bill not found: " + id);
        return bill;
    }

    /**
     * Lists all bills, optionally filtered by reservationId, with pagination.
     *
     * @param reservationId optional filter (nullable)
     * @param page          1-based page number
     * @param pageSize      records per page (max 100)
     */
    public BillPageDto getAll(Long reservationId, int page, int pageSize) {
        if (page < 1)       page = 1;
        if (pageSize < 1)   pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        long total = billDao.count(reservationId);
        List<BillModel> data = billDao.findAll(reservationId, page, pageSize);
        return new BillPageDto(data, total, page, pageSize);
    }

    public void delete(long id) {
        if (!billDao.delete(id)) throw new IllegalArgumentException("Bill not found: " + id);
    }
}

