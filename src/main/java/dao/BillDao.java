package dao;

import model.BillModel;
import util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDao {

    // ── CREATE ────────────────────────────────────────────────────────────────
    public BillModel create(BillModel b) {
        String sql = "INSERT INTO bills " +
                     "(reservation_id, guest_name, room_name, price_per_night, check_in, check_out, number_of_nights, total_cost) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, b.getReservationId());
            ps.setString(2, b.getGuestName());
            ps.setString(3, b.getRoomName());
            ps.setDouble(4, b.getPricePerNight());
            ps.setDate(5, Date.valueOf(b.getCheckIn()));
            ps.setDate(6, Date.valueOf(b.getCheckOut()));
            ps.setLong(7, b.getNumberOfNights());
            ps.setDouble(8, b.getTotalCost());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) b.setId(keys.getLong(1));
            }
            return findById(b.getId());
        } catch (SQLException e) {
            throw new RuntimeException("DB error in createBill", e);
        }
    }

    // ── READ ONE ──────────────────────────────────────────────────────────────
    public BillModel findById(long id) {
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in findBillById", e);
        }
    }

    // ── READ ALL (paginated) ──────────────────────────────────────────────────
    public List<BillModel> findAll(Long reservationId, int page, int pageSize) {
        StringBuilder where = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (reservationId != null) {
            where.append(" WHERE reservation_id = ?");
            params.add(reservationId);
        }

        String sql = "SELECT * FROM bills" + where + " ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idx = 1;
            for (Object p : params) {
                if (p instanceof Long) ps.setLong(idx, (Long) p);
                idx++;
            }
            ps.setInt(idx++, pageSize);
            ps.setInt(idx, (page - 1) * pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                List<BillModel> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in findAllBills", e);
        }
    }

    public long count(Long reservationId) {
        String sql = reservationId != null
                ? "SELECT COUNT(*) FROM bills WHERE reservation_id = ?"
                : "SELECT COUNT(*) FROM bills";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (reservationId != null) ps.setLong(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in countBills", e);
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean delete(long id) {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in deleteBill", e);
        }
    }

    // ── Check if bill exists for reservation ─────────────────────────────────
    public boolean existsByReservationId(long reservationId) {
        String sql = "SELECT 1 FROM bills WHERE reservation_id = ? LIMIT 1";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in existsByReservationId", e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static BillModel map(ResultSet rs) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("created_at");
        return new BillModel(
                rs.getLong("id"),
                rs.getLong("reservation_id"),
                rs.getString("guest_name"),
                rs.getString("room_name"),
                rs.getDouble("price_per_night"),
                rs.getDate("check_in").toLocalDate().toString(),
                rs.getDate("check_out").toLocalDate().toString(),
                rs.getLong("number_of_nights"),
                rs.getDouble("total_cost"),
                createdAt != null ? createdAt.toLocalDateTime().toString() : null
        );
    }
}

