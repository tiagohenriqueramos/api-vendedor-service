package br.com.casasbahia.controller;

import br.com.casasbahia.domain.Vendedor;
import br.com.casasbahia.dto.*;
import br.com.casasbahia.mapper.VendedorMapper;
import br.com.casasbahia.service.VendedorService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/vendedor")
public class VendedorController {

    private final VendedorService vendedorService;

    public VendedorController(VendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProtocoloResponse salvar(@RequestBody @Valid VendedorRequestDTO dto) {
        return vendedorService.salvar(dto, dto.getFilialId());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VendedorResponseDTO buscarPorId(@PathVariable String id) {
        return VendedorMapper.toDTO(vendedorService.buscarPorId(new ObjectId(id)));
    }

    @GetMapping("/protocolo/{protocolo}")
    @ResponseStatus(HttpStatus.OK)
    public VendedorConsultaDTO buscarPorProtocolo(@PathVariable String protocolo) {
        return vendedorService.buscarPorProtocolo(protocolo);
    }

    @GetMapping("/matricula/{matricula}")
    @ResponseStatus(HttpStatus.OK)
    public VendedorResponseDTO buscarPorMatricula(@PathVariable String matricula) {
        return VendedorMapper.toDTO(vendedorService.buscarPorMatricula(matricula));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponseDTO<VendedorResponseDTO> listar(@PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return new PageResponseDTO<>(vendedorService.listar(pageable));
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VendedorResponseDTO atualizar(@PathVariable String id, @RequestBody @Valid VendedorRequestDTO dto) {
        Vendedor vendedor = VendedorMapper.toEntity(dto);
        return VendedorMapper.toDTO(vendedorService.atualizarVendedor(new ObjectId(id), vendedor, dto.getFilialId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable String id) {
        vendedorService.deletarVendedor(new ObjectId(id));
    }
}
