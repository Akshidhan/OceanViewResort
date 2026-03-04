package dto;

import model.RoomModel;

import java.util.List;

public class RoomPageDto {
    private List<RoomModel> data;
    private long total;
    private int page;
    private int pageSize;
    private int totalPages;

    public RoomPageDto(List<RoomModel> data, long total, int page, int pageSize) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public List<RoomModel> getData() { return data; }
    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return totalPages; }
}

