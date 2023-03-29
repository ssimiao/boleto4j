package br.com.boleto.transformer.transformer;

import br.com.boleto.*;
import br.com.boleto.bancos.BancoDoBrasil;
import br.com.boleto.transformer.GeradorDeBoleto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste de INTEGRACAO apesar de estar no de unidade FIXME
 * 
 */
public class BoletoTransformerIntegrationTest{

    private Boleto boleto;

	@BeforeEach
	public void setUp() {

		Locale.setDefault(new Locale("pt", "br"));

		apagaArquivosGerados();

		Datas datas = Datas.novasDatas().comDocumento(4, 5, 2008).comProcessamento(4, 5, 2008)
				.comVencimento(2, 5, 2008);
		Beneficiario beneficiario = Beneficiario.novoBeneficiario().comNomeBeneficiario("Caue").comAgencia("1824").comDigitoAgencia("4")
				.comCodigoBeneficiario("76000").comNumeroConvenio("1207113").comDigitoCodigoBeneficiario("5").comCarteira("18")
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

		GeradorDeBoleto generator = new GeradorDeBoleto(boleto);

		generator.geraPDF("arquivo.pdf");
		generator.geraPNG("arquivo.png");

	}

	@Test
	public void testPDFWriterGeneration() {
		assertTrue(new File("arquivo.pdf").exists());
	}

	@Test
	public void testPDFWriterEscreveValorCorreto() throws IOException {
		PDFTextStripper stripper = new PDFTextStripper();

		PDDocument document = PDDocument.load(new File("arquivo.pdf"));
		String text = stripper.getText(document);
		document.close();
		assertTrue(text.contains("40,00"));
	}

	@Test
	public void testPDFWriterEscreveLinhaDigitavelCorreta() throws IOException {
		PDFTextStripper stripper = new PDFTextStripper();

		PDDocument document = PDDocument.load(new File("arquivo.pdf"));
		String text = stripper.getText(document);
		document.close();

		assertTrue(text.contains("00190.00009  01207.113000  09000.206186  5  38600000004000"));
	}

	@Test
	public void testPNGWriteGeneration() {
		assertTrue(new File("arquivo.png").exists());
	}

    @Test
    public void testByteArrayGeneration() {
        GeradorDeBoleto geradorDeBoleto = new GeradorDeBoleto(this.boleto);
        assertNotNull(geradorDeBoleto.geraPDF());
        assertNotNull(geradorDeBoleto.geraPNG());
    }

	@AfterEach
	public void apagaArquivosGerados() {
		final File pngFile = new File("arquivo.png");
		final File pdfFile = new File("arquivo.pdf");
		apagaArquivoSeExistir(pngFile);
		apagaArquivoSeExistir(pdfFile);
	}

	private void apagaArquivoSeExistir(final File pngFile) {
		if (pngFile.exists()) {
			pngFile.delete();
		}
	}

}
