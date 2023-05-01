package integration;

public interface FilesEndpointsITTemplate {

    void getFile_Exists_Returns200();

    void getFile_DoesntExists_Returns404();
}
