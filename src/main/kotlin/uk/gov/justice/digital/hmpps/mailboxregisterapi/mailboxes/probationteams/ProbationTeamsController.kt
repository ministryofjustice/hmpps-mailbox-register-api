package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ValidationErrorResponse
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLog
import uk.gov.justice.hmpps.kotlin.common.ErrorResponse

@RestController
@PreAuthorize("hasRole('MAILBOX_REGISTER_ADMIN')")
@RequestMapping(value = ["/probation-teams"], produces = ["application/json"])
class ProbationTeamsController(
  private val probationTeamsService: ProbationTeamsService,
  private val auditLog: AuditLog,
) {
  @PostMapping(value = [""])
  @ResponseStatus(code = HttpStatus.CREATED)
  @Operation(
    summary = "Creates a new local delivery unit mailbox",
    description = "Creates a new local delivery unit mailbox",
    security = [SecurityRequirement(name = "mailbox-register-api-ui-role")],
    responses = [
      ApiResponse(responseCode = "201", description = "The local delivery unit mailbox was created"),
      ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ValidationErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden access to this endpoint",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class))],
      ),
    ],
  )
  fun create(@Valid @RequestBody probationTeamForm: ProbationTeamForm) =
    probationTeamsService.createProbationTeam(probationTeamForm)
      .also { auditLog.logCreationOf(it) }
}
