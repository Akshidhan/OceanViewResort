package dao;

import model.ReservationModel;
import util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {

    // ── CREATE ────────────────────────────────────────────────────────────────
    public ReservationModel create(ReservationModel r) {
        String sql = "INSERT INTO reservations (guest_name, address, contact_number, room_id, check_in, check_out) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getGuestName());
            ps.setString(2, r.getAddress());
            ps.setString(3, r.getContactNumber());
            ps.setLong(4, r.getRoomId());
            ps.setDate(5, Date.valueOf(r.getCheckIn()));
            ps.setDate(6, Date.valueOf(r.getCheckOut()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }
            // Re-fetch to populate room name and price
            return findById(r.getId());
        } catch (SQLException e) {
            throw new RuntimeException("DB error in createReservation", e);
        }
    }

    // ── READ ONE ──────────────────────────────────────────────────────────────
    public ReservationModel findById(long id) {
        String sql = "SELECT r.*, rm.name AS room_name, rm.price_per_night " +
                     "FROM reservations r " +
                     "JOIN rooms rm ON rm.id = r.room_id " +
                     "WHERE r.id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in findReservationById", e);
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean update(ReservationModel r) {
        String sql = "UPDATE reservations SET guest_name=?, address=?, contact_number=?, " +
                     "room_id=?, check_in=?, check_out=? WHERE id=?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getGuestName());
            ps.setString(2, r.getAddress());
            ps.setString(3, r.getContactNumber());
            ps.setLong(4, r.getRoomId());
            ps.setDate(5, Date.valueOf(r.getCheckIn()));
            ps.setDate(6, Date.valueOf(r.getCheckOut()));
            ps.setLong(7, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in updateReservation", e);
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean delete(long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in deleteReservation", e);
        }
    }

    // ── LIST (filtered + paginated) ────────────────────────────────────────────
    /**
     * @param search     free-text search across guest_name, address, contact_number (nullable)
     * @param roomId     exact room id filter (nullable)
     * @param dateFrom   check_in >= dateFrom (nullable)
     * @param dateTo     check_in <= dateTo   (nullable)
     * @param page       1-based page number
     * @param pageSize   number of records per page
     */
    public List<ReservationModel> findAll(String search, Long roomId,
                                          String dateFrom, String dateTo,
                                          int page, int pageSize) {
        QueryParts qp = buildWhere(search, roomId, dateFrom, dateTo);
        String sql = "SELECT r.*, rm.name AS room_name, rm.price_per_night " +
                     "FROM reservations r " +
                     "JOIN rooms rm ON rm.id = r.room_id" +
                     qp.where +
                     " ORDER BY r.check_in DESC LIMIT ? OFFSET ?";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idx = qp.bind(ps, 1);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx,   (page - 1) * pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                List<ReservationModel> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in findAllReservations", e);
        }
    }

    public long count(String search, Long roomId, String dateFrom, String dateTo) {
        QueryParts qp = buildWhere(search, roomId, dateFrom, dateTo);
        String sql = "SELECT COUNT(*) FROM reservations r " +
                     "JOIN rooms rm ON rm.id = r.room_id" +
                     qp.where;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            qp.bind(ps, 1);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in countReservations", e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static ReservationModel map(ResultSet rs) throws SQLException {
        return new ReservationModel(
                rs.getLong("id"),
                rs.getString("guest_name"),
                rs.getString("address"),
                rs.getString("contact_number"),
                rs.getLong("room_id"),
                rs.getString("room_name"),
                rs.getDouble("price_per_night"),
                rs.getDate("check_in").toLocalDate().toString(),
                rs.getDate("check_out").toLocalDate().toString()
        );
    }

    /** Builds the WHERE clause and captures how to bind parameters. */
    private static QueryParts buildWhere(String search, Long roomId,
                                         String dateFrom, String dateTo) {
        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            String like = "%" + search.trim() + "%";
            sb.append(" AND (r.guest_name LIKE ? OR r.address LIKE ? OR r.contact_number LIKE ?)");
            params.add(like); params.add(like); params.add(like);
        }
        if (roomId != null) {
            sb.append(" AND r.room_id = ?");
            params.add(roomId);
        }
        if (dateFrom != null && !dateFrom.isBlank()) {
            sb.append(" AND r.check_in >= ?");
            params.add(Date.valueOf(dateFrom.trim()));
        }
        if (dateTo != null && !dateTo.isBlank()) {
            sb.append(" AND r.check_in <= ?");
            params.add(Date.valueOf(dateTo.trim()));
        }

        String where = params.isEmpty() ? "" : " WHERE" + sb.substring(4); // replace first " AND"
        return new QueryParts(where, params);
    }

    private static class QueryParts {
        final String where;
        final List<Object> params;

        QueryParts(String where, List<Object> params) {
            this.where = where;
            this.params = params;
        }

        int bind(PreparedStatement ps, int startIdx) throws SQLException {
            int i = startIdx;
            for (Object p : params) {
                if (p instanceof String)    ps.setString(i, (String) p);
                else if (p instanceof Date) ps.setDate(i, (Date) p);
                else if (p instanceof Long) ps.setLong(i, (Long) p);
                i++;
            }
            return i;
        }
    }
}

