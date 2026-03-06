package dao;

import model.userModel;
import util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao implements IUserDao {
    public userModel findByUsername(String username) {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    if(!rs.next()) return null;

                    return new userModel(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password_hash")
                    );
                }
        } catch (Exception e) {
            throw new RuntimeException("DB error in findUserByUsername", e);
        }
    }
}
