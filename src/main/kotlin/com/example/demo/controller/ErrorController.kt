import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomErrorController : ErrorController {
    @RequestMapping("/error")
    fun handleError(): String {
        return "Oops! The page you're looking for doesn't exist (unlike mermaids)"
    }
}
