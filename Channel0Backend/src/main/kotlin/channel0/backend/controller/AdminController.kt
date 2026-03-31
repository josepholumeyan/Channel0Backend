package channel0.backend.controller

import channel0.backend.data.dto.request.PopulateRequest
import channel0.backend.domain.services.AdminService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService
) {
    @PostMapping("/populate")
    fun populate(
        @RequestHeader("X-ADMIN-KEY") key: String,
        @RequestBody request: PopulateRequest
    ) {
        val realKey = System.getenv("ADMIN_KEY")
        if (key == realKey) {
            adminService.populateDatabase(request)
        }else{
            throw RuntimeException("unauthorized")
        }
    }
}