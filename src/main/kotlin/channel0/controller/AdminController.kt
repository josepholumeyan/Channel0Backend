package channel0.controller

import channel0.data.dto.request.PopulateRequest
import channel0.data.dto.responses.EpisodeAdminDto
import channel0.domain.services.AdminService
import channel0.domain.services.Mapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService,
    private val mapper: Mapper
) {
    @PostMapping("/populate")
    fun populate(
        @RequestBody request: PopulateRequest
    ) {
            adminService.populateDatabase(request)
    }

    @PostMapping("/update-show-counts")
    fun updateShowCounts(){
        adminService.updateShowCountForChannels()
    }

    @GetMapping("/get-disabled-episodes")
    fun getDisabledEpisodes(): List<EpisodeAdminDto>{
        return mapper.episodeToAdminDto(adminService.getDisabledEpisodes())
    }

    @GetMapping("/get-user-count")
    fun getUserCount(): Long {
        return adminService.getUserCount()
    }

    @GetMapping("/get-device-count")
    fun getDeviceCount(): Long {
        return adminService.getDeviceCount()
    }
}