package integration.services;

public interface ServiceITTemplate {
    void anyPath_NoAccessToken_Returns401();
    void anyPath_PathDoesntExists_Returns404();
}
