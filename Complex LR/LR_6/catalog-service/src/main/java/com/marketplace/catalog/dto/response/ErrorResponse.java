package com.marketplace.catalog.dto.response;
import java.time.LocalDateTime;
public class ErrorResponse {
    private LocalDateTime timestamp; private int status; private String message; private String path;
    public ErrorResponse() {}
    public ErrorResponse(LocalDateTime timestamp, int status, String message, String path) { this.timestamp = timestamp; this.status = status; this.message = message; this.path = path; }
    public LocalDateTime getTimestamp() { return timestamp; } public void setTimestamp(LocalDateTime t) { this.timestamp = t; }
    public int getStatus() { return status; } public void setStatus(int s) { this.status = s; }
    public String getMessage() { return message; } public void setMessage(String m) { this.message = m; }
    public String getPath() { return path; } public void setPath(String p) { this.path = p; }
}
