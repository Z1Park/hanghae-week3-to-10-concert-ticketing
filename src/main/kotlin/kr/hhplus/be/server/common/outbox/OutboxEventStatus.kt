package kr.hhplus.be.server.common.outbox

enum class OutboxEventStatus {
	CREATED,
	PROCESSED,
	ROLLBACKED,
	FAIL,
	;
}