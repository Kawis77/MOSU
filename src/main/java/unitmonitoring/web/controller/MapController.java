package unitmonitoring.web.controller;


import org.springframework.web.bind.annotation.GetMapping;

public class MapController {

    @GetMapping("/")
    public String getMap(){
        return "";
    }
}
