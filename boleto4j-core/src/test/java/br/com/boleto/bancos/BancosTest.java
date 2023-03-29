package br.com.boleto.bancos;

import br.com.boleto.Banco;
import br.com.boleto.Beneficiario;
import br.com.boleto.exception.BancoNaoSuportadoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BancosTest {

    private Beneficiario beneficiario = Beneficiario.novoBeneficiario();

    @Test
    public void deveRetornarOBancoBaseadoNoNumero() throws Exception {
        Banco brasil = Bancos.getPorNumero("001");
        Banco itau = Bancos.getPorNumero("341");

        assertNotNull(brasil);
        assertNotNull(itau);
    }

    @Test
    public void deveLancarExceptionSeOBancoNaoEhSuportado() {
        Assertions.assertThrows(BancoNaoSuportadoException.class, () -> Bancos.getPorNumero("9999"));
    }

    @Test
    public void obterAgenciaECodigoBeneficiarioFormatadoSemDV() throws Exception {
        Banco banco = new BancoDoBrasil();

        beneficiario.comAgencia("1234").comDigitoAgencia(null)
                .comCodigoBeneficiario("12345678").comDigitoCodigoBeneficiario(null);

        assertEquals(banco.getAgenciaECodigoBeneficiario(beneficiario), "1234/12345678");
    }

    @Test
    public void obterAgenciaECodigoBeneficiarioFormatado() throws Exception {
        Banco banco = new BancoDoBrasil();

        beneficiario.comAgencia("1234").comDigitoAgencia("3")
                .comCodigoBeneficiario("12345678").comDigitoCodigoBeneficiario("9");

        assertEquals(banco.getAgenciaECodigoBeneficiario(beneficiario), "1234-3/12345678-9");
    }

}
