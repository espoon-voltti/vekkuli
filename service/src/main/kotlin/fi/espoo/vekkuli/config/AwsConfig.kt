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
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.utils.AttributeMap
import java.net.URI

@Configuration
class AwsConfig {
    val bucketName = "vekkuli-attachments"

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

    @Bean
    @Profile("local")
    fun amazonS3Local(credentialsProvider: AwsCredentialsProvider): S3Client {
        val attrs =
            AttributeMap
                .builder()
                .put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
                .build()
        val client =
            S3Client
                .builder()
                .httpClient(DefaultSdkHttpClientBuilder().buildWithDefaults(attrs))
                .region(Region.US_EAST_1)
                .serviceConfiguration(
                    S3Configuration.builder().pathStyleAccessEnabled(true).build()
                ).endpointOverride(URI("https://localhost:9191"))
                .credentialsProvider(credentialsProvider)
                .build()

        val existingBuckets = client.listBuckets().buckets().map { it.name()!! }
        if (!existingBuckets.contains(bucketName)) {
            val request = CreateBucketRequest.builder().bucket(bucketName).build()
            client.createBucket(request)
        }
        return client
    }
}
