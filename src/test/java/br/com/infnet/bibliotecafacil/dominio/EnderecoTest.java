package br.com.infnet.bibliotecafacil.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EnderecoTest {

    @Test
    public void deveRepresentarCamposDaV1() {
        final Endereco endereco = criarEndereco(
                10L,
                "20040020",
                "Rua Principal",
                "10",
                "Sala 2",
                "Centro",
                "Rio de Janeiro",
                "RJ",
                -22.9,
                -43.2);

        assertEquals(10L, endereco.getId());
        assertEquals("20040020", endereco.getCep());
        assertEquals("Rua Principal", endereco.getLogradouro());
        assertEquals("10", endereco.getNumero());
        assertEquals("Sala 2", endereco.getComplemento());
        assertEquals("Centro", endereco.getBairro());
        assertEquals("Rio de Janeiro", endereco.getCidade());
        assertEquals("RJ", endereco.getUf());
        assertEquals(-22.9, endereco.getLatitude());
        assertEquals(-43.2, endereco.getLongitude());
        assertNotNull(endereco.getDataCriacao());
        assertEquals(endereco.getDataCriacao(), endereco.getDataAtualizacao());
        assertTrue(endereco.toString().contains("cep='20040020'"));
    }

    @Test
    public void deveRepresentarCamposOpcionaisAusentesComoNull() {
        final Endereco endereco = criarEndereco(
                1L,
                "20040020",
                "Rua Principal",
                "10",
                null,
                "Centro",
                "Rio de Janeiro",
                "RJ",
                null,
                null);

        assertNull(endereco.getComplemento());
        assertNull(endereco.getLatitude());
        assertNull(endereco.getLongitude());
    }

    private Endereco criarEndereco(final Long id, final String cep, final String logradouro, final String numero, final String complemento, final String bairro, final String cidade, final String uf, final Double latitude, final Double longitude) {
        final Endereco endereco = new Endereco();
        endereco.setId(id);
        endereco.setCep(cep);
        endereco.setLogradouro(logradouro);
        endereco.setNumero(numero);
        endereco.setComplemento(complemento);
        endereco.setBairro(bairro);
        endereco.setCidade(cidade);
        endereco.setUf(uf);
        endereco.setLatitude(latitude);
        endereco.setLongitude(longitude);
        return endereco;
    }
}
