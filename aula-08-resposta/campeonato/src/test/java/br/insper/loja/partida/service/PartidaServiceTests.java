package br.insper.loja.partida.service;

import br.insper.loja.partida.dto.EditarPartidaDTO;
import br.insper.loja.partida.dto.RetornarPartidaDTO;
import br.insper.loja.partida.dto.SalvarPartidaDTO;
import br.insper.loja.partida.exception.PartidaNaoEncontradaException;
import br.insper.loja.partida.model.Partida;
import br.insper.loja.partida.repository.PartidaRepository;
import br.insper.loja.time.model.Time;
import br.insper.loja.time.service.TimeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PartidaServiceTests {

    @Autowired
    private PartidaService partidaService;

    @MockBean
    private PartidaRepository partidaRepository;

    @MockBean
    private TimeService timeService;

    @Test
    public void testCadastrarPartida() {
        SalvarPartidaDTO salvarPartidaDTO = new SalvarPartidaDTO();
        salvarPartidaDTO.setMandante(1);
        salvarPartidaDTO.setVisitante(2);

        Time mandante = new Time();
        mandante.setNome("Mandante");

        Time visitante = new Time();
        visitante.setNome("Visitante");

        Mockito.when(timeService.getTime(1)).thenReturn(mandante);
        Mockito.when(timeService.getTime(2)).thenReturn(visitante);

        Partida partida = new Partida();
        partida.setMandante(mandante);
        partida.setVisitante(visitante);
        partida.setStatus("AGENDADA");

        Mockito.when(partidaRepository.save(Mockito.any(Partida.class))).thenReturn(partida);

        RetornarPartidaDTO resultado = partidaService.cadastrarPartida(salvarPartidaDTO);

        assertNotNull(resultado);
        assertEquals("Mandante", resultado.getNomeMandante());
        assertEquals("Visitante", resultado.getNomeVisitante());
        assertEquals("AGENDADA", resultado.getStatus());
    }

    @Test
    public void testListarPartidas() {
        List<Partida> partidas = new ArrayList<>();

        Time mandante = new Time();
        mandante.setNome("Mandante");
        Time visitante = new Time();
        visitante.setNome("Visitante");

        Partida partida1 = new Partida();
        partida1.setMandante(mandante);
        partida1.setVisitante(visitante);
        partida1.setPlacarMandante(1);
        partida1.setPlacarVisitante(2);
        partida1.setStatus("REALIZADA");

        partidas.add(partida1);

        Mockito.when(partidaRepository.findAll()).thenReturn(partidas);

        List<RetornarPartidaDTO> resultado = partidaService.listarPartidas(null);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Mandante", resultado.get(0).getNomeMandante());
        assertEquals("Visitante", resultado.get(0).getNomeVisitante());
    }

    @Test
    public void testListarPartidasFiltradasPorMandante() {
        List<Partida> partidas = new ArrayList<>();

        Time mandante = new Time();
        mandante.setIdentificador("1");
        mandante.setNome("Mandante");

        Time visitante = new Time();
        visitante.setIdentificador("2");
        visitante.setNome("Visitante");

        Partida partida1 = new Partida();
        partida1.setMandante(mandante);
        partida1.setVisitante(visitante);
        partida1.setPlacarMandante(1);
        partida1.setPlacarVisitante(2);
        partida1.setStatus("REALIZADA");

        partidas.add(partida1);

        Mockito.when(partidaRepository.findAll()).thenReturn(partidas);

        List<RetornarPartidaDTO> resultado = partidaService.listarPartidas("1");

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Mandante", resultado.get(0).getNomeMandante());
        assertEquals("Visitante", resultado.get(0).getNomeVisitante());
    }

    @Test
    public void testListarPartidasFiltradasPorMandanteInvalido() {
        List<Partida> partidas = new ArrayList<>();

        Time mandante = new Time();
        mandante.setNome("Mandante");
        mandante.setIdentificador("1");

        Time visitante = new Time();
        visitante.setNome("Visitante");
        visitante.setIdentificador("2");

        Partida partida1 = new Partida();
        partida1.setMandante(mandante);
        partida1.setVisitante(visitante);
        partida1.setPlacarMandante(1);
        partida1.setPlacarVisitante(2);
        partida1.setStatus("REALIZADA");

        partidas.add(partida1);

        Mockito.when(partidaRepository.findAll()).thenReturn(partidas);

        List<RetornarPartidaDTO> resultado = partidaService.listarPartidas("999");

        assertTrue(resultado.isEmpty());
    }


    @Test
    public void testEditarPartida() {
        EditarPartidaDTO editarPartidaDTO = new EditarPartidaDTO();
        editarPartidaDTO.setPlacarMandante(3);
        editarPartidaDTO.setPlacarVisitante(4);

        Time mandante = new Time();
        mandante.setNome("Mandante");

        Time visitante = new Time();
        visitante.setNome("Visitante");

        Partida partida = new Partida();
        partida.setId(1);
        partida.setMandante(mandante);
        partida.setVisitante(visitante);
        partida.setPlacarMandante(1);
        partida.setPlacarVisitante(2);
        partida.setStatus("AGENDADA");

        Mockito.when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        Mockito.when(partidaRepository.save(Mockito.any(Partida.class))).thenAnswer(invocation -> {
            Partida updatedPartida = invocation.getArgument(0);
            partida.setPlacarMandante(updatedPartida.getPlacarMandante());
            partida.setPlacarVisitante(updatedPartida.getPlacarVisitante());
            partida.setStatus("REALIZADA");
            return partida;
        });

        RetornarPartidaDTO resultado = partidaService.editarPartida(editarPartidaDTO, 1);

        assertNotNull(resultado);
        assertEquals(3, resultado.getPlacarMandante());
        assertEquals(4, resultado.getPlacarVisitante());
        assertEquals("REALIZADA", resultado.getStatus());
        assertEquals("Mandante", resultado.getNomeMandante());
        assertEquals("Visitante", resultado.getNomeVisitante());
    }

    @Test
    public void testGetPartida() {
        Partida partida = new Partida();
        partida.setId(1);
        partida.setPlacarMandante(1);
        partida.setPlacarVisitante(2);
        partida.setStatus("REALIZADA");

        Time mandante = new Time();
        mandante.setNome("Mandante");

        Time visitante = new Time();
        visitante.setNome("Visitante");

        partida.setMandante(mandante);
        partida.setVisitante(visitante);

        Mockito.when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));

        RetornarPartidaDTO resultado = partidaService.getPartida(1);

        assertNotNull(resultado);
        assertEquals("Mandante", resultado.getNomeMandante());
        assertEquals("Visitante", resultado.getNomeVisitante());
        assertEquals(1, resultado.getPlacarMandante());
        assertEquals(2, resultado.getPlacarVisitante());
        assertEquals("REALIZADA", resultado.getStatus());
    }

    @Test
    public void testGetPartidaWhenNotFound() {
        Mockito.when(partidaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(PartidaNaoEncontradaException.class, () -> partidaService.getPartida(999));
    }
}