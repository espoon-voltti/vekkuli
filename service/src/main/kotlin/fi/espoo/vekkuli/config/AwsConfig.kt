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
    const val ATTACHMENT_BUCKET_NAME = "vekkuli-attachments"
    const val MAX_FILE_SIZE = 10 * 1000 * 1000
}

@Configuration
class AwsConfig {
    @Bean
    @Profile("local || test")
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

    @Bean
    @Profile("local || test")
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

        return client.addAttachmentBucket()
    }

    private fun S3Client.addAttachmentBucket(): S3Client {
        val existingBuckets = this.listBuckets().buckets().map { it.name() }
        if (!existingBuckets.contains(AwsConstants.ATTACHMENT_BUCKET_NAME)) {
            val request = CreateBucketRequest.builder().bucket(AwsConstants.ATTACHMENT_BUCKET_NAME).build()
            this.createBucket(request)
        }
        return this
    }

    @Bean
    @Profile("production")
    fun amazonS3Prod(
        env: EmailEnv,
        credentialsProvider: AwsCredentialsProvider
    ): S3Client {
        val client =
            S3Client
                .builder()
                .credentialsProvider(credentialsProvider)
                .region(env.region)
                .build()
        return client.addAttachmentBucket()
    }
}
