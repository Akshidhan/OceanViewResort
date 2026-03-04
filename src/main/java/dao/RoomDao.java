package dao;

import model.RoomModel;
import util.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDao {

    // ── CREATE ────────────────────────────────────────────────────────────────
    public RoomModel create(RoomModel r) {
        String sql = "INSERT INTO rooms (name, price_per_night, capacity) VALUES (?, ?, ?)";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getName());
            ps.setDouble(2, r.getPricePerNight());
            ps.setInt(3, r.getCapacity());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }
            return r;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in createRoom", e);
        }
    }

    // ── READ ONE ──────────────────────────────────────────────────────────────
    public RoomModel findById(long id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in findRoomById", e);
        }
    }

    // ── READ ALL (filtered + paginated) ──────────────────────────────────────
    /**
     * @param search    free-text search across name (nullable)
     * @param minCapacity minimum capacity filter (nullable)
     * @param page      1-based page number
     * @param pageSize  records per page
     */
    public List<RoomModel> findAll(String search, Integer minCapacity, int page, int pageSize) {
        QueryParts qp = buildWhere(search, minCapacity);
        String sql = "SELECT * FROM rooms" + qp.where + " ORDER BY id LIMIT ? OFFSET ?";

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int idx = qp.bind(ps, 1);
            ps.setInt(idx++, pageSize);
            ps.setInt(idx, (page - 1) * pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                List<RoomModel> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in findAllRooms", e);
        }
    }

    public long count(String search, Integer minCapacity) {
        QueryParts qp = buildWhere(search, minCapacity);
        String sql = "SELECT COUNT(*) FROM rooms" + qp.where;

        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            qp.bind(ps, 1);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in countRooms", e);
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean update(RoomModel r) {
        String sql = "UPDATE rooms SET name=?, price_per_night=?, capacity=? WHERE id=?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getName());
            ps.setDouble(2, r.getPricePerNight());
            ps.setInt(3, r.getCapacity());
            ps.setLong(4, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in updateRoom", e);
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean delete(long id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in deleteRoom", e);
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private static RoomModel map(ResultSet rs) throws SQLException {
        return new RoomModel(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getDouble("price_per_night"),
                rs.getInt("capacity")
        );
    }

    /** Builds the WHERE clause and captures how to bind parameters. */
    private static QueryParts buildWhere(String search, Integer minCapacity) {
        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sb.append(" AND name LIKE ?");
            params.add("%" + search.trim() + "%");
        }
        if (minCapacity != null && minCapacity > 0) {
            sb.append(" AND capacity >= ?");
            params.add(minCapacity);
        }

        String where = params.isEmpty() ? "" : " WHERE" + sb.substring(4);
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
                if (p instanceof String)  ps.setString(i, (String) p);
                else if (p instanceof Integer) ps.setInt(i, (Integer) p);
                i++;
            }
            return i;
        }
    }
}



