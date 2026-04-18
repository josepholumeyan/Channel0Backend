package channel0.data.dto.request

import channel0.backend.data.dto.request.ChannelRequest

data class PopulateRequest(
    val channels: List<ChannelRequest>
)
