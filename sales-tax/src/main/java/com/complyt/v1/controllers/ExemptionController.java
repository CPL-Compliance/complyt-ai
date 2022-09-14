package com.complyt.v1.controllers;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.facades.ExemptionFacade;
import com.complyt.security.permissions.transaction.TransactionReadPermission;
import com.complyt.security.permissions.transaction.TransactionUpdatePermission;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.model.TransactionDto;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Tag(name = "Exemption", description = "This is the Exemption controller")
@RestController
@RequestMapping(ExemptionController.BASE_URL)
public class ExemptionController {

    public static final String BASE_URL = "/v1/exemptions";

    @NonNull
    private ExemptionFacade exemptionFacade;

    @Operation(summary = "Gets exemption by id")
    @TransactionReadPermission
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<ExemptionDto>> getOne(@PathVariable("id") @NonNull String id) {
        return exemptionFacade.findById(id)
                .map(exemptionItem -> new ResponseEntity<>(ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemptionItem), HttpStatus.OK))
                .switchIfEmpty(Mono.error(new NotFoundException(id)));
    }

    @Operation(summary = "This will update the exemption if found by id, otherwise it will create it")
    @TransactionUpdatePermission
    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<ExemptionDto>> create(@RequestBody @NonNull ExemptionDto exemptionDto) {
        log.debug("Create exemption - DTO received in request body : " + exemptionDto);
        Exemption receivedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);
        return exemptionFacade.save(receivedExemption)
                .map(exemption -> ResponseEntity.status(HttpStatus.CREATED).body(ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption)));
    }
    
    @Operation(summary = "This will update the exemption if found by id, otherwise it will create it")
    @TransactionUpdatePermission
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<ExemptionDto>> update(@PathVariable String id, @RequestBody @NonNull ExemptionDto exemptionDto) {
        log.debug("Update exemption - DTO received in request body : " + exemptionDto);
        Exemption receivedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);

        return exemptionFacade.findById(exemptionDto.getId())
                .flatMap(originalExemption -> exemptionFacade.update(receivedExemption, id))
                .map(updatedExemption -> ResponseEntity.status(HttpStatus.OK).body(ExemptionMapper.INSTANCE.exemptionToExemptionDto(updatedExemption)));
    }

    @Operation(summary = "Gets all exemptions")
    @TransactionReadPermission
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<ExemptionDto> getAll() {
        return exemptionFacade.findAll().map(ExemptionMapper.INSTANCE::exemptionToExemptionDto);
    }

}
