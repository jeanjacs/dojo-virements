package fr.dojo.virements.exposition.api;

import fr.dojo.virements.application.transfer.port.in.TransferUseCase;
import fr.dojo.virements.domaine.transfer.exception.InvalidTransferException;
import fr.dojo.virements.exposition.api.model.AccountResponse;
import fr.dojo.virements.exposition.api.model.TransferRequest;
import fr.dojo.virements.exposition.api.model.TransferResponse;
import fr.dojo.virements.exposition.common.error.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.base-path:/api/v1}")
@Tag(name = "Transfers")
public class TransferController {
    private final TransferUseCase transfers;

    public TransferController(TransferUseCase transfers) {
        this.transfers = transfers;
    }

    @Operation(summary = "Create an internal transfer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transfer recorded"),
            @ApiResponse(responseCode = "400", description = "Request rejected",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse createTransfer(@RequestBody TransferRequest request) {
        if (request == null) {
            throw new InvalidTransferException("The transfer request is required.");
        }
        return TransferResponse.from(
                transfers.transfer(request.source(), request.destination(), request.amount()));
    }

    @Operation(summary = "List transfer history")
    @GetMapping("/transfers")
    public List<TransferResponse> listTransfers() {
        return transfers.getHistory().stream()
                .map(TransferResponse::from)
                .toList();
    }

    @Operation(summary = "Get an account balance")
    @ApiResponse(responseCode = "400", description = "Unknown account",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/accounts/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber) {
        return new AccountResponse(accountNumber, transfers.getBalance(accountNumber));
    }
}
