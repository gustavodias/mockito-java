package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


class GeradorDePagamentoTest {

    @Mock
    private PagamentoDao pagamentoDao;

    private GeradorDePagamento service;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @Mock
    private Clock clock;

    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.initMocks(this);
        this.service= new GeradorDePagamento(pagamentoDao, clock);
    }

    @Test
    public void deveriaCriarPagamentoParaVnecedorDoLeilao() {
        Leilao leilao = leiloes();
        Lance lanceVencedor = leilao.getLanceVencedor();


        LocalDate data = LocalDate.of(2021, 12, 30);

        Instant instant  = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        service.gerarPagamento(lanceVencedor);
        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento =captor.getValue();
        Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
        Assert.assertFalse(pagamento.getPago());
        Assert.assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        Assert.assertEquals(leilao, pagamento.getLeilao());
    }

    private Leilao leiloes() {

        Leilao leilao = new Leilao("Celular",
                new BigDecimal("500"),
                new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"),
                new BigDecimal("900"));

        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);
        return leilao;
    }
}