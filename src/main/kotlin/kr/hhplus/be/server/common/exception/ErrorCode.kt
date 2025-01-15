package kr.hhplus.be.server.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(val httpStatus: HttpStatus, val message: String) {
	USER_TOKEN_NOT_FOUND(UNAUTHORIZED, "유저 인증 실패 : 쿠키에 유저 토큰이 없습니다."),
	INVALID_USER_TOKEN(UNAUTHORIZED, "유저 인증 실패 : 없는 유저 입니다."),

	QUEUE_TOKEN_NOT_FOUND(FORBIDDEN, "대기열 인증 실패 : 쿠키에 대기열 토큰이 없습니다."),
	INVALID_QUEUE_TOKEN(FORBIDDEN, "대기열 인증 실패 : 없는 대기열 토큰 입니다."),
	REQUIRE_ACTIVATED_QUEUE_TOKEN(FORBIDDEN, "대기열 인증 실패 : 아직 입장할 수 없는 대기열 토큰입니다."),

	ENTITY_NOT_FOUND(BAD_REQUEST, "엔티티를 찾을 수 없습니다."),

	EXPIRED_RESERVATION(BAD_REQUEST, "이미 만료된 예약입니다."),
	ALREADY_RESERVED(BAD_REQUEST, "이미 예약된 좌석입니다."),

	ALREADY_PAYED_RESERVATION(BAD_REQUEST, "이미 결제된 예약입니다."),

	EXCEED_CHARGE_LIMIT(BAD_REQUEST, "한도를 초과해서 충전할 수 없습니다."),
	NOT_ENOUGH_POINT(BAD_REQUEST, "잔액이 부족합니다."),

	NOT_MATCH_SCHEDULE(BAD_REQUEST, "콘서트 내 콘서트 일정이 없습니다."),
	NOT_MATCH_SEAT(BAD_REQUEST, "콘서트 일정 내 콘서트 좌석이 없습니다."),
	;
}