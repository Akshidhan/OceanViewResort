package dto;

public class UserSessionDto {
    private Long id;
    private String username;

    public UserSessionDto(long id, String username) {
        this.id = id;
        this.username = username;
    }
}
