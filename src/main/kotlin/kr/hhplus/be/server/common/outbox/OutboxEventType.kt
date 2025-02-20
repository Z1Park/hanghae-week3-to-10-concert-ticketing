package kr.hhplus.be.server.common.outbox

enum class OutboxEventType {
	RESERVE,    // 예약 로직
	PAY,        // 결제 로직
	;
}