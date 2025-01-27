package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class OffenderManagementUnitMailboxService(
  private val repository: OffenderManagementUnitMailboxRepository,
) {
  @Transactional
  fun createMailbox(newMailbox: OffenderManagementUnitMailboxForm): OffenderManagementUnitMailbox {
    return repository.saveAndFlush(newMailbox.asEntity())
  }
}
