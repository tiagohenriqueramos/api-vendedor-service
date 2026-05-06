package br.com.casasbahia.dto;

import br.com.casasbahia.dlq.entity.VendedorDlqEntity;
import br.com.casasbahia.domain.Vendedor;
import br.com.casasbahia.mapper.VendedorMapper;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendedorConsultaDTO {

    private String protocolo;
    private String status;
    private String payload;
    private String erro;
    private VendedorResponseDTO vendedor;

    public VendedorConsultaDTO(String protocolo, String status, VendedorResponseDTO vendedor) {
        this.protocolo = protocolo;
        this.status = status;
        this.vendedor = vendedor;
    }

    public VendedorConsultaDTO(String protocolo, String status, String payload, String erro) {
        this.protocolo = protocolo;
        this.status = status;
        this.payload = payload;
        this.erro = erro;
    }
    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public VendedorResponseDTO getVendedor() {
        return vendedor;
    }

    public void setVendedor(VendedorResponseDTO vendedor) {
        this.vendedor = vendedor;
    }

    public static VendedorConsultaDTO fromEntity(Vendedor vendedor) {
        return new VendedorConsultaDTO(
                vendedor.getProtocolo(),
                "PROCESSADO",
                VendedorMapper.toDTO(vendedor)
        );
    }

    public static VendedorConsultaDTO fromDlq(VendedorDlqEntity dlq) {
        return new VendedorConsultaDTO(
                dlq.getProtocolo(),
                "ERRO",
                dlq.getPayload(),
                dlq.getError()
        );
    }
}
