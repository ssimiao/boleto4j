package br.com.boleto.bancos;

import static br.com.boleto.utils.StellaStringUtils.prefixNotNullStringOrDefault;
import br.com.boleto.Banco;
import br.com.boleto.Beneficiario;
import br.com.boleto.Boleto;
import br.com.boleto.bancos.gerador.GeradorDeDigito;
import br.com.boleto.bancos.gerador.GeradorDeDigitoPadrao;

public abstract class AbstractBanco implements Banco {

	private static final long serialVersionUID = 1L;

	protected final GeradorDeDigito geradorDeDigito = new GeradorDeDigitoPadrao();

	@Override
	public GeradorDeDigito getGeradorDeDigito() {
		return geradorDeDigito;
	}

	@Override
	public String getNossoNumeroECodigoDocumento(Boleto boleto) {
		Beneficiario beneficiario = boleto.getBeneficiario();
		StringBuilder builder = new StringBuilder().append(beneficiario.getCarteira());
		builder.append("/").append(getNossoNumeroFormatado(beneficiario));
		return builder.toString();
	}

	@Override
	public String getAgenciaECodigoBeneficiario(Beneficiario beneficiario) {
		StringBuilder builder = new StringBuilder();
		builder.append(beneficiario.getAgenciaFormatada());
		builder.append(prefixNotNullStringOrDefault(beneficiario.getDigitoAgencia(), "", "-"));
		builder.append("/");
		builder.append(getCodigoBeneficiarioFormatado(beneficiario));
		builder.append(prefixNotNullStringOrDefault(beneficiario.getDigitoCodigoBeneficiario(), "", "-"));	
		return builder.toString();
	}

}
