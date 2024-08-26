package br.insper.aposta.aposta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aposta")
public class ApostaController {

    @Autowired
    private ApostaService apostaService;

    @GetMapping
    public List<Aposta> listar(@RequestParam(required = false) String status) {
        if (status != null) {
            return apostaService.listarPorStatus(status);
        }
        return apostaService.listar();
    }

    @PostMapping
    public void salvar(@RequestBody Aposta aposta) {
        apostaService.salvar(aposta);
    }

    @GetMapping("/{idAposta}")
    public Aposta verificarAposta(@PathVariable String idAposta) {
        return apostaService.verificarAposta(idAposta);
    }
}