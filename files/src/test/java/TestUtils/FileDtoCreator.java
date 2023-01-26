package TestUtils;

import io.complyt.files.v1.models.FileDto;

public class FileDtoCreator {

    public static FileDto create(){
        return new FileDto("https://www.example.example.com/example.txt");
    }
}
