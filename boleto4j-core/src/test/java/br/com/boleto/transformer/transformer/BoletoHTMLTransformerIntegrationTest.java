package br.com.boleto.transformer.transformer;

import br.com.boleto.*;
import br.com.boleto.bancos.BancoDoBrasil;
import br.com.boleto.transformer.GeradorDeBoletoHTML;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test para testar o boleto html, deixei em um teste separado pois ainda não
 * foi colocado no BoletoGenerator.
 * 
 * @author Alberto
 * 
 */
public class BoletoHTMLTransformerIntegrationTest{
	
	private static final String FILE_NAME = "arquivo.html";
	
	
	@AfterEach
	public void removeFiles(){
		delete(new File(FILE_NAME));
		delete(new File(FILE_NAME + "_files"));
	}
	
	private boolean delete(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = delete(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return file.delete();
	}
	
	@BeforeEach
	public void setUp() throws Exception {

		Locale.setDefault(new Locale("pt", "br"));

		Boleto boleto;
		Datas datas = Datas.novasDatas().comDocumento(4, 5, 2008).comProcessamento(4, 5, 2008)
				.comVencimento(2, 5, 2008);
		Beneficiario beneficiario = Beneficiario.novoBeneficiario()
				.comNomeBeneficiario("Caue").comAgencia("1824").comDigitoAgencia("4")
				.comCodigoBeneficiario("76000").comNumeroConvenio("1207113")
				.comDigitoCodigoBeneficiario("5").comCarteira("18")
				.comNossoNumero("12071130009000206");

		Endereco endereco = new Endereco("Av dos testes, 111 apto 333", "Bairro Teste", "01234-111", "São Paulo", "SP");

		Pagador pagador = Pagador.novoPagador().comNome("Fulano da Silva").comDocumento("111.222.333-12")
				.comEndereco(endereco);

		String[] descricoes = { "descricao 1", "descricao 2", "descricao 3", "descricao 4", "descricao 5" };

		String[] locaisDePagamento = { "local 1", "local 2" };

		String[] instrucoes = { "instrucao 1", "instrucao 2", "instrucao 3", "instrucao 4", "instrucao 5" };

		Banco banco = new BancoDoBrasil();

		boleto = Boleto.novoBoleto().comBanco(banco).comDatas(datas).comDescricoes(descricoes).comBeneficiario(beneficiario)
				.comPagador(pagador).comValorBoleto("40.00").comNumeroDoDocumento("4323").comInstrucoes(instrucoes)
				.comLocaisDePagamento(locaisDePagamento);

		GeradorDeBoletoHTML gerador = new GeradorDeBoletoHTML(boleto);
		gerador.geraHTML(FILE_NAME);
	}

	@Test
	public void testHTMLWriterGeneration() {
		assertTrue(new File(FILE_NAME).exists());
	}

	@Test
	public void testHTMLWriterEscreveValorCorreto() {
		assertTrue(lerArquivo().contains("40,00"));
	}

	@Test
	public void testHTMLWriterEscreveLinhaDigitavelCorreta() {
		assertTrue(lerArquivo().contains("00190.00009 01207.113000 09000.206186 5 38600000004000"));
	}
	
	private String lerArquivo() {
		try {
			@SuppressWarnings("resource")
			FileInputStream fileInputStream = new FileInputStream(new File(FILE_NAME));
			int c = 0;
			StringBuffer boleto = new StringBuffer();
			while ((c = fileInputStream.read()) != -1) {
				boleto.append((char) c);
			}
			return boleto.toString().replaceAll("&nbsp;", "");
		} catch (IOException fileNotFoundException) {
			throw new RuntimeException(fileNotFoundException);
		}
	}

}
