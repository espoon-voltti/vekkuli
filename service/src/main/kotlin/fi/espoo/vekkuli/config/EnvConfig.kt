package fi.espoo.vekkuli.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.env.Environment

@Configuration
@Lazy
class EnvConfig {
    @Bean fun emailEnv(env: Environment): EmailEnv = EmailEnv.fromEnvironment(env)

    @Bean fun paytrailEnv(env: Environment): PaytrailEnv = PaytrailEnv.fromEnvironment(env)
}
