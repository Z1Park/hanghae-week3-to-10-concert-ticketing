package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.domain.user.UserInfo
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Component

/**
 * MSA 환경에서 각 서비스의 외부 API 클라이언트는 Infrastructure 계층이 맞지만,
 * 현재 구조상 MSA 환경을 가정하고 사용하고 있고 Service/Domain을 호출하기 때문에 application 계층에 둔다.
 */
@Component
class UserApiClient(
	private val userService: UserService
) {

	fun userApiGetUserByUUID(userUUID: String): UserInfo =
		UserInfo.from(userService.getByUuid(userUUID))
}