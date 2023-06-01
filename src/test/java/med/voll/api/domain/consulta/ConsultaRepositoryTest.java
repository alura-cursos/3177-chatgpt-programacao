package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.dto.DadosRelatorioConsultaMensal;
import med.voll.api.domain.endereco.dto.DadosEndereco;
import med.voll.api.domain.medico.Especialidade;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.dto.DadosCadastroMedico;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.dto.DadosCadastroPaciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ConsultaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConsultaRepository consultaRepository;

    @BeforeEach
    public void setup() {
        // Criar dados de exemplo
        DadosEndereco enderecoMedico1 = new DadosEndereco(
                "Rua A",
                "Bairro X",
                "12345678",
                "Cidade X",
                "UF",
                null,
                null
        );

        DadosEndereco enderecoMedico2 = new DadosEndereco(
                "Rua B",
                "Bairro Y",
                "87654321",
                "Cidade Y",
                "UF",
                null,
                null
        );

        DadosEndereco enderecoPaciente1 = new DadosEndereco(
                "Rua A",
                "Bairro X",
                "12345678",
                "Cidade X",
                "UF",
                null,
                null
        );

        DadosEndereco enderecoPaciente2 = new DadosEndereco(
                "Rua B",
                "Bairro Y",
                "87654321",
                "Cidade Y",
                "UF",
                null,
                null
        );

        DadosCadastroMedico dadosMedico1 = new DadosCadastroMedico(
                "Dr. João",
                "joao@example.com",
                "123456789",
                "CRM123",
                Especialidade.CARDIOLOGIA,
                enderecoMedico1
        );

        DadosCadastroMedico dadosMedico2 = new DadosCadastroMedico(
                "Dr. Maria",
                "maria@example.com",
                "987654321",
                "CRM456",
                Especialidade.DERMATOLOGIA,
                enderecoMedico2
        );

        DadosCadastroPaciente dadosPaciente1 = new DadosCadastroPaciente(
                "João",
                "joao@example.com",
                "123456789",
                "123.456.789-00",
                enderecoPaciente1
        );

        DadosCadastroPaciente dadosPaciente2 = new DadosCadastroPaciente(
                "Maria",
                "maria@example.com",
                "987654321",
                "987.654.321-00",
                enderecoPaciente2
        );

        Medico medico1 = new Medico(dadosMedico1);
        Medico medico2 = new Medico(dadosMedico2);
        Paciente paciente1 = new Paciente(dadosPaciente1);
        Paciente paciente2 = new Paciente(dadosPaciente2);

        entityManager.persist(medico1);
        entityManager.persist(medico2);
        entityManager.persist(paciente1);
        entityManager.persist(paciente2);

        Consulta consulta1 = new Consulta(medico1, paciente1, LocalDateTime.now());
        Consulta consulta2 = new Consulta(medico1, paciente2, LocalDateTime.now());
        Consulta consulta3 = new Consulta(medico2, paciente1, LocalDateTime.now().plusDays(1));
        Consulta consulta4 = new Consulta(medico2, paciente2, LocalDateTime.now().plusDays(2));

        entityManager.persist(consulta1);
        entityManager.persist(consulta2);
        entityManager.persist(consulta3);
        entityManager.persist(consulta4);

        entityManager.flush();
    }

    @Test
    public void gerarRelatorioConsultaMensal_DeveRetornarRelatorioConsultasPorMedico() {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fimMes = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);

        List<DadosRelatorioConsultaMensal> relatorio = consultaRepository.gerarRelatorioConsultaMensal(inicioMes, fimMes);

        assertEquals(2, relatorio.size());

        DadosRelatorioConsultaMensal relatorioMedico1 = relatorio.stream()
                .filter(dados -> dados.crm().equals("CRM123"))
                .findFirst()
                .orElse(null);

        DadosRelatorioConsultaMensal relatorioMedico2 = relatorio.stream()
                .filter(dados -> dados.crm().equals("CRM456"))
                .findFirst()
                .orElse(null);

        assertEquals("Dr. João", relatorioMedico1.nome());
        assertEquals(2, relatorioMedico1.quantidadeConsultasNoMes());

        assertEquals("Dr. Maria", relatorioMedico2.nome());
        assertEquals(2, relatorioMedico2.quantidadeConsultasNoMes());
    }
}