package fi.espoo.vekkuli.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder
import software.amazon.awssdk.http.SdkHttpConfigurationOption
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.utils.AttributeMap
import java.net.URI

object AwsConstants {
    const val MAX_FILE_SIZE: Long = 10L * 1000 * 1000

    /**
     * Raw (pre-MIME-encoded) combined-size cap for all attachments on a single
     * email. Chosen so the base64-encoded MIME message stays safely under the
     * AWS SES 10 MB limit, with headroom for body, headers and MIME boundaries.
     */
    const val MAX_RAW_ATTACHMENT_TOTAL_BYTES: Long = 7L * 1000 * 1000
}

@Configuration
class AwsConfig {
    @Bean
    @Profile("local | test")
    fun credentialsProviderLocal(): AwsCredentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create("foo", "bar"))

    @Bean
    @Profile("production | staging")
    fun credentialsProviderProd(): AwsCredentialsProvider =
        DefaultCredentialsProvider
            .builder()
            .build()

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

    @Bean
    @Profile("local | test")
    fun amazonS3Local(
        env: EmailEnv,
        credentialsProvider: AwsCredentialsProvider
    ): S3Client {
        val attrs =
            AttributeMap
                .builder()
                .put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
                .build()
        val client =
            S3Client
                .builder()
                .httpClient(DefaultSdkHttpClientBuilder().buildWithDefaults(attrs))
                .region(env.region)
                .serviceConfiguration(
                    S3Configuration.builder().pathStyleAccessEnabled(true).build()
                ).endpointOverride(URI(env.s3MockUrl))
                .credentialsProvider(credentialsProvider)
                .build()

        val existingBuckets = client.listBuckets().buckets().map { it.name() }
        if (!existingBuckets.contains(env.s3BucketName)) {
            val request = CreateBucketRequest.builder().bucket(env.s3BucketName).build()
            client.createBucket(request)
        }
        return client
    }

    @Bean
    @Profile("production | staging")
    fun amazonS3Prod(credentialsProvider: AwsCredentialsProvider): S3Client =
        S3Client
            .builder()
            .credentialsProvider(credentialsProvider)
            .build()
}
