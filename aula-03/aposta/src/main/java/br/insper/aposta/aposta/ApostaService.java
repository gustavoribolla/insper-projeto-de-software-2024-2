package br.insper.aposta.aposta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class ApostaService {

    @Autowired
    private ApostaRepository apostaRepository;

    public void salvar(Aposta aposta) {
        aposta.setId(UUID.randomUUID().toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<RetornarPartidaDTO> partida = restTemplate.getForEntity(
                "http://3.85.83.193:8080/partida/" + aposta.getIdPartida(),
                RetornarPartidaDTO.class);

        if (partida.getStatusCode().is2xxSuccessful())  {
            apostaRepository.save(aposta);
        }

    }

    public List<Aposta> listar() {
        return apostaRepository.findAll();
    }

    public Aposta verificarAposta(String idAposta) {
        Aposta aposta = apostaRepository.findById(idAposta)
                .orElseThrow(() -> new IllegalArgumentException("Aposta não encontrada"));

        if ("REALIZADA".equals(aposta.getStatus())) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<RetornarPartidaDTO> partida = restTemplate.getForEntity(
                    "http://3.85.83.193:8080/partida/" + aposta.getIdPartida(),
                    RetornarPartidaDTO.class);

            if (partida.getStatusCode().is2xxSuccessful()) {
                RetornarPartidaDTO partidaDTO = partida.getBody();

                if (partidaDTO == null || partidaDTO.getStatus() == null) {
                    throw new JogoNaoEncontradoException("Jogo com ID " + aposta.getIdPartida() + " não encontrado.");
                }

                if ("FINALIZADA".equals(partidaDTO.getStatus())) {

                    String resultadoJogo;
                    if (partidaDTO.getPlacarMandante() > partidaDTO.getPlacarVisitante()) {
                        resultadoJogo = "VITORIA_MANDANTE";
                    } else if (partidaDTO.getPlacarMandante() < partidaDTO.getPlacarVisitante()) {
                        resultadoJogo = "VITORIA_VISITANTE";
                    } else {
                        resultadoJogo = "EMPATE";
                    }

                    if (resultadoJogo.equals(aposta.getResultado())) {
                        aposta.setStatus("GANHOU");
                    } else {
                        aposta.setStatus("PERDEU");
                    }

                    apostaRepository.save(aposta);
                }
            } else {
                throw new JogoNaoEncontradoException("Jogo com ID " + aposta.getIdPartida() + " não encontrado.");
            }
        }
        return aposta;
    }

    public List<Aposta> listarPorStatus(String status) {
        return apostaRepository.findByStatus(status);
    }
}