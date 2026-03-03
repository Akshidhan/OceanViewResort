package dto;

import model.ReservationModel;

import java.util.List;

public class ReservationPageDto {
    private List<ReservationModel> data;
    private long total;
    private int page;
    private int pageSize;
    private int totalPages;

    public ReservationPageDto(List<ReservationModel> data, long total, int page, int pageSize) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public List<ReservationModel> getData() { return data; }
    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return totalPages; }
}
