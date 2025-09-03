package nexxus.shared.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Execute a simple query and return results
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Execute an update statement (INSERT, UPDATE, DELETE)
     */
    public int executeUpdate(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    /**
     * Execute a query with parameters
     */
    public List<Map<String, Object>> executeQueryWithParams(String sql, Object... args) {
        return jdbcTemplate.queryForList(sql, args);
    }

    /**
     * Get a single value from a query
     */
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
        return jdbcTemplate.queryForObject(sql, requiredType, args);
    }

    /**
     * Check if database connection is working
     */
    public boolean testConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get database information
     */
    public String getDatabaseInfo() {
        try {
            String productName = jdbcTemplate.queryForObject(
                "SELECT ?", String.class, "Database connection established successfully"
            );
            return productName;
        } catch (Exception e) {
            return "Error getting database info: " + e.getMessage();
        }
    }
}
