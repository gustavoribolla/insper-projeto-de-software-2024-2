package br.insper.loja.time.service;

import br.insper.loja.time.exception.TimeNaoEncontradoException;
import br.insper.loja.time.model.Time;
import br.insper.loja.time.repository.TimeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TimeServiceTests {


    @InjectMocks
    private TimeService timeService;

    @Mock
    private TimeRepository timeRepository;

    @Test
    public void testListarTimesWhenEstadoIsNull() {

        // preparacao
        Mockito.when(timeRepository.findAll()).thenReturn(new ArrayList<>());

        // chamada do codigo testado
        List<Time> times = timeService.listarTimes(null);

        // verificacao dos resultados
        Assertions.assertTrue(times.isEmpty());
    }

    @Test
    public void testListarTimesWhenEstadoIsNotNull() {

        // preparacao
        List<Time> lista = new ArrayList<>();

        Time time = new Time();
        time.setEstado("SP");
        time.setIdentificador("time-1");
        lista.add(time);

        Mockito.when(timeRepository.findByEstado(Mockito.anyString())).thenReturn(lista);

        // chamada do codigo testado
        List<Time> times = timeService.listarTimes("SP");

        // verificacao dos resultados
        Assertions.assertTrue(times.size() == 1);
        Assertions.assertEquals("SP", times.getFirst().getEstado());
        Assertions.assertEquals("time-1", times.getFirst().getIdentificador());
    }

    @Test
    public void testGetTimeWhenTimeIsNotNull() {

        Time time = new Time();
        time.setEstado("SP");
        time.setIdentificador("time-1");

        Mockito.when(timeRepository.findById(1)).thenReturn(Optional.of(time));

        Time timeRetorno = timeService.getTime(1);

        Assertions.assertNotNull(timeRetorno);
        Assertions.assertEquals("SP", timeRetorno.getEstado());
        Assertions.assertEquals("time-1", timeRetorno.getIdentificador());

    }

    @Test
    public void testGetTimeWhenTimeIsNull() {

        Mockito.when(timeRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(TimeNaoEncontradoException.class,
                () -> timeService.getTime(1));

    }

    @Test
    public void testCadastrarTimeWithValidData() {
        Time time = new Time("Time A", "time-a", "Estádio A", "SP");

        Mockito.when(timeRepository.save(time)).thenReturn(time);

        Time timeSalvo = timeService.cadastrarTime(time);

        Assertions.assertNotNull(timeSalvo);
        Assertions.assertEquals("Time A", timeSalvo.getNome());
        Assertions.assertEquals("time-a", timeSalvo.getIdentificador());
    }

    @Test
    public void testCadastrarTimeWithInvalidData() {
        Time time = new Time("", "time-a", "Estádio A", "SP");

        Assertions.assertThrows(RuntimeException.class,
                () -> timeService.cadastrarTime(time));
    }

    @Test
    public void testListarTimesWhenEstadoHasNoTeams() {
        Mockito.when(timeRepository.findByEstado("RJ")).thenReturn(new ArrayList<>());

        List<Time> times = timeService.listarTimes("RJ");

        Assertions.assertTrue(times.isEmpty());
    }

    @Test
    public void testListarTimesWhenEstadoHasMultipleTeams() {
        List<Time> lista = new ArrayList<>();

        Time time1 = new Time("Time A", "time-a", "Estádio A", "SP");
        Time time2 = new Time("Time B", "time-b", "Estádio B", "SP");

        lista.add(time1);
        lista.add(time2);

        Mockito.when(timeRepository.findByEstado("SP")).thenReturn(lista);

        List<Time> times = timeService.listarTimes("SP");

        Assertions.assertEquals(2, times.size());
        Assertions.assertEquals("Time A", times.get(0).getNome());
        Assertions.assertEquals("Time B", times.get(1).getNome());
    }

    @Test
    public void testGetTimeWithInvalidId() {
        Mockito.when(timeRepository.findById(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(TimeNaoEncontradoException.class,
                () -> timeService.getTime(999));
    }

    @Test
    public void testCadastrarTimeWithEmptyIdentificador() {
        Time time = new Time("Time B", "", "Estádio B", "SP");

        Assertions.assertThrows(RuntimeException.class,
                () -> timeService.cadastrarTime(time));
    }

    @Test
    public void testCadastrarTimeWithDuplicateIdentificador() {
        Time time1 = new Time("Time A", "time-a", "Estádio A", "SP");

        Time time2 = new Time("Time B", "time-a", "Estádio B", "SP");

        // Stubbing importante: simulando uma exceção para identificadores duplicados
        Mockito.when(timeRepository.save(time2))
                .thenThrow(new RuntimeException("Identificador já existe"));

        // Verificação: esperando que uma exceção seja lançada ao tentar cadastrar um time com identificador duplicado
        Assertions.assertThrows(RuntimeException.class,
                () -> timeService.cadastrarTime(time2));
    }
}