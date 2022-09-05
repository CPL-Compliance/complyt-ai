package io.complyt.testService;

import lombok.Generated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Generated
@RestController
public class Controller {
    // temp
    
    @GetMapping("/")
    public String hi(){
        return "hi :)";
    }
}
