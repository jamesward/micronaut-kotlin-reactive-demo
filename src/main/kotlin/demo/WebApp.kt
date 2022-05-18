package demo

import io.micronaut.http.HttpHeaders.USER_AGENT
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.Micronaut

data class Release(val name: String)

@Client("https://api.github.com/repos/jetbrains/kotlin/tags")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
interface ReleasesService {
    @Get
    suspend fun releases(): List<Release>
}

@Controller
class WebApp(private val releasesService: ReleasesService) {
    @Get("/", produces = [MediaType.APPLICATION_JSON])
    suspend fun index() = run {
        releasesService.releases()
            .filterNot { it.name.contains('-') }
            .firstOrNull()?.name ?: "not found"
    }
}

fun main() {
    Micronaut.run()
}
