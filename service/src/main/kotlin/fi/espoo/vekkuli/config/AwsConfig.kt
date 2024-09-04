package fi.espoo.vekkuli.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.ses.SesClient

@Configuration
class AwsConfig {
    @Bean
    @Profile("local")
    fun credentialsProviderLocal(): AwsCredentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create("foo", "bar"))

    @Bean
    @Profile("production")
    fun credentialsProviderProd(): AwsCredentialsProvider = DefaultCredentialsProvider.create()

    @Bean
    fun amazonSES(
        env: EmailEnv,
        awsCredentialsProvider: AwsCredentialsProvider?
    ): SesClient =
        SesClient
            .builder()
            .credentialsProvider(awsCredentialsProvider)
            .region(env.region)
            .build()
}
