package med.voll.api.domain.consulta.validacoes.agendamento;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.dto.DadosAgendamentoConsulta;
import med.voll.api.domain.medico.Especialidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorHorarioFuncionamentoClinicaTest {

    @InjectMocks
    private ValidadorHorarioFuncionamentoClinica validador;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void validar_ConsultaDentroDoHorario_NaoDeveLancarExcecao() {
        // Cenário
        DadosAgendamentoConsulta dados = criarDadosAgendamentoConsulta(LocalDateTime.of(2023, 6, 1, 9, 0)); // Segunda-feira, 9:00

        // Execução
        validador.validar(dados);

        // Verificação
        // Nenhuma exceção deve ser lançada
    }

    @Test
    void validar_ConsultaForaDoHorario_LancaValidacaoException() {
        // Cenário
        DadosAgendamentoConsulta dados = criarDadosAgendamentoConsulta(LocalDateTime.of(2023, 6, 1, 19, 0)); // Segunda-feira, 19:00

        // Execução e Verificação
        assertThrows(ValidacaoException.class, () -> validador.validar(dados));
    }

    @Test
    void validar_ConsultaNoDomingo_LancaValidacaoException() {
        // Cenário
        DadosAgendamentoConsulta dados = criarDadosAgendamentoConsulta(LocalDateTime.of(2023, 6, 4, 10, 0)); // Domingo, 10:00

        // Execução e Verificação
        assertThrows(ValidacaoException.class, () -> validador.validar(dados));
    }

    @Test
    void validar_ConsultaNoHorarioDeAbertura_NaoDeveLancarExcecao() {
        DadosAgendamentoConsulta dados = criarDadosAgendamentoConsulta(LocalDateTime.of(2023, 6, 1, 7, 0)); // Segunda-feira, 7:00

        assertDoesNotThrow(() -> validador.validar(dados));
    }

    @Test
    void validar_ConsultaNoHorarioDeEncerramento_NaoDeveLancarExcecao() {
        DadosAgendamentoConsulta dados = criarDadosAgendamentoConsulta(LocalDateTime.of(2023, 6, 1, 18, 0)); // Segunda-feira, 18:00

        assertDoesNotThrow(() -> validador.validar(dados));
    }

    @Test
    void validar_ConsultaEmDiaDeSemana_NaoDeveLancarExcecao() {
        DadosAgendamentoConsulta dados = criarDadosAgendamentoConsulta(LocalDateTime.of(2023, 6, 5, 10, 0)); // Segunda-feira, 10:00

        assertDoesNotThrow(() -> validador.validar(dados));
    }

    @Test
    void validar_ConsultaAntesDoHorarioDeAberturaEmDiaDeSemana_LancaValidacaoException() {
        DadosAgendamentoConsulta dados = criarDadosAgendamentoConsulta(LocalDateTime.of(2023, 6, 1, 6, 59)); // Segunda-feira, 6:59

        assertThrows(ValidacaoException.class, () -> validador.validar(dados));
    }

    @Test
    void validar_ConsultaDepoisDoHorarioDeEncerramentoEmDiaDeSemana_LancaValidacaoException() {
        DadosAgendamentoConsulta dados = criarDadosAgendamentoConsulta(LocalDateTime.of(2023, 6, 1, 18, 1)); // Segunda-feira, 18:01

        assertThrows(ValidacaoException.class, () -> validador.validar(dados));
    }


    private DadosAgendamentoConsulta criarDadosAgendamentoConsulta(LocalDateTime data) {
        Long idMedico = 1L; // ID do médico desejado
        Long idPaciente = 2L; // ID do paciente desejado
        Especialidade especialidade = Especialidade.CARDIOLOGIA; // Especialidade desejada

        return new DadosAgendamentoConsulta(idMedico, idPaciente, data, especialidade);
    }

}