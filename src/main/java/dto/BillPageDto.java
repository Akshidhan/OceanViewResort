package dto;

import model.BillModel;

import java.util.List;

public class BillPageDto {
    private List<BillModel> data;
    private long total;
    private int page;
    private int pageSize;
    private int totalPages;

    public BillPageDto(List<BillModel> data, long total, int page, int pageSize) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    public List<BillModel> getData() { return data; }
    public long getTotal() { return total; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return totalPages; }
}

