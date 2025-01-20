package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.LocalDeliveryUnitMailbox
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.LocalDeliveryUnitMailboxRepository

private const val DUMMY_MAILBOX_ID = "8d044b2e-96b1-45ef-a2ce-cce9c6f6a0c2"
private const val BASE_URI: String = "/local-delivery-unit-mailboxes"

class GettingMailboxesTest {
  @Nested
  @DisplayName("GET /local-delivery-unit-mailboxes/:id")
  inner class LduMailboxes : IntegrationTestBase() {
    @Autowired
    lateinit var localDeliveryUnitMailboxes: LocalDeliveryUnitMailboxRepository

    @Test
    fun `should return unauthorized if no token`() {
      webTestClient.get()
        .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
        .exchange()
        .expectStatus()
        .isUnauthorized
    }

    @Test
    fun `should return forbidden if no role`() {
      webTestClient.get()
        .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus()
        .isForbidden
    }

    @Test
    fun `should return forbidden if wrong role`() {
      webTestClient.get()
        .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
        .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
        .exchange()
        .expectStatus()
        .isForbidden
    }

    @Test
    fun `should return not found if mailbox does not exist`() {
      webTestClient.get()
        .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
        .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
        .exchange()
        .expectStatus()
        .isNotFound
    }

    @Sql(
      "classpath:test_data/reset.sql",
      "classpath:test_data/some_ldu_mailboxes.sql",
    )
    @Test
    fun `should return the mailbox details if it exists`() {
      val mailboxId = localDeliveryUnitMailboxes.findAll().first().id
      assertThat(mailboxId).isNotNull

      val mailbox = webTestClient.get()
        .uri("$BASE_URI/$mailboxId")
        .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
        .exchange()
        .expectStatus()
        .isOk.expectBody(object : ParameterizedTypeReference<LocalDeliveryUnitMailbox>() {})
        .returnResult().responseBody!!

      mailbox.apply {
        assertThat(id).isEqualTo(mailboxId)
        assertThat(unitCode).isEqualTo("UNIT_CODE_1")
        assertThat(areaCode).isEqualTo("AREA_CODE_1")
        assertThat(name).isEqualTo("Test Mailbox 1")
        assertThat(emailAddress).isEqualTo("ldu1@example.com")
        assertThat(country).isEqualTo("England")
        assertThat(createdAt).isNotNull
        assertThat(updatedAt).isNotNull
      }
    }
  }
}
