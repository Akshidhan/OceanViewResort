package service;

import dao.RoomDao;
import dto.RoomPageDto;
import dto.RoomRequestDto;
import model.RoomModel;

import java.util.List;

public class RoomService {

    private final RoomDao dao;

    public RoomService(RoomDao dao) {
        this.dao = dao;
    }

    public RoomModel create(RoomRequestDto dto) {
        validate(dto);
        RoomModel model = toModel(null, dto);
        return dao.create(model);
    }

    public RoomModel getById(long id) {
        RoomModel r = dao.findById(id);
        if (r == null) throw new IllegalArgumentException("Room not found: " + id);
        return r;
    }

    /**
     * @param search      free-text search on room name (nullable)
     * @param minCapacity minimum capacity filter (nullable)
     * @param page        1-based; defaults to 1
     * @param pageSize    records per page; defaults to 10, max 100
     */
    public RoomPageDto getAll(String search, Integer minCapacity, int page, int pageSize) {
        if (page < 1)       page = 1;
        if (pageSize < 1)   pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        long total = dao.count(search, minCapacity);
        List<RoomModel> data = dao.findAll(search, minCapacity, page, pageSize);
        return new RoomPageDto(data, total, page, pageSize);
    }

    public RoomModel update(long id, RoomRequestDto dto) {
        // ...existing code...
        validate(dto);
        if (dao.findById(id) == null) throw new IllegalArgumentException("Room not found: " + id);
        RoomModel model = toModel(id, dto);
        dao.update(model);
        return model;
    }

    public void delete(long id) {
        if (!dao.delete(id)) throw new IllegalArgumentException("Room not found: " + id);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static void validate(RoomRequestDto dto) {
        if (dto == null) throw new IllegalArgumentException("Request body is required");
        if (blank(dto.name)) throw new IllegalArgumentException("name is required");
        if (dto.pricePerNight == null || dto.pricePerNight <= 0)
            throw new IllegalArgumentException("pricePerNight must be a positive number");
        if (dto.capacity == null || dto.capacity <= 0)
            throw new IllegalArgumentException("capacity must be a positive integer");
    }

    private static RoomModel toModel(Long id, RoomRequestDto dto) {
        return new RoomModel(id, dto.name.trim(), dto.pricePerNight, dto.capacity);
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}


