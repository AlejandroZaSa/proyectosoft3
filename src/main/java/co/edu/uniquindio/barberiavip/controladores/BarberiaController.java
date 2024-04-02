package co.edu.uniquindio.barberiavip.controladores;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BarberiaController {

    @GetMapping
    public String funcionar(){
        return "Hola";
    }

}
