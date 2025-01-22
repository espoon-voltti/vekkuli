package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.service.PaytrailPaymentResponse

data class PaymentInformationResponse(
    val providers: List<PaymentProvider>,
) {
    data class PaymentProvider(
        val name: String,
        val url: String,
        val method: String,
        val icon: String,
        val svg: String,
        val id: String,
        val group: String,
        val parameters: List<PaymentProviderParameter>,
    )

    data class PaymentProviderParameter(
        val name: String,
        val value: String,
    )
}

fun PaytrailPaymentResponse.toPaymentInformationResponse() =
    PaymentInformationResponse(
        providers =
            providers.map { provider ->
                PaymentInformationResponse.PaymentProvider(
                    name = provider.name,
                    url = provider.url,
                    method = if (provider.methodIsPost) "POST" else "GET",
                    icon = provider.icon,
                    svg = provider.svg,
                    id = provider.id,
                    group = provider.group,
                    parameters =
                        provider.parameters.map { parameter ->
                            PaymentInformationResponse.PaymentProviderParameter(
                                name = parameter.name,
                                value = parameter.value
                            )
                        }
                )
            }
    )
